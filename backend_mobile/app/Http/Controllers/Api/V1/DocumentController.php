<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\Document;
use App\Models\DocumentShare;
use App\Models\Lawyer;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;
use Tymon\JWTAuth\Facades\JWTAuth;

class DocumentController extends ApiController
{
    /**
     * List documents for current user
     * GET /api/v1/documents
     */
    public function index(Request $request): JsonResponse
    {
        $user = JWTAuth::user();
        $client = $user->client;

        if (!$client) {
            return $this->error('CLIENT_NOT_FOUND', 'Client profile not found.', 404);
        }

        $page = $request->input('page', 1);
        $limit = $request->input('limit', 20);
        $search = $request->input('search');
        $type = $request->input('type', 'all');
        $sortBy = $request->input('sort_by', 'date');
        $sortOrder = $request->input('sort_order', 'desc');

        $query = Document::where('client_id', $client->id);

        if ($search) {
            $query->where('title', 'like', "%{$search}%");
        }

        if ($type !== 'all') {
            $query->where('file_type', $type);
        }

        $sortField = match($sortBy) {
            'name' => 'title',
            'size' => 'file_size_kb',
            default => 'created_at'
        };

        $documents = $query->orderBy($sortField, $sortOrder)
            ->paginate($limit, ['*'], 'page', $page);

        $data = $documents->map(fn($doc) => [
            'id' => $doc->id,
            'name' => $doc->title,
            'file_url' => $doc->file_url,
            'file_type' => $doc->file_type,
            'file_size_kb' => $doc->file_size_kb,
            'is_shared_with_lawyer' => $doc->shares()->exists(),
        ]);

        $storageUsed = Document::where('client_id', $client->id)->sum('file_size_kb') / 1024;
        $storageLimit = 200; // MB

        return $this->success([
            'documents' => $data,
            'storage_used_mb' => round($storageUsed, 1),
            'storage_limit_mb' => $storageLimit,
        ]);
    }

    /**
     * Upload new document (client only)
     * POST /api/v1/documents
     */
    public function store(Request $request): JsonResponse
    {
        try {
            $validated = $request->validate([
                'file' => 'required|file|mimes:pdf,jpg,png,docx,xlsx|max:51200', // 50MB
                'name' => 'nullable|string|max:255',
            ]);

            $user = JWTAuth::user();
            $client = $user->client;

            if (!$client) {
                return $this->error('CLIENT_NOT_FOUND', 'Client profile not found.', 404);
            }

            // Check storage limit
            $currentStorage = Document::where('client_id', $client->id)->sum('file_size_kb') / 1024;
            $file = $request->file('file');
            $newFileSizeMB = $file->getSize() / (1024 * 1024);

            if (($currentStorage + $newFileSizeMB) > 200) {
                return $this->error('STORAGE_LIMIT_EXCEEDED', 'The client\'s 200 MB storage quota has been reached.', 403);
            }

            $filename = 'doc_' . $user->id . '_' . time() . '_' . Str::random(8) . '.' . $file->getClientOriginalExtension();
            $path = $file->storeAs('documents', $filename, 'private');
            $fileUrl = Storage::url($path);

            $documentId = 'doc_' . Str::random(12);
            $documentName = $request->input('name', $file->getClientOriginalName());

            $document = Document::create([
                'id' => $documentId,
                'client_id' => $client->id,
                'title' => $documentName,
                'file_url' => $fileUrl,
                'file_type' => pathinfo($filename, PATHINFO_EXTENSION),
                'file_size_kb' => round($file->getSize() / 1024),
                'status' => 'active',
                'upload_date' => now(),
            ]);

            return $this->success([
                'id' => $document->id,
                'name' => $document->title,
                'file_url' => $document->file_url,
                'file_type' => $document->file_type,
                'file_size_kb' => $document->file_size_kb,
                'added_at' => $document->created_at->toIso8601String(),
            ], 'Document uploaded successfully.', 201);
        } catch (\Illuminate\Validation\ValidationException $e) {
            return $this->error('VALIDATION_ERROR', 'The given data was invalid.', 422, $e->errors());
        } catch (\Exception $e) {
            return $this->error('UPLOAD_FAILED', 'Failed to upload document: ' . $e->getMessage(), 500);
        }
    }

    /**
     * Show document details
     */
    public function show(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $document = Document::with(['client.profile', 'shares.lawyer.profile'])
            ->where('id', $id)
            ->first();

        if (!$document) {
            return $this->error('DOCUMENT_NOT_FOUND', 'Document not found.', 404);
        }

        // Check authorization
        $isAuthorized = ($user->isClient() && $document->client_id === $user->client?->id) ||
            ($user->isLawyer() && $document->shares()->where('lawyer_id', $user->lawyer?->id)->exists());

        if (!$isAuthorized) {
            return $this->error('FORBIDDEN', 'You are not authorized to view this document.', 403);
        }

        return $this->success([
            'id' => $document->id,
            'title' => $document->title,
            'file_url' => $document->file_url,
            'file_type' => $document->file_type,
            'file_size_kb' => $document->file_size_kb,
            'status' => $document->status,
            'upload_date' => $document->upload_date?->toIso8601String(),
            'client_name' => $document->client?->profile?->full_name,
            'shares' => $document->shares->map(fn($share) => [
                'lawyer_name' => $share->lawyer?->name,
                'shared_at' => $share->created_at->toIso8601String(),
                'expires_at' => $share->expires_at?->toIso8601String(),
            ]),
        ]);
    }

    /**
     * Update document metadata (client only)
     */
    public function update(string $id, Request $request): JsonResponse
    {
        $request->validate([
            'title' => 'sometimes|string|max:255',
            'status' => 'sometimes|in:active,archived',
        ]);

        $user = JWTAuth::user();

        $document = Document::where('id', $id)
            ->where('client_id', $user->client?->id)
            ->first();

        if (!$document) {
            return $this->error('DOCUMENT_NOT_FOUND', 'Document not found.', 404);
        }

        $document->update($request->only(['title', 'status']));

        return $this->success([
            'id' => $document->id,
            'title' => $document->title,
            'status' => $document->status,
        ], 'Document updated successfully.');
    }

    /**
     * Delete document (client only)
     */
    public function destroy(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $document = Document::where('id', $id)
            ->where('client_id', $user->client?->id)
            ->first();

        if (!$document) {
            return $this->error('DOCUMENT_NOT_FOUND', 'Document not found.', 404);
        }

        // Delete file from storage
        try {
            $path = str_replace('/storage/', '', $document->file_url);
            Storage::delete($path);
        } catch (\Exception $e) {
            // Log error but continue
        }

        $document->delete();

        return $this->success(null, 'Document deleted successfully.');
    }

    /**
     * Share document with lawyer (client only)
     */
    public function share(string $id, Request $request): JsonResponse
    {
        $request->validate([
            'lawyer_id' => 'required|integer|exists:users,id',
            'expires_at' => 'nullable|date|after:now',
        ]);

        $user = JWTAuth::user();

        $document = Document::where('id', $id)
            ->where('client_id', $user->client?->id)
            ->first();

        if (!$document) {
            return $this->error('DOCUMENT_NOT_FOUND', 'Document not found.', 404);
        }

        $lawyer = Lawyer::with('profile.user')
            ->whereHas('profile.user', fn($q) => $q->where('id', $request->input('lawyer_id')))
            ->first();

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer not found.', 404);
        }

        // Check if already shared
        $existingShare = DocumentShare::where('document_id', $id)
            ->where('lawyer_id', $lawyer->id)
            ->first();

        if ($existingShare) {
            return $this->error('ALREADY_SHARED', 'Document is already shared with this lawyer.', 409);
        }

        $shareId = 'share_' . Str::random(12);

        $share = DocumentShare::create([
            'id' => $shareId,
            'document_id' => $id,
            'lawyer_id' => $lawyer->id,
            'expires_at' => $request->input('expires_at'),
        ]);

        return $this->success([
            'id' => $share->id,
            'lawyer_name' => $lawyer->name,
            'shared_at' => $share->created_at->toIso8601String(),
            'expires_at' => $share->expires_at?->toIso8601String(),
        ], 'Document shared successfully.');
    }
}


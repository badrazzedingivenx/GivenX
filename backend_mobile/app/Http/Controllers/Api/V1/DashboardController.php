<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\Appointment;
use App\Models\Conversation;
use App\Models\Notification;
use App\Models\Payment;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Tymon\JWTAuth\Facades\JWTAuth;

class DashboardController extends ApiController
{
    /**
     * User dashboard summary
     */
    public function user(): JsonResponse
    {
        $user = JWTAuth::user();

        // Upcoming appointments
        $upcomingAppointments = Appointment::where('client_user_id', $user->id)
            ->upcoming()
            ->with('lawyer.profile')
            ->orderBy('date')
            ->orderBy('time')
            ->take(5)
            ->get();

        // Recent conversations
        $recentConversations = Conversation::forUser($user->id)
            ->with(['lawyer.profile', 'client.profile'])
            ->orderBy('last_message_sent_at', 'desc')
            ->take(5)
            ->get();

        // Unread notifications count
        $unreadNotifications = Notification::forUser($user->id)->unread()->count();

        // Recent payments
        $recentPayments = Payment::where('client_id', $user->client?->id)
            ->orderBy('created_at', 'desc')
            ->take(5)
            ->get();

        return $this->success([
            'upcoming_appointments' => $upcomingAppointments->map(fn($apt) => [
                'id' => $apt->id,
                'date' => $apt->date?->toDateString(),
                'time' => $apt->time,
                'status' => $apt->status,
                'lawyer_name' => $apt->lawyer->profile?->full_name ?? $apt->lawyer->full_name,
            ]),
            'recent_conversations' => $recentConversations->map(fn($conv) => [
                'id' => $conv->id,
                'other_party_name' => $conv->lawyer_user_id === $user->id
                    ? ($conv->client->profile?->full_name ?? $conv->client->full_name)
                    : ($conv->lawyer->profile?->full_name ?? $conv->lawyer->full_name),
                'last_message' => $conv->last_message_content,
                'unread_count' => $conv->client_user_id === $user->id
                    ? $conv->unread_count_user
                    : $conv->unread_count_lawyer,
            ]),
            'unread_notifications' => $unreadNotifications,
            'recent_payments' => $recentPayments->map(fn($pay) => [
                'id' => $pay->id,
                'amount' => $pay->amount,
                'amount_text' => $pay->amount_text,
                'status' => $pay->status,
                'date' => $pay->date?->toDateString(),
            ]),
        ]);
    }

    /**
     * Lawyer dashboard summary
     */
    public function lawyer(): JsonResponse
    {
        $user = JWTAuth::user();
        $lawyer = $user->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        // Today's appointments
        $todayAppointments = Appointment::where('lawyer_user_id', $user->id)
            ->where('date', today())
            ->whereNotIn('status', ['cancelled', 'no_show'])
            ->with('client.profile')
            ->orderBy('time')
            ->get();

        // Upcoming appointments count
        $upcomingCount = Appointment::where('lawyer_user_id', $user->id)
            ->upcoming()
            ->count();

        // Total earnings this month
        $monthlyEarnings = Payment::where('lawyer_id', $lawyer->id)
            ->where('status', 'Completed')
            ->whereMonth('paid_at', now()->month)
            ->whereYear('paid_at', now()->year)
            ->sum('amount');

        // Pending consultation requests
        $pendingRequests = \App\Models\Consultation::where('lawyer_id', $lawyer->id)
            ->where('status', 'pending')
            ->count();

        // Recent conversations
        $recentConversations = Conversation::forUser($user->id)
            ->with('client.profile')
            ->orderBy('last_message_sent_at', 'desc')
            ->take(5)
            ->get();

        // Unread notifications
        $unreadNotifications = Notification::forUser($user->id)->unread()->count();

        return $this->success([
            'today_appointments' => $todayAppointments->map(fn($apt) => [
                'id' => $apt->id,
                'time' => $apt->time,
                'status' => $apt->status,
                'client_name' => $apt->client->profile?->full_name ?? $apt->client->full_name,
                'type' => $apt->type,
            ]),
            'upcoming_appointments_count' => $upcomingCount,
            'monthly_earnings' => $monthlyEarnings,
            'pending_requests' => $pendingRequests,
            'rating' => $lawyer->rating,
            'review_count' => $lawyer->review_count,
            'recent_conversations' => $recentConversations->map(fn($conv) => [
                'id' => $conv->id,
                'client_name' => $conv->client->profile?->full_name ?? $conv->client->full_name,
                'last_message' => $conv->last_message_content,
                'unread_count' => $conv->unread_count_lawyer,
            ]),
            'unread_notifications' => $unreadNotifications,
        ]);
    }

    /**
     * Lawyer schedule for the week
     */
    public function lawyerSchedule(): JsonResponse
    {
        $user = JWTAuth::user();

        $startOfWeek = now()->startOfWeek();
        $endOfWeek = now()->endOfWeek();

        $appointments = Appointment::where('lawyer_user_id', $user->id)
            ->whereBetween('date', [$startOfWeek, $endOfWeek])
            ->whereNotIn('status', ['cancelled', 'no_show'])
            ->with('client.profile')
            ->orderBy('date')
            ->orderBy('time')
            ->get();

        // Group by day
        $schedule = $appointments->groupBy(fn($apt) => $apt->date->format('Y-m-d'))
            ->map(fn($dayAppointments) => $dayAppointments->map(fn($apt) => [
                'id' => $apt->id,
                'time' => $apt->time,
                'duration_min' => $apt->duration_min,
                'status' => $apt->status,
                'client_name' => $apt->client->profile?->full_name ?? $apt->client->full_name,
                'type' => $apt->type,
            ]));

        return $this->success($schedule);
    }

    /**
     * Add task (using cache for simplicity)
     */
    public function addTask(Request $request): JsonResponse
    {
        $request->validate([
            'title' => 'required|string|max:255',
            'due_date' => 'nullable|date',
            'priority' => 'nullable|in:low,medium,high',
        ]);

        $user = JWTAuth::user();
        $taskId = 'task_' . Str::random(8);

        $tasks = Cache::get('lawyer_tasks_' . $user->id, []);

        $tasks[] = [
            'id' => $taskId,
            'title' => $request->input('title'),
            'due_date' => $request->input('due_date'),
            'priority' => $request->input('priority', 'medium'),
            'completed' => false,
            'created_at' => now()->toIso8601String(),
        ];

        Cache::put('lawyer_tasks_' . $user->id, $tasks, now()->addDays(30));

        return $this->success([
            'id' => $taskId,
            'title' => $request->input('title'),
        ], 'Task added successfully.', 201);
    }

    /**
     * Update task
     */
    public function updateTask(string $id, Request $request): JsonResponse
    {
        $request->validate([
            'title' => 'sometimes|string|max:255',
            'completed' => 'sometimes|boolean',
            'due_date' => 'nullable|date',
            'priority' => 'nullable|in:low,medium,high',
        ]);

        $user = JWTAuth::user();
        $tasks = Cache::get('lawyer_tasks_' . $user->id, []);

        $taskIndex = null;
        foreach ($tasks as $index => $task) {
            if ($task['id'] === $id) {
                $taskIndex = $index;
                break;
            }
        }

        if ($taskIndex === null) {
            return $this->error('TASK_NOT_FOUND', 'Task not found.', 404);
        }

        $tasks[$taskIndex] = array_merge($tasks[$taskIndex], $request->only(['title', 'completed', 'due_date', 'priority']));

        Cache::put('lawyer_tasks_' . $user->id, $tasks, now()->addDays(30));

        return $this->success($tasks[$taskIndex], 'Task updated successfully.');
    }

    /**
     * Delete task
     */
    public function deleteTask(string $id): JsonResponse
    {
        $user = JWTAuth::user();
        $tasks = Cache::get('lawyer_tasks_' . $user->id, []);

        $tasks = array_filter($tasks, fn($task) => $task['id'] !== $id);
        $tasks = array_values($tasks); // Re-index array

        Cache::put('lawyer_tasks_' . $user->id, $tasks, now()->addDays(30));

        return $this->success(null, 'Task deleted successfully.');
    }
}


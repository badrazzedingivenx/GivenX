<?php
/**
 * Point d'entrée pour accéder à l'application Laravel (haqqi-ai) via WAMP
 * Ex: http://localhost/Haqqiv1/api/ai/ask
 */

$uri = $_SERVER['REQUEST_URI'] ?? '/';
$uri = preg_replace('/\?.*/', '', $uri); // Enlever la query string pour le matching

// Détecter le préfixe dynamiquement (ex: /Haqqiv1, /haqqiv1)
$docRoot = rtrim(str_replace('\\', '/', $_SERVER['DOCUMENT_ROOT'] ?? ''), '/');
$scriptDir = str_replace('\\', '/', dirname($_SERVER['SCRIPT_FILENAME'] ?? __DIR__));
$prefix = ($docRoot && strpos($scriptDir, $docRoot) === 0)
    ? '/' . trim(substr($scriptDir, strlen($docRoot)), '/')
    : '/Haqqiv1';

// Corriger REQUEST_URI pour que Laravel reçoive /api/ai/ask
if ($prefix !== '/' && stripos($uri, $prefix) === 0) {
    $path = substr($uri, strlen($prefix)) ?: '/';
    $_SERVER['REQUEST_URI'] = $path . (isset($_SERVER['QUERY_STRING']) && $_SERVER['QUERY_STRING'] ? '?' . $_SERVER['QUERY_STRING'] : '');
}

require __DIR__ . '/haqqi-ai/public/index.php';

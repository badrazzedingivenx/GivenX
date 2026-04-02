<?php
/**
 * Routeur pour le serveur PHP intégré - envoie toutes les requêtes vers Laravel
 */
$uri = urldecode(parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) ?: '/');
$path = __DIR__ . $uri;

// Fichiers statiques existants uniquement
if ($uri !== '/' && $uri !== '' && file_exists($path) && is_file($path)) {
    return false;
}

require __DIR__ . '/index.php';

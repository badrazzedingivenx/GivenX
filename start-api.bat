@echo off
echo Demarrage du serveur Laravel (API AI)...
cd /d "%~dp0haqqi-ai\public"
php -S 127.0.0.1:8000 router.php
echo.
echo API disponible: POST http://127.0.0.1:8000/api/ai/ask
pause

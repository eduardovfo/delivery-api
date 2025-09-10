@echo off
echo ========================================
echo  DESAFIO DELIVERY - AMBIENTE DE DEV
echo ========================================
echo.

echo 1. Verificando Java...
java -version
if %errorlevel% neq 0 (
    echo ERRO: Java nao encontrado!
    pause
    exit /b 1
)
echo.

echo 2. Iniciando PostgreSQL...
docker-compose up -d postgres
if %errorlevel% neq 0 (
    echo ERRO: Falha ao iniciar PostgreSQL!
    pause
    exit /b 1
)
echo.

echo 3. Aguardando PostgreSQL inicializar...
timeout /t 10 /nobreak > nul
echo.

echo 4. Iniciando aplicacao Spring Boot...
echo    - Flyway ativado: Sim
echo    - Banco: PostgreSQL
echo    - Porta: 8080
echo.
./gradlew bootRun

pause

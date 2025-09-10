@echo off
echo ========================================
echo  EXECUTANDO TESTES UNITÁRIOS
echo ========================================
echo.

echo 1. Compilando projeto...
call gradlew clean compileTestJava
if %errorlevel% neq 0 (
    echo ERRO: Falha na compilação!
    pause
    exit /b 1
)
echo.

echo 2. Executando testes unitários...
call gradlew test --info
if %errorlevel% neq 0 (
    echo ERRO: Alguns testes falharam!
    pause
    exit /b 1
)
echo.

echo 3. Gerando relatório de cobertura...
call gradlew jacocoTestReport
if %errorlevel% neq 0 (
    echo AVISO: Falha ao gerar relatório de cobertura
)
echo.

echo ========================================
echo  TESTES CONCLUÍDOS COM SUCESSO!
echo ========================================
echo.
echo Relatórios disponíveis em:
echo - build/reports/tests/test/index.html
echo - build/reports/jacoco/test/html/index.html
echo.

pause

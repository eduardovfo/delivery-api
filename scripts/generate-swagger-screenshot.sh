#!/bin/bash

# Script para gerar screenshot do Swagger UI
# Requer: docker, node, playwright

echo "🚀 Iniciando aplicação para gerar screenshot do Swagger UI..."

# Inicia a aplicação em background
./gradlew bootRun --args='--spring.profiles.active=openapi' &
APP_PID=$!

# Aguarda a aplicação iniciar
echo "⏳ Aguardando aplicação iniciar..."
sleep 30

# Verifica se a aplicação está rodando
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Aplicação iniciada com sucesso!"
    
    # Cria diretório para imagens
    mkdir -p docs/images
    
    # Gera screenshot usando Playwright (se disponível)
    if command -v npx > /dev/null 2>&1; then
        echo "📸 Gerando screenshot do Swagger UI..."
        npx playwright install chromium
        npx playwright run --headed=false --browser=chromium << 'EOF'
const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  
  // Acessa o Swagger UI
  await page.goto('http://localhost:8080/swagger-ui.html');
  
  // Aguarda carregar completamente
  await page.waitForLoadState('networkidle');
  
  // Aguarda o conteúdo do Swagger carregar
  await page.waitForSelector('.swagger-ui', { timeout: 10000 });
  
  // Tira screenshot
  await page.screenshot({ 
    path: 'docs/images/swagger-ui.png',
    fullPage: true,
    type: 'png'
  });
  
  await browser.close();
  console.log('Screenshot salvo em docs/images/swagger-ui.png');
})();
EOF
    else
        echo "⚠️ Playwright não encontrado. Criando screenshot manual..."
        # Cria um arquivo placeholder
        echo "Screenshot do Swagger UI disponível em: http://localhost:8080/swagger-ui.html" > docs/images/swagger-ui.txt
    fi
    
    echo "📄 Gerando OpenAPI JSON..."
    curl -o build/generated/openapi/openapi.json http://localhost:8080/v3/api-docs
    
else
    echo "❌ Falha ao iniciar aplicação"
    exit 1
fi

# Para a aplicação
echo "🛑 Parando aplicação..."
kill $APP_PID

echo "✅ Processo concluído!"
echo "📁 Arquivos gerados:"
echo "   - docs/images/swagger-ui.png (screenshot)"
echo "   - build/generated/openapi/openapi.json (OpenAPI JSON)"

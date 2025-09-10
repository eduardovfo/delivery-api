# Correção da Versão do Java - Passo a Passo

## Problema Identificado

O projeto **desafio-delivery** requer **Java 17+**, mas o sistema possui **Java 8** instalado.

### Evidências:
- **Java atual**: Java 1.8.0_261 (verificado com `java -version`)
- **Projeto requer**: Java 17+ (definido em `build.gradle.kts`)
- **Spring Boot 3**: Requer Java 17 como versão mínima

## Soluções Propostas

### Opção 1: Instalar Java 17 (Recomendado)

#### Windows com Chocolatey
```powershell
# Instalar Chocolatey (se não tiver)
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))

# Instalar OpenJDK 17
choco install openjdk17

# Ou instalar Oracle JDK 17
choco install oraclejdk17
```

#### Windows Manual
1. Baixar OpenJDK 17: https://adoptium.net/temurin/releases/
2. Instalar seguindo o wizard
3. Configurar JAVA_HOME:
   ```powershell
   # Definir JAVA_HOME
   [Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot", "Machine")
   
   # Atualizar PATH
   $path = [Environment]::GetEnvironmentVariable("PATH", "Machine")
   $newPath = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot\bin;$path"
   [Environment]::SetEnvironmentVariable("PATH", $newPath, "Machine")
   ```

#### Verificar Instalação
```powershell
# Reiniciar PowerShell e verificar
java -version
javac -version
echo $env:JAVA_HOME
```

### Opção 2: Usar SDKMAN (Windows com WSL)

```bash
# Instalar SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Instalar Java 17
sdk install java 17.0.9-tem
sdk use java 17.0.9-tem
sdk default java 17.0.9-tem
```

### Opção 3: Usar Docker para Desenvolvimento

Se não quiser instalar Java 17 localmente:

```yaml
# docker-compose.override.yml
version: '3.8'
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile.dev
    ports:
      - "8080:8080"
    volumes:
      - .:/workspace
    environment:
      - SPRING_PROFILES_ACTIVE=dev
    depends_on:
      - postgres
      - redis
      - keycloak
```

```dockerfile
# Dockerfile.dev
FROM openjdk:17-jdk-slim

WORKDIR /workspace

# Instalar Gradle
RUN apt-get update && apt-get install -y wget unzip
RUN wget https://gradle.org/releases/download/gradle-8.5/gradle-8.5-bin.zip
RUN unzip gradle-8.5-bin.zip && mv gradle-8.5 /opt/gradle
ENV PATH="/opt/gradle/bin:${PATH}"

COPY . .

CMD ["./gradlew", "bootRun"]
```

## Próximos Passos

Após instalar Java 17:

1. **Verificar instalação**:
   ```powershell
   java -version  # Deve mostrar Java 17
   ```

2. **Testar build do projeto**:
   ```powershell
   ./gradlew clean build
   ```

3. **Iniciar serviços Docker**:
   ```powershell
   docker-compose up -d
   ```

4. **Executar aplicação**:
   ```powershell
   ./gradlew bootRun
   ```

5. **Verificar funcionamento**:
   ```powershell
   curl http://localhost:8080/actuator/health
   ```

## Arquivos Afetados

- `build.gradle.kts`: Define Java 17 como versão mínima
- `src/main/resources/application.yml`: Configurações da aplicação
- `docker-compose.yml`: Serviços dependentes (PostgreSQL, Redis, Keycloak)

## Observações Importantes

- **Não altere** a versão do Java no `build.gradle.kts` para Java 8
- **Spring Boot 3** não é compatível com Java 8
- **Mantenha** Java 17+ para garantir compatibilidade com todas as dependências
- **Use** o Gradle Wrapper (`./gradlew`) que já está configurado no projeto

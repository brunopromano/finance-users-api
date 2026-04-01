# Finance Users API

API REST para gerenciamento de usuários (CRUD) usando Spring Boot, MySQL e OpenAPI/Swagger.

## Requisitos

- Java 21 (JDK)
- Docker e Docker Compose (v2)
- Maven (ou use o wrapper `./mvnw` / `mvnw.cmd`)

> Observação: a imagem Docker usa Java 21, então você só precisa do JDK 21 para executar localmente sem Docker.

## Executar com Docker (recomendado)

1. Subir os serviços (aplicação + MySQL):

```bash
docker compose up --build
```

2. Ver logs da aplicação:

```bash
docker compose logs -f app
```

3. Parar e remover containers/volumes:

```bash
docker compose down -v
```

Portas padrão (host → container):

- Aplicação: `8093` → `8080` (acesso HTTP)
- MySQL: `3307` → `3306`

A aplicação estará em: `http://localhost:8093`

## Executar sem Docker

Você pode executar a aplicação localmente apontando para um banco MySQL (pode usar o container apenas para o DB):

1. (Opcional) Subir apenas o MySQL via Docker:

```bash
docker run --name users-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=finance_users -e MYSQL_USER=finance -e MYSQL_PASSWORD=finance -p 3307:3306 -d mysql:8.0
```

2. Ajuste as variáveis de ambiente (exemplo para MySQL exposto em `localhost:3307`):

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3307/finance_users?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"
export SPRING_DATASOURCE_USERNAME=finance
export SPRING_DATASOURCE_PASSWORD=finance
```

No Windows PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:3307/finance_users?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true'
$env:SPRING_DATASOURCE_USERNAME = 'finance'
$env:SPRING_DATASOURCE_PASSWORD = 'finance'
```

3. Executar com Maven (wrapper):

```bash
# Unix / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Ou empacotar e executar o JAR (requer JDK 21):

```bash
./mvnw package -DskipTests
java -jar target/*.jar
```

Também é possível passar as propriedades diretamente:

```bash
java -Dspring.datasource.url="jdbc:mysql://localhost:3307/finance_users?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true" \
     -Dspring.datasource.username=finance \
     -Dspring.datasource.password=finance \
     -jar target/*.jar
```

## Swagger / OpenAPI

- Swagger UI: `http://localhost:8093/swagger-ui/index.html` (o endpoint `/swagger-ui.html` redireciona para `/swagger-ui/index.html`).
- OpenAPI JSON: `http://localhost:8093/api-docs`

Exemplo de consulta do JSON do OpenAPI:

```bash
curl http://localhost:8093/api-docs | jq '.'
```

(Se não tiver `jq`, remova o pipe `| jq`.)

## Testes rápidos (smoke)

```bash
# Criar um usuário
curl -s -X POST http://localhost:8093/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name":"João Silva","email":"joao@finance.com","password":"senha123"}' | jq

# Listar
curl -s http://localhost:8093/api/v1/users | jq

# Acessar OpenAPI JSON
curl -s http://localhost:8093/api-docs | jq
```

## Observações

- A configuração do `springdoc` foi ajustada para expor os docs em `/api-docs` e a UI em `/swagger-ui/index.html`.
- No `docker compose` o serviço `app` aponta internamente para `mysql:3306`. O mapeamento de portas (`3307:3306`) é apenas para acesso ao banco a partir do host.

---

Se quiser, eu posso:
- adicionar um `LICENSE` e um `.gitignore`,
- criar o repositório no GitHub e fazer o push (preciso que confirme nome e visibilidade ou que `gh` esteja autenticado aqui).

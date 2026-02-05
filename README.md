# 📦 Artistas API
Projeto desenvolvido com Spring Boot para manter dados de artistas e seus álbuns

---

## 👤 Informações de incrição

- **Nome:** Caio Oliveira Braz
- **Fone/Cel:** (65) 99922-5041
- **GitHub:** [@caiobrazpl](https://github.com/caiobrazpl)
- **Vaga:** Backend

---

## ⚙️ Tecnologias

- Java 21
- Spring Boot 3.5.0
- Spring Web
- Spring Data JPA
- Spring Security + OAuth2 Resource Server
- JSON Web Token (JJWT)
- PostgreSQL
- Lombok
- MinIO (armazenamento de arquivos)
- SpringDoc OpenAPI (Swagger UI)
- Flyway (migração de banco de dados)

---

## 🚀 Como executar o projeto

### ✅ Pré-requisitos

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

*Para rodar testes localmente (opcional):* [Java 21](https://adoptium.net/) e Maven (ou use o wrapper `mvnw` / `mvnw.cmd` incluso no projeto).

---

### 🔧 Passo a passo: executar a aplicação (Docker)

Este projeto inclui um `docker-compose.yml` que sobe:

- **app** – aplicação Spring Boot (porta 8080)
- **db** – PostgreSQL (porta 5432)
- **minio** – armazenamento de arquivos (portas 9000 e 9001)

#### 1. Clone e entre na pasta do projeto

```bash
git clone https://github.com/caiobrazpl/artista-api.git
cd artista-api
```

#### 2. Suba todos os serviços

```bash
docker-compose up --build
```

- Na **primeira execução**, a imagem da aplicação será construída e o PostgreSQL criará automaticamente os bancos `artista_db` (app) e `artista_test` (testes).
- Aguarde até aparecer no log algo como *"Started ArtistaApiApplication"* — a API estará pronta.

#### 3. Acesse os serviços

| Serviço        | URL |
|----------------|-----|
| **API**        | http://localhost:8080 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **MinIO API**  | http://localhost:9000 |
| **MinIO Console** | http://localhost:9001 (usuário: `minioadmin` / senha: `minioadmin`) |
| **PostgreSQL** | `localhost:5432` — usuário: `meuusuario`, senha: `minhasenha`, bancos: `artista_db`, `artista_test` |

---

### 🧪 Passo a passo: testar a API após subir

1. **Health check**  
   - GET http://localhost:8080/actuator/health  
   - Deve retornar status saudável (ex.: `{"status":"UP"}` ou detalhes de `readinessState`, `db`, etc.).

2. **Login (obter token)**  
   - No Swagger: http://localhost:8080/swagger-ui.html  
   - Use o endpoint **POST /api/v1/auth/login** com body:
   ```json
   { "login": "admin", "senha": "admin123" }
   ```
   - Copie o `accessToken` da resposta.

3. **Chamar endpoints protegidos**  
   - No Swagger, clique em **Authorize** e informe: `Bearer <seu-token>` (ou digite o login e senha no pop-up Js).  
   - Depois teste, por exemplo: **GET /api/v1/artistas** ou **GET /api/v1/albums**.

---


## 📑 Documentação da API

Acesse a documentação interativa via Swagger:

```
http://localhost:8080/swagger-ui.html
```

---

## 📮 Criação de tabelas

- Todas as tabelas são criadas via migração (Flyway)
- Carga inicial de dados via migração (Flyway)

---

## 📮 Usuário para teste

Usuário inicial criado via migração para testes: 
- **usuário:** admin
- **senha:** admin123

---

## 🧪 Testes

O projeto inclui **testes unitários** e **testes de integração**. Use o Maven wrapper do projeto: no Linux/macOS use `./mvnw`; no Windows use `mvnw.cmd` (ou `.\mvnw.cmd` no PowerShell).

### Executar todos os testes

Requer **PostgreSQL** rodando com o banco `artista_test` (veja passo a passo abaixo).

```bash
./mvnw test
```

*No Windows (PowerShell):*
```powershell
.\mvnw.cmd test
```

### Apenas testes unitários (sem integração)

Não precisa de banco de dados. Útil para feedback rápido.

```bash
./mvnw test -Dtest="*ServiceTest"
```

*No Windows:* `.\mvnw.cmd test -Dtest="*ServiceTest"`

### Passo a passo: testes de integração

Os testes de integração (`*IntegrationTest`) sobem o contexto Spring e usam o banco **artista_test** em `localhost:5432` (configurado em `src/test/resources/application-test.properties`).

1. **Subir só o PostgreSQL** (e, na primeira vez, criar os bancos):
   ```bash
   docker-compose up -d db
   ```
   Na **primeira subida** (volume vazio), o script `docker/postgres/init-databases.sql` cria `artista_db` e `artista_test`. Se o volume já existia e algum banco faltar, crie manualmente:
   ```bash
   docker exec -it postgres_db psql -U meuusuario -d postgres -c "CREATE DATABASE artista_db;" -c "CREATE DATABASE artista_test;"
   ```

2. **Rodar os testes** (com perfil `test`; o Flyway aplica as migrações em `artista_test` ao subir o contexto):
   ```bash
   ./mvnw test
   ```
   Os testes usam o usuário `admin` / `admin123` (conforme migração).

---

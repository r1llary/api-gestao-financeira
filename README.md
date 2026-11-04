# api-gestao-financeira
API REST para gestão financeira de múltiplos usuários, desenvolvida com Java, Spring Boot, PostgreSQL e Docker, permitindo gerenciar usuários,
transações financeiras, saldos, conversão de moedas e análise de despesas.

## MS1 - Serviço de usuários

Responsável pelo cadastro, consulta, atualização e exclusão de usuários, além da importação de usuários por arquivo CSV.

### Tecnologias

- Java 25
- Spring Boot
- Spring Data JPA
- Spring Security com autenticação HTTP Basic
- Bean Validation
- PostgreSQL 17
- JUnit e Mockito
- Springdoc OpenAPI e Swagger UI

### Arquitetura

O MS1 utiliza arquitetura hexagonal:
- core.usuario.model: modelo de domínio.
- core.usuario.port.input: contratos dos casos de uso.
- core.usuario.port.output: contratos de acesso à persistência.
- core.usuario.service: regras de negócio.
- adapter.input.rest: endpoints REST, DTOSs e tratamento de erros.
- adapter.input.csv: leitura e importação de usuários.
- adapter.input.security: consulta de usuários para autenticação.
- adapter.output.persistence: integração com o banco usando JPA.
- config: configuração e ligação dos componentes.

### Endpoints

| Método | Rota | Descrição | Autenticação |
| ------ | ---- | --------- | ------------
| POST | /users | Cadastrar Usuário | Pública |
| GET | /users|Listar Usuário | Basic |
| GET | /users/{id}| Buscar usuário por ID | Basic |
| PUT | /users/{id}|Atualizar usuário | Basic |
| DELETE| /users/{id} |Excluir usuário| Basic |
| POST | /users/import | Importar arquivo CSV | Basic |

### Autenticação

As operações protegidas utilizam o e-mail cadastrado com usuário e a senha original do cadastro.
As senhas são armazenadas como hash BCrypt e não são incluídas nas respostas de usuários.

### Swagger

Com a aplicação em execução, acesse:
http://localhost:8080/swagger-ui.html

Para testar operações protegidas, clique em Authorize e informe o e-mail e a senha de um usuário cadastrado.

### Execução

Requisitos: Java 25 e Docker Compose iniciado.

Na pasta principal, copie .env.example para .env
e preencha as variáveis.

### CSV

Envie um arquivo UTF-8 para POST /users/import,
usando autenticação Basic e multipart/form-data.
no campo file.

Cabeçalho: name,email,password.
Exemplos: ms-usuarios/examples

## MS2 - Serviço de transações

Em desenvolvimento. Até o momento, foram validadas a compilação e a inicialização da aplicação com conexão ao PostgreSQL.

### Execução local

O banco é executado no Docker e a aplicação pelo Intellij.

Na raiz do projeto, com as variáveis TRANSACTIONS_DB_NAME, TRANSACTIONS_DB_USER e TRANSACTIONS_DB_PASSWORD preenchidas no .env, execute:
docker compose up -d transactions-db

Na configuração de execução de MsTransacoesApplication no intelliJ, defina:

| Variável | Valor |
| -------- | ----- |
| DB_URL | jdbc:postgresql://localhost:5434/<nome-do-banco> |
| DB_USERNAME | Valor de TRANSACTIONS_DB_USER do .env |
| DB_PASSWORD | Valor de TRANSACTIONS_DB_PASSWORD do .env |

Substitua <nome-do-banco> pelo valor de TRANSACTIONS_DB_NAME

Execute MsTransacoesApplication. O serviço utiliza a porta 8081

O compose lê o .env, mas a execução pelo IntelliJ precisa receber as variáveis separadamente. Não versionar credenciais.-


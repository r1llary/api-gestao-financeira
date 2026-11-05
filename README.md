# api-gestao-financeira
API REST para gestão financeira de múltiplos usuários, desenvolvida com Java, Spring Boot, PostgreSQL e Docker, permitindo gerenciar usuários,
transações financeiras, saldos, conversão de moedas e análise de despesas.

## Executar com Docker Compose

Na raiz do projeto, copie o `.env.example` para `.env` e preencha as variáveis dos dois bancos. Mantenha o `.env` fora do Git.

Com o Docker em execução, rode na raiz do projeto:

```bash
docker compose up --build -d
```

O comando gera os JARs durante a construção das imagens e sobe os dois microsserviços e seus bancos PostgreSQL.

| Microsserviço | Endereço local |
| --- | --- |
| MS1 — Usuários | `http://localhost:8080` |
| MS2 — Transações | `http://localhost:8081` |

Para conferir se os quatro serviços estão em execução, use `docker compose ps`. Para parar o ambiente preservando os dados dos bancos, use `docker compose down`.

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

### Executar o MS2 pelo IntelliJ (opcional)

Neste modo, o banco roda no Docker e a aplicação roda pelo IntelliJ. Se o ambiente completo estiver ativo, execute `docker compose stop ms-transacoes` para liberar a porta `8081`.

Com `TRANSACTIONS_DB_NAME`, `TRANSACTIONS_DB_USER` e `TRANSACTIONS_DB_PASSWORD` preenchidas no `.env`, rode na raiz do projeto:

```bash
docker compose up -d transactions-db
```

Na configuração de execução de `MsTransacoesApplication` no IntelliJ, preencha as variáveis de ambiente abaixo.

| Variável | Valor |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5434/<nome-do-banco>` |
| `DB_USERNAME` | Valor de `TRANSACTIONS_DB_USER` no `.env` |
| `DB_PASSWORD` | Valor de `TRANSACTIONS_DB_PASSWORD` no `.env` |

Substitua `<nome-do-banco>` pelo valor de `TRANSACTIONS_DB_NAME` e execute `MsTransacoesApplication`. O serviço usa a porta `8081`.

O Compose lê o `.env`. Para rodar pelo IntelliJ, forneça as variáveis na configuração de execução.

### Autenticação

O MS2 utiliza HTTP Basic e valida o e-mail e a senha no MS1.

As requisições protegidas devem enviar as credenciais de um usuário
cadastrado no MS1.

A rota GET /auth/me retorna o ID e o e-mail do usuário autenticado.
O campo name, quando presente, corresponde ao e-mail usado no login.

- Credenciais válidas retornam 200.
- Credenciais ausentes ou inválidas retornam 401.
- Falha na comunicação com o MS1 retorna 503.

A URL do MS1 é configurada pela variável USERS_SERVICE_URL.
No Docker Compose, seu valor é http://ms-usuarios:8080.
Para execução local pelo IntelliJ, o endereço padrão é
http://localhost:8080.

O acesso às transações de cada usuário será validado junto
à implementação das rotas financeiras.

### Depósito

POST http://localhost:8081/transactions

Exige HTTP Basic com e-mail e senha de um usuário cadastrado no MS1.
O dono do depósito é identificado pela autenticação.

Exemplo de corpo da requisição:

```json
{
  "amount": 100.00,
  "description": "Depósito inicial",
  "category": "Deposit"
}
```

Resposta de sucesso: HTTP 201 Created.

```json
{
  "id": 1,
  "status": "APPROVED"
}
```

O ID é gerado pelo banco e pode variar.

- O valor é obrigatório e deve ser maior que zero.
- Descrição e categoria são obrigatórias e não podem conter apenas espaços.
- Dados inválidos retornam 400 com uma lista dos campos e suas mensagens de validação.
- Credenciais ausentes ou inválidas retornam 401.
- Indisponibilidade do serviço de autenticação retorna 503.

Testes unitários cobrem a criação do depósito aprovado no serviço,
o uso do ID do usuário autenticado no controller e a resposta 201.



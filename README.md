# API de Gestão Financeira

Projeto desenvolvido no Desafio Beca Java 2026, organizado em dois microsserviços com arquitetura hexagonal.

- **MS1 — Usuários:** cadastro, consulta, atualização, exclusão e importação de usuários por CSV.
- **MS2 — Transações:** em desenvolvimento. Projeto criado, compilado e iniciado localmente com conexão ao PostgreSQL.

## Tecnologias

- Java 25
- Spring Boot 4.0.9
- Maven e Maven Wrapper
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL 17
- Spring Security com autenticação básica no MS1
- JUnit e Mockito
- Swagger / OpenAPI no MS1
- Docker e Docker Compose

## Estrutura do projeto

| Caminho | Responsabilidade |
| --- | --- |
| `ms-usuarios/` | Código, configuração, testes e Dockerfile do MS1 |
| `ms-usuarios/examples/` | Arquivos CSV de exemplo |
| `ms-transacoes/` | Código, configuração e testes do MS2 |
| `docker-compose.yml` | Configuração dos serviços e bancos em containers |
| `.env` | Variáveis locais, sem versionamento |
| `.env.example` | Modelo de variáveis, sem segredos reais |
| `README.md` | Documentação do projeto |

Cada microsserviço possui seu próprio `pom.xml` e sua classe principal.

## Arquitetura hexagonal

Os pacotes base são:

- MS1: `com.beca.usuarios`
- MS2: `com.beca.transacoes`

| Pacote | Responsabilidade |
| --- | --- |
| `core.usuario` / `core.transacao` | Domínio, regras de negócio e portas |
| `adapter` | Comunicação HTTP, persistência e integrações externas |
| `config` | Configuração e ligação dos componentes no Spring |

O domínio deve permanecer independente de Spring, JPA e HTTP.

As classes `MsUsuariosApplication` e `MsTransacoesApplication` ficam nos respectivos pacotes base.

Os pacotes iniciais do MS2 serão preenchidos conforme as funcionalidades forem implementadas. O Git não registra pastas vazias.

## Pré-requisitos

- JDK 25 configurado.
- Docker com suporte a containers Linux e Docker Compose.
- IntelliJ IDEA ou outra IDE compatível com Java e Maven.
- Postman ou navegador para acessar o Swagger do MS1.

Os comandos desta documentação utilizam PowerShell no Windows e devem ser executados na raiz `api-gestao-financeira`, salvo indicação diferente.

O Maven Wrapper está incluído em cada serviço.

## Variáveis de ambiente

Se ainda não existir um `.env`, copie o arquivo de exemplo:

```powershell
Copy-Item .env.example .env
```

Não substitua um `.env` já preenchido.

Configure as seguintes variáveis:

| Variável | Finalidade |
| --- | --- |
| `USERS_DB_NAME` | Nome do banco do MS1 |
| `USERS_DB_USER` | Usuário do banco do MS1 |
| `USERS_DB_PASSWORD` | Senha do banco do MS1 |
| `TRANSACTIONS_DB_NAME` | Nome do banco do MS2 |
| `TRANSACTIONS_DB_USER` | Usuário do banco do MS2 |
| `TRANSACTIONS_DB_PASSWORD` | Senha do banco do MS2 |

O `.env` deve permanecer no `.gitignore`. O `.env.example` deve documentar as variáveis usando valores fictícios.

Não versionar senhas, tokens ou chaves.

O Docker Compose lê o `.env` da raiz. Aplicações iniciadas pelo IntelliJ precisam receber suas variáveis separadamente na configuração de execução.

## Bancos de dados

O Compose utiliza dois containers PostgreSQL, com volumes separados:

| Serviço | Acesso pelo notebook | Endereço dentro do Compose | Volume |
| --- | --- | --- | --- |
| `users-db` | `localhost:5433` | `users-db:5432` | `users_data` |
| `transactions-db` | `localhost:5434` | `transactions-db:5432` | `transactions_data` |

Cada microsserviço utiliza seu próprio banco. O MS2 não deve acessar diretamente as tabelas do MS1.

### Iniciar os bancos

```powershell
docker compose up -d users-db transactions-db
```

### Conferir os containers

```powershell
docker compose ps
```

Os volumes mantêm os dados após reiniciar os containers.

**Atenção:** `docker compose down -v` remove os volumes e seus dados. Esse comando não é necessário para reiniciar o ambiente.

As variáveis de inicialização do PostgreSQL são aplicadas quando o diretório de dados é criado. Alterar o `.env` não modifica automaticamente usuários, senhas ou bancos já existentes no volume.

## MS1 — Serviço de usuários

### Funcionalidades

- Cadastrar usuários.
- Listar usuários.
- Consultar usuário pelo identificador.
- Atualizar usuários.
- Excluir usuários.
- Validar dados com Bean Validation.
- Tratar erros com respostas padronizadas.
- Rejeitar e-mails duplicados.
- Importar usuários por CSV.
- Autenticação básica com Spring Security.
- Documentação dos endpoints no Swagger.
- Testes unitários com JUnit e Mockito.

### Executar com Docker Compose

Com o `.env` preenchido, gere o pacote do MS1:

```powershell
.\ms-usuarios\mvnw.cmd -f ms-usuarios/pom.xml clean package -DskipTests
```

A opção `-DskipTests` empacota a aplicação sem executar os testes. Os testes devem ser executados separadamente.

Depois, construa a imagem e inicie o serviço:

```powershell
docker compose up -d --build ms-usuarios
```

O Compose também inicia o serviço `users-db`, do qual o MS1 depende.

### Endpoints

Endereço base:

```text
http://localhost:8080
```

| Método | Rota | Finalidade |
| --- | --- | --- |
| POST | `/users` | Cadastrar usuário |
| GET | `/users` | Listar usuários |
| GET | `/users/{id}` | Consultar usuário |
| PUT | `/users/{id}` | Atualizar usuário |
| DELETE | `/users/{id}` | Excluir usuário |
| POST | `/users/import` | Importar usuários por CSV |

Nas rotas protegidas, utilizar Basic Auth com as credenciais configuradas na aplicação.

### Swagger

Com o MS1 em execução, acessar:

```text
http://localhost:8080/swagger-ui.html
```

### Importação de usuários por CSV

O arquivo deve utilizar codificação UTF-8, separador por vírgulas e este cabeçalho:

```csv
name,email,password
```

Os arquivos de exemplo ficam em:

```text
ms-usuarios/examples/
```

Para importar pelo Swagger:

1. Abrir `POST /users/import`.
2. Selecionar o arquivo no campo `file`.
3. Executar a requisição.
4. Conferir o resultado da importação.
5. Consultar `GET /users` para verificar os usuários cadastrados.

No Postman, utilizar `Body > form-data`, com uma chave `file` do tipo arquivo.

E-mails já cadastrados devem ser rejeitados, sem gerar usuários duplicados.

### Testes unitários

Para executar a classe `UserServiceTest`:

```powershell
.\ms-usuarios\mvnw.cmd -f ms-usuarios/pom.xml -Dtest=UserServiceTest test
```

Para executar a suíte completa:

```powershell
.\ms-usuarios\mvnw.cmd -f ms-usuarios/pom.xml test
```

Testes que carregam o contexto Spring podem exigir banco disponível e as variáveis `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` no ambiente do processo Maven.

Para o MS1 executado no notebook, a URL de conexão utiliza `localhost:5433` e o nome definido em `USERS_DB_NAME`.

As variáveis configuradas na execução pelo IntelliJ não são automaticamente utilizadas pelos comandos Maven no terminal.

A aprovação dos testes de `UserServiceTest` não comprova a aprovação da suíte completa. O relatório de cobertura será consolidado na etapa de qualidade.

## MS2 — Serviço de transações

### Estado atual

- Projeto Maven criado com Java 25 e Spring Boot 4.0.9.
- Dependências Web, JPA, Validation e PostgreSQL adicionadas.
- Classe principal `MsTransacoesApplication` criada.
- Pacotes iniciais para arquitetura hexagonal organizados.
- Compilação executada com sucesso.
- PostgreSQL do MS2 executando no Docker.
- Aplicação iniciada localmente na porta 8081.
- Inicialização do pool de conexões com o PostgreSQL confirmada.

Os endpoints financeiros, a autenticação do MS2, as integrações externas, os relatórios e o Swagger do MS2 ainda serão implementados.

A execução atual do MS2 é feita pelo IntelliJ, com o banco no Docker. O container da aplicação MS2 ainda será integrado ao Compose.

### Iniciar o banco do MS2

```powershell
docker compose up -d transactions-db
```

Conferir o estado:

```powershell
docker compose ps transactions-db
```

O container deve aparecer em execução, com a porta publicada:

```text
127.0.0.1:5434->5432
```

### Configuração da aplicação

Arquivo:

```text
ms-transacoes/src/main/resources/application.properties
```

Configuração:

```properties
spring.application.name=ms-transacoes
server.port=8081

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

### Executar pelo IntelliJ

1. Abrir `Run > Edit Configurations`.
2. Selecionar `MsTransacoesApplication`.
3. Configurar as variáveis de ambiente:

| Variável | Valor |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5434/<nome-do-banco>` |
| `DB_USERNAME` | Valor de `TRANSACTIONS_DB_USER` do `.env` |
| `DB_PASSWORD` | Valor de `TRANSACTIONS_DB_PASSWORD` do `.env` |

Substituir `<nome-do-banco>` pelo valor de `TRANSACTIONS_DB_NAME`.

Preencher os valores reais na configuração local, sem copiá-los para o código ou para o README.

4. Salvar a configuração.
5. Executar `MsTransacoesApplication`.

Na execução validada, foram identificadas as mensagens:

```text
HikariPool-1 - Start completed.
Started MsTransacoesApplication
```

### Compilar o MS2

```powershell
.\ms-transacoes\mvnw.cmd -f ms-transacoes/pom.xml compile
```

Esse comando compila o código principal. Ele não inicia a aplicação nem executa os testes.

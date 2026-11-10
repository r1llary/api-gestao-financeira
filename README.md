# API de Gestão Financeira

API REST para gestão financeira de múltiplos usuários, desenvolvida com Java 25, Spring Boot, PostgreSQL e Docker Compose.

O projeto utiliza arquitetura hexagonal e é composto por dois microsserviços:

- **MS1 — Usuários:** gerenciamento de usuários e autenticação.
- **MS2 — Transações:** operações financeiras, saldos, limites, conversão de moedas, análise de despesas e exportação de relatórios.

## 1. Tecnologias utilizadas

- Java 25
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- PostgreSQL 17
- Docker e Docker Compose
- Maven
- JUnit 5
- Mockito
- H2 para testes de integração
- Swagger / OpenAPI
- MockAPI
- BrasilAPI
- Git e GitHub

## 2. Arquitetura do projeto

Os microsserviços seguem a arquitetura hexagonal (Ports and Adapters), separando as regras de negócio das tecnologias externas.

Estrutura principal:

```text
api-gestao-financeira/
├── ms-usuarios/
├── ms-transacoes/
├── docker-compose.yml
├── .env.example
└── README.md
```

### Organização das camadas

| Camada | Responsabilidade |
| --- | --- |
| core/model | Modelos e entidades de domínio |
| core/port/input | Interfaces dos casos de uso |
| core/port/output | Interfaces de persistência e integrações |
| core/service | Implementação das regras de negócio |
| adapter/input/rest | Controllers e endpoints REST |
| adapter/input/security | Autenticação e identificação do usuário |
| adapter/output/persistence | Persistência utilizando JPA |
| config | Configurações e injeção de dependências |

### Responsabilidades dos microsserviços

**MS1 — Usuários**

Responsável pelo CRUD de usuários, importação de arquivos CSV, validação de dados e autenticação.

**MS2 — Transações**

Responsável pelo registro e consulta de transações financeiras, operações de depósito, saque, transferência e compra, consulta de saldo, aplicação de limites, conversão de moedas e análise de despesas.

O MS2 utiliza PostgreSQL para persistir transações, MockAPI para dados de contas e saldos e BrasilAPI para conversões monetárias.

## 3. Configuração do ambiente

### Pré-requisitos

Para executar o projeto, é necessário possuir:

- Docker com Docker Compose
- Java 25
- Maven
- Git
- IntelliJ IDEA ou outra IDE Java
- Postman ou Swagger para testar os endpoints

### Variáveis de ambiente

Na raiz do projeto, copie o arquivo `.env.example` para `.env`.

No PowerShell:

```powershell
Copy-Item .env.example .env
```

No Linux:

```bash
cp .env.example .env
```

Preencha as variáveis conforme a configuração dos microsserviços.

Entre as variáveis utilizadas pelo MS2 estão:

| Variável | Descrição |
| --- | --- |
| TRANSACTIONS_DB_NAME | Nome do banco do MS2 |
| TRANSACTIONS_DB_USER | Usuário do banco do MS2 |
| TRANSACTIONS_DB_PASSWORD | Senha do banco do MS2 |
| DB_URL | URL de conexão JDBC |
| DB_USERNAME | Usuário de conexão JDBC |
| DB_PASSWORD | Senha de conexão JDBC |
| USERS_SERVICE_URL | Endereço do serviço MS1 |
| EXCHANGE_SERVICE_URL | Endereço da BrasilAPI |

As configurações do banco do MS1 e da MockAPI devem seguir os nomes definidos no `.env.example`, no Docker Compose e nos arquivos de configuração do projeto.

**Importante:** o arquivo `.env` contém informações sensíveis e não deve ser enviado ao GitHub. Mantenha-o no `.gitignore`.

## 4. Executar com Docker Compose

Na raiz do projeto, execute:

```bash
docker compose up --build -d
```

O comando constrói as imagens, gera os arquivos JAR e inicia os microsserviços e os bancos PostgreSQL configurados.

### Endereços dos microsserviços

| Serviço | Endereço |
| --- | --- |
| MS1 — Usuários | http://localhost:8080 |
| MS2 — Transações | http://localhost:8081 |
| Swagger MS1 | http://localhost:8080/swagger-ui.html |
| Swagger MS2 | http://localhost:8081/swagger-ui/index.html |

### Comandos úteis

Verificar containers:

```bash
docker compose ps
```

Visualizar logs:

```bash
docker compose logs -f
```

Parar o ambiente preservando os volumes:

```bash
docker compose down
```

**Atenção:** não utilize `docker compose down -v` se quiser preservar os dados armazenados nos volumes PostgreSQL.

## 5. Executar o MS2 pelo IntelliJ

Também é possível executar o MS2 diretamente pelo IntelliJ, mantendo o PostgreSQL no Docker.

Se o MS2 estiver sendo executado pelo Docker Compose, primeiro libere a porta 8081:

```bash
docker compose stop ms-transacoes
```

Para iniciar apenas o banco de transações:

```bash
docker compose up -d transactions-db
```

No IntelliJ, abra as configurações de execução da classe `MsTransacoesApplication`.

Configure as variáveis:

```text
DB_URL=jdbc:postgresql://localhost:5434/<nome-do-banco>
DB_USERNAME=<usuario-do-banco>
DB_PASSWORD=<senha-do-banco>
USERS_SERVICE_URL=http://localhost:8080
EXCHANGE_SERVICE_URL=https://brasilapi.com.br
```

Substitua os valores de exemplo pelas configurações reais do ambiente.

Configure também a integração com a MockAPI de acordo com as propriedades existentes no projeto.

Execute a aplicação.

O MS2 ficará disponível em:

```text
http://localhost:8081
```

Swagger:

```text
http://localhost:8081/swagger-ui/index.html
```

Quando executado pelo Docker Compose, o MS2 utiliza o endereço interno:

```text
http://ms-usuarios:8080
```

Quando executado localmente pelo IntelliJ, utiliza:

```text
http://localhost:8080
```

## 6. MS1 — Serviço de usuários

O MS1 é responsável pelo gerenciamento de usuários.

### Funcionalidades

- Cadastro de usuários.
- Listagem de usuários.
- Consulta de usuário por ID.
- Atualização de usuários.
- Exclusão de usuários.
- Importação de usuários por CSV.
- Validação de dados com Bean Validation.
- Autenticação HTTP Basic.
- Armazenamento seguro de senhas com BCrypt.
- Tratamento de erros HTTP.
- Documentação Swagger.
- Testes automatizados.

### Endpoints do MS1

| Método | Endpoint | Descrição | Autenticação |
| --- | --- | --- | --- |
| POST | /users | Cadastrar usuário | Pública |
| GET | /users | Listar usuários | Basic |
| GET | /users/{id} | Consultar usuário | Basic |
| PUT | /users/{id} | Atualizar usuário | Basic |
| DELETE | /users/{id} | Excluir usuário | Basic |
| POST | /users/import | Importar usuários CSV | Basic |

### Importação CSV

O MS1 permite importar usuários utilizando um arquivo CSV.

Endpoint:

```http
POST /users/import
```

Utilize autenticação HTTP Basic e `multipart/form-data`, enviando o arquivo no campo `file`.

Formato esperado:

```csv
name,email,password
```

Exemplo:

```csv
Usuario Teste,teste@example.com,senha-de-teste
```

O arquivo deve utilizar codificação UTF-8.

Os exemplos de importação estão na pasta:

```text
ms-usuarios/examples
```

### Autenticação do MS1

As operações protegidas utilizam autenticação HTTP Basic.

O usuário informa:

- Username: e-mail cadastrado.
- Password: senha original do cadastro.

As senhas são armazenadas utilizando BCrypt e não são retornadas nas respostas de usuário.

### Swagger do MS1

Com a aplicação em execução:

```text
http://localhost:8080/swagger-ui.html
```

Para testar endpoints protegidos, clique em **Authorize** e informe as credenciais de um usuário cadastrado.

## 7. MS2 — Serviço de transações

O MS2 é responsável pelas funcionalidades financeiras do sistema.

### Funcionalidades

- Autenticação integrada ao MS1.
- Registro de depósitos.
- Registro de saques.
- Registro de transferências.
- Registro de compras.
- Consulta de transações.
- Listagem de transações com filtros.
- Consulta de saldo e limites.
- Integração com MockAPI.
- Conversão de valores para dólar e euro.
- Integração com BrasilAPI.
- Consulta de despesas por categoria.
- Agrupamento de despesas por dia e mês em branches específicas.
- Exportação de transações em CSV.
- Testes automatizados.
- Documentação Swagger.

### Principais endpoints confirmados

| Método | Endpoint | Descrição |
| --- | --- | --- |
| GET | /auth/me | Consultar usuário autenticado |
| GET | /accounts/me/balance | Consultar saldo e limites |
| POST | /transactions | Registrar depósito |
| GET | /transactions/{id} | Consultar transação |
| GET | /transactions/expenses/summary | Resumo de despesas por categoria |
| GET | /transactions/export/csv | Exportar transações CSV |

Os endpoints de saque, transferência, compra e listagem também estão implementados.

Consulte os respectivos controllers ou o Swagger para verificar os caminhos e parâmetros exatos dessas operações.

## 8. Autenticação do MS2

O MS2 utiliza HTTP Basic e valida as credenciais no MS1.

As requisições protegidas devem enviar o e-mail e a senha de um usuário cadastrado no MS1.

### Consultar usuário autenticado

```http
GET /auth/me
```

Exemplo de resposta:

```json
{
  "id": 6,
  "email": "user.three@example.com",
  "name": "user.three@example.com"
}
```

### Códigos HTTP

| Código | Significado |
| --- | --- |
| 200 | Autenticação realizada com sucesso |
| 401 | Credenciais ausentes ou inválidas |
| 503 | Serviço de autenticação indisponível |

O identificador do usuário autenticado é utilizado nas operações financeiras para associar as transações ao proprietário correto.

## 9. Consulta de saldo e limites

O MS2 consulta os dados de conta por meio da MockAPI.

Endpoint:

```http
GET /accounts/me/balance
```

Exemplo de resposta:

```json
{
  "userId": 6,
  "balance": 1000,
  "dailyLimit": 500,
  "transactionLimit": 200
}
```

### Campos

- `userId`: identificador do usuário.
- `balance`: saldo disponível.
- `dailyLimit`: limite diário.
- `transactionLimit`: limite por transação.

Esses valores são utilizados nas validações das operações financeiras.

## 10. Depósito

O depósito permite registrar a entrada de um valor financeiro.

Endpoint:

```http
POST /transactions
```

Exemplo de requisição:

```json
{
  "amount": 100.00,
  "description": "Depósito inicial",
  "category": "Deposit"
}
```

Resposta de sucesso:

```json
{
  "id": 1,
  "status": "APPROVED"
}
```

O identificador é gerado pelo banco de dados.

### Validações

- O valor deve ser maior que zero.
- Descrição é obrigatória.
- Categoria é obrigatória.
- O usuário deve estar autenticado.

### Respostas HTTP documentadas

| Código | Descrição |
| --- | --- |
| 201 | Transação criada |
| 400 | Dados inválidos |
| 401 | Usuário não autenticado |
| 503 | Serviço necessário indisponível |

## 11. Saque

O saque permite retirar valores da conta do usuário.

### Regras

- O usuário deve estar autenticado.
- O valor deve ser maior que zero.
- O sistema valida o saldo disponível.
- O sistema verifica os limites financeiros.
- Saques aprovados descontam o saldo na MockAPI.
- Saques rejeitados não devem modificar o saldo.

### Possíveis rejeições

- Saldo insuficiente.
- Limite diário excedido.
- Limite por transação excedido.

As transações podem apresentar os status:

```text
APPROVED
REJECTED
```

A resposta HTTP para a criação da transação é `201 Created`, conforme a documentação validada.

## 12. Transferência

A transferência permite enviar valores entre contas.

### Regras

- O usuário de origem deve estar autenticado.
- O usuário de destino deve existir.
- O valor deve ser maior que zero.
- O saldo deve ser suficiente.
- Os limites financeiros devem ser respeitados.

### Transferência aprovada

Quando aprovada:

1. A transferência é registrada.
2. O valor é descontado da conta de origem na MockAPI.
3. O valor é adicionado à conta de destino.

### Transferência rejeitada

Quando rejeitada, a transação é registrada com status `REJECTED`, sem movimentar os saldos.

Possíveis motivos:

```text
Destination user not found
Transaction limit exceeded
Insufficient balance
```

### Tratamento de falhas

Caso a atualização do saldo de destino falhe após o débito da origem, o sistema tenta devolver o valor à conta de origem.

A operação de compensação também pode falhar, sendo necessário registrar os erros para investigação.

O uso de `@Transactional` controla a transação no banco, mas não garante rollback das alterações realizadas na MockAPI.

## 13. Compra

A compra permite registrar despesas financeiras.

### Regras

- O usuário deve estar autenticado.
- O valor deve ser maior que zero.
- A categoria é obrigatória.
- O saldo deve ser suficiente.
- Os limites financeiros devem ser respeitados.

### Compra aprovada

Uma compra aprovada:

- É registrada no PostgreSQL.
- Recebe o status `APPROVED`.
- Desconta o valor do saldo na MockAPI.

### Compra rejeitada

Uma compra rejeitada:

- Recebe o status `REJECTED`.
- Não deve modificar o saldo.
- Informa o motivo da rejeição.

Exemplos:

```text
Transaction limit exceeded
Insufficient balance
```

## 14. Consulta de transações

O MS2 permite consultar transações pelo identificador.

Endpoint:

```http
GET /transactions/{id}
```

### Respostas HTTP

| Código | Descrição |
| --- | --- |
| 200 | Transação encontrada |
| 401 | Usuário não autenticado |
| 403 | Acesso não permitido |
| 404 | Transação não encontrada |
| 503 | Serviço indisponível |

A consulta utiliza a identidade do usuário autenticado para aplicar as regras de acesso.

O sistema também possui listagem de transações com filtros por período e tipo.

## 15. Conversão de moedas — BrasilAPI

O MS2 permite consultar valores de transações em:

- BRL — Real brasileiro.
- USD — Dólar americano.
- EUR — Euro.

A integração utiliza a BrasilAPI para consultar cotações PTAX.

### Endpoint de consulta da transação

```http
GET /transactions/{id}
```

A resposta da transação pode incluir os dados de conversão:

- `originalCurrency`
- `usd`
- `eur`

Cada conversão pode apresentar:

- `currency`
- `convertedAmount`
- `rate`
- `quoteDate`
- `available`

### Cálculo

A conversão utiliza:

```text
Valor convertido = Valor em BRL / Cotação de venda
```

Os cálculos são realizados com `BigDecimal`, utilizando arredondamento `HALF_UP` com duas casas decimais.

O sistema utiliza o fuso:

```text
America/Sao_Paulo
```

### Configuração da BrasilAPI

```text
EXCHANGE_SERVICE_URL=https://brasilapi.com.br
```

Endpoint externo:

```http
GET /api/cambio/v1/cotacao/{moeda}/{data}
```

### Tratamento de indisponibilidade

Caso a cotação não esteja disponível:

- A transação continua consultável.
- `available` retorna `false`.
- O valor convertido pode retornar `null`.
- Nenhuma cotação fictícia é utilizada.

## 16. Análise de despesas

O MS2 disponibiliza consultas de despesas financeiras.

### Resumo por categoria

Endpoint:

```http
GET /transactions/expenses/summary
```

Exemplo com período:

```http
GET /transactions/expenses/summary?startDate=2026-11-01&endDate=2026-11-30
```

### Regras

- Considera somente transações `APPROVED`.
- Inclui saques, transferências enviadas e compras.
- Exclui depósitos.
- Exclui transações rejeitadas.
- Considera apenas as transações do usuário autenticado.
- Agrupa os valores por categoria.
- Calcula o total geral das despesas.

Sem informar datas, o período padrão corresponde ao mês atual.

### Exemplo de resposta

```json
{
  "startDate": "2026-11-01",
  "endDate": "2026-11-30",
  "totalAmount": 600.00,
  "categories": [
    {
      "category": "Cash",
      "total": 500.00
    },
    {
      "category": "Food",
      "total": 50.00
    },
    {
      "category": "Transfer",
      "total": 50.00
    }
  ]
}
```

### Validações

- A data inicial não pode ser posterior à data final.
- Caso uma data seja informada, a outra também é obrigatória.
- Períodos inválidos retornam `400 Bad Request`.

### Despesas por dia

A implementação de agrupamento diário está disponível no PR #36.

O objetivo é agrupar as despesas aprovadas do usuário por dia, respeitando o período informado e as regras de filtragem.

PR:

https://github.com/r1llary/api-gestao-financeira/pull/36

### Despesas por mês

A implementação de agrupamento mensal está disponível no PR #37.

O objetivo é agrupar as despesas aprovadas do usuário por ano e mês, respeitando o período informado e as regras de filtragem.

PR:

https://github.com/r1llary/api-gestao-financeira/pull/37

**Observação:** os PRs #36 e #37 aguardavam revisão em 10/11/2026. As rotas específicas devem ser confirmadas após sua integração à branch principal.

## 17. Exportação de transações em CSV

O MS2 permite exportar transações financeiras para um arquivo CSV.

Endpoint:

```http
GET /transactions/export/csv
```

### Filtrar por período

Exemplo:

```http
GET /transactions/export/csv?startDate=2026-11-01&endDate=2026-11-10
```

Se nenhuma data for informada, o relatório inclui todas as transações disponíveis do usuário autenticado.

### Estrutura do arquivo

```csv
Data;Tipo;Categoria;Descrição;Valor (BRL);Status;Motivo da rejeição
```

### Regras

- Inclui depósitos, saques, compras e transferências.
- Inclui transações aprovadas e rejeitadas.
- Exporta somente transações do usuário autenticado.
- Utiliza o fuso `America/Sao_Paulo`.
- Utiliza ponto e vírgula como separador.
- Utiliza vírgula decimal nos valores.
- Utiliza UTF-8 com BOM para compatibilidade com Excel.
- Trata aspas, separadores e quebras de linha nos textos.
- Sem registros, retorna somente o cabeçalho.

### Respostas HTTP

| Código | Descrição |
| --- | --- |
| 200 | Arquivo exportado com sucesso |
| 400 | Período inválido |
| 401 | Usuário não autenticado |
| 503 | Serviço necessário indisponível |

### Validação realizada

A exportação foi testada pelo Swagger do MS2.

Resultados observados:

- Resposta `200 OK`.
- Arquivo `transactions.csv` disponibilizado para download.
- Conteúdo retornado como `text/csv`.
- 21 transações exportadas no período testado.
- Transações aprovadas e rejeitadas presentes.
- Datas, categorias, valores e motivos de rejeição presentes.
- Acentuação preservada ao abrir o arquivo no Bloco de Notas.

## 18. Documentação Swagger

Os microsserviços possuem documentação OpenAPI.

### Swagger MS1

```text
http://localhost:8080/swagger-ui.html
```

### Swagger MS2

```text
http://localhost:8081/swagger-ui/index.html
```

### Utilização

1. Inicie os microsserviços.
2. Abra o Swagger no navegador.
3. Clique em **Authorize**.
4. Informe o e-mail e a senha de um usuário de teste.
5. Selecione o endpoint desejado.
6. Clique em **Try it out**.
7. Preencha os parâmetros necessários.
8. Clique em **Execute**.
9. Confira o código HTTP e a resposta.

A documentação do MS2 apresenta parâmetros, exemplos e códigos de resposta das operações.

A exportação CSV está documentada com o tipo de conteúdo:

```text
text/csv
```

O PR #40 reúne a documentação Swagger e a exportação CSV.

## 19. Testes automatizados

O projeto utiliza JUnit e Mockito para validar as regras de negócio.

Também existem testes de integração com H2 para verificar cenários relacionados à persistência e rollback.

### Executar testes do MS1

Dentro da pasta `ms-usuarios`:

```bash
mvn test
```

### Executar testes do MS2

Dentro da pasta `ms-transacoes`:

```bash
mvn test
```

### Resultado da última execução validada do MS2

```text
Tests run: 69
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

### Funcionalidades cobertas pelos testes

- Criação de depósitos.
- Operações de saque.
- Transferências aprovadas e rejeitadas.
- Compras aprovadas e rejeitadas.
- Validação de saldo e limites.
- Tratamento de falhas externas.
- Conversão de moedas.
- Resumo de despesas por categoria.
- Exportação de transações CSV.
- Tratamento de caracteres especiais no CSV.
- Exportação sem registros.
- Persistência e rollback em cenários específicos.

### Cobertura de testes — JaCoCo

O PR #39 contém a configuração de cobertura JaCoCo e testes adicionais de autenticação.

PR:

https://github.com/r1llary/api-gestao-financeira/pull/39

Após a integração e execução da configuração JaCoCo, o relatório HTML poderá ser consultado no caminho configurado no `pom.xml`, normalmente:

```text
target/site/jacoco/index.html
```

O percentual real de cobertura deve ser obtido diretamente do relatório gerado.

**Importante:** 69 testes aprovados não significam 100% de cobertura.

## 20. Consistência de dados e limitações conhecidas

O projeto utiliza PostgreSQL para armazenar transações e MockAPI para armazenar os dados das contas.

Esses sistemas são independentes.

### Limitação do @Transactional

A anotação `@Transactional` permite controlar transações no banco de dados.

Entretanto, ela não desfaz automaticamente operações já realizadas em serviços externos, como a MockAPI.

### Saque

A implementação busca impedir que saques rejeitados alterem o saldo.

Falhas na comunicação externa exigem tratamento específico.

### Transferência

Em caso de falha no crédito da conta de destino, o sistema tenta compensar o débito realizado na origem.

A compensação também pode falhar.

### Compra

Uma falha durante a persistência ou atualização do saldo pode causar inconsistência entre o registro da transação e os dados da MockAPI.

### Melhorias para produção

Uma aplicação financeira de produção exigiria mecanismos adicionais, como:

- Idempotência.
- Compensação de operações.
- Reconciliação de saldos.
- Monitoramento de falhas.
- Recuperação de operações incompletas.
- Controle de consistência entre sistemas.

A implementação atual não garante atomicidade distribuída entre PostgreSQL e MockAPI.

## 21. Pull Requests

Situação registrada em 10/11/2026:

| PR | Funcionalidade | Situação |
| --- | --- | --- |
| #36 | Resumo diário de despesas | Aguardando revisão |
| #37 | Resumo mensal de despesas | Aguardando revisão |
| #39 | JaCoCo e testes de autenticação | Aguardando revisão |
| #40 | Exportação CSV e documentação Swagger | Aguardando revisão |

Links:

- PR #36: https://github.com/r1llary/api-gestao-financeira/pull/36
- PR #37: https://github.com/r1llary/api-gestao-financeira/pull/37
- PR #39: https://github.com/r1llary/api-gestao-financeira/pull/39
- PR #40: https://github.com/r1llary/api-gestao-financeira/pull/40

O PR #38 foi fechado porque sua implementação de exportação CSV foi incorporada ao PR #40.

As funcionalidades presentes nos PRs ainda precisam passar pela revisão e integração à branch `main`.

## 22. Checklist para entrega

Antes da entrega final, validar:

- [ ] MS1 e MS2 executando corretamente.
- [ ] PostgreSQL funcionando nos dois microsserviços.
- [ ] Docker Compose iniciando o ambiente.
- [ ] Swagger dos dois serviços acessível.
- [ ] Cadastro e importação de usuários funcionando.
- [ ] Autenticação HTTP Basic funcionando.
- [ ] Consulta de saldo e limites funcionando.
- [ ] Depósito aprovado e validado.
- [ ] Saque aprovado e rejeitado.
- [ ] Transferência aprovada e rejeitada.
- [ ] Compra aprovada e rejeitada.
- [ ] Consulta de transações funcionando.
- [ ] Conversões de moedas funcionando.
- [ ] Resumo por categoria funcionando.
- [ ] Resumo diário integrado e validado.
- [ ] Resumo mensal integrado e validado.
- [ ] Exportação CSV funcionando.
- [ ] Testes automatizados passando.
- [ ] Relatório real de cobertura JaCoCo conferido.
- [ ] Persistência validada após reinicialização dos containers.
- [ ] Pull Requests revisados e integrados conforme aprovação.
- [ ] README revisado.
- [ ] Demonstração final preparada.

## 23. Demonstração final

A demonstração do projeto deve apresentar o funcionamento integrado dos dois microsserviços.

Roteiro sugerido:

1. Apresentar a arquitetura do projeto.
2. Demonstrar o Docker Compose.
3. Abrir o Swagger do MS1.
4. Demonstrar cadastro ou importação de usuários.
5. Autenticar no MS2.
6. Consultar saldo e limites.
7. Executar transações aprovadas e rejeitadas.
8. Consultar uma transação e suas conversões.
9. Demonstrar consultas de despesas.
10. Exportar o relatório CSV.
11. Apresentar os testes e a cobertura disponível.
12. Explicar as limitações conhecidas.

Tempo previsto: até 15 minutos.

## 24. Informações do projeto

**Projeto:** API de Gestão Financeira

**Desafio:** Beca Java 2026

**Arquitetura:** Microsserviços com arquitetura hexagonal

**Linguagem:** Java 25

**Framework:** Spring Boot

**Banco de dados:** PostgreSQL 17

**Containerização:** Docker Compose

**Testes:** JUnit, Mockito e H2

**Documentação:** Swagger / OpenAPI

**Repositório:**

https://github.com/r1llary/api-gestao-financeira

**Entrega prevista:** 13/11/2026

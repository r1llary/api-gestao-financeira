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

### Conversão de moedas — BrasilAPI

O MS2 permite consultar uma transação com seu valor
original em reais (BRL) e as conversões para dólar
(USD) e euro (EUR).

As cotações são consultadas na BrasilAPI, utilizando
o boletim de fechamento PTAX.

#### Endpoint

GET /transactions/{id}

Requer autenticação HTTP Basic.

Exemplo:

GET http://localhost:8081/transactions/1

A resposta inclui os dados originais da transação
e os campos:

- originalCurrency: moeda original (BRL).
- usd: conversão para dólar.
- eur: conversão para euro.

Cada conversão apresenta:

- currency: moeda consultada.
- convertedAmount: valor convertido.
- rate: cotação utilizada.
- quoteDate: data da cotação.
- available: disponibilidade da conversão.

#### Cálculo

Valor convertido = valor em BRL / cotação de venda

O cálculo utiliza BigDecimal e arredondamento
HALF_UP para duas casas decimais.

A data da transação é interpretada no fuso
America/Sao_Paulo para consultar a cotação.

#### Tratamento de falhas

Se a BrasilAPI estiver indisponível ou não retornar
uma cotação válida, a transação continua consultável.

Nesse caso, a conversão retorna:

- available: false
- convertedAmount: null
- rate: null
- quoteDate: null

Nenhuma taxa fictícia é utilizada.

#### Configuração

Variável de ambiente:

EXCHANGE_SERVICE_URL=https://brasilapi.com.br

Endpoint da BrasilAPI:

GET /api/cambio/v1/cotacao/{moeda}/{data}

A URL é configurável e a integração possui
timeout de conexão e de leitura.

A variável está documentada no .env.example
e é repassada ao MS2 pelo Docker Compose.

#### Testes

Os testes automatizados verificam:

- Conversão de BRL para USD.
- Conversão de BRL para EUR.
- Retorno de indisponibilidade quando a API falha.

As conversões também foram validadas no Postman.

### Resumo de despesas por categoria

O MS2 permite consultar o total de despesas do usuário
autenticado, agrupado por categoria.

#### Endpoint

GET /transactions/expenses/summary

Requer autenticação HTTP Basic.

Sem informar datas, a consulta considera o mês atual.

Também é possível informar um período:

GET /transactions/expenses/summary?startDate=2026-11-01&endDate=2026-11-30

#### Regras

- Considera somente transações APPROVED.
- Inclui saques (WITHDRAWAL).
- Inclui transferências enviadas (TRANSFER).
- Inclui compras (PURCHASE).
- Exclui depósitos e transações rejeitadas.
- Retorna somente as despesas do usuário autenticado.

#### Exemplo de resposta

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

#### Validações

- A data inicial não pode ser posterior à data final.
- Se uma data for informada, a outra também será obrigatória.
- Períodos inválidos retornam HTTP 400 em JSON.

#### Testes

Foram realizados testes unitários de agrupamento
por categoria e exclusão de transações não elegíveis.

O endpoint também foi validado no Postman.

### Resumo de despesas por mês

O MS2 permite consultar as despesas do usuário
autenticado, agrupadas por mês e ano.

#### Endpoint

GET /transactions/expenses/monthly

Requer autenticação HTTP Basic.

Sem informar datas, a consulta considera o mês atual.

Também é possível informar um período:

GET /transactions/expenses/monthly?startDate=2026-11-01&endDate=2026-11-30

#### Regras

- Considera somente transações APPROVED.
- Inclui saques (WITHDRAWAL).
- Inclui transferências enviadas (TRANSFER).
- Inclui compras (PURCHASE).
- Exclui depósitos e transações rejeitadas.
- Retorna somente despesas do usuário autenticado.
- Agrupa os valores por mês e ano.
- Utiliza o fuso horário America/Sao_Paulo.
- Ordena os meses cronologicamente.

#### Exemplo de resposta

```json
{
  "startDate": "2026-11-01",
  "endDate": "2026-11-30",
  "totalAmount": 610.00,
  "months": [
    {
      "month": "2026-11",
      "amount": 610.00
    }
  ]
}
```

#### Validações

- A data inicial não pode ser posterior à data final.
- Se uma data for informada, a outra também será obrigatória.
- Períodos inválidos retornam HTTP 400.
- Credenciais ausentes ou inválidas retornam HTTP 401.
- Sem despesas, retorna total zero e lista vazia.

#### Testes

Foram implementados quatro testes unitários com JUnit e Mockito:

- Agrupamento de despesas por mês.
- Exclusão de depósitos e transações rejeitadas.
- Mudança de mês e ano considerando o fuso horário.
- Consulta sem despesas.

Os testes automatizados do MS2 foram executados
com sucesso: 69 testes, nenhuma falha.

O endpoint também foi validado com requisições HTTP,
incluindo consultas com e sem datas, erro 400 e
autenticação obrigatória com retorno 401.

### Exportação de transações em CSV

O MS2 permite exportar as transações financeiras do
usuário autenticado para um arquivo CSV.

#### Endpoint

GET /transactions/export/csv

Requer autenticação HTTP Basic.

É possível informar um período:

GET /transactions/export/csv?startDate=2026-11-01&endDate=2026-11-30

Sem informar datas, o relatório inclui todas as
transações disponíveis do usuário autenticado.

#### Conteúdo do relatório

O arquivo contém as seguintes colunas:

- Data
- Tipo
- Categoria
- Descrição
- Valor (BRL)
- Status
- Motivo da rejeição

#### Regras

- Inclui depósitos, saques, compras e transferências.
- Inclui transações aprovadas e rejeitadas.
- Retorna somente transações do usuário autenticado.
- Utiliza o fuso horário America/Sao_Paulo.
- Utiliza ponto e vírgula como separador.
- Utiliza UTF-8 com BOM para compatibilidade com Excel.
- Trata aspas, separadores e quebras de linha nos textos.
- Sem transações, retorna apenas o cabeçalho do CSV.

#### Validações

- Data inicial posterior à final retorna HTTP 400.
- Credenciais ausentes ou inválidas retornam HTTP 401.
- Exportação bem-sucedida retorna HTTP 200.

#### Testes

Foram implementados quatro testes unitários com
JUnit e Mockito, cobrindo:

- Exportação de transações aprovadas.
- Exportação de transações rejeitadas.
- Tratamento de caracteres especiais.
- Exportação sem transações.

A suíte automatizada do MS2 foi executada com sucesso.

O endpoint também foi validado no Postman.
O download e a abertura do arquivo no Excel foram testados.

### Atualização de saldo no saque

Quando um saque é aprovado, o MS2 desconta o valor
do saldo da conta no MockAPI.

Regras:
- Saques aprovados descontam o saldo.
- Saques rejeitados não alteram o saldo.
- Falhas na validação impedem o salvamento.
- Falhas no salvamento não movimentam o saldo.

### Testes de consistência

Foram adicionados testes unitários com JUnit e Mockito
e um teste de integração com banco H2 em memória.

O teste de integração verifica o rollback da gravação
quando o MockAPI apresenta uma falha.

Limitação:
O PostgreSQL e o MockAPI são sistemas independentes.
O rollback do banco não desfaz alterações já realizadas
no MockAPI. Uma solução de compensação ou reconciliação
seria necessária para garantir consistência completa.


### Transferência entre contas (MS2)

A transferência permite enviar um valor de uma conta para outra.

Quando a transferência é aprovada:
- A transação é registrada no banco de dados.
- O valor é descontado da conta de origem no MockAPI.
- O mesmo valor é adicionado à conta de destino.

Quando a transferência é rejeitada:
- A transação é registrada como REJECTED.
- Os saldos das contas não são alterados.

#### Tratamento de falhas

Se o crédito na conta de destino falhar:
- O sistema tenta devolver o valor à conta de origem.
- O erro original é preservado.
- O @Transactional permite reverter a gravação no PostgreSQL.

Se a devolução também falhar, os dois erros são preservados
para facilitar a investigação.

#### Testes

Foram implementados testes unitários com JUnit e Mockito
e teste de integração utilizando H2.

Os testes verificam:
- Transferências aprovadas e rejeitadas.
- Atualização dos saldos de origem e destino.
- Tentativa de devolução em caso de falha.
- Rollback do registro no banco de dados.
- Preservação dos erros de crédito e devolução.

#### Limitação conhecida

O PostgreSQL e o MockAPI são sistemas independentes.

O @Transactional não desfaz alterações feitas no MockAPI.
A compensação também pode falhar.

Portanto, a implementação não garante atomicidade entre as
duas contas. Em um ambiente de produção, seriam necessários
mecanismos adicionais de controle, reconciliação e recuperação.

### Compra (Purchase) — MS2

A funcionalidade de compra permite registrar despesas
e descontar o valor do saldo do usuário.

Regras:
- Compras aprovadas descontam o valor do saldo no MockAPI.
- Compras rejeitadas não alteram o saldo.
- Falhas ao salvar a transação impedem a atualização do saldo.
- Falhas na atualização do saldo propagam um erro.

#### Testes

Foram implementados testes unitários com JUnit e Mockito
e um teste de integração utilizando H2.

Os testes verificam:
- Compra aprovada com desconto no saldo.
- Compra rejeitada por limite excedido.
- Falha no salvamento da transação.
- Falha na comunicação com o MockAPI.
- Rollback do registro no banco de dados.

#### Limitação conhecida

O @Transactional permite reverter alterações no banco
quando ocorre uma exceção, mas não desfaz alterações
já realizadas no MockAPI.

Por isso, ainda existe risco de inconsistência entre
o registro da compra e o saldo externo.


### Resumo de despesas por dia

O MS2 permite consultar as despesas do usuário autenticado,
agrupadas por dia.

#### Endpoint

GET /transactions/expenses/daily

Requer autenticação HTTP Basic.

Sem informar datas, a consulta considera o mês atual.

Também é possível informar um período:

GET /transactions/expenses/daily?startDate=2026-11-01&endDate=2026-11-30

#### Regras

- Considera somente transações APPROVED.
- Inclui saques (WITHDRAWAL).
- Inclui transferências enviadas (TRANSFER).
- Inclui compras (PURCHASE).
- Exclui depósitos e transações rejeitadas.
- Agrupa os valores por dia, usando o fuso America/Sao_Paulo.
- Retorna somente as despesas do usuário autenticado.
- Apresenta o total geral e os totais diários.

#### Exemplo de resposta

```json
{
  "startDate": "2026-11-01",
  "endDate": "2026-11-30",
  "totalAmount": 610.00,
  "days": [
    {
      "date": "2026-11-06",
      "amount": 500.00
    },
    {
      "date": "2026-11-07",
      "amount": 100.00
    },
    {
      "date": "2026-11-09",
      "amount": 10.00
    }
  ]
}
```

#### Validações

- A data inicial não pode ser posterior à data final.
- Se uma data for informada, a outra também será obrigatória.
- Períodos inválidos retornam HTTP 400.
- Requisições sem autenticação retornam HTTP 401.
- Sem despesas no período, retorna total zero e lista vazia.

#### Testes

Foram implementados quatro testes unitários com JUnit e Mockito:

- Agrupamento de despesas por dia.
- Exclusão de depósitos e transações rejeitadas.
- Conversão de datas para o fuso de São Paulo.
- Retorno de total zero quando não existem despesas.

Os 69 testes automatizados do MS2 passaram com sucesso.

O endpoint também foi validado no Postman.


# API Gestão Financeira

API REST para gestão financeira de múltiplos usuários, desenvolvida com Java 25, Spring Boot, PostgreSQL e Docker.

O projeto utiliza dois microsserviços:

- **MS1 — Usuários:** responsável pelo gerenciamento e autenticação dos usuários.
- **MS2 — Transações:** responsável pelas operações financeiras, consulta de saldos, conversão de moedas, relatórios de despesas e exportação CSV.

O projeto utiliza arquitetura hexagonal, autenticação HTTP Basic, testes automatizados e Docker Compose.

---

## 1. Tecnologias utilizadas

- Java 25
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- PostgreSQL 17
- Docker e Docker Compose
- JUnit
- Mockito
- JaCoCo
- H2 para testes de integração
- Springdoc OpenAPI e Swagger UI
- Maven
- Postman
- MockAPI
- BrasilAPI

---

## 2. Estrutura do projeto

O projeto é dividido em dois microsserviços independentes.

### MS1 — Serviço de usuários

Responsável por:

- Cadastrar usuários.
- Listar usuários.
- Consultar usuários por ID.
- Atualizar dados dos usuários.
- Excluir usuários.
- Importar usuários por arquivo CSV.
- Autenticar usuários utilizando HTTP Basic.

### MS2 — Serviço de transações

Responsável por:

- Registrar depósitos.
- Realizar saques.
- Realizar transferências entre contas.
- Registrar compras.
- Consultar transações financeiras.
- Consultar o saldo do usuário.
- Validar saldo e limites financeiros.
- Converter valores de BRL para USD e EUR.
- Consultar despesas por categoria.
- Consultar despesas por dia.
- Consultar despesas por mês.
- Exportar transações em CSV.
- Utilizar autenticação integrada ao MS1.

---

## 3. Executar com Docker Compose

### Pré-requisitos

- Docker com Docker Compose.
- Portas necessárias disponíveis.
- Arquivo `.env` configurado.

Na raiz do projeto, copie o arquivo `.env.example` para `.env` e preencha as variáveis dos dois bancos de dados.

O arquivo `.env` contém informações sensíveis e não deve ser enviado ao GitHub.

### Iniciar o ambiente

Na raiz do projeto, execute:

```bash
docker compose up --build -d
```

Esse comando constrói as imagens das aplicações e inicia os dois microsserviços e seus respectivos bancos PostgreSQL.

### Endereços dos microsserviços

| Microsserviço | Endereço |
| --- | --- |
| MS1 — Usuários | http://localhost:8080 |
| MS2 — Transações | http://localhost:8081 |

### Verificar os containers

```bash
docker compose ps
```

O ambiente deve apresentar os microsserviços e os bancos de dados em execução.

### Parar o ambiente

```bash
docker compose down
```

Esse comando para e remove os containers sem remover os volumes persistentes dos bancos.

---

# MS1 — Serviço de Usuários

## 4. Funcionalidades do MS1

O MS1 é responsável pelo gerenciamento dos usuários da aplicação.

### Tecnologias

- Java 25
- Spring Boot
- Spring Data JPA
- Spring Security
- Bean Validation
- PostgreSQL
- JUnit e Mockito
- Springdoc OpenAPI
- Swagger UI

### Arquitetura

O MS1 utiliza arquitetura hexagonal, separando as regras de negócio das tecnologias externas.

Principais pacotes:

| Pacote | Responsabilidade |
| --- | --- |
| `core.usuario.model` | Modelos de domínio |
| `core.usuario.port.input` | Contratos dos casos de uso |
| `core.usuario.port.output` | Contratos de persistência |
| `core.usuario.service` | Regras de negócio |
| `adapter.input.rest` | Endpoints REST, DTOs e tratamento de erros |
| `adapter.input.csv` | Importação de usuários via CSV |
| `adapter.input.security` | Integração com a autenticação |
| `adapter.output.persistence` | Persistência com JPA |
| `config` | Configuração dos componentes |

---

## 5. Endpoints do MS1

| Método | Rota | Descrição | Autenticação |
| --- | --- | --- | --- |
| POST | `/users` | Cadastrar usuário | Pública |
| GET | `/users` | Listar usuários | Basic |
| GET | `/users/{id}` | Buscar usuário por ID | Basic |
| PUT | `/users/{id}` | Atualizar usuário | Basic |
| DELETE | `/users/{id}` | Excluir usuário | Basic |
| POST | `/users/import` | Importar usuários por CSV | Basic |

### Autenticação

As operações protegidas utilizam:

- E-mail do usuário cadastrado.
- Senha original informada durante o cadastro.

As senhas são armazenadas utilizando BCrypt.

Por segurança, as senhas não são retornadas nas respostas dos endpoints.

### Validações

O MS1 utiliza Bean Validation para validar os dados enviados nas requisições.

Entre as validações estão:

- Campos obrigatórios.
- Formato do e-mail.
- Dados inválidos.
- Tratamento de usuários não encontrados.

Os erros são tratados pela aplicação e retornados com códigos HTTP adequados.

---

## 6. Importação de usuários por CSV

### Endpoint

```http
POST /users/import
```

Requer autenticação HTTP Basic.

A requisição utiliza `multipart/form-data`, com o arquivo enviado no campo `file`.

### Formato do arquivo

O arquivo deve utilizar UTF-8 e conter o seguinte cabeçalho:

```csv
name,email,password
```

Exemplo:

```csv
name,email,password
Maria Silva,maria@email.com,Senha123
Joao Souza,joao@email.com,Senha456
```

Os exemplos de arquivos estão disponíveis em:

```text
ms-usuarios/examples
```

---

## 7. Swagger do MS1

Com o MS1 em execução, acesse:

http://localhost:8080/swagger-ui.html

Para testar os endpoints protegidos:

1. Clique em `Authorize`.
2. Informe o e-mail de um usuário cadastrado.
3. Informe a senha correspondente.
4. Autorize e execute as requisições.

---

# MS2 — Serviço de Transações

## 8. Funcionalidades do MS2

O MS2 é responsável pelas operações financeiras dos usuários, incluindo depósitos, saques, transferências e compras.

O microsserviço também oferece consulta de transações, saldo, conversão de moedas, resumos de despesas por categoria, dia e mês, além da exportação de relatórios CSV.

Utiliza Java, Spring Boot, PostgreSQL, autenticação HTTP Basic integrada ao MS1 e testes automatizados com JUnit, Mockito e JaCoCo.

---

## 9. Executar o MS2 pelo IntelliJ

Opcionalmente, é possível executar o banco PostgreSQL pelo Docker e a aplicação diretamente pelo IntelliJ.

Se o ambiente completo estiver em execução, pare o container da aplicação MS2 para liberar a porta 8081:

```bash
docker compose stop ms-transacoes
```

### Iniciar somente o banco do MS2

Na raiz do projeto, execute:

```bash
docker compose up -d transactions-db
```

### Configurar variáveis de ambiente

No IntelliJ, abra a configuração de execução de `MsTransacoesApplication`.

Configure:

| Variável | Valor |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5434/<nome-do-banco>` |
| `DB_USERNAME` | Valor de `TRANSACTIONS_DB_USER` no `.env` |
| `DB_PASSWORD` | Valor de `TRANSACTIONS_DB_PASSWORD` no `.env` |

Substitua `<nome-do-banco>` pelo valor configurado em `TRANSACTIONS_DB_NAME`.

O MS2 utiliza a porta:

```text
8081
```

As variáveis de ambiente devem ser fornecidas na configuração de execução do IntelliJ.

---

## 10. Autenticação do MS2

O MS2 utiliza HTTP Basic e valida as credenciais dos usuários por meio do MS1.

As requisições protegidas precisam enviar:

- E-mail de um usuário cadastrado.
- Senha correspondente.

### Endpoint de autenticação

```http
GET /auth/me
```

O endpoint retorna informações do usuário autenticado, incluindo seu ID e e-mail.

Quando presente, o campo `name` corresponde ao e-mail utilizado na autenticação.

### Respostas

| HTTP | Significado |
| --- | --- |
| 200 | Credenciais válidas |
| 401 | Credenciais ausentes ou inválidas |
| 503 | Serviço de autenticação MS1 indisponível |

### Configuração

A URL do MS1 é configurada pela variável:

```text
USERS_SERVICE_URL
```

No Docker Compose:

```text
http://ms-usuarios:8080
```

Na execução local pelo IntelliJ:

```text
http://localhost:8080
```

As transações financeiras são associadas ao usuário autenticado.

---

## 11. Depósito

Permite registrar um depósito financeiro para o usuário autenticado.

### Endpoint

```http
POST /transactions
```

Requer autenticação HTTP Basic.

### Exemplo de requisição

```json
{
  "amount": 100.00,
  "description": "Depósito inicial",
  "category": "Deposit"
}
```

### Exemplo de resposta

HTTP 201 Created:

```json
{
  "id": 1,
  "status": "APPROVED"
}
```

O ID é gerado pelo banco de dados e pode variar.

### Validações

- O valor é obrigatório.
- O valor deve ser maior que zero.
- Descrição e categoria são obrigatórias.
- Descrição e categoria não podem conter somente espaços.
- Dados inválidos retornam HTTP 400.
- Credenciais inválidas retornam HTTP 401.
- Indisponibilidade do serviço de autenticação retorna HTTP 503.

### Testes

Os testes verificam:

- Criação de depósito aprovado.
- Associação ao usuário autenticado.
- Retorno HTTP 201.
- Validações das requisições.

---

## 12. Saque

Permite retirar valores da conta do usuário.

### Regras

- Verifica o saldo disponível.
- Verifica os limites financeiros.
- Saques aprovados são registrados como `APPROVED`.
- Saques rejeitados são registrados como `REJECTED`.
- Saques aprovados descontam o valor do saldo no MockAPI.
- Saques rejeitados não alteram o saldo.

### Tratamento de falhas

- Falhas na validação impedem a movimentação.
- Falhas ao salvar a transação impedem a atualização do saldo.
- Exceções durante a operação são tratadas pela aplicação.

### Testes

Foram implementados testes unitários com JUnit e Mockito e teste de integração utilizando H2.

O teste de integração verifica o rollback da gravação quando ocorre falha na comunicação com o MockAPI.

### Limitação conhecida

O PostgreSQL e o MockAPI são sistemas independentes.

O rollback no banco de dados não desfaz automaticamente alterações que já ocorreram no MockAPI.

Uma solução de compensação ou reconciliação seria necessária para garantir maior consistência em um ambiente de produção.

---

## 13. Transferência entre contas

Permite transferir valores entre contas.

### Quando a transferência é aprovada

- A transação é registrada no banco de dados.
- O valor é descontado da conta de origem no MockAPI.
- O mesmo valor é adicionado à conta de destino.

### Quando a transferência é rejeitada

- A transação é registrada como `REJECTED`.
- Os saldos não são alterados.

### Validações

- Usuário de destino existente.
- Saldo suficiente.
- Limite por transação.
- Limite diário.

### Tratamento de falhas

Se ocorrer falha ao creditar o valor na conta de destino:

- O sistema tenta devolver o valor à conta de origem.
- O erro original é preservado.
- O `@Transactional` permite reverter a gravação no PostgreSQL.

Se a devolução também falhar, os erros são preservados para investigação.

### Testes

Foram implementados testes unitários com JUnit e Mockito e testes de integração utilizando H2.

Os cenários incluem:

- Transferência aprovada.
- Transferência rejeitada.
- Atualização dos saldos.
- Falha no crédito da conta de destino.
- Tentativa de devolução do valor.
- Rollback no banco.
- Preservação dos erros.

### Limitação conhecida

O `@Transactional` não desfaz alterações realizadas no MockAPI.

A compensação também pode falhar.

Portanto, a implementação não garante atomicidade entre as duas contas. Em produção, seriam necessários mecanismos adicionais de controle, reconciliação e recuperação.

---

## 14. Compra (Purchase)

Permite registrar compras e descontar os valores do saldo do usuário.

### Regras

- Compras aprovadas descontam o valor do saldo no MockAPI.
- Compras rejeitadas não alteram o saldo.
- A operação verifica os limites financeiros.
- Falhas ao salvar a transação impedem a atualização do saldo.
- Falhas na atualização do saldo propagam um erro.

### Testes

Foram implementados testes unitários e de integração utilizando JUnit, Mockito e H2.

Os cenários incluem:

- Compra aprovada.
- Compra rejeitada por limite excedido.
- Falha no salvamento da transação.
- Falha na comunicação com o MockAPI.
- Rollback do registro no banco.

### Limitação conhecida

O rollback do PostgreSQL não desfaz alterações realizadas no MockAPI.

Por isso, ainda existe risco de inconsistência entre o registro da compra e o saldo externo.

---

## 15. Consulta de transações

O MS2 permite consultar informações de transações financeiras.

### Consultar transação por ID

```http
GET /transactions/{id}
```

Requer autenticação HTTP Basic.

Exemplo:

```http
GET http://localhost:8081/transactions/1
```

A consulta apresenta os dados da transação e as informações de conversão de moedas, quando disponíveis.

---

## 16. Consulta de saldo

Permite consultar as informações da conta do usuário autenticado.

### Endpoint

```http
GET /accounts/me/balance
```

Requer autenticação HTTP Basic.

As informações da conta são obtidas por meio da integração com o MockAPI.

---

## 17. Conversão de moedas — BrasilAPI

O MS2 permite consultar o valor original de uma transação em reais (BRL) e as conversões para:

- Dólar americano (USD).
- Euro (EUR).

As cotações são consultadas na BrasilAPI, utilizando os dados de câmbio disponíveis pela API.

### Endpoint

```http
GET /transactions/{id}
```

### Informações retornadas

A resposta inclui os dados originais da transação e os campos:

- `originalCurrency`: moeda original.
- `usd`: informações da conversão para dólar.
- `eur`: informações da conversão para euro.

Cada conversão apresenta:

- `currency`: moeda consultada.
- `convertedAmount`: valor convertido.
- `rate`: cotação utilizada.
- `quoteDate`: data da cotação.
- `available`: disponibilidade da conversão.

### Cálculo

```text
Valor convertido = Valor em BRL / Cotação de venda
```

O cálculo utiliza `BigDecimal` com arredondamento `HALF_UP` para duas casas decimais.

A data da transação é interpretada no fuso horário:

```text
America/Sao_Paulo
```

### Tratamento de falhas

Se a BrasilAPI estiver indisponível ou não retornar uma cotação válida, a consulta da transação permanece disponível.

Nesse cenário, os dados de conversão podem retornar:

```json
{
  "available": false,
  "convertedAmount": null,
  "rate": null,
  "quoteDate": null
}
```

Nenhuma taxa de câmbio fictícia é utilizada.

### Configuração

Variável de ambiente:

```text
EXCHANGE_SERVICE_URL=https://brasilapi.com.br
```

Endpoint utilizado pela integração:

```http
GET /api/cambio/v1/cotacao/{moeda}/{data}
```

A integração possui timeout de conexão e leitura.

A variável está documentada no `.env.example` e é repassada ao MS2 pelo Docker Compose.

### Limitação conhecida

A consulta de cotação para a data atual pode retornar HTTP 400 na BrasilAPI.

O tratamento específico para datas atuais ou futuras precisa considerar a consulta ao dia anterior e utilizar a data efetivamente retornada pela API.

Enquanto esse ajuste não estiver integrado, uma conversão referente à data atual poderá ser apresentada como indisponível.

### Testes

Os testes automatizados verificam:

- Conversão de BRL para USD.
- Conversão de BRL para EUR.
- Tratamento de falhas na API.
- Retorno de conversão indisponível.

A integração também foi validada no Postman.

---

## 18. Resumo de despesas por categoria

Permite consultar as despesas do usuário autenticado, agrupadas por categoria.

### Endpoint

```http
GET /transactions/expenses/summary
```

Requer autenticação HTTP Basic.

Sem informar datas, a consulta considera o mês atual.

Também é possível informar um período:

```http
GET /transactions/expenses/summary?startDate=2026-11-01&endDate=2026-11-30
```

### Regras

- Considera somente transações `APPROVED`.
- Inclui saques (`WITHDRAWAL`).
- Inclui transferências enviadas (`TRANSFER`).
- Inclui compras (`PURCHASE`).
- Exclui depósitos.
- Exclui transações rejeitadas.
- Retorna somente as despesas do usuário autenticado.

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
- Se uma data for informada, a outra também será obrigatória.
- Períodos inválidos retornam HTTP 400.

### Testes

Foram realizados testes unitários de agrupamento por categoria e exclusão de transações não elegíveis.

O endpoint também foi validado no Postman.

---

## 19. Resumo de despesas por dia

Permite consultar as despesas do usuário autenticado, agrupadas por dia.

### Endpoint

```http
GET /transactions/expenses/daily
```

Requer autenticação HTTP Basic.

Sem informar datas, a consulta considera o mês atual.

Também é possível informar um período:

```http
GET /transactions/expenses/daily?startDate=2026-11-01&endDate=2026-11-30
```

### Regras

- Considera somente transações `APPROVED`.
- Inclui saques (`WITHDRAWAL`).
- Inclui transferências enviadas (`TRANSFER`).
- Inclui compras (`PURCHASE`).
- Exclui depósitos e transações rejeitadas.
- Retorna somente despesas do usuário autenticado.
- Agrupa os valores por dia.
- Utiliza o fuso horário `America/Sao_Paulo`.
- Apresenta o total geral e os totais diários.

### Exemplo de resposta

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

### Validações

- A data inicial não pode ser posterior à data final.
- Se uma data for informada, a outra também será obrigatória.
- Períodos inválidos retornam HTTP 400.
- Credenciais ausentes ou inválidas retornam HTTP 401.
- Sem despesas, retorna total zero e lista vazia.

### Testes

Foram implementados quatro testes unitários com JUnit e Mockito:

- Agrupamento de despesas por dia.
- Exclusão de depósitos e transações rejeitadas.
- Conversão de datas para o fuso de São Paulo.
- Retorno de total zero quando não existem despesas.

O endpoint também foi validado no Postman.

---

## 20. Resumo de despesas por mês

Permite consultar as despesas do usuário autenticado, agrupadas por mês e ano.

### Endpoint

```http
GET /transactions/expenses/monthly
```

Requer autenticação HTTP Basic.

Sem informar datas, a consulta considera o mês atual.

Também é possível informar um período:

```http
GET /transactions/expenses/monthly?startDate=2026-11-01&endDate=2026-11-30
```

### Regras

- Considera somente transações `APPROVED`.
- Inclui saques (`WITHDRAWAL`).
- Inclui transferências enviadas (`TRANSFER`).
- Inclui compras (`PURCHASE`).
- Exclui depósitos e transações rejeitadas.
- Retorna somente despesas do usuário autenticado.
- Agrupa os valores por mês e ano.
- Utiliza o fuso horário `America/Sao_Paulo`.
- Ordena os meses cronologicamente.

### Exemplo de resposta

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

### Validações

- A data inicial não pode ser posterior à data final.
- Se uma data for informada, a outra também será obrigatória.
- Períodos inválidos retornam HTTP 400.
- Credenciais ausentes ou inválidas retornam HTTP 401.
- Sem despesas, retorna total zero e lista vazia.

### Testes

Foram implementados quatro testes unitários com JUnit e Mockito:

- Agrupamento de despesas por mês.
- Exclusão de depósitos e transações rejeitadas.
- Mudança de mês e ano considerando o fuso horário.
- Consulta sem despesas.

O endpoint também foi validado com requisições HTTP, incluindo consultas com e sem datas, erro 400 e autenticação obrigatória.

---

## 21. Exportação de transações em CSV

Permite exportar as transações financeiras do usuário autenticado para um arquivo CSV.

### Endpoint

```http
GET /transactions/export/csv
```

Requer autenticação HTTP Basic.

Também é possível informar um período:

```http
GET /transactions/export/csv?startDate=2026-11-01&endDate=2026-11-30
```

Sem informar datas, o relatório inclui todas as transações disponíveis do usuário autenticado.

### Conteúdo do arquivo

O CSV contém as seguintes colunas:

- Data
- Tipo
- Categoria
- Descrição
- Valor (BRL)
- Status
- Motivo da rejeição

### Regras

- Inclui depósitos, saques, compras e transferências.
- Inclui transações aprovadas e rejeitadas.
- Retorna somente transações do usuário autenticado.
- Utiliza o fuso horário `America/Sao_Paulo`.
- Utiliza ponto e vírgula (`;`) como separador.
- Utiliza vírgula como separador decimal.
- Utiliza UTF-8 com BOM para compatibilidade com Excel.
- Trata aspas, separadores e quebras de linha.
- Aplica proteção contra interpretação de conteúdo como fórmula em planilhas.
- Sem transações, retorna apenas o cabeçalho do CSV.

### Validações

- Data inicial posterior à data final retorna HTTP 400.
- Credenciais ausentes ou inválidas retornam HTTP 401.
- Exportação bem-sucedida retorna HTTP 200.

### Testes

Foram implementados quatro testes unitários com JUnit e Mockito, cobrindo:

- Exportação de transações aprovadas.
- Exportação de transações rejeitadas.
- Tratamento de caracteres especiais.
- Exportação sem transações.

O endpoint também foi validado no Postman.

O download e a abertura do arquivo no Excel foram testados.

---

# Testes Automatizados e Qualidade

## 22. Testes do MS2

O MS2 utiliza testes automatizados para verificar as principais regras de negócio e o comportamento das funcionalidades.

### Tecnologias

- JUnit
- Mockito
- H2 para testes de integração
- JaCoCo para cobertura de código

### Cenários testados

Os testes verificam:

- Saldo suficiente e insuficiente.
- Limite por transação.
- Limite diário.
- Aprovação e rejeição de operações.
- Validação de depósitos.
- Atualização do saldo em saques.
- Transferência entre contas.
- Compras aprovadas e rejeitadas.
- Cálculo de despesas por categoria.
- Cálculo de despesas por dia.
- Cálculo de despesas por mês.
- Exportação de transações em CSV.
- Conversão de BRL para USD e EUR.
- Tratamento de indisponibilidade da API de câmbio.
- Autenticação válida e inválida.
- Falhas de comunicação com o MS1.
- Rollback em cenários de falha.

Os testes utilizam mocks e simulações locais quando necessário para reduzir a dependência de serviços externos reais.

### Executar os testes

Na pasta `ms-transacoes`, execute:

```bash
mvn test
```

### Resultado validado em 10/11/2026

- **84 testes executados.**
- **0 falhas.**
- **0 erros.**
- **0 testes ignorados.**
- **BUILD SUCCESS.**

---

## 23. Cobertura de testes — JaCoCo

O MS2 utiliza JaCoCo para medir a cobertura dos testes automatizados.

### Gerar o relatório

Na pasta `ms-transacoes`, execute:

```bash
mvn clean verify
```

O comando:

1. Remove resultados anteriores da compilação.
2. Compila a aplicação.
3. Executa os testes automatizados.
4. Gera o relatório de cobertura do JaCoCo.

### Consultar o relatório

Relatório HTML:

```text
ms-transacoes/target/site/jacoco/index.html
```

Relatório CSV:

```text
ms-transacoes/target/site/jacoco/jacoco.csv
```

Para abrir o relatório HTML no Windows PowerShell, a partir da pasta `ms-transacoes`:

```powershell
start .\target\site\jacoco\index.html
```

### Resultados da cobertura

Na medição realizada em **10/11/2026**, após a integração dos resumos diário e mensal e da exportação CSV:

| Indicador | Resultado |
| --- | --- |
| Testes automatizados | 84 |
| Falhas | 0 |
| Erros | 0 |
| Classes analisadas | 62 |
| Linhas cobertas | 551 |
| Linhas não cobertas | 180 |
| Total de linhas analisadas | 731 |
| Cobertura global de linhas | 75,4% |
| Cobertura de instruções | 73% |
| Cobertura de desvios (branches) | 67% |

### Cobertura dos serviços de transação

O pacote `core.transacao.service` apresentou:

| Indicador | Resultado |
| --- | --- |
| Classes analisadas | 11 |
| Linhas cobertas | 253 |
| Linhas não cobertas | 18 |
| Total de linhas | 271 |
| Cobertura de linhas | 93,4% |

### Configuração das exclusões

O JaCoCo foi configurado para excluir do relatório:

- Classes de configuração.
- DTOs.
- Classe principal `Application`.

Essas exclusões não removem classes da aplicação nem impedem sua execução.

Elas apenas retiram essas classes do cálculo apresentado no relatório de cobertura.

### Interpretação dos resultados

A cobertura global de linhas do MS2 foi de **75,4%**.

Já os serviços de transação apresentaram **93,4% de cobertura de linhas**.

Esses indicadores representam conjuntos diferentes de classes.

A cobertura dos serviços de transação não representa a cobertura global do MS2, pois o relatório geral também considera controllers, adapters e outros componentes.

A cobertura é utilizada como indicador de qualidade, sem exigir 100% das linhas do projeto.

---

## 24. Limitações conhecidas

O projeto utiliza serviços externos para simular funcionalidades financeiras.

### MockAPI

O PostgreSQL e o MockAPI são sistemas independentes.

Transações com múltiplas atualizações podem apresentar inconsistências caso uma operação externa falhe após outra ter sido concluída.

A aplicação utiliza tratamento de exceções, rollback no banco e tentativas de compensação nos cenários implementados.

Entretanto, esses mecanismos não garantem atomicidade completa entre o banco de dados e o MockAPI.

Em produção, seriam necessários mecanismos adicionais de reconciliação, monitoramento e recuperação.

### BrasilAPI

A integração depende da disponibilidade das cotações fornecidas pela API externa.

Falhas de consulta não impedem a consulta dos dados originais da transação.

A conversão é apresentada como indisponível quando não é possível obter uma cotação válida.

O tratamento da data atual e de datas futuras está identificado como um ajuste necessário antes da demonstração.

---

## 25. Segurança e boas práticas

O projeto utiliza as seguintes práticas:

- Autenticação HTTP Basic.
- Senhas armazenadas com BCrypt no MS1.
- Validação de credenciais pelo MS1.
- Associação das transações ao usuário autenticado.
- Validação dos dados recebidos pelos endpoints.
- Tratamento de erros HTTP.
- Variáveis de ambiente para configurações sensíveis.
- Arquivo `.env` fora do controle de versão.
- Testes automatizados com JUnit e Mockito.
- Testes de integração para cenários selecionados.
- Medição de cobertura com JaCoCo.
- Separação das responsabilidades por microsserviço.
- Arquitetura hexagonal.
- Uso de DTOs para entrada e saída de dados.

---

## 26. Validações realizadas

Durante o desenvolvimento, foram realizadas verificações utilizando:

- IntelliJ IDEA.
- Maven.
- Docker Compose.
- PostgreSQL.
- Postman.
- Swagger UI do MS1.
- JUnit.
- Mockito.
- JaCoCo.
- H2.
- MockAPI.
- BrasilAPI.

Entre os cenários validados estão:

- Cadastro e autenticação de usuários.
- Importação de usuários por CSV.
- Depósitos.
- Saques.
- Transferências.
- Compras.
- Consultas financeiras.
- Resumos de despesas.
- Exportação de transações.
- Tratamento de erros.
- Execução dos testes automatizados.
- Geração de relatório de cobertura.

---

## 27. Considerações finais

A API Gestão Financeira demonstra a construção de uma aplicação backend utilizando microsserviços em Java e Spring Boot.

O projeto contempla gerenciamento de usuários, autenticação, operações financeiras, persistência, integrações externas, tratamento de erros e testes automatizados.

A separação entre MS1 e MS2 permite organizar melhor as responsabilidades da aplicação.

O uso de arquitetura hexagonal contribui para separar as regras de negócio dos detalhes de infraestrutura.

Os testes automatizados e o JaCoCo auxiliam na validação das funcionalidades e no acompanhamento da qualidade do código.

O projeto também documenta limitações relacionadas às integrações externas, especialmente em operações que envolvem o PostgreSQL, o MockAPI e a BrasilAPI.
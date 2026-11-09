package com.beca.transacoes.config;

import com.beca.transacoes.core.conta.port.input.AccountUseCase;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.conta.service.AccountService;
import com.beca.transacoes.core.transacao.port.input.*;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import com.beca.transacoes.core.transacao.port.output.UserLookupPort;
import com.beca.transacoes.core.transacao.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

    @Bean
    public AccountUseCase accountUseCase(AccountGatewayPort gateway){
        return new AccountService(gateway);
    }

    @Bean
    public TransactionValidationService transactionValidationService(
            AccountUseCase accountUseCase,
            TransactionRepositoryPort repository
    ){
        return new TransactionValidationService(accountUseCase, repository);
    }

    @Bean
    public WithdrawalUseCase withdrawalUseCase(
            TransactionRepositoryPort repository,
            TransactionValidationService validationService,
            AccountGatewayPort accountGateway
    ) {
        return new WithdrawalService(
                repository,
                validationService,
                accountGateway
        );
    }

    @Bean
    public TransferUseCase transferUseCase(
            TransactionRepositoryPort repository,
            TransactionValidationService validationService,
            UserLookupPort userLookupPort,
            AccountGatewayPort accountGateway) {

        return new TransferService(
                repository,
                validationService,
                userLookupPort,
                accountGateway
        );
    }

    @Bean
    public PurchaseUseCase purchaseUseCase(
            TransactionRepositoryPort repository,
            TransactionValidationService validationService,
            AccountGatewayPort accountGateway) {

        return new PurchaseService(
                repository,
                validationService,
                accountGateway
        );
    }


    @Bean
    public ListTransactionsUseCase listTransactionsUseCase(
            TransactionRepositoryPort repository) {

        return new ListTransactionsService(repository);
    }

    @Bean
    public ExpenseSummaryUseCase expenseSummaryUseCase(
            ListTransactionsUseCase listTransactionsUseCase) {

        return new ExpenseSummaryService(listTransactionsUseCase);
    }


}

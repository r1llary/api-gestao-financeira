
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.adapter.output.persistence.TransactionJpaRepository;
import com.beca.transacoes.adapter.output.persistence.TransactionPersistenceAdapter;
import com.beca.transacoes.config.AccountConfig;
import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.transacao.port.input.TransferUseCase;
import com.beca.transacoes.core.transacao.port.output.UserLookupPort;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({AccountConfig.class, TransactionPersistenceAdapter.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TransferIntegrationTest {

    @Autowired
    private TransferUseCase transferUseCase;

    @Autowired
    private TransactionJpaRepository repository;

    @MockitoBean
    private TransactionValidationService validationService;

    @MockitoBean
    private AccountGatewayPort accountGateway;

    @MockitoBean
    private UserLookupPort userLookupPort;

    @Test
    void shouldRollbackWhenDestinationUpdateFails() {

        // Arrange
        when(userLookupPort.userExists(5L, "Basic test"))
                .thenReturn(true);

        Account originAccount = mock(Account.class);
        Account destinationAccount = mock(Account.class);

        when(originAccount.balance())
                .thenReturn(new BigDecimal("1000"));

        when(destinationAccount.balance())
                .thenReturn(new BigDecimal("500"));

        when(accountGateway.findByUserId(6L))
                .thenReturn(originAccount);

        when(accountGateway.findByUserId(5L))
                .thenReturn(destinationAccount);

        // Simular falha no crédito ao destino
        doThrow(new RuntimeException("MockAPI error"))
                .when(accountGateway)
                .updateBalance(5L, new BigDecimal("550"));

        long countBefore = repository.count();

        // Act
        assertThrows(RuntimeException.class, () ->
                transferUseCase.transfer(
                        6L,
                        5L,
                        new BigDecimal("50"),
                        "Test transfer",
                        "Transfer",
                        "Basic test"
                )
        );

        // Assert
        long countAfter = repository.count();

        // A transação não deve permanecer no banco
        assertEquals(countBefore, countAfter);

        // Verificar desconto e tentativa de devolução
        verify(accountGateway).updateBalance(
                6L, new BigDecimal("950")
        );

        verify(accountGateway).updateBalance(
                6L, new BigDecimal("1000")
        );
    }
}

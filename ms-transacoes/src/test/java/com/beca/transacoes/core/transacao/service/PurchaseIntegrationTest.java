
package com.beca.transacoes.core.transacao.service;

import com.beca.transacoes.adapter.output.persistence.TransactionJpaRepository;
import com.beca.transacoes.adapter.output.persistence.TransactionPersistenceAdapter;
import com.beca.transacoes.config.AccountConfig;
import com.beca.transacoes.core.conta.model.Account;
import com.beca.transacoes.core.conta.port.output.AccountGatewayPort;
import com.beca.transacoes.core.transacao.port.input.PurchaseUseCase;
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
class PurchaseIntegrationTest {

    @Autowired
    private PurchaseUseCase purchaseUseCase;

    @Autowired
    private TransactionJpaRepository repository;

    @MockitoBean
    private TransactionValidationService validationService;

    @MockitoBean
    private AccountGatewayPort accountGateway;

    @MockitoBean
    private UserLookupPort userLookupPort;

    @Test
    void shouldRollbackWhenMockApiFails() {

        // Arrange
        Account account = mock(Account.class);

        when(accountGateway.findByUserId(6L))
                .thenReturn(account);

        when(account.balance())
                .thenReturn(new BigDecimal("1000"));

        doThrow(new RuntimeException("MockAPI unavailable"))
                .when(accountGateway)
                .updateBalance(6L, new BigDecimal("950"));

        long countBefore = repository.count();

        // Act
        assertThrows(RuntimeException.class, () ->
                purchaseUseCase.purchase(
                        6L,
                        new BigDecimal("50"),
                        "Grocery shopping",
                        "Food"
                )
        );

        // Assert
        long countAfter = repository.count();

        assertEquals(countBefore, countAfter);

        verify(accountGateway).updateBalance(
                6L,
                new BigDecimal("950")
        );
    }
}

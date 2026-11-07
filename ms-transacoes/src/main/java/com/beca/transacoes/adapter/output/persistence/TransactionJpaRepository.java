package com.beca.transacoes.adapter.output.persistence;

import com.beca.transacoes.core.transacao.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByUserIdAndStatusAndOccurredAtGreaterThanEqualAndOccurredAtLessThan(
            Long userId,
            TransactionStatus status,
            Instant start,
            Instant end
    );


    List<TransactionEntity> findByUserIdOrderByOccurredAtDesc(Long userId);

}

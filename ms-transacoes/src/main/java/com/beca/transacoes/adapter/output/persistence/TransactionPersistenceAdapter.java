package com.beca.transacoes.adapter.output.persistence;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.model.TransactionStatus;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository repository;

    public TransactionPersistenceAdapter(TransactionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transaction save(Transaction transaction){
        TransactionEntity entity = TransactionEntity.fromDomain(transaction);
        TransactionEntity savedEntity = repository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Transaction findById(Long id){
        TransactionEntity entity = repository.findById(id).orElse(null);

        if (entity == null){
            return null;
        }
        return entity.toDomain();
    }

    @Override
    public List<Transaction> findApprovedByUserIdAndPeriod(
            Long userId,
            Instant start,
            Instant end
    ) {
        List<TransactionEntity> entities =
                repository.findByUserIdAndStatusAndOccurredAtGreaterThanEqualAndOccurredAtLessThan(
                        userId,
                        TransactionStatus.APPROVED,
                        start,
                        end
                );

        List<Transaction> transactions = new ArrayList<>();

        for (TransactionEntity entity : entities) {
            transactions.add(entity.toDomain());
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {

        List<TransactionEntity> entities =
                repository.findByUserIdOrderByOccurredAtDesc(userId);

        List<Transaction> transactions = new ArrayList<>();

        for (TransactionEntity entity : entities) {
            transactions.add(entity.toDomain());
        }

        return transactions;
    }

}

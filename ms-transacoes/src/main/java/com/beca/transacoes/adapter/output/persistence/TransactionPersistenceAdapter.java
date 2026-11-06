package com.beca.transacoes.adapter.output.persistence;

import com.beca.transacoes.core.transacao.model.Transaction;
import com.beca.transacoes.core.transacao.port.output.TransactionRepositoryPort;
import org.springframework.stereotype.Repository;

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
}

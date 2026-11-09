package com.beca.transacoes.core.transacao.port.output;

public interface UserLookupPort {

    boolean userExists(Long id, String authorization);

}

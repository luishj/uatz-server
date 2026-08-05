package br.com.uatz.server.repository;

import br.com.uatz.model.StatusEntity;
import br.com.uatz.model.enumerador.StatusType;
import java.util.Optional;

public interface StatusRepository extends GenericRepository<StatusEntity, Long> {

    Optional<StatusEntity> findByTypeAndCode(StatusType type, String code);
}

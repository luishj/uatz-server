package br.com.uatz.server.repository.impl;

import br.com.uatz.model.StatusEntity;
import br.com.uatz.model.enumerador.StatusType;
import br.com.uatz.server.repository.StatusRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class StatusRepositoryImpl extends GenericRepositoryImpl<StatusEntity, Long> implements StatusRepository {

    @Override
    public Optional<StatusEntity> findByTypeAndCode(StatusType type, String code) {
        return find("type = ?1 and code = ?2", type, code).firstResultOptional();
    }
}

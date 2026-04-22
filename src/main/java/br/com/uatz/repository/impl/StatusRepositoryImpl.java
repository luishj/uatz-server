package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.StatusEntity;
import br.com.uatz.model.enums.StatusType;
import br.com.uatz.repository.StatusRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class StatusRepositoryImpl implements StatusRepository, PanacheRepositoryBase<StatusEntity, Long> {

    @Override
    public Optional<StatusEntity> findByTypeAndCode(StatusType type, String code) {
        return find("type = ?1 and code = ?2", type, code).firstResultOptional();
    }
}

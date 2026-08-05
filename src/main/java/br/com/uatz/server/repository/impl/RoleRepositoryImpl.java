package br.com.uatz.server.repository.impl;

import br.com.uatz.model.RoleEntity;
import br.com.uatz.server.repository.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class RoleRepositoryImpl extends GenericRepositoryImpl<RoleEntity, Long> implements RoleRepository {

    @Override
    public Optional<RoleEntity> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }
}

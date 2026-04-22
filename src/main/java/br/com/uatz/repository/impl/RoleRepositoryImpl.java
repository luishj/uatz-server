package br.com.uatz.repository.impl;

import br.com.uatz.model.entity.RoleEntity;
import br.com.uatz.repository.RoleRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class RoleRepositoryImpl implements RoleRepository, PanacheRepositoryBase<RoleEntity, Long> {

    @Override
    public Optional<RoleEntity> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }
}

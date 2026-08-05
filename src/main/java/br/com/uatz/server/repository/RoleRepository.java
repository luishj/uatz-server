package br.com.uatz.server.repository;

import br.com.uatz.model.RoleEntity;
import java.util.Optional;

public interface RoleRepository extends GenericRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByCode(String code);
}

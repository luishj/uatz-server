package br.com.uatz.repository;

import br.com.uatz.model.entity.RoleEntity;
import java.util.Optional;

public interface RoleRepository {

    Optional<RoleEntity> findByCode(String code);
}

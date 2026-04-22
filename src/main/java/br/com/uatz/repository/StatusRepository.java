package br.com.uatz.repository;

import br.com.uatz.model.entity.StatusEntity;
import br.com.uatz.model.enums.StatusType;
import java.util.Optional;

public interface StatusRepository {

    Optional<StatusEntity> findByTypeAndCode(StatusType type, String code);
}

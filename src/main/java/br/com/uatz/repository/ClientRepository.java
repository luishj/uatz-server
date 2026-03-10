package br.com.uatz.repository;

import br.com.uatz.model.entity.Client;
import java.util.Optional;

public interface ClientRepository {

    Client save(Client client);

    Optional<Client> findOptionalById(Long id);

    Optional<Client> findByPhone(String phone);
}

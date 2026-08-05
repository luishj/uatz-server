package br.com.uatz.server.repository;

import br.com.uatz.model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends GenericRepository<Client, Long> {

    Client save(Client client);

    List<Client> listAllClients();

    Optional<Client> findOptionalById(Long id);

    Optional<Client> findByPhone(String phone);
}

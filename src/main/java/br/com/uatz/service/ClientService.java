package br.com.uatz.service;

import br.com.uatz.model.entity.Client;
import java.util.Optional;

public interface ClientService {

    Client save(Client client);

    Optional<Client> findById(Long id);

    Optional<Client> findByPhone(String phone);
}


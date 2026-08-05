package br.com.uatz.server.repository.impl;

import java.util.List;
import java.util.Optional;

import br.com.uatz.model.Client;
import br.com.uatz.server.repository.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ClientRepositoryImpl extends GenericRepositoryImpl<Client, Long> implements ClientRepository {

    @Override
    @Transactional
    public Client save(Client client) {
        persist(client);
        return client;
    }

    @Override
    public List<Client> listAllClients() {
        return listAll();
    }

    @Override
    public Optional<Client> findOptionalById(Long id) {
        return findByIdOptional(id);
    }

    @Override
    public Optional<Client> findByPhone(String phone) {
        return find("phone", phone).firstResultOptional();
    }
}

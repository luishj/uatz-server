package br.com.uatz.repository.impl;

import java.util.List;
import java.util.Optional;

import br.com.uatz.model.entity.Client;
import br.com.uatz.repository.ClientRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ClientRepositoryImpl implements ClientRepository, PanacheRepositoryBase<Client, Long> {

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

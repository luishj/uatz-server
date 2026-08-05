package br.com.uatz.server.service.impl;

import br.com.uatz.model.Client;
import br.com.uatz.server.repository.ClientRepository;
import br.com.uatz.server.service.ClientService;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.listAllClients();
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findOptionalById(id);
    }

    @Override
    public Optional<Client> findByPhone(String phone) {
        return clientRepository.findByPhone(phone);
    }
}

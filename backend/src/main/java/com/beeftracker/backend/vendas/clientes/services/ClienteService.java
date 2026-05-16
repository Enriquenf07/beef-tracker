package com.beeftracker.backend.vendas.clientes.services;

import java.util.Comparator;
import java.util.List;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.beeftracker.backend.vendas.clientes.repositories.ClienteRepository;
import com.beeftracker.backend.vendas.clientes.models.Cliente;
import com.beeftracker.backend.vendas.clientes.models.ClienteData;

@Service
public class ClienteService {
    public final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> pesquisar(String chave, Boolean status) {
        List<Cliente> cliente = (List<Cliente>) clienteRepository.pesquisar(chave, status);
        return cliente.stream().sorted(Comparator.comparingLong(f -> f.metadata().id())).toList();
    }

    public Long cadastrar(ClienteData clienteData) throws InvalidFormException {
        clienteData.validate();
        return clienteRepository.salvar(clienteData);
    }

    public void atualizar(Long id, ClienteData clienteData) throws ResourceNotFoundException, InvalidFormException {
        findById(id);
        clienteData.validate();
        clienteRepository.atualizar(id, clienteData);
    }

    public void atualizarStatus(Long id) throws ResourceNotFoundException {
        Cliente cliente = findById(id);
        clienteRepository.atualizarStatus(id, !cliente.data().ativo());
    }

    public Cliente findById(Long id) throws ResourceNotFoundException {
        return clienteRepository.buscarPorId(id).orElseThrow(() -> new ResourceNotFoundException());
    }
}

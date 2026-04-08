package com.beeftracker.backend.veiculos.services;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.veiculos.models.Veiculo;
import com.beeftracker.backend.veiculos.models.VeiculoData;
import com.beeftracker.backend.veiculos.repositories.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    /**
     * Cadastra um novo veículo retornando o ID gerado.
     * Utiliza o método 'salvar' da implementação JDBC customizada.
     */
    @Transactional
    public Long cadastrar(VeiculoData data) throws InvalidFormException {
        // 1. Validação dos campos obrigatórios (placa, modelo, capacidadeCarga)
        data.validate();

        // 2. Validação de duplicidade de placa usando a query do Repository
        if (veiculoRepository.findByPlaca(data.placa()).isPresent()) {
            throw new RuntimeException("Veículo com esta placa já cadastrado.");
        }

        // 3. Executa o INSERT manual via VeiculoCustomRepositoryImpl
        // Isso evita o erro de mapeamento das colunas 'capacidade_carga' e 'ativo'
        return veiculoRepository.salvar(data);
    }

    /**
     * Lista veículos aplicando filtros de busca (placa/modelo) e status.
     */
    public List<Veiculo> listar(String chave, Boolean status) {
        // Chama o método 'pesquisar' definido com @Query nativa no Repository
        return veiculoRepository.pesquisar(chave, status);
    }

    /**
     * Retorna todos os veículos sem nenhum filtro aplicado.
     */
    public List<Veiculo> listarTodos() {
        // Passar null garante que o WHERE na Query ignore os critérios de busca
        return veiculoRepository.pesquisar(null, null);
    }
}
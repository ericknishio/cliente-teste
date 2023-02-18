package br.com.teste.cliente.service;

import br.com.teste.cliente.entity.Endereco;
import br.com.teste.cliente.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    public Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public List<Endereco> listarEnderecos() {
        return enderecoRepository.findAll();
    }

    public List<Endereco> listarPorCliente(Long id) {
        return enderecoRepository.findByCliente(id);
    }

    public Optional<Endereco> buscarPorId(Long id) {
        return enderecoRepository.findById(id);
    }

    public void removerPorId(Long id) {
        enderecoRepository.deleteById(id);
    }
}

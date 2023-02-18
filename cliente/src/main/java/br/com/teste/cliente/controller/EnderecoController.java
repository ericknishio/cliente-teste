package br.com.teste.cliente.controller;

import br.com.teste.cliente.entity.Cliente;
import br.com.teste.cliente.entity.Endereco;
import br.com.teste.cliente.service.ClienteService;
import br.com.teste.cliente.service.EnderecoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/endereco")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Endpoint para criar um endereco
     * @param endereco
     * @return endereco criado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Endereco criarEndereco(@RequestBody Endereco endereco) {
        Cliente cliente = clienteService.buscarPorId(endereco.getCliente().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        endereco.setCliente(cliente);
        return enderecoService.salvar(endereco);
    }

    /**
     * Endpoint para alterar endereço
     * Pode ser usado para alterar campos do endereço
     * bem como a flag de endereço principal
     * @param id: id do endereco
     * @param endereco: campo(s) do endereço
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarEndereco(@PathVariable(name = "id") Long id, @RequestBody Endereco endereco) {
        enderecoService.buscarPorId(id)
                .map(enderecoDb -> {
                    modelMapper.map(endereco, enderecoDb);
                    enderecoService.salvar(enderecoDb);
                    return Void.TYPE;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereco não encontrado"));
    }

    /**
     * Endpoint para listar endereços de um determinado cliente
     * @param id: id do cliente
     * @return lista de endereco
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<Endereco> listarEnderecosPorCliente(@PathVariable(name = "id") Long id) {
        return enderecoService.listarPorCliente(id);
    }

}

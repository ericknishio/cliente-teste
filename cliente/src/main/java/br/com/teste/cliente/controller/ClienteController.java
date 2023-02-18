package br.com.teste.cliente.controller;

import br.com.teste.cliente.entity.Cliente;
import br.com.teste.cliente.service.ClienteService;
import br.com.teste.cliente.service.EnderecoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;
    @Autowired
    private EnderecoService enderecoService;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Endpoint para criar um cliente
     * @param cliente
     * @return cliente criado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente criarCliente(@RequestBody Cliente cliente) {
        Cliente clienteDb = clienteService.salvar(cliente);
        if (!CollectionUtils.isEmpty(cliente.getEnderecos())) {
            cliente.getEnderecos().forEach(e -> {
                e.setCliente(clienteDb);
                enderecoService.salvar(e);
            });
        }
        return clienteDb;
    }

    /**
     * Endpoint para alterar cliente
     * Pode ser usado para alterar campos do cliente
     * @param id: id do cliente
     * @param cliente: campo(s) do cliente
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarCliente(@PathVariable(name = "id") Long id, @RequestBody Cliente cliente) {
        clienteService.buscarPorId(id)
                .map(clienteDb -> {
                    modelMapper.map(cliente, clienteDb);
                    clienteService.salvar(clienteDb);
                    return Void.TYPE;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    /**
     * Endpoint para listar clientes
     * @return lista de cliente
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }

    /**
     * Endpoint para buscar um determinado cliente
     * @param id: id do cliente
     * @return cliente
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Cliente buscarClientePorId(@PathVariable(name = "id") Long id) {
        return clienteService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }
}

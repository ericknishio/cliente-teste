package br.com.teste.cliente.controller;

import br.com.teste.cliente.ClienteApplication;
import br.com.teste.cliente.entity.Cliente;
import br.com.teste.cliente.entity.Endereco;
import br.com.teste.cliente.service.ClienteService;
import br.com.teste.cliente.service.EnderecoService;
import br.com.teste.cliente.util.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ClienteApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@ActiveProfiles("test")
public class EnderecoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private EnderecoService enderecoService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @AfterEach
    public void afterEach() {
        enderecoService.deleteAll();
        clienteService.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        jdbcTemplate.execute("TRUNCATE TABLE endereco");
        jdbcTemplate.execute("ALTER SEQUENCE endereco_id_seq RESTART");
        jdbcTemplate.execute("TRUNCATE TABLE cliente");
        jdbcTemplate.execute("ALTER SEQUENCE cliente_id_seq RESTART");
    }

    @Test
    public void givenEndereco_whenPostEndereco_thenStatus201() throws Exception {
        Cliente cliente = createCliente("Nome 1", "email@teste.com", "55794825820", Calendar.getInstance(), true);
        Endereco endereco = createEndereco("Rua Ana Faco", "265", "60335430", "Fortaleza", true, cliente, false);
        String enderecoParaEnvio = Util.objectToJson(endereco);

        mockMvc.perform(post("/endereco")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enderecoParaEnvio))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.logradouro").value(endereco.getLogradouro()));
    }

    @Test
    public void givenEndereco_whenPutEndereco_thenStatus204() throws Exception {
        Cliente cliente = createCliente("Nome 1", "email@teste.com", "55794825820", Calendar.getInstance(), true);
        Endereco endereco = createEndereco("Rua Ana Faco", "265", "60335430", "Fortaleza", true, cliente, true);
        endereco.setPrincipal(false);
        String enderecoParaEnvio = Util.objectToJson(endereco);

        mockMvc.perform(put("/endereco/{id}", endereco.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enderecoParaEnvio))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenEndereco_whenPutEndereco_thenStatus404() throws Exception {
        Cliente cliente = createCliente("Nome 1", "email@teste.com", "55794825820", Calendar.getInstance(), true);
        Endereco endereco = createEndereco("Rua Ana Faco", "265", "60335430", "Fortaleza", true, cliente, true);
        endereco.setPrincipal(false);
        String enderecoParaEnvio = Util.objectToJson(endereco);

        mockMvc.perform(put("/endereco/{id}", endereco.getId() + 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enderecoParaEnvio))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenEnderecos_whenGetEnderecosByCliente_thenStatus200() throws Exception {
        Cliente cliente = createCliente("Nome 1", "email@teste.com", "55794825820", Calendar.getInstance(), true);
        createEndereco("Rua Ana Faco", "265", "60335430", "Fortaleza", true, cliente, true);
        createEndereco("Rua Parnaíba", "473", "40436-790", "Salvador", true, cliente, true);
        createEndereco("Rua Sena Madureira", "764", "76914840", "Ji-Paraná", true, cliente, true);

        MvcResult mvcResult = mockMvc.perform(get("/endereco/{id}", cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        Endereco[] enderecos = Util.jsonToObject(mvcResult.getResponse().getContentAsString(), Endereco[].class);
        Assert.assertTrue(enderecos.length > 0);
    }

    @Test
    public void givenEnderecos_whenGetEnderecosByCliente_thenStatus200_size0() throws Exception {
        Cliente cliente = createCliente("Nome 1", "email@teste.com", "55794825820", Calendar.getInstance(), true);
        createEndereco("Rua Ana Faco", "265", "60335430", "Fortaleza", true, cliente, true);
        createEndereco("Rua Parnaíba", "473", "40436-790", "Salvador", true, cliente, true);
        createEndereco("Rua Sena Madureira", "764", "76914840", "Ji-Paraná", true, cliente, true);

        MvcResult mvcResult = mockMvc.perform(get("/endereco/{id}", cliente.getId() + 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        Endereco[] enderecos = Util.jsonToObject(mvcResult.getResponse().getContentAsString(), Endereco[].class);
        Assert.assertTrue(enderecos.length == 0);
    }

    private Cliente createCliente(String nome, String email, String cpf, Calendar dataNascimento, boolean persist) {
        Cliente cliente = new Cliente(nome, email, cpf, dataNascimento);
        if (persist) {
            return clienteService.salvar(cliente);
        } else {
            return cliente;
        }
    }

    private Endereco createEndereco(String logradouro, String numero, String cep, String cidade, boolean principal, Cliente cliente, boolean persist) {
        Endereco endereco = new Endereco(logradouro, numero, cep, cidade, principal, cliente);
        if (persist) {
            return enderecoService.salvar(endereco);
        } else {
            return endereco;
        }
    }

}

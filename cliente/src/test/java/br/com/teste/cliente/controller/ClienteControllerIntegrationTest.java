package br.com.teste.cliente.controller;

import br.com.teste.cliente.ClienteApplication;
import br.com.teste.cliente.entity.Cliente;
import br.com.teste.cliente.service.ClienteService;
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

import javax.ws.rs.core.Response;
import java.util.Calendar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ClienteApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@ActiveProfiles("test")
public class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @AfterEach
    public void afterEach() {
        System.out.println("After Each cleanUpEach() method called");
        clienteService.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        System.out.println("After All cleanUp() method called");
        jdbcTemplate.execute("TRUNCATE TABLE cliente");
        jdbcTemplate.execute("ALTER SEQUENCE cliente_id_seq RESTART");
    }

    @Test
    public void givenCliente_whenPostCliente_thenStatus200() throws Exception {
        Cliente cliente = createCliente("Nome 1", "email@teste.com", "55794825820", Calendar.getInstance(), false);
        String clienteParaEnvio = Util.objectToJson(cliente);

        mockMvc.perform(post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteParaEnvio))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.nome").value("Nome 1"));
    }

    @Test
    public void givenCliente_whenGetClienteById_thenStatus200() throws Exception {
        Cliente cliente = createCliente("Nome 1", "email@teste.com", "55794825820", Calendar.getInstance(), true);

        mockMvc.perform(get("/cliente/{id}", cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.nome").value("Nome 1"));
    }

    @Test
    public void givenCliente_whenGetClienteById_thenStatus404() throws Exception {
        Cliente cliente = createCliente("Nome 1", "email@teste.com", "55794825820", Calendar.getInstance(), true);

        mockMvc.perform(get("/cliente/{id}", cliente.getId() + 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenClientes_whenGetClientes_thenStatus200() throws Exception {
        createCliente("Nome 1", "email@teste.com", "55794825820", Calendar.getInstance(), true);
        createCliente("Nome 2", "email@teste.com", "84837746314", Calendar.getInstance(), true);
        createCliente("Nome 3", "email@teste.com", "87999603546", Calendar.getInstance(), true);

        mockMvc.perform(get("/cliente")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        MvcResult mvcResult = mockMvc.perform(get("/cliente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Cliente[] clientes = Util.jsonToObject(mvcResult.getResponse().getContentAsString(), Cliente[].class);
        Assert.assertTrue(clientes.length > 0);
    }

    private Cliente createCliente(String nome, String email, String cpf, Calendar dataNascimento, boolean persist) {
        Cliente cliente = new Cliente(nome, email, cpf, dataNascimento);
        if (persist) {
            return clienteService.salvar(cliente);
        } else {
            return cliente;
        }
    }

}

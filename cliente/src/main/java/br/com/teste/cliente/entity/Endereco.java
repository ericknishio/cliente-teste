package br.com.teste.cliente.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "endereco")
public class Endereco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enderecpId")
    @SequenceGenerator(name = "enderecoId", sequenceName = "endereco_id_seq")
    private Long id;

    @Column(name = "logradouro", nullable = false)
    private String logradouro;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "cep", nullable = false)
    private String cep;

    @Column(name = "cidade", nullable = false)
    private String cidade;

    @Column(name = "principal", columnDefinition = "boolean default false")
    private Boolean principal = false;

    @JoinColumn(name = "_cliente", foreignKey = @ForeignKey(name = "fk_cliente"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Cliente cliente;

    public Endereco(String logradouro, String numero, String cep, String cidade, Boolean principal, Cliente cliente) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.cep = cep;
        this.cidade = cidade;
        this.principal = principal;
        this.cliente = cliente;
    }
}

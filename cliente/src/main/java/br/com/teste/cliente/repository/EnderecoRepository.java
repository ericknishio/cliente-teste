package br.com.teste.cliente.repository;

import br.com.teste.cliente.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    @Query(value = "SELECT ed FROM endereco ed "
            + "JOIN FETCH cliente c "
            + "WHERE c.id = :id ", nativeQuery = false)
    List<Endereco> findByCliente(@Param(value = "id") Long id);

}

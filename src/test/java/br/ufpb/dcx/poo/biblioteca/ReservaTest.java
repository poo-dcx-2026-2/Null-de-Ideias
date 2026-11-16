package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReservaTest {

    Biblioteca biblioteca;

    @BeforeEach
    void preparaReserva() throws Exception{
        biblioteca = Fabrica.novaBiblioteca();

        biblioteca.acervo().cadastrarItem("L1","Matematica discrete","Diogo M.","Ensino",2000);
        biblioteca.acervo().adicionarExemplar("L1","T001");

        biblioteca.usuarios().cadastrarUsuario( "U001", "Maria");
        biblioteca.usuarios().cadastrarUsuario("U002", "João");
        biblioteca.usuarios().cadastrarUsuario("U003", "Pedro");


    }
    @Test
    @DisplayName("Deve permitir reservar item sem exemplar disponível")
    void resevarExemplarDisponivel() throws Exception{
        biblioteca.emprestimos().emprestar("T001", "U001", LocalDate.of(2026, 11, 16));

        String idReserva = biblioteca.emprestimos().reservar("L1", "U002", LocalDate.of(2026, 11, 17));

        assertNotNull(idReserva);
        assertNotNull(idReserva.isBlank());

        List<ReservaView> reservas = biblioteca.emprestimos().listarReservas("L1");

        assertEquals(1,reservas.size());
        assertEquals(idReserva,reservas.get(0).id());
        assertEquals("U002",reservas.get(0).matricula());
        assertEquals(1,reservas.get(0).posicaoNaFila());





    }


}

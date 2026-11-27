package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.*;
import br.ufpb.dcx.poo.biblioteca.inicial.Emprestimo;
import br.ufpb.dcx.poo.biblioteca.inicial.Exemplar;
import br.ufpb.dcx.poo.biblioteca.inicial.Item;
import br.ufpb.dcx.poo.biblioteca.inicial.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDominioTest {

    private static final LocalDate DIA = LocalDate.of(2026, 11, 2);

    private Item item;
    private Usuario ana;

    @BeforeEach
    void criarCenario() {
        item = new Item("F1", "Matrix", "Wachowski", "ficcao", 1999);
        for (String tombo : new String[]{"T1", "T2", "T3", "T4"}) {
            item.adicionarExemplar(new Exemplar(tombo, item));
        }
        ana = new Usuario("U1", "Ana");
    }

    @Test
    @DisplayName("emprestar cria um Emprestimo ativo e marca o exemplar como EMPRESTADO")
    void emprestarCriaEmprestimo() throws Exception {
        Emprestimo emprestimo = ana.emprestar("E1", item.buscarExemplar("T1"), DIA);

        assertTrue(emprestimo.estaAtivo());
        assertEquals(1, ana.emprestimosAtivos());
        assertEquals(StatusExemplar.EMPRESTADO, item.buscarExemplar("T1").getStatus());
    }

    @Test
    @DisplayName("usuário inativo não empresta e o exemplar não muda")
    void inativoNaoEmpresta() {
        ana.setAtivo(false);

        assertThrows(UsuarioInativoException.class,
                () -> ana.emprestar("E1", item.buscarExemplar("T1"), DIA));
        assertEquals(StatusExemplar.DISPONIVEL, item.buscarExemplar("T1").getStatus());
        assertEquals(0, ana.emprestimosAtivos());
    }

    @Test
    @DisplayName("o usuário recusa o empréstimo além do limite sem mexer no exemplar")
    void limiteDeEmprestimos() throws Exception {
        ana.emprestar("E1", item.buscarExemplar("T1"), DIA);
        ana.emprestar("E2", item.buscarExemplar("T2"), DIA);
        ana.emprestar("E3", item.buscarExemplar("T3"), DIA);

        assertThrows(LimiteDeEmprestimosExcedidoException.class,
                () -> ana.emprestar("E4", item.buscarExemplar("T4"), DIA));
        assertEquals(StatusExemplar.DISPONIVEL, item.buscarExemplar("T4").getStatus());
        assertEquals(3, ana.emprestimosAtivos());
    }

    @Test
    @DisplayName("exemplar já emprestado não pode ser emprestado de novo")
    void exemplarEmprestadoRecusado() throws Exception {
        Usuario bruno = new Usuario("U2", "Bruno");
        ana.emprestar("E1", item.buscarExemplar("T1"), DIA);

        assertThrows(ExemplarIndisponivelException.class,
                () -> bruno.emprestar("E2", item.buscarExemplar("T1"), DIA));
    }

    @Test
    @DisplayName("devolver registra a data, libera o exemplar e deixa de contar como ativo")
    void devolver() throws Exception {
        Emprestimo emprestimo = ana.emprestar("E1", item.buscarExemplar("T1"), DIA);

        ana.devolver("T1", DIA.plusDays(2));

        assertFalse(emprestimo.estaAtivo());
        assertEquals(StatusExemplar.DISPONIVEL, item.buscarExemplar("T1").getStatus());
        assertEquals(0, ana.emprestimosAtivos());
    }

    @Test
    @DisplayName("devolver tombo sem empréstimo ativo lança RecursoNaoEncontradoException")
    void devolverSemEmprestimo() {
        assertThrows(RecursoNaoEncontradoException.class, () -> ana.devolver("T1", DIA));
    }

    @Test
    @DisplayName("devolver compara o tombo com equals, não com ==")
    void devolverUsaEquals() throws Exception {
        ana.emprestar("E1", item.buscarExemplar("T1"), DIA);

        assertDoesNotThrow(() -> ana.devolver(new String("T1"), DIA.plusDays(1)));
        assertEquals(0, ana.emprestimosAtivos());
    }

    @Test
    @DisplayName("a lista de empréstimos devolvida pelo Usuario não pode ser alterada de fora")
    void listaDeEmprestimosImutavel() throws Exception {
        ana.emprestar("E1", item.buscarExemplar("T1"), DIA);

        assertThrows(UnsupportedOperationException.class, () -> ana.getEmprestimos().clear());
        assertEquals(1, ana.emprestimosAtivos());
    }
}

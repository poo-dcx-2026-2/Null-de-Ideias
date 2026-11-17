package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.inicial.Emprestimo;
import br.ufpb.dcx.poo.biblioteca.inicial.Exemplar;
import br.ufpb.dcx.poo.biblioteca.inicial.Item;
import br.ufpb.dcx.poo.biblioteca.inicial.Reserva;
import br.ufpb.dcx.poo.biblioteca.inicial.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegressaoDasRefatoracoesTest {

    private static final LocalDate DIA = LocalDate.of(2026, 11, 2);

    private Biblioteca biblioteca;

    @TempDir
    Path pasta;

    @BeforeEach
    void criarCenario() throws Exception {
        biblioteca = Fabrica.novaBiblioteca();
        biblioteca.acervo().cadastrarItem("F1", "Matrix", "Wachowski", "ficcao", 1999);
        biblioteca.usuarios().cadastrarUsuario("U1", "Ana");
        biblioteca.usuarios().cadastrarUsuario("U2", "Bruno");
        biblioteca.usuarios().cadastrarUsuario("U3", "Carla");
    }

    private void adicionarExemplares(String codigoDoItem, String... tombos) throws Exception {
        for (String tombo : tombos) {
            biblioteca.acervo().adicionarExemplar(codigoDoItem, tombo);
        }
    }

    private StatusExemplar statusDe(String codigoDoItem, String tombo) throws Exception {
        for (ExemplarView exemplar : biblioteca.acervo().listarExemplares(codigoDoItem)) {
            if (exemplar.tombo().equals(tombo)) {
                return exemplar.status();
            }
        }
        throw new AssertionError("Tombo não encontrado no item " + codigoDoItem + ": " + tombo);
    }

    @AfterEach
    void limparPropriedade() {
        System.clearProperty("biblioteca.dados");
    }


    @Test
    @DisplayName("emprestar funciona com tombo e matrícula montados em tempo de execução")
    void emprestarComTomboEMatriculaMontados() throws Exception {
        adicionarExemplares("F1", "T1");

        assertDoesNotThrow(() ->
                biblioteca.emprestimos().emprestar(new String("T1"), new String("U1"), DIA));
        assertEquals(StatusExemplar.EMPRESTADO, statusDe("F1", "T1"));
    }

    @Test
    @DisplayName("devolver funciona com tombo montado em tempo de execução")
    void devolverComTomboMontado() throws Exception {
        adicionarExemplares("F1", "T1");
        biblioteca.emprestimos().emprestar("T1", "U1", DIA);

        assertDoesNotThrow(() -> biblioteca.emprestimos().devolver(new String("T1"), DIA.plusDays(1)));
        assertEquals(StatusExemplar.DISPONIVEL, statusDe("F1", "T1"));
    }

    @Test
    @DisplayName("reservar e listarReservas funcionam com código de item montado em tempo de execução")
    void reservarComCodigoMontado() throws Exception {
        adicionarExemplares("F1", "T1");
        biblioteca.emprestimos().emprestar("T1", "U1", DIA);

        assertDoesNotThrow(() -> biblioteca.emprestimos().reservar(new String("F1"), new String("U2"), DIA));
        assertEquals(1, biblioteca.emprestimos().listarReservas(new String("F1")).size());
    }

    @Test
    @DisplayName("cancelarReserva funciona com id montado em tempo de execução")
    void cancelarReservaComIdMontado() throws Exception {
        adicionarExemplares("F1", "T1");
        biblioteca.emprestimos().emprestar("T1", "U1", DIA);
        String id = biblioteca.emprestimos().reservar("F1", "U2", DIA);

        assertDoesNotThrow(() -> biblioteca.emprestimos().cancelarReserva(new String(id)));
        assertTrue(biblioteca.emprestimos().listarReservas("F1").isEmpty());
    }

    @Test
    @DisplayName("baixarExemplar, desativarUsuario e reativarUsuario funcionam com texto montado em tempo de execução")
    void baixarEDesativarComTextoMontado() throws Exception {
        adicionarExemplares("F1", "T1");

        assertDoesNotThrow(() -> biblioteca.acervo().baixarExemplar(new String("T1")));
        assertDoesNotThrow(() -> biblioteca.usuarios().desativarUsuario(new String("U1")));
        assertDoesNotThrow(() -> biblioteca.usuarios().reativarUsuario(new String("U1")));
        assertTrue(biblioteca.usuarios().buscarUsuario("U1").ativo());
    }


    @Test
    @DisplayName("alterar a lista devolvida por listarEmprestimosDoUsuario não altera o estado do sistema")
    void listaDeEmprestimosNaoVaza() throws Exception {
        adicionarExemplares("F1", "T1");
        biblioteca.emprestimos().emprestar("T1", "U1", DIA);

        List<EmprestimoView> lista = biblioteca.emprestimos().listarEmprestimosDoUsuario("U1");
        try {
            lista.clear();
        } catch (UnsupportedOperationException aceitavel) {
            // lista imutável também é uma resposta correta
        }

        assertEquals(1, biblioteca.emprestimos().listarEmprestimosDoUsuario("U1").size());
        assertEquals(1, biblioteca.usuarios().buscarUsuario("U1").emprestimosAtivos());
    }

    @Test
    @DisplayName("alterar a lista devolvida por listarReservas não altera a fila")
    void listaDeReservasNaoVaza() throws Exception {
        adicionarExemplares("F1", "T1");
        biblioteca.emprestimos().emprestar("T1", "U1", DIA);
        biblioteca.emprestimos().reservar("F1", "U2", DIA);

        try {
            biblioteca.emprestimos().listarReservas("F1").clear();
        } catch (UnsupportedOperationException aceitavel) {
            // lista imutável também é uma resposta correta
        }

        assertEquals(1, biblioteca.emprestimos().listarReservas("F1").size());
    }


    private void assertIdentidadeFinalESemSetter(Class<?> classe, String campo, String setter)
            throws Exception {
        Field field = classe.getDeclaredField(campo);
        assertTrue(Modifier.isFinal(field.getModifiers()),
                classe.getSimpleName() + "." + campo + " deveria ser final");
        for (Method metodo : classe.getDeclaredMethods()) {
            assertNotEquals(setter, metodo.getName(),
                    classe.getSimpleName() + " não deveria ter " + setter + "()");
        }
    }

    @Test
    @DisplayName("o código do Item é final e não tem setter")
    void identidadeDoItem() throws Exception {
        assertIdentidadeFinalESemSetter(Item.class, "codigo", "setCodigo");
    }

    @Test
    @DisplayName("a matrícula do Usuario é final e não tem setter")
    void identidadeDoUsuario() throws Exception {
        assertIdentidadeFinalESemSetter(Usuario.class, "matricula", "setMatricula");
    }

    @Test
    @DisplayName("o tombo do Exemplar é final e não tem setter")
    void identidadeDoExemplar() throws Exception {
        assertIdentidadeFinalESemSetter(Exemplar.class, "tombo", "setTombo");
    }

    @Test
    @DisplayName("o id do Emprestimo é final e não tem setter")
    void identidadeDoEmprestimo() throws Exception {
        assertIdentidadeFinalESemSetter(Emprestimo.class, "id", "setId");
    }

    @Test
    @DisplayName("o id da Reserva é final e não tem setter")
    void identidadeDaReserva() throws Exception {
        assertIdentidadeFinalESemSetter(Reserva.class, "id", "setId");
    }

    // ---------- Fabrica: instância nova, vazia e independente ----------

    @Test
    @DisplayName("empréstimos e reservas de uma biblioteca não aparecem em outra criada pela Fabrica")
    void bibliotecasIndependentes() throws Exception {
        adicionarExemplares("F1", "T1");
        biblioteca.emprestimos().emprestar("T1", "U1", DIA);
        biblioteca.emprestimos().reservar("F1", "U2", DIA);

        Biblioteca outra = Fabrica.novaBiblioteca();

        assertTrue(outra.acervo().listarItens().isEmpty());
        assertTrue(outra.usuarios().listarUsuarios().isEmpty());
        outra.acervo().cadastrarItem("F1", "Matrix", "Wachowski", "ficcao", 1999);
        assertTrue(outra.emprestimos().listarReservas("F1").isEmpty());
    }

    @Test
    @DisplayName("Fabrica.novaBiblioteca não carrega nada do disco sozinha")
    void fabricaNaoCarregaSozinha() throws Exception {
        System.setProperty("biblioteca.dados", pasta.toString());
        adicionarExemplares("F1", "T1");
        biblioteca.salvar();

        Biblioteca nova = Fabrica.novaBiblioteca();

        assertTrue(nova.acervo().listarItens().isEmpty());
        assertTrue(nova.usuarios().listarUsuarios().isEmpty());
    }
}
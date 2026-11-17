package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusEmprestimo;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.inicial.Emprestimo;
import br.ufpb.dcx.poo.biblioteca.inicial.Exemplar;
import br.ufpb.dcx.poo.biblioteca.inicial.Item;
import br.ufpb.dcx.poo.biblioteca.inicial.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class EmprestimoDominioTest {

    private static final LocalDate DIA = LocalDate.of(2026, 11, 2);

    private Usuario usuario;
    private Exemplar exemplar;
    private Emprestimo emprestimo;

    @BeforeEach
    void criarEmprestimo() throws Exception {
        Item item = new Item("F1", "Matrix", "Wachowski", "ficcao", 1999);
        exemplar = new Exemplar("T1", item);
        item.adicionarExemplar(exemplar);
        usuario = new Usuario("U1", "Ana");
        emprestimo = usuario.emprestar("E1", exemplar, DIA);
    }

    @Test
    @DisplayName("emprestimo novo nasce ativo, sem data de devolução, com identidade e vínculos")
    void nasceAtivo() {
        assertTrue(emprestimo.estaAtivo());
        assertNull(emprestimo.getDataDaDevolucao());
        assertEquals("E1", emprestimo.getId());
        assertEquals(DIA, emprestimo.getDataDoEmprestimo());
        assertEquals(usuario, emprestimo.getUsuario());
        assertEquals(exemplar, emprestimo.getExemplar());
    }

    @Test
    @DisplayName("a data prevista é a data do empréstimo mais PRAZO_EM_DIAS")
    void dataPrevistaSegueOPrazo() {
        assertEquals(DIA.plusDays(EmprestimoService.PRAZO_EM_DIAS), emprestimo.getDataPrevista());
        assertEquals(LocalDate.of(2026, 11, 16), emprestimo.getDataPrevista());
    }

    @Test
    @DisplayName("antes e no próprio dia previsto não há atraso")
    void semAtrasoAteODiaPrevisto() {
        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 15)));
        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 16)));
        assertEquals(0, emprestimo.diasDeAtrasoEm(LocalDate.of(2026, 11, 16)));
    }

    @Test
    @DisplayName("um dia depois da data prevista já há atraso de 1 dia")
    void atrasoComecaNoDiaSeguinte() {
        assertTrue(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 17)));
        assertEquals(1, emprestimo.diasDeAtrasoEm(LocalDate.of(2026, 11, 17)));
        assertEquals(10, emprestimo.diasDeAtrasoEm(LocalDate.of(2026, 11, 26)));
    }

    @Test
    @DisplayName("registrarDevolucao guarda a data e deixa o empréstimo inativo")
    void registrarDevolucao() {
        emprestimo.registrarDevolucao(LocalDate.of(2026, 11, 5));

        assertFalse(emprestimo.estaAtivo());
        assertEquals(LocalDate.of(2026, 11, 5), emprestimo.getDataDaDevolucao());
    }

    @Test
    @DisplayName("devolver no mesmo dia do empréstimo é aceito")
    void devolverNoMesmoDia() {
        assertDoesNotThrow(() -> emprestimo.registrarDevolucao(DIA));
    }

    @Test
    @DisplayName("devolução anterior ao empréstimo lança DadosInvalidosException e não altera o empréstimo")
    void devolucaoAntesDoEmprestimo() {
        assertThrows(DadosInvalidosException.class,
                () -> emprestimo.registrarDevolucao(DIA.minusDays(1)));

        assertTrue(emprestimo.estaAtivo());
        assertNull(emprestimo.getDataDaDevolucao());
    }

    @Test
    @DisplayName("empréstimo devolvido nunca está atrasado, mesmo muito depois do prazo")
    void devolvidoNaoAtrasa() {
        emprestimo.registrarDevolucao(DIA.plusDays(1));

        assertFalse(emprestimo.estaAtrasadoEm(DIA.plusDays(100)));
        assertEquals(0, emprestimo.diasDeAtrasoEm(DIA.plusDays(100)));
    }

    @Test
    @DisplayName("paraView devolve ATIVO, ATRASADO ou DEVOLVIDO conforme a data de referência")
    void statusNaView() {
        assertEquals(StatusEmprestimo.ATIVO, emprestimo.paraView(DIA.plusDays(3)).status());
        assertEquals(StatusEmprestimo.ATRASADO, emprestimo.paraView(DIA.plusDays(30)).status());

        emprestimo.registrarDevolucao(DIA.plusDays(40));

        EmprestimoView devolvido = emprestimo.paraView(DIA.plusDays(50));
        assertEquals(StatusEmprestimo.DEVOLVIDO, devolvido.status());
        assertEquals(DIA.plusDays(40), devolvido.dataDaDevolucao());
    }

    @Test
    @DisplayName("a view carrega id, tombo, matrícula e datas do empréstimo")
    void viewCarregaOsDados() {
        EmprestimoView view = emprestimo.paraView(DIA);

        assertEquals("E1", view.id());
        assertEquals("T1", view.tombo());
        assertEquals("U1", view.matricula());
        assertEquals(DIA, view.dataDoEmprestimo());
        assertEquals(DIA.plusDays(14), view.dataPrevistaDeDevolucao());
        assertNull(view.dataDaDevolucao());
    }
}

package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.inicial.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClassificacaoIndicativaTest {

    private Item livroRestrito16;
    private Item livroLivre;
    private Usuario usuarioMenor;  // 14 anos
    private Usuario usuarioExato;  // Exatamente 16 anos
    private Usuario usuarioAdulto; // 20 anos
    private Usuario usuarioSemDataNascimento;
    private LocalDate dataOperacao;

    @BeforeEach
    void setUp() {

        dataOperacao = LocalDate.of(2026, 11, 27);

        livroRestrito16 = new Item("001", "O Mistério da Meia-Noite", "Autor X", "Suspense", 2021);
        livroRestrito16.setClassificacaoIndicativa(ClassificacaoIndicativa.DEZESSEIS);
        livroLivre = new Item("002", "Contos Infantis", "Autor Y", "Infantil", 2018);
        livroLivre.setClassificacaoIndicativa(ClassificacaoIndicativa.LIVRE);

        usuarioMenor = new Usuario("111", "Lucas Silva");
        usuarioMenor.setDataDeNascimento(LocalDate.of(2012, 5, 23));

        usuarioExato = new Usuario("222", "Beatriz Rocha");
        usuarioExato.setDataDeNascimento(LocalDate.of(2010, 1, 21));

        usuarioAdulto = new Usuario("333", "Mariana Costa");
        usuarioAdulto.setDataDeNascimento(LocalDate.of(2006, 3, 15));

        usuarioSemDataNascimento = new Usuario("444", "João Sem Data");
    }


    @Test
    @DisplayName("Deve negar empréstimo de exemplar para usuário menor que a faixa etária exigida")
    void deveNegarEmprestimoParaUsuarioMenorDeIdade() {
        Exemplar exemplar = new Exemplar("T-01", livroRestrito16);

        boolean podeEmprestar = exemplar.podeSerEmprestadoA(usuarioMenor, dataOperacao);

        assertFalse(podeEmprestar, "Não deve permitir empréstimo para usuário de 14 anos em item de 16 anos.");
    }

    @Test
    @DisplayName("Deve autorizar empréstimo quando usuário atinge a idade exata da classificação")
    void deveAutorizarEmprestimoParaUsuarioComIdadeExata() {
        Exemplar exemplar = new Exemplar("T-01", livroRestrito16);

        boolean podeEmprestar = exemplar.podeSerEmprestadoA(usuarioExato, dataOperacao);

        assertTrue(podeEmprestar, "Deve permitir empréstimo quando a idade é exatamente igual à classificação.");
    }

    @Test
    @DisplayName("Deve autorizar empréstimo para usuário com idade acima da classificação")
    void deveAutorizarEmprestimoParaUsuarioMaiorDeIdade() {
        Exemplar exemplar = new Exemplar("T-01", livroRestrito16);

        boolean podeEmprestar = exemplar.podeSerEmprestadoA(usuarioAdulto, dataOperacao);

        assertTrue(podeEmprestar, "Deve permitir empréstimo para usuário mais velho que a classificação exigida.");
    }

    @Test
    @DisplayName("Deve autorizar empréstimo de item com classificação LIVRE para qualquer usuário")
    void deveAutorizarEmprestimoDeItemLivre() {
        Exemplar exemplar = new Exemplar("T-02", livroLivre);

        boolean podeEmprestar = exemplar.podeSerEmprestadoA(usuarioMenor, dataOperacao);

        assertTrue(podeEmprestar, "Item LIVRE deve poder ser emprestado a qualquer idade.");
    }

    @Test
    @DisplayName("Deve negar empréstimo se usuário não possui data de nascimento e o item exige idade mínima")
    void deveNegarEmprestimoParaUsuarioSemDataNascimento() {
        Exemplar exemplar = new Exemplar("T-01", livroRestrito16);

        boolean podeEmprestar = exemplar.podeSerEmprestadoA(usuarioSemDataNascimento, dataOperacao);

        assertFalse(podeEmprestar, "Usuário sem data de nascimento deve ser tratado como 0 anos e ter o empréstimo negado.");
    }


    @Test
    @DisplayName("Deve lançar DadosInvalidosException ao tentar reservar item com idade insuficiente")
    void deveLancarExcecaoAoReservarComIdadeInsuficiente() {
        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> livroRestrito16.reservar("RES-01", usuarioMenor, dataOperacao),
                "Deve barrar a reserva lançando DadosInvalidosException."
        );

        assertTrue(excecao.getMessage().contains("idade mínima exigida"));
    }

    @Test
    @DisplayName("Deve realizar a reserva com sucesso quando a idade do usuário for permitida")
    void devePermitirReservaParaUsuarioComIdadeAdequada() throws Exception {
        Reserva reserva = livroRestrito16.reservar("RES-02", usuarioAdulto, dataOperacao);

        assertNotNull(reserva, "Reserva deve ser criada com sucesso.");
        assertEquals(1, livroRestrito16.tamanhoDaFila(), "A fila de reservas do item deve conter 1 elemento.");
    }
}

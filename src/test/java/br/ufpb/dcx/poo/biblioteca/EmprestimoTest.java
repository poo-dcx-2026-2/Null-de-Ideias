package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusEmprestimo;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmprestimoTest {

    Biblioteca biblioteca;

    @BeforeEach void prepararCenario() throws Exception {

        biblioteca = Fabrica.novaBiblioteca();


        biblioteca.acervo().cadastrarItem("L1","Codigo Limpo"," Robert C.Martin","Programação e Linguagens",2008);
        biblioteca.acervo().adicionarExemplar("L1","0123");

        biblioteca.usuarios().cadastrarUsuario("000","ana");
        biblioteca.usuarios().cadastrarUsuario("001","marcos");
        biblioteca.usuarios().cadastrarUsuario("002","Jo");


    }

    @Test
    @DisplayName(" O emprestimo dever ser realizado com sucessor")
    void deveRealizarEmprestimo()throws Exception{

        LocalDate data = LocalDate.of(2026,10,16);

        String id = biblioteca.emprestimos().emprestar("0123","000",data);

        assertNotNull(id);
        assertFalse(id.isBlank());

        List<EmprestimoView> emprestimos = biblioteca.emprestimos().listarEmprestimosDoUsuario("000");

        assertEquals(1,emprestimos.size());

        EmprestimoView emprestimo = emprestimos.get(0);
        assertEquals(id , emprestimo.id());
        assertEquals("0123", emprestimo.tombo());
        assertEquals("000",emprestimo.matricula());

        assertEquals(LocalDate.of(2026,10,16),emprestimo.dataDoEmprestimo());
        assertEquals(LocalDate.of(2026,10,30),emprestimo.dataPrevistaDeDevolucao());
        assertEquals(StatusEmprestimo.ATIVO,emprestimo.status());
        assertEquals(StatusExemplar.EMPRESTADO,biblioteca.acervo().listarExemplares("L1").get(0).status());

        assertEquals(1,biblioteca.usuarios().buscarUsuario("000").emprestimosAtivos());
    }

}

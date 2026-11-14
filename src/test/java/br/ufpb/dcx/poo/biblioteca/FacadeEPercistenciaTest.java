package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FacadeEPercistenciaTest {

    @TempDir
    Path pasta;

    @BeforeEach
    void apontarOsDadosParaPastaTemporaria(){
        System.setProperty("biblioteca.dados", pasta.toString());
    }

    @AfterEach
    void desfazerAPropriedade(){
        System.clearProperty("biblioteca.dados");
    }

    @Test
    void categoriaIginoraMaiuculaEOrdenaPorTitulo() throws BibliotecaException{
        Biblioteca biblioteca = Fabrica.novaBiblioteca();
        biblioteca.acervo().cadastrarItem("F1", "Matrix", "Lana e Lilly Wachowski", "ficcao",1999);
        biblioteca.acervo().cadastrarItem("F2", "O Poderoso Chefão", "Francis Ford Coppola", "drama",1972);
        biblioteca.acervo().cadastrarItem("F3", "Interestelar", "Christopher Nolan", "ficcao",2014);

        List<ItemView> filmes = biblioteca.acervo().buscarPorCategoria("FICCAO");
        assertEquals(2, filmes.size());
        assertEquals("Interestelar", filmes.get(0).titulo());
        assertEquals("Matrix", filmes.get(1).titulo());

    }

    @Test
    void estadoSalvoVoltaNumaBibliotecaNova() throws BibliotecaException{
        Biblioteca original = Fabrica.novaBiblioteca();
        original.acervo().cadastrarItem("F1", "Matrix", "Lana e Lilly Wachowski", "ficcao", 1999);
        original.salvar();

        Biblioteca recarregada = Fabrica.novaBiblioteca();
        recarregada.carregar();
        assertEquals("Matrix", recarregada.acervo().buscarItem("F1").titulo());
    }

    @Test
    void arquivoCorrompidoNaoAlteraOEstado() throws BibliotecaException, IOException {
        Biblioteca biblioteca = Fabrica.novaBiblioteca();
        biblioteca.acervo().cadastrarItem("F1", "Matrix", "Lana e Lilly Wachowski", "ficcao", 1999);
        Files.writeString(pasta.resolve("itens.txt"),
                "F2;O Poderoso Chefão;Francis Ford Coppola;drama;1972\nF3;Interestelar;Christopher Nolan\n");

        assertThrows(PersistenciaException.class, biblioteca::carregar);
        assertEquals(1, biblioteca.acervo().listarItens().size());
    }


}
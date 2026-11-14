package br.ufpb.dcx.poo.biblioteca.tela;

import br.ufpb.dcx.poo.biblioteca.contrato.Biblioteca;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;

import java.io.PrintStream;

public class TelaDeConsulta {
    private final Biblioteca biblioteca;
    private final PrintStream saida;

    public TelaDeConsulta(Biblioteca biblioteca, PrintStream saida){
        this.biblioteca = biblioteca;
        this.saida = saida;
    }

    public void listarCategoria(String categoria){
        for (ItemView item : biblioteca.acervo().buscarPorCategoria(categoria)){
            saida.println(item.titulo() + " (" + item.exemplaresDisponiveis() + " de " + item.totalDeExemplares() + " disponíveis) " );
        }
    }
}

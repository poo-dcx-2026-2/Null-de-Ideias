package br.ufpb.dcx.poo.biblioteca.inicial;

public class Filmes extends Item {
    private int classificacaoIndicativa;
    private boolean disponivel;

    public Filmes(String codigo, String titulo, String autoria, String categoria, int ano, int classificacaoIndicativa) {
        super(codigo, titulo, autoria, categoria, ano);
        this.classificacaoIndicativa = classificacaoIndicativa;
        this.disponivel= true;
    }

    public int getClassificacaoIndicativa() {
        return classificacaoIndicativa;
    }

    public void setClassificacaoIndicativa(int classificacaoIndicativa) {
        this.classificacaoIndicativa = classificacaoIndicativa;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}

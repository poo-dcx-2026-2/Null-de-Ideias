package br.ufpb.dcx.poo.biblioteca.persistencia;

public final class LinhaDeItem {
    private final String codigo;
    private final String titulo;
    private final String autoria;
    private final String categoria;
    private final int ano;

    public LinhaDeItem (String codigo,String titulo, String autoria, String categoria, int ano){
        this.codigo = codigo;
        this.titulo = titulo;
        this.autoria = autoria;
        this.categoria = categoria;
        this.ano = ano;
    }

    public String codigo(){
        return codigo;
    }
    public String titulo(){
        return titulo;
    }
    public String autoria(){
        return autoria;
    }
    public String categoria(){
        return categoria;
    }
    public int ano(){
        return ano;
    }
}

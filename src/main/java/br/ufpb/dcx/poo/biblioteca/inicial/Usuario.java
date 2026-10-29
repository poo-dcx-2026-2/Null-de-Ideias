package br.ufpb.dcx.poo.biblioteca.inicial;
public class Usuario {
    private final String matricula;
    private final String nome;

    public Usuario(String matricula, String nome) {
        this.matricula = matricula;
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }
}

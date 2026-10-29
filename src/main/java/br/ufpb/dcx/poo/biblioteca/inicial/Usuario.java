package br.ufpb.dcx.poo.biblioteca.inicial;
public class Usuario {
    private final String matricula;
    private String nome;
    private boolean ativo;
    private int emprestimosAtivos;

    public Usuario(String matricula, String nome) {
        this.matricula = matricula;
        this.nome = nome;
        this.ativo = true;
        this.emprestimosAtivos = 0;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public int getEmprestimosAtivos() {
        return emprestimosAtivos;
    }

    public void setEmprestimosAtivos(int emprestimosAtivos) {
        this.emprestimosAtivos = emprestimosAtivos;
    }

    public void incrementEmprestimosAtivos() {
        emprestimosAtivos++;
    }
    public void decrementEmprestimosAtivos() {
        if  (emprestimosAtivos > 0) {
            emprestimosAtivos--;
        }
    }
}

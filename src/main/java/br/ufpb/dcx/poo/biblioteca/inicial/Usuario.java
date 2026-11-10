package br.ufpb.dcx.poo.biblioteca.inicial;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.LimiteDeEmprestimosExcedidoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService.LIMITE_DE_EMPRESTIMOS;

public class Usuario {
    private final String matricula;
    private String nome;
    private boolean ativo;
    private List<Emprestimo> emprestimos = new ArrayList<>();

    public Usuario(String matricula, String nome) {
        this.matricula = matricula;
        this.nome = nome;
        this.ativo = true;

    }
    public int getEmprestimosAtivos() {
        int ativos = 0;
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.estaAtivo()) {
                ativos++;
            }
        }
        return ativos;
    }
    public Emprestimo emprestar(String tombo, LocalDate data) throws UsuarioInativoException, LimiteDeEmprestimosExcedidoException {
        if (getEmprestimosAtivos() >= LIMITE_DE_EMPRESTIMOS) {
            throw new LimiteDeEmprestimosExcedidoException("Limite de empréstimos atingido");
        }
        if (!isAtivo()){
            throw new UsuarioInativoException("Usuario inativo!");
        }
        Emprestimo emprestimo = new Emprestimo(this, tombo, data);
        emprestimos.add(emprestimo);
        return emprestimo;
    }


    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
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

}

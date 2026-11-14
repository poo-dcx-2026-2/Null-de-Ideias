package br.ufpb.dcx.poo.biblioteca.inicial;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.LimiteDeEmprestimosExcedidoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public int emprestimosAtivos() {
        int ativos = 0;
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.estaAtivo()) {
                ativos++;
            }
        }
        return ativos;
    }

    public Emprestimo emprestar(String tombo, LocalDate data) throws UsuarioInativoException, LimiteDeEmprestimosExcedidoException {
        if (emprestimosAtivos() >= EmprestimoService.LIMITE_DE_EMPRESTIMOS) {
            throw new LimiteDeEmprestimosExcedidoException("Limite de empréstimos Excedido!");
        }
        if (!isAtivo()){
            throw new UsuarioInativoException("Usuario inativo!");
        }
        Emprestimo emprestimo = new Emprestimo(this, tombo, data);
        emprestimos.add(emprestimo);
        return emprestimo;
    }


    public void devolver(String tombo, LocalDate data) throws UsuarioInativoException, OperacaoNaoPermitidaException {
        if (!isAtivo()) {
            throw new UsuarioInativoException("Usuário inativo não pode realizar devolução!");
        }
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.estaAtivo() && emprestimo.getTombo().equals(tombo)) {
                emprestimo.registrarDevolucao(data);
                return;
            }
        }
        throw new OperacaoNaoPermitidaException("Nenhum empréstimo ativo encontrado para este tombo!");
    }



    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
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


    public UsuarioView paraView() {
        return new UsuarioView(matricula, nome, ativo, emprestimosAtivos());
    }


}

package br.ufpb.dcx.poo.biblioteca.inicial;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.ExemplarIndisponivelException;
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
    private boolean ativo = true;
    private final List<Emprestimo> emprestimos = new ArrayList<>();

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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
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


    public Emprestimo emprestar(String id, Exemplar exemplar, LocalDate data)
            throws UsuarioInativoException, ExemplarIndisponivelException,
            LimiteDeEmprestimosExcedidoException {

        if (!ativo) {
            throw new UsuarioInativoException("Usuário inativo: " + matricula);
        }
        if (!exemplar.podeSerEmprestadoA(this)) {
            throw new ExemplarIndisponivelException("Exemplar indisponível: " + exemplar.getTombo());
        }
        if (emprestimosAtivos() >= EmprestimoService.LIMITE_DE_EMPRESTIMOS) {
            throw new LimiteDeEmprestimosExcedidoException("Limite atingido: " + matricula);
        }

        Emprestimo emprestimo = new Emprestimo(id, this, exemplar, data);

        emprestimos.add(emprestimo);

        exemplar.marcarComoEmprestado();
        return emprestimo;
    }


    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }


    public UsuarioView paraView() {
        return new UsuarioView(matricula, nome, ativo, emprestimosAtivos());
    }

}

package br.ufpb.dcx.poo.biblioteca.inicial;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.ExemplarIndisponivelException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.LimiteDeEmprestimosExcedidoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;

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

    public void devolver(String tombo, LocalDate data) throws RecursoNaoEncontradoException {
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.estaAtivo() && emprestimo.getExemplar().temTombo(tombo)) {
                emprestimo.registrarDevolucao(data);
                emprestimo.getExemplar().marcarComoDisponivel();
                return;
            }
        }
        throw new RecursoNaoEncontradoException("Empréstimo ativo não encontrado para o tombo: " + tombo);
    }

    /** Usado pelo serviço para achar quem está com o exemplar. */
    boolean temEmprestimoAtivoDe(String tombo) {
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.estaAtivo() && emprestimo.getExemplar().temTombo(tombo)) {
                return true;
            }
        }
        return false;
    }

    private final List<Reserva> reservas = new ArrayList<>();

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    void adicionarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    void removerReserva(Reserva reserva) {
        reservas.remove(reserva);
    }

    public boolean temReservaPara(String codigoDoItem) {
        for (Reserva reserva : reservas) {
            if (reserva.getCodigoDoItem().equals(codigoDoItem)) {
                return true;
            }
        }
        return false;
    }

    public List<ReservaView> listarReservas() {
        List<ReservaView> resultado = new ArrayList<>();
        for (Reserva reserva : reservas) {
            resultado.add(reserva.paraView());
        }
        return resultado;
    }
}
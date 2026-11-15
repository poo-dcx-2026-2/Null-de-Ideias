package br.ufpb.dcx.poo.biblioteca.inicial;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.ExemplarIndisponivelException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.LimiteDeEmprestimosExcedidoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;


public class EmprestimosEmMemoria implements EmprestimoService {
    private static final LocalDate SEM_DATA_DE_REFERENCIA = LocalDate.MIN;

    private final Supplier<AcervoEmMemoria> fonteDoAcervo;
    private final UsuariosEmMemoria usuarios;

    private int contadorDeEmprestimos = 0;
    private int contadorDeReservas = 0;


    public EmprestimosEmMemoria(Supplier<AcervoEmMemoria> fonteDoAcervo, UsuariosEmMemoria usuarios) {
        this.fonteDoAcervo = fonteDoAcervo;
        this.usuarios = usuarios;
    }

    private AcervoEmMemoria acervo() {
        return fonteDoAcervo.get();
    }

    @Override
    public String emprestar(String tombo, String matricula, LocalDate data)
            throws RecursoNaoEncontradoException, ExemplarIndisponivelException,
            UsuarioInativoException, LimiteDeEmprestimosExcedidoException {

        Usuario usuario = usuarios.buscarUsuarioOuFalhar(matricula);
        Exemplar exemplar = acervo().buscarExemplarOuFalhar(tombo);

        Emprestimo emprestimo = usuario.emprestar(proximoIdDeEmprestimo(), exemplar, data);

        // Se o usuário era o primeiro da fila deste item, a reserva foi atendida.
        Item item = exemplar.getItem();
        Reserva primeira = item.proximaDaFila();
        if (primeira != null && primeira.pertenceAoUsuario(matricula)) {
            item.removerPrimeiraDaFila();
            usuario.removerReserva(primeira);
        }

        return emprestimo.getId();
    }

    @Override
    public void devolver(String tombo, LocalDate data)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {

        Exemplar exemplar = acervo().buscarExemplarOuFalhar(tombo); // inexistente -> RecursoNaoEncontrado

        for (Usuario usuario : usuarios.todos()) {
            if (usuario.temEmprestimoAtivoDe(tombo)) {
                usuario.devolver(tombo, data);
                // Com fila de espera, o exemplar fica guardado em vez de voltar a ficar disponível.
                if (exemplar.getItem().temFilaPendente()) {
                    exemplar.marcarComoReservado();
                }
                return;
            }
        }
        throw new OperacaoNaoPermitidaException("Exemplar não está emprestado: " + tombo);
    }

    @Override
    public List<EmprestimoView> listarEmprestimosDoUsuario(String matricula)
            throws RecursoNaoEncontradoException {

        Usuario usuario = usuarios.buscarUsuarioOuFalhar(matricula);
        List<EmprestimoView> resultado = new ArrayList<>();
        for (Emprestimo emprestimo : usuario.getEmprestimos()) {
            resultado.add(emprestimo.paraView(SEM_DATA_DE_REFERENCIA));
        }
        return resultado;
    }

    @Override
    public List<EmprestimoView> listarAtrasados(LocalDate data) {
        List<EmprestimoView> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios.todos()) {
            for (Emprestimo emprestimo : usuario.getEmprestimos()) {
                if (emprestimo.estaAtrasadoEm(data)) {
                    resultado.add(emprestimo.paraView(data));
                }
            }
        }
        return resultado;
    }

    @Override
    public String reservar(String codigoDoItem, String matricula, LocalDate data)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {

        Item item = acervo().buscarItemOuFalhar(codigoDoItem);
        Usuario usuario = usuarios.buscarUsuarioOuFalhar(matricula);

        try {
            Reserva reserva = item.reservar(proximoIdDeReserva(), usuario, data);
            return reserva.getId();
        } catch (DadosInvalidosException e) {
            // Item com exemplar disponível ou usuário que já está na fila: o contrato pede
            // OperacaoNaoPermitidaException.
            throw new OperacaoNaoPermitidaException(e.getMessage());
        }
    }

    @Override
    public List<ReservaView> listarReservas(String codigoDoItem)
            throws RecursoNaoEncontradoException {

        return acervo().buscarItemOuFalhar(codigoDoItem).listarReservas();
    }

    @Override
    public void cancelarReserva(String idDaReserva) throws RecursoNaoEncontradoException {
        for (Usuario usuario : usuarios.todos()) {
            for (Reserva reserva : usuario.getReservas()) {
                if (reserva.getId().equals(idDaReserva)) {
                    Item item = acervo().buscarItemOuFalhar(reserva.getCodigoDoItem());
                    item.cancelarReserva(usuario);
                    return;
                }
            }
        }
        throw new RecursoNaoEncontradoException("Reserva não encontrada: " + idDaReserva);
    }

    private String proximoIdDeEmprestimo() {
        contadorDeEmprestimos++;
        return "E" + contadorDeEmprestimos;
    }

    private String proximoIdDeReserva() {
        contadorDeReservas++;
        return "R" + contadorDeReservas;
    }
}
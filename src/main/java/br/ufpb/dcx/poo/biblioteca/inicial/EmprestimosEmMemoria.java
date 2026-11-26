package br.ufpb.dcx.poo.biblioteca.inicial;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        exigirTextoPreenchido(tombo, "tombo");
        exigirTextoPreenchido(matricula, "matricula");
        exigirDataPreenchida(data, "data");

        Usuario usuario = usuarios.buscarUsuarioOuFalhar(matricula);
        Exemplar exemplar = acervo().buscarExemplarOuFalhar(tombo);

        Emprestimo emprestimo = usuario.emprestar(proximoIdDeEmprestimo(), exemplar, data);

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

        exigirTextoPreenchido(tombo, "tombo");
        exigirDataPreenchida(data, "data");

        Exemplar exemplar = acervo().buscarExemplarOuFalhar(tombo);

        for (Usuario usuario : usuarios.todos()) {
            if (usuario.temEmprestimoAtivoDe(tombo)) {
                usuario.devolver(tombo, data);
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
        List<Emprestimo> maisRecenteDoCadastroPrimeiro = new ArrayList<>(usuario.getEmprestimos());
        Collections.reverse(maisRecenteDoCadastroPrimeiro);

        List<EmprestimoView> resultado = new ArrayList<>();
        for (Emprestimo emprestimo : maisRecenteDoCadastroPrimeiro) {
            resultado.add(emprestimo.paraView(SEM_DATA_DE_REFERENCIA));
        }
        resultado.sort(Comparator.comparing(EmprestimoView::dataDoEmprestimo).reversed());
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
        resultado.sort(Comparator.comparing(EmprestimoView::dataPrevistaDeDevolucao));
        return resultado;
    }

    @Override
    public String reservar(String codigoDoItem, String matricula, LocalDate data)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {

        exigirTextoPreenchido(codigoDoItem, "codigo do item");
        exigirTextoPreenchido(matricula, "matricula");
        exigirDataPreenchida(data, "data");

        Item item = acervo().buscarItemOuFalhar(codigoDoItem);
        Usuario usuario = usuarios.buscarUsuarioOuFalhar(matricula);

        try {
            Reserva reserva = item.reservar(proximoIdDeReserva(), usuario, data);
            return reserva.getId();
        } catch (DadosInvalidosException e) {
            throw new OperacaoNaoPermitidaException(e.getMessage());
        }
    }

    @Override
    public List<ReservaView> listarReservas(String codigoDoItem)
            throws RecursoNaoEncontradoException {

        exigirTextoPreenchido(codigoDoItem, "codigo do item");

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

    private static void exigirTextoPreenchido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo " + campo + " é obrigatório.");
        }
    }

    private static void exigirDataPreenchida(LocalDate valor, String campo) {
        if (valor == null) {
            throw new DadosInvalidosException("O campo " + campo + " é obrigatório.");
        }
    }
}
package br.ufpb.dcx.poo.biblioteca.inicial;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusEmprestimo;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;

public class Emprestimo {

    private final String id;
    private final Usuario usuario;
    private final Exemplar exemplar;
    private final LocalDate dataDoEmprestimo;
    private final LocalDate dataPrevista;
    private LocalDate dataDaDevolucao;

    Emprestimo(String id, Usuario usuario, Exemplar exemplar, LocalDate dataDoEmprestimo) {
        this.id = id;
        this.usuario = usuario;
        this.exemplar = exemplar;
        this.dataDoEmprestimo = dataDoEmprestimo;
        this.dataPrevista = dataDoEmprestimo.plusDays(EmprestimoService.PRAZO_EM_DIAS);
    }

    public String getId() { return id; }

    public Usuario getUsuario() { return usuario; }

    public Exemplar getExemplar() { return exemplar; }

    public LocalDate getDataDoEmprestimo() { return dataDoEmprestimo; }

    public LocalDate getDataPrevista() { return dataPrevista; }

    public LocalDate getDataDaDevolucao() { return dataDaDevolucao; }

    public boolean estaAtivo() {
        return dataDaDevolucao == null;
    }

    public boolean estaAtrasadoEm(LocalDate data) {
        return estaAtivo() && data.isAfter(dataPrevista);
    }

    public long diasDeAtrasoEm(LocalDate hoje) {
        return estaAtrasadoEm(hoje) ? ChronoUnit.DAYS.between(dataPrevista, hoje) : 0;
    }

    public void registrarDevolucao(LocalDate data) {
        if (data.isBefore(dataDoEmprestimo)) {
            throw new DadosInvalidosException("Devolução antes da data do empréstimo");
        }
        this.dataDaDevolucao = data;
    }

    public EmprestimoView paraView(LocalDate hoje) {
        StatusEmprestimo status;
        if (!estaAtivo()) {
            status = StatusEmprestimo.DEVOLVIDO;
        } else if (estaAtrasadoEm(hoje)) {
            status = StatusEmprestimo.ATRASADO;
        } else {
            status = StatusEmprestimo.ATIVO;
        }
        return new EmprestimoView(id, exemplar.getTombo(), usuario.getMatricula(),
                dataDoEmprestimo, dataPrevista, dataDaDevolucao, status);
    }
}
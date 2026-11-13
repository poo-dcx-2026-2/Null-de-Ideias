package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService;
import br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusEmprestimo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


public class Emprestimo {
    private String id;
    private Usuario usuario;
    private String tombo;
    private LocalDate dataDoEmprestimo;
    private LocalDate dataPrevista;
    private LocalDate dataDaDevolucao;
    private StatusEmprestimo status;


    Emprestimo( Usuario usuario, String tombo, LocalDate dataDoEmprestimo) {
        this.id = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.tombo = tombo;
        this.dataDoEmprestimo = dataDoEmprestimo;
        this.dataPrevista = dataDoEmprestimo.plusDays(EmprestimoService.PRAZO_EM_DIAS);
        this.dataDaDevolucao = null;
        this.status = StatusEmprestimo.ATIVO;
    }
    public void registrarDevolucao(LocalDate data){
        this.dataDaDevolucao = data;
        this.status = StatusEmprestimo.DEVOLVIDO;
    }

    public boolean estaAtivo() {
        return dataDaDevolucao == null;
    }

    public StatusEmprestimo getStatus() {
        return status;
    }

    public void setStatus(StatusEmprestimo status) {
        this.status = status;
    }

    public boolean estaAtrasadoEm(LocalDate hoje) {
        return estaAtivo() && hoje.isAfter(dataPrevista);
    }

    public long diasDeAtrasoEm(LocalDate hoje) {
        return estaAtrasadoEm(hoje) ? ChronoUnit.DAYS.between(dataPrevista, hoje) : 0;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTombo() {
        return tombo;
    }

    public void setTombo(String tombo) {
        this.tombo = tombo;
    }

    public LocalDate getDataDoEmprestimo() {
        return dataDoEmprestimo;
    }

    public void setDataDoEmprestimo(LocalDate dataDoEmprestimo) {
        this.dataDoEmprestimo = dataDoEmprestimo;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    public void setDataPrevista(LocalDate dataPrevista) {
        this.dataPrevista = dataPrevista;
    }

    public LocalDate getDataDaDevolucao() {
        return dataDaDevolucao;
    }

    public void setDataDaDevolucao(LocalDate dataDaDevolucao) {
        this.dataDaDevolucao = dataDaDevolucao;
    }

    public String getId() {
        return id;
    }

    public EmprestimoView paraView() { return new EmprestimoView(id, tombo, usuario.getMatricula(), dataDoEmprestimo, dataPrevista, dataDaDevolucao, status);}
}

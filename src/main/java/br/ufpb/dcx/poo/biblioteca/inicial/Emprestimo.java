package br.ufpb.dcx.poo.biblioteca.inicial;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static br.ufpb.dcx.poo.biblioteca.contrato.EmprestimoService.PRAZO_EM_DIAS;

public class Emprestimo {
    private String id;
    private Usuario usuario;
    private String tombo;
    private LocalDate dataDoEmprestimo;
    private LocalDate dataPrevista;
    private LocalDate dataDaDevolucao;

    public Emprestimo( Usuario usuario, String tombo, LocalDate dataDoEmprestimo) {
        this.id = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.tombo = tombo;
        this.dataDoEmprestimo = dataDoEmprestimo;
        this.dataPrevista = dataDoEmprestimo.plusDays(PRAZO_EM_DIAS);
        this.dataDaDevolucao = null;
    }

    public boolean estaAtivo() {
        return dataDaDevolucao == null;
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
}

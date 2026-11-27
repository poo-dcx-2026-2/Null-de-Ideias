package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import java.time.LocalDate;

public class Exemplar {

    private String tombo;
    private Item item;
    private StatusExemplar status;

    public Exemplar(String tombo, Item item) {
        this.tombo = tombo;
        this.item = item;
        this.status = StatusExemplar.DISPONIVEL;
    }

    public String getTombo() { return tombo; }
    public void setTombo(String tombo) { this.tombo = tombo; }//Generalidade especulativa

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }//Generalidade especulativa

    public StatusExemplar getStatus() { return status; }
    public void setStatus(StatusExemplar status) { this.status = status; }//Generalidade especulativa

    public boolean isDisponivel() {
        return status == StatusExemplar.DISPONIVEL;
    }

    public boolean temTombo(String tombo) {
        return this.tombo.equals(tombo);
    }

    public ExemplarView paraView() {
        return new ExemplarView(tombo, item.getCodigo(), status);
    }

    /** Disponível: qualquer usuário. Reservado: só o primeiro da fila do item. */
    public boolean podeSerEmprestadoA(Usuario usuario,LocalDate data) {
        if (this.item != null && this.item.getClassificacaoIndicativa() != null) {
            if (!this.item.getClassificacaoIndicativa().permiteIdade(usuario.getIdade(data))) {
                return false;
            }
        }
        if (this.status == StatusExemplar.DISPONIVEL) {
            return true;
        }
        if (this.status == StatusExemplar.RESERVADO) {
            Reserva primeira = item.proximaDaFila();
            return primeira != null && primeira.pertenceAoUsuario(usuario.getMatricula());
        }
        return false;
    }

    public void marcarComoEmprestado() {
        this.status = StatusExemplar.EMPRESTADO;
    }

    public void marcarComoDisponivel() {
        this.status = StatusExemplar.DISPONIVEL;
    }

    public void marcarComoReservado() {
        this.status = StatusExemplar.RESERVADO;
    }

    public void baixar() throws OperacaoNaoPermitidaException {
        if (status == StatusExemplar.EMPRESTADO) {
            throw new OperacaoNaoPermitidaException(
                    "Exemplar emprestado não pode ser baixado: " + tombo);
        }
        status = StatusExemplar.INDISPONIVEL;
    }
}

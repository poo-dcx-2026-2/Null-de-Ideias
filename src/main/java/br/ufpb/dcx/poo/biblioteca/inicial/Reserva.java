package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;

import java.time.LocalDate;

public class Reserva {
    private String id;
    private String codigoDoItem;
    private String matricula;
    private String nome;
    private LocalDate dataDeReserva;
    private int posicaoNaFila;

     Reserva(String id, String codigoDoItem, String matricula, String nome, LocalDate dataDeReserva, int posicaoNaFila) {
        this.id = id;
        this.codigoDoItem = codigoDoItem;
        this.matricula = matricula;
        this.nome = nome;
        this.dataDeReserva = dataDeReserva;
        this.posicaoNaFila = posicaoNaFila;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigoDoItem() {
        return codigoDoItem;
    }

    public void setCodigoDoItem(String codigoDoItem) {
        this.codigoDoItem = codigoDoItem;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataDeReserva() {
        return dataDeReserva;
    }

    public void setDataDeReserva(LocalDate dataDeReserva) {
        this.dataDeReserva = dataDeReserva;
    }

    public int getPosicaoNaFila() {
        return posicaoNaFila;
    }

    public void setPosicaoNaFila(int posicaoNaFila) {
        this.posicaoNaFila = posicaoNaFila;
    }

    public ReservaView paraView() {
        return new ReservaView(id, codigoDoItem, matricula, dataDeReserva, posicaoNaFila);
    }
}

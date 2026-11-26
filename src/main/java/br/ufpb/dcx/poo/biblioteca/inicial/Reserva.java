package br.ufpb.dcx.poo.biblioteca.inicial;

import java.time.LocalDate;

import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;

public class Reserva {
    private final String id;
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

    public String getCodigoDoItem() {
        return codigoDoItem;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataDeReserva() {
        return dataDeReserva;
    }

    public int getPosicaoNaFila() {
        return posicaoNaFila;
    }

    public boolean pertenceAoUsuario(String matricula) {
        return this.matricula.equals(matricula);
    }

    void atualizarPosicao(int novaPosicao) {
        this.posicaoNaFila = novaPosicao;
    }
    public ReservaView paraView() {
        return new ReservaView(id, codigoDoItem, matricula, dataDeReserva, posicaoNaFila);
    }
}
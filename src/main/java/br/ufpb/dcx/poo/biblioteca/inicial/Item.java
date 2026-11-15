package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;

import java.time.LocalDate;
import java.util.*;

public class Item {

    private final String codigo;
    private String titulo;
    private String autoria;
    private String categoria;
    private int ano;
    private final Map<String, Exemplar> exemplares = new HashMap<>();

    public Item(String codigo, String titulo, String autoria, String categoria, int ano) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autoria = autoria;
        this.categoria = categoria;
        this.ano = ano;
    }

    public String getCodigo() { return codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutoria() { return autoria; }
    public void setAutoria(String autoria) { this.autoria = autoria; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public List<Exemplar> getExemplares() { return List.copyOf(exemplares.values()); }

    public int exemplares() { return exemplares.size(); }

    public int exemplaresDisponiveis(){
        int disponiveis = 0;
        for (Exemplar exemplar : exemplares.values()) {
            if (exemplar.isDisponivel()) {
                disponiveis++;
            }
        }
        return disponiveis;
    }

    public void adicionarExemplar(Exemplar exemplar) {
        exemplares.put(exemplar.getTombo(), exemplar);
    }

    public boolean exemplarComTombo(String tombo) {
        return exemplares.containsKey(tombo);
    }

    public Exemplar buscarExemplar(String tombo) {
        return exemplares.get(tombo);
    }

    public boolean tituloContem(String trecho) {
        return titulo.toLowerCase().contains(trecho.toLowerCase());
    }

    public boolean temCategoria(String categoria) {
        return categoria.equalsIgnoreCase(this.categoria);
    }

    public ItemView paraView() {
        return new ItemView(codigo, titulo, autoria, categoria, ano, exemplares(), exemplaresDisponiveis());
    }

    private final List<Reserva> reservas = new ArrayList<>();


    public Reserva reservar(String id, Usuario usuario, LocalDate data)
            throws UsuarioInativoException, DadosInvalidosException {

        if (!usuario.isAtivo()) {
            throw new UsuarioInativoException("Usuário inativo: " + usuario.getMatricula());
        }

        if (exemplaresDisponiveis() > 0) {
            throw new DadosInvalidosException("Item possui exemplares disponíveis para empréstimo.");
        }

        for (Reserva reserva : reservas) {
            if (reserva.pertenceAoUsuario(usuario.getMatricula())) {
                throw new DadosInvalidosException("Usuário já possui reserva para este item.");
            }
        }

        int posicao = reservas.size() + 1;
        Reserva novaReserva = new Reserva(
                id,
                this.codigo,
                usuario.getMatricula(),
                usuario.getNome(),
                data,
                posicao
        );
        reservas.add(novaReserva);
        usuario.adicionarReserva(novaReserva);

        return novaReserva;
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    public int tamanhoDaFila() {
        return reservas.size();
    }

    public List<ReservaView> listarReservas() {
        List<ReservaView> resultado = new ArrayList<>();
        for (Reserva reserva : reservas) {
            resultado.add(reserva.paraView());
        }
        return resultado;
    }

    public Reserva proximaDaFila() {
        return reservas.isEmpty() ? null : reservas.get(0);
    }

    public void cancelarReserva(Usuario usuario) throws RecursoNaoEncontradoException {
        for (Reserva reserva : reservas) {
            if (reserva.pertenceAoUsuario(usuario.getMatricula())) {
                reservas.remove(reserva);
                usuario.removerReserva(reserva);
                reordenarFila();
                liberarExemplaresSemFila();
                return;
            }
        }
        throw new RecursoNaoEncontradoException(
                "Reserva não encontrada para o usuário " + usuario.getMatricula()
                        + " no item " + codigo);
    }

    public Reserva removerPrimeiraDaFila() {
        if (reservas.isEmpty()) {
            return null;
        }
        Reserva primeira = reservas.remove(0);
        reordenarFila();
        return primeira;
    }

    private void reordenarFila() {
        for (int i = 0; i < reservas.size(); i++) {
            reservas.get(i).atualizarPosicao(i + 1);
        }
    }

    private int exemplaresReservados() {
        int total = 0;
        for (Exemplar exemplar : exemplares.values()) {
            if (exemplar.getStatus() == StatusExemplar.RESERVADO) {
                total++;
            }
        }
        return total;
    }

    boolean temFilaPendente() {
        return reservas.size() > exemplaresReservados();
    }

    private void liberarExemplaresSemFila() {
        int sobrando = exemplaresReservados() - reservas.size();
        for (Exemplar exemplar : exemplares.values()) {
            if (sobrando <= 0) {
                break;
            }
            if (exemplar.getStatus() == StatusExemplar.RESERVADO) {
                exemplar.marcarComoDisponivel();
                sobrando--;
            }
        }
    }
}
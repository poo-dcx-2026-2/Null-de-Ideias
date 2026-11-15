package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Um item do acervo: um livro, um filme, um jogo, um instrumento.
 *
 * <p>Esta classe é ponto de partida, não modelo a seguir. Ela existe para que o
 * projeto compile e execute desde o primeiro dia. Ao longo do semestre você vai
 * decidir se ela permanece assim, se ganha invariantes, se vira uma hierarquia,
 * se delega responsabilidades ou se desaparece.</p>
 */
public class Item {

    private String codigo;
    private String titulo;
    private String autoria;
    private String categoria;
    private int ano;
    private final List<Exemplar> exemplares = new ArrayList<>();

    public Item(String codigo, String titulo, String autoria, String categoria, int ano) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autoria = autoria;
        this.categoria = categoria;
        this.ano = ano;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutoria() { return autoria; }
    public void setAutoria(String autoria) { this.autoria = autoria; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public List<Exemplar> getExemplares() { return exemplares; }

    public int exemplares() { return exemplares.size(); }

    public int exemplaresDisponiveis(){
        int disponiveis = 0;
        for (Exemplar exemplar : exemplares) {
            if (exemplar.isDisponivel()) {
                disponiveis++;
            }
        }
        return disponiveis;
    }

    public void adicionarExemplar(Exemplar exemplar) {
        exemplares.add(exemplar);
    }

    public boolean exemplarComTombo(String tombo) {
        for (Exemplar exemplar : exemplares) {
            if (exemplar.temTombo(tombo)) {
                return true;
            }
        }
        return false;
    }

    public boolean temCodigo(String codigo) {
        return this.codigo.equals(codigo);
    }

    public boolean tituloContem(String trecho) {
        return titulo.toLowerCase().contains(trecho.toLowerCase());
    }

    public ItemView paraView() {
        return new ItemView(codigo, titulo, autoria, categoria, ano, exemplares(), exemplaresDisponiveis());
    }

    private final List<Reserva> reservas = new ArrayList<>();


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
}

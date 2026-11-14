package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;

import java.util.*;

/**
 * Um item do acervo: um livro, um filme, um jogo, um instrumento.
 *
 * <p>Esta classe é ponto de partida, não modelo a seguir. Ela existe para que o
 * projeto compile e execute desde o primeiro dia. Ao longo do semestre você vai
 * decidir se ela permanece assim, se ganha invariantes, se vira uma hierarquia,
 * se delega responsabilidades ou se desaparece.</p>
 */
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
}

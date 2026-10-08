package br.ufpb.dcx.poo.biblioteca.inicial;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.ufpb.dcx.poo.biblioteca.contrato.AcervoService;
import br.ufpb.dcx.poo.biblioteca.contrato.ExemplarView;
import br.ufpb.dcx.poo.biblioteca.contrato.ItemView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

public class AcervoEmMemoria implements AcervoService {

    private final List<Item> itens = new ArrayList<>();

    @Override
    public void cadastrarItem(String codigo, String titulo, String autoria,
                              String categoria, int ano)
            throws RecursoDuplicadoException {

        exigirTextoPreenchido(codigo, "codigo");
        exigirTextoPreenchido(titulo, "titulo");

        if (localizar(codigo) != null) {
            throw new RecursoDuplicadoException("Já existe item com o código " + codigo);
        }
        itens.add(new Item(codigo, titulo, autoria, categoria, ano));
    }

    @Override
    public ItemView buscarItem(String codigo) throws RecursoNaoEncontradoException {
        exigirTextoPreenchido(codigo, "codigo");
        Item item = localizar(codigo);
        verificacaoDeItemNaoEncontra(codigo, item);
        return paraView(item);
    }

    private static void verificacaoDeItemNaoEncontra(String codigo, Item item) throws RecursoNaoEncontradoException {
        if (item == null) {
            throw new RecursoNaoEncontradoException("Item não encontrado: " + codigo);
        }
    }

    @Override
    public List<ItemView> listarItens() {
        List<ItemView> resultado = new ArrayList<>();
        for (Item item : itens) {
            resultado.add(paraView(item));
        }
        Collator collator = Collator.getInstance(new Locale("pt", "BR"));
        resultado.sort((a, b) -> collator.compare(a.titulo(),b.titulo()));
        return resultado;
    }

    @Override
    public List<ItemView> buscarPorTitulo(String trecho){
        exigirTextoPreenchido(trecho, "trecho");
        List<ItemView> resultado = new ArrayList<>();
        for (Item item : itens){
            if (item.getTitulo().toLowerCase().contains(trecho.toLowerCase())){
                resultado.add(paraView(item));
            }
        }
        return resultado;

    }

    @Override
    public List<ItemView> buscarPorCategoria(String categoria) {
        throw new UnsupportedOperationException("Entrega 2: implementar buscarPorCategoria");
    }

    @Override
    public void adicionarExemplar(String codigoDoItem, String tombo)
            throws RecursoNaoEncontradoException, RecursoDuplicadoException{
        exigirTextoPreenchido(codigoDoItem, "codigo do item");
        exigirTextoPreenchido(tombo, "tombo");
        Item item = localizar(codigoDoItem);
        verificacaoDeItemNaoEncontra(codigoDoItem, item);
        for (Item outro : itens) {
            for (Exemplar exemplar : outro.getExemplares()) {
                if (exemplar.getTombo().equals(tombo)) {
                    throw new RecursoDuplicadoException("Já existe exemplar com o tombo " + tombo);
                }
            }
        }

        item.getExemplares().add(new Exemplar(tombo, item));

    }

    @Override
    public List<ExemplarView> listarExemplares(String codigoDoItem)
            throws RecursoNaoEncontradoException {
            exigirTextoPreenchido(codigoDoItem, "codigo do item");
            Item item = localizar(codigoDoItem);
        verificacaoDeItemNaoEncontra(codigoDoItem, item);

        List<ExemplarView> listaDeExemplares = new ArrayList<>();

            for (Exemplar exemplarAtual : item.getExemplares()) {
                listaDeExemplares.add(new ExemplarView(
                        exemplarAtual.getTombo(),
                        codigoDoItem,
                        exemplarAtual.getStatus()
                ));
            }

            return listaDeExemplares;
        }

    @Override
    public void baixarExemplar(String tombo)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        throw new UnsupportedOperationException("Entrega 2: implementar baixarExemplar");
    }

    private Item localizar(String codigo) {
        for (Item item : itens) {
            if (item.getCodigo().equals( codigo)) {
                return item;
            }
        }
        return null;
    }

    private ItemView paraView(Item item) {
        int disponiveis = 0;
        for (Exemplar exemplar : item.getExemplares()) {
            if (exemplar.getStatus() == StatusExemplar.DISPONIVEL) {
                disponiveis++;
            }
        }
        return new ItemView(
                item.getCodigo(),
                item.getTitulo(),
                item.getAutoria(),
                item.getCategoria(),
                item.getAno(),
                item.getExemplares().size(),
                disponiveis);
    }

    private static void exigirTextoPreenchido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo " + campo + " é obrigatório.");
        }
    }

    /** Acesso interno usado pelos demais serviços da implementação inicial. */
    List<Item> itens() {
        return itens;
    }
}

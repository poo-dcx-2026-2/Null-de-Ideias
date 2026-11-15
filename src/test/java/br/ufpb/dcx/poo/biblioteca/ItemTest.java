package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.inicial.Exemplar;
import br.ufpb.dcx.poo.biblioteca.inicial.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemTest {
    @Test
    @DisplayName("a lista de exemplares devolvida não pode ser alterada")
    void listaDeExemplaresDevolvidaEImutavel() {
        Item item = new Item("L1", "Java Efetivo", "Bloch", "livro", 2019);
        item.adicionarExemplar(new Exemplar("T1", item));

        assertThrows(UnsupportedOperationException.class,
                () -> item.getExemplares().add(new Exemplar("T2", item)));
    }
}

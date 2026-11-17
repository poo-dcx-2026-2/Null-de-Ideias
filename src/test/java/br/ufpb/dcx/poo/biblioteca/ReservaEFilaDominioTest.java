package br.ufpb.dcx.poo.biblioteca;

import br.ufpb.dcx.poo.biblioteca.contrato.ReservaView;
import br.ufpb.dcx.poo.biblioteca.contrato.StatusExemplar;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.UsuarioInativoException;
import br.ufpb.dcx.poo.biblioteca.inicial.Exemplar;
import br.ufpb.dcx.poo.biblioteca.inicial.Item;
import br.ufpb.dcx.poo.biblioteca.inicial.Reserva;
import br.ufpb.dcx.poo.biblioteca.inicial.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservaEFilaDominioTest {

    private static final LocalDate DIA = LocalDate.of(2026, 11, 2);

    private Item item;
    private Usuario ana;
    private Usuario bruno;
    private Usuario carla;

    @BeforeEach
    void criarItemSemExemplarDisponivel() {
        item = new Item("F1", "Matrix", "Wachowski", "ficcao", 1999);
        ana = new Usuario("U1", "Ana");
        bruno = new Usuario("U2", "Bruno");
        carla = new Usuario("U3", "Carla");
    }

    @Test
    @DisplayName("Reserva guarda identidade, item, usuário, data e posição")
    void reservaGuardaOsDados() throws Exception {
        Reserva reserva = item.reservar("R1", ana, DIA);

        assertEquals("R1", reserva.getId());
        assertEquals("F1", reserva.getCodigoDoItem());
        assertEquals("U1", reserva.getMatricula());
        assertEquals("Ana", reserva.getNome());
        assertEquals(DIA, reserva.getDataDeReserva());
        assertEquals(1, reserva.getPosicaoNaFila());
    }

    @Test
    @DisplayName("pertenceAoUsuario compara a matrícula com equals, não com ==")
    void pertenceAoUsuarioUsaEquals() throws Exception {
        Reserva reserva = item.reservar("R1", ana, DIA);

        assertTrue(reserva.pertenceAoUsuario(new String("U1")));
        assertFalse(reserva.pertenceAoUsuario("U2"));
    }

    @Test
    @DisplayName("a view da reserva reflete a posição atual: ao sair a primeira, a segunda passa à posição 1")
    void viewReflitePosicaoAtual() throws Exception {
        item.reservar("R1", ana, DIA);
        Reserva segunda = item.reservar("R2", bruno, DIA);
        assertEquals(2, segunda.paraView().posicaoNaFila());

        item.cancelarReserva(ana);

        ReservaView view = segunda.paraView();
        assertEquals("R2", view.id());
        assertEquals("F1", view.codigoDoItem());
        assertEquals("U2", view.matricula());
        assertEquals(DIA, view.dataDaReserva());
        assertEquals(1, view.posicaoNaFila());
    }


    @Test
    @DisplayName("reservar coloca cada usuário no fim da fila, com posição de 1 em diante")
    void filaCresceNoFim() throws Exception {
        Reserva primeira = item.reservar("R1", ana, DIA);
        Reserva segunda = item.reservar("R2", bruno, DIA);

        assertEquals(1, primeira.getPosicaoNaFila());
        assertEquals(2, segunda.getPosicaoNaFila());
        assertEquals(2, item.tamanhoDaFila());
        assertEquals(primeira, item.proximaDaFila());
    }

    @Test
    @DisplayName("reservar com usuário inativo lança UsuarioInativoException e não cria reserva")
    void reservarUsuarioInativo() {
        ana.setAtivo(false);

        assertThrows(UsuarioInativoException.class, () -> item.reservar("R1", ana, DIA));
        assertEquals(0, item.tamanhoDaFila());
    }

    @Test
    @DisplayName("removerPrimeiraDaFila devolve a mais antiga e reordena as posições")
    void removerPrimeiraReordena() throws Exception {
        item.reservar("R1", ana, DIA);
        item.reservar("R2", bruno, DIA);
        item.reservar("R3", carla, DIA);

        Reserva removida = item.removerPrimeiraDaFila();

        assertEquals("R1", removida.getId());
        assertEquals(2, item.tamanhoDaFila());
        assertEquals(1, item.getReservas().get(0).getPosicaoNaFila());
        assertEquals("U2", item.getReservas().get(0).getMatricula());
        assertEquals(2, item.getReservas().get(1).getPosicaoNaFila());
    }

    @Test
    @DisplayName("removerPrimeiraDaFila com fila vazia devolve null")
    void removerPrimeiraComFilaVazia() {
        assertNull(item.removerPrimeiraDaFila());
        assertNull(item.proximaDaFila());
    }

    @Test
    @DisplayName("cancelarReserva do usuário tira só a dele e reordena os que ficaram")
    void cancelarReservaReordena() throws Exception {
        item.reservar("R1", ana, DIA);
        item.reservar("R2", bruno, DIA);
        item.reservar("R3", carla, DIA);

        item.cancelarReserva(bruno);

        assertEquals(2, item.tamanhoDaFila());
        assertEquals("U1", item.getReservas().get(0).getMatricula());
        assertEquals("U3", item.getReservas().get(1).getMatricula());
        assertEquals(2, item.getReservas().get(1).getPosicaoNaFila());
    }

    @Test
    @DisplayName("cancelarReserva de usuário sem reserva lança RecursoNaoEncontradoException")
    void cancelarSemReserva() throws Exception {
        item.reservar("R1", ana, DIA);

        assertThrows(RecursoNaoEncontradoException.class, () -> item.cancelarReserva(bruno));
        assertEquals(1, item.tamanhoDaFila());
    }

    @Test
    @DisplayName("cancelar a última reserva libera o exemplar RESERVADO que ficou sem fila")
    void cancelarLiberaExemplarSemFila() throws Exception {
        Exemplar exemplar = new Exemplar("T1", item);
        item.adicionarExemplar(exemplar);
        exemplar.marcarComoEmprestado();
        item.reservar("R1", ana, DIA);
        exemplar.marcarComoReservado();

        item.cancelarReserva(ana);

        assertEquals(StatusExemplar.DISPONIVEL, exemplar.getStatus());
    }

    @Test
    @DisplayName("a lista de reservas devolvida pelo Item não pode ser alterada de fora")
    void listaDeReservasImutavel() throws Exception {
        item.reservar("R1", ana, DIA);

        assertThrows(UnsupportedOperationException.class, () -> item.getReservas().clear());
        assertEquals(1, item.tamanhoDaFila());
    }

    @Test
    @DisplayName("exemplar RESERVADO só pode ser emprestado ao primeiro da fila")
    void exemplarReservadoSoParaOPrimeiro() throws Exception {
        Exemplar exemplar = new Exemplar("T1", item);
        item.adicionarExemplar(exemplar);
        exemplar.marcarComoEmprestado();
        item.reservar("R1", ana, DIA);
        item.reservar("R2", bruno, DIA);
        exemplar.marcarComoReservado();

        assertTrue(exemplar.podeSerEmprestadoA(ana));
        assertFalse(exemplar.podeSerEmprestadoA(bruno));
        assertFalse(exemplar.podeSerEmprestadoA(carla));
    }

    @Test
    @DisplayName("exemplar DISPONIVEL pode ser emprestado a qualquer um; EMPRESTADO e INDISPONIVEL a ninguém")
    void podeSerEmprestadoPorStatus() throws Exception {
        Exemplar exemplar = new Exemplar("T1", item);

        assertTrue(exemplar.podeSerEmprestadoA(carla));

        exemplar.marcarComoEmprestado();
        assertFalse(exemplar.podeSerEmprestadoA(carla));

        exemplar.marcarComoDisponivel();
        exemplar.baixar();
        assertFalse(exemplar.podeSerEmprestadoA(carla));
    }
}

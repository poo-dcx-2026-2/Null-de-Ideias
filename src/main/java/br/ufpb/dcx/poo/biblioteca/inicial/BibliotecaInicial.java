package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.*;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;
import java.util.ArrayList;
import java.util.List;

public class BibliotecaInicial implements Biblioteca {

    private AcervoEmMemoria acervo = new AcervoEmMemoria();
    private final UsuariosEmMemoria usuarios = new UsuariosEmMemoria();
    private final EmprestimosEmMemoria emprestimos = new EmprestimosEmMemoria(() -> acervo, usuarios);
    private final RelatoriosNaoImplementados relatorios = new RelatoriosNaoImplementados();

    @Override
    public AcervoService acervo() {
        return acervo;
    }

    @Override
    public UsuarioService usuarios() {
        return usuarios;
    }

    @Override
    public EmprestimoService emprestimos() {
        return emprestimos;
    }

    @Override
    public RelatorioService relatorios() {
        return relatorios;
    }

    @Override
    public void salvar() throws PersistenciaException {

    }

    @Override
    public void carregar() throws PersistenciaException {

    }
}
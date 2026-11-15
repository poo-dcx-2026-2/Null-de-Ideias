package br.ufpb.dcx.poo.biblioteca.inicial;

import br.ufpb.dcx.poo.biblioteca.contrato.*;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.BibliotecaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.PersistenciaException;
import br.ufpb.dcx.poo.biblioteca.persistencia.ArquivoDeItens;
import br.ufpb.dcx.poo.biblioteca.persistencia.DiretorioDeDados;
import br.ufpb.dcx.poo.biblioteca.persistencia.LinhaDeItem;

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
        List<LinhaDeItem> linhas = new ArrayList<>();
        for (ItemView item : acervo.listarItens()) {
            linhas.add(new LinhaDeItem(item.codigo(), item.titulo(), item.autoria(), item.categoria(), item.ano()));
        }
        new ArquivoDeItens(DiretorioDeDados.atual()).gravar(linhas);
    }

    @Override
    public void carregar() throws PersistenciaException {
        List<LinhaDeItem> linhas = new ArquivoDeItens(DiretorioDeDados.atual()).ler();
        AcervoEmMemoria novo = new AcervoEmMemoria();
        for (LinhaDeItem linha : linhas) {
            try {
                novo.cadastrarItem(linha.codigo(), linha.titulo(), linha.autoria(), linha.categoria(), linha.ano());
            } catch (BibliotecaException e) {
                PersistenciaException ex = new PersistenciaException(" Item recusado pelo acervo: " + linha.codigo());
                ex.initCause(e);
                throw ex;
            }
        }
        this.acervo = novo;
    }
}
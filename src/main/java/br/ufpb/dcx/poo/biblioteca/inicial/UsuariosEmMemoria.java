package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.ArrayList;
import java.util.List;

import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;

public class UsuariosEmMemoria implements UsuarioService {

    private final List<String> matriculas = new ArrayList<>();
    private final List<String> nomes = new ArrayList<>();
    // modificar a estrutura destas listas criar uma nova class para comporta o atributor List<map> que sera composto por matriculas e nomes

    @Override
    public void cadastrarUsuario(String matricula, String nome)
            throws RecursoDuplicadoException {

        exigirTextoPreenchido(matricula, "matricula");
        exigirTextoPreenchido(nome, "nome");

        if (matriculas.contains(matricula)) {
            throw new RecursoDuplicadoException("Já existe usuário com a matrícula " + matricula);
        }
        matriculas.add(matricula);
        nomes.add(nome);
    }

    @Override
    public UsuarioView buscarUsuario(String matricula) throws RecursoNaoEncontradoException {
        int posicao = matriculas.indexOf(matricula);
        if (posicao < 0) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado: " + matricula);
        }
        return paraView(posicao);
    }

    @Override
    public List<UsuarioView> listarUsuarios() {
        List<UsuarioView> resultado = new ArrayList<>();
        for (int i = 0; i < matriculas.size(); i++) {
            resultado.add(paraView(i));
        }
        resultado.sort((a, b) -> a.nome().compareToIgnoreCase(b.nome()));
        return resultado;
    }

    @Override
    public void desativarUsuario(String matricula)
            throws RecursoNaoEncontradoException, OperacaoNaoPermitidaException {
        throw new UnsupportedOperationException("Entrega 2: implementar desativarUsuario");
    }

    @Override
    public void reativarUsuario(String matricula) throws RecursoNaoEncontradoException {
        throw new UnsupportedOperationException("Entrega 2: implementar reativarUsuario");
    }

    private static void exigirTextoPreenchido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo " + campo + " é obrigatório.");
        }
    }

    private UsuarioView paraView(int posicao) {
        return new  UsuarioView(matriculas.get(posicao), nomes.get(posicao), true, 0);
    }
}

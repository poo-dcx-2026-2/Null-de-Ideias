package br.ufpb.dcx.poo.biblioteca.inicial;

import java.util.*;

import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioService;
import br.ufpb.dcx.poo.biblioteca.contrato.UsuarioView;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.DadosInvalidosException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.OperacaoNaoPermitidaException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoDuplicadoException;
import br.ufpb.dcx.poo.biblioteca.contrato.excecoes.RecursoNaoEncontradoException;


public class UsuariosEmMemoria implements UsuarioService {

    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();

    @Override
    public void cadastrarUsuario(String matricula, String nome)
            throws RecursoDuplicadoException {
        exigirTextoPreenchido(matricula, "matricula");
        exigirTextoPreenchido(nome, "nome");
        if (usuarios.containsKey(matricula)) {
            throw new RecursoDuplicadoException("Já existe usuário com a matrícula " + matricula);
        }
        usuarios.put(matricula, new Usuario(matricula, nome));
    }

    @Override
    public UsuarioView buscarUsuario(String matricula) throws RecursoNaoEncontradoException {
       Usuario usuario = usuarios.get(matricula);
        if (usuario ==null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado: " + matricula);
        }
        return paraView(usuario);
    }

    @Override
    public List<UsuarioView> listarUsuarios() {
        List<UsuarioView> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios.values()) {
            resultado.add(paraView(usuario));
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

    private UsuarioView paraView(Usuario usuario) {
        return new UsuarioView
                (usuario.getMatricula(), usuario.getNome(), usuario.isAtivo(), usuario.getEmprestimosAtivos());
    }
}

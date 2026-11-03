# Sistema de Gestão de Biblioteca/Acervo

Projeto incremental da disciplina de Programação Orientada a Objetos — DCX/CCAE/UFPB, 2026.2.

O sistema implementa, em memória, o contrato definido no pacote contrato para um acervo de filmes.

---
## Equipe

| Nome                        | Matrícula | GitHub |
|-----------------------------|---|---|
| Andrêciele Ferreira Da Cruz | 20250104904 | andreciele |
| Ellen Cristine Da Silva     | 20250114268 | ellensilva15 |
| Mylena Paulino De Lima      | 20250138232 | Mylena-p  |

## Começando

Requisitos: JDK 21 e Maven (no IntelliJ IDEA basta abrir a pasta do projeto).

mvn -B verify      # compila, roda os testes e empacota
mvn -B test        # só os testes

Resultado esperado:

Tests run: 19 , Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
---

## Mapa do projeto

```
src/main/java/br/ufpb/dcx/poo/biblioteca/
├── contrato/          ← CONGELADO. Não edite nada aqui.
│   ├── Biblioteca.java              ponto único de acesso aos serviços
│   ├── AcervoService.java           itens e exemplares
│   ├── UsuarioService.java          usuários
│   ├── EmprestimoService.java       empréstimos, devoluções e reservas
│   ├── RelatorioService.java        consultas e importação em lote
│   ├── *View.java                   o que as consultas devolvem
│   ├── Status*.java                 estados de exemplar e de empréstimo
│   └── excecoes/                    a hierarquia de erros do sistema
│
├── Fabrica.java       ← nome e assinatura congelados; o corpo é seu
│
└── inicial/           ← ponto de partida; altere, mova, renomeie ou apague
    ├── Item.java                    item do acervo e seus exemplares
    ├── Exemplar.java                cópia física do item, identificada pelo tombo
    ├── Usuario.java                 matrícula, nome, ativo e empréstimos ativos
    ├── AcervoEmMemoria.java         serviço do acervo
    ├── UsuariosEmMemoria.java       serviço de usuários
    ├── EmprestimosNaoImplementados.java
    ├── RelatoriosNaoImplementados.java
    └── BibliotecaInicial.java

src/test/java/…       ← seus testes; comece pelos que já estão aqui
dados/                ← arquivos de exemplo
.github/workflows/    ← a integração contínua, já configurada
```

---

## Decisões de modelagem e justificativa das coleções

A identidade de um usuário é definida na construção e não muda depois: em Usuario, a matrícula fica em um campo final, sem setter. O nome pode mudar (para permitir nome social), e o estado de ativo e a contagem de empréstimos só mudam por métodos próprios (setAtivo, incrementarEmprestimosAtivos, decrementarEmprestimosAtivos).

| Estrutura              | Onde | Uso|	Justificativa |
|------------------------|---|---|---|
| `List<Item>`           |	AcervoEmMemoria|	Itens do acervo|	Mantém a ordem de cadastro e é a estrutura mais simples para um acervo em memória de poucos itens. A busca por código (localizar) percorre a lista, o que é aceitável nesse volume. Com um acervo grande, um Map pelo código seria mais eficiente.|
| `List<Exemplar>`       |	Item	|Exemplares que pertencem a um item	|Representa a composição item–exemplar: o exemplar só existe ligado a um item, e a ordem de cadastro é preservada.
| `Map<String, Usuario>` |	UsuariosEmMemoria|	Usuários indexados pela matrícula|	Buscar por matrícula e checar matrícula repetida são operações diretas, sem varrer a coleção. Elimina as duas listas paralelas que existiam (matrículas e nomes) e mantém matrícula e nome no mesmo objeto.|

As consultas devolvem ItemView, ExemplarView e UsuarioView (tipos do contrato), e não as entidades internas. Assim o estado do domínio não vaza para quem chama os serviços.

---

## Relato do defeito

**Defeito:** o método `localizar` (`AcervoEmMemoria`) comparava o código do item com `==`, que compara objetos e não conteúdo. Um código com o mesmo texto, mas vindo de outro objeto `String`, não era reconhecido: `buscarItem` não achava o item e `cadastrarItem` aceitava código repetido. Os testes iniciais passavam porque usavam literais, que o Java reaproveita como o mesmo objeto. O defeito já estava no código inicial (commit `32463b5`).

**Como foi reproduzido:** cadastrando o item `"L1"` e buscando com `new String("L1")`.

**Como foi encontrado:** Durante a análise e os testes do método `localizar`, foi identificado que a comparação dos códigos utilizava `==`, causando falhas quando o código pesquisado era uma String com o mesmo conteúdo, mas com uma referência de memória diferente.

**Correção:** commit `94613dc` (05/10/2026, Andrêciele), que trocou `==` por `.equals(...)`.

**Teste de regressão:** `buscarItemComCodigoMontadoEmTempoDeExecucao`, em `AcervoTest`, adicionado depois da correção. Commit: `7e92820` (30/10/2026, Mylena).

--- 

## As duas regras

**1. O pacote `contrato` é congelado.** Não renomeie, não altere assinaturas, não acrescente nem remova métodos. Os testes de correção são escritos contra esses tipos: se você mudar qualquer coisa ali, eles não compilam e a entrega não pode ser avaliada.

**2. `Fabrica.novaBiblioteca()` precisa continuar funcionando.** É por esse método que os testes obtêm o seu sistema. Você vai trocar o que ele devolve — não troque o nome, o pacote nem a assinatura. Cada chamada precisa devolver uma instância nova e independente.

Fora isso, **tudo é seu**. O pacote `inicial` não é modelo: é matéria-prima. Você pode reescrevê-lo inteiro.

---

## O que já funciona e o que falta

| Serviço | Situação |
|---|---|
| `AcervoService` | `cadastrarItem`, `buscarItem` e `listarItens` funcionam para os casos simples. O resto é seu. |
| `UsuarioService` | `cadastrarUsuario`, `buscarUsuario` e `listarUsuarios` funcionam. `desativar`/`reativar` são da Entrega 2. |
| `EmprestimoService` | Esqueleto. Entrega 2. |
| `RelatorioService` | Esqueleto. Entrega 3. |
| `Biblioteca.salvar/carregar` | Esqueleto. Entrega 2. |

Métodos ainda não implementados lançam `UnsupportedOperationException` com a indicação da entrega. Cada mensagem diz o que fazer.

---

## Um aviso honesto

O código do pacote `inicial` **tem problemas**. Alguns são de design e você vai reconhecê-los conforme a disciplina avançar. Pelo menos um é um defeito de comportamento que os testes atuais **não** pegam: o sistema faz a coisa errada em uma situação que ninguém testou ainda.

Isso é proposital. Encontrar esse tipo de defeito — reproduzir, escrever o teste que falha, corrigir, ver o teste passar — é uma das competências avaliadas. Não confie em suíte verde como prova de correção.

---

## Extensão autoral

* Acervo escolhido: Filmes

    * Regras de negócio própria

          1.Classificação indicativa: Cada filme tem uma classificação etária.
    * 
          2.Penalidade por atraso: Se o filme é devolvido depois da data prevista (prazo de 14 dias), o usuário fica suspenso por um número de dias proporcional ao atraso. Durante a suspensão, ele não consegue emprestar.
    * 
          3.Lista de espera: Quando todos os exemplares de um filme estão emprestados, o usuário entra em uma fila daquele filme. Ao devolver, o exemplar fica reservado para o primeiro da fila. 
    * 
          4.Filmes indisponíveis: Um exemplar baixado (perdido, danificado ou retirado) passa a INDISPONIVEL e não pode mais ser emprestado. O filme continua no acervo, mas conta como sem cópia disponível.
    * 
          5.Limite de empréstimos: o sistema impede que o usuário tenha mais empréstimos ativos do que o limite permitido. 

---

## Uso de ferramentas de IA

O uso é permitido como apoio, desde que declarado em [`DECLARACAO-DE-USO-DE-IA.md`](DECLARACAO-DE-USO-DE-IA.md). Você continua responsável por explicar, testar e adaptar todo o código entregue — inclusive em uma alteração feita presencialmente, sem consulta, na defesa da Entrega 3.

---
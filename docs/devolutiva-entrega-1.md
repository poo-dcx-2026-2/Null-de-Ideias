# Devolutiva da Entrega 1 — Null de Ideias

## Nota: 6,5 de 10 (65 de 100 pontos)

A modelagem e a suíte própria ficaram muito próximas do que eu forneci como repositório base.

Avaliei o último commit da branch `main` até 12/10/2026 às 23h59 (o prazo original, 05/10, mais uma semana de extensão), pela leitura do código, dos testes, do histórico de commits, dos pull requests e da documentação. 

## Pontos por critério

| Critério | O que é avaliado | Pontos | Nível |
|---|---|---|---|
| 4.1 | Correção funcional e aderência ao contrato | 18 de 20 | Adequado |
| 4.2 | Modelagem OO, responsabilidades, coesão e acoplamento | 9 de 25 | Parcial |
| 4.3 | Testes e evidências de depuração | 7 de 15 | Parcial |
| 4.4 | Robustez, exceções e casos-limite | 8 de 10 | Adequado |
| 4.5 | Evolução, refatoração e qualidade do código | 7 de 10 | Parcial |
| 4.6 | Git, build, pipeline e documentação | 8 de 10 | Adequado |
| 4.7 | Processo, checkpoints, explicação e autoria | 8 de 10 | Adequado |
| | **Total** | **65 de 100** | |

## Justificativa (onde tiro pontos)

- Ausência de ordenação por tombo em listarExemplares.

- Não há entidade Usuario, e responsabilidades permanecem concentradas nos serviços.

- Não há suíte própria de testes.

- Tem uma coleção vazada e os setters permitem quebrar invariantes fora das operações do serviço.

- Houve uma aprovação de PR sem comentário.

- Faltam imagem UML, justificativa de coleções e relato do defeito no README.

- Declaração de IA não está assinada.

## Checklist de correções

- [X] **Ordenar a resposta de `listarExemplares` pelo tombo.** O tombo é o número de registro de cada exemplar. O enunciado pede os exemplares "ordenados por tombo", e hoje a lista sai na ordem de cadastro: foi o único teste oculto que falhou. Uma forma de fazer: antes do `return`, ordenar com `lista.sort(Comparator.comparing(ExemplarView::tombo))`. Para conferir, escrevam um teste que cadastra os tombos fora de ordem (por exemplo `T3`, `T1`, `T2`) e verifica que a lista volta `T1`, `T2`, `T3`.
- [X] **Criar a entidade `Usuario` e acabar com as listas paralelas.** Listas paralelas são duas listas em que a posição 3 de uma (matrícula) corresponde à posição 3 da outra (nome); basta uma inserção errada para os dados se desencontrarem. Criem uma classe `Usuario` com matrícula `final` e nome, e guardem os objetos num `Map<String, Usuario>` com a matrícula como chave.
- [X] **Trocar a lista de itens por um `Map<String, Item>`.** Hoje achar um item exige percorrer a lista inteira (busca linear). Com o mapa, a busca é direta pelo código.
- [X] **Tornar a identidade das entidades imutável.** Identidade é o dado que diz *quem* o objeto é: o código do item, o tombo do exemplar, a matrícula do usuário. Se existe um `setCodigo(...)`, qualquer parte do programa pode trocar o código de um item que já está guardado num mapa, e o mapa passa a apontar para o lugar errado. Declarem esses campos como `private final`, preencham-nos só no construtor e apaguem os *setters* (métodos `setAlgo`) correspondentes.
- [X] **Não devolver a lista interna de exemplares.** Quando um método faz `return exemplares;`, quem chama recebe a *mesma* lista que o objeto usa por dentro e pode adicionar ou apagar exemplares sem passar pelas regras (tombo repetido, item inexistente). Devolvam uma cópia que não pode ser alterada: `return List.copyOf(exemplares);` (ou `Collections.unmodifiableList(exemplares)`). Para conferir, escrevam um teste que tenta `add` na lista devolvida e espera `UnsupportedOperationException`.
- [X] **Criar um teste de regressão para o defeito investigado.** Teste de regressão é o teste que reproduz um defeito já corrigido, para que ele não volte sem ninguém perceber. Para o defeito da comparação de `String` com `==`: montem o código em tempo de execução (por exemplo `new String("L1")` ou `"L" + 1`), cadastrem o item e verifiquem que `buscarItem` o encontra e que um segundo cadastro com o mesmo código lança `RecursoDuplicadoException`. Deem ao teste um nome que conte a história, como `buscaFuncionaComCodigoMontadoEmTempoDeExecucao`.
- [X] **Escrever testes próprios, além dos do template**: tombo repetido, item inexistente, entradas em branco ou nulas, e o isolamento (uma operação que falha não deixa nada pela metade).
- [X] **Pedir revisão com comentário, e não só aprovação.** Ao revisar um PR, escrevam pelo menos um comentário sobre o que leram antes de clicar em *Approve*.
- [X] **Versionar a imagem do UML.** Além do `docs/modelo.puml` (o texto do diagrama), o enunciado pede a imagem gerada a partir dele (PNG ou SVG) dentro de `docs/`, adicionada ao repositório com `git add` e `commit`. Gerem a imagem pelo plugin PlantUML do IntelliJ ou pelo site do PlantUML e confiram que ela aparece no GitHub.
- [X] **Justificar no README cada coleção escolhida.** Para cada `List`, `Set` ou `Map` do código, uma frase dizendo qual operação ela torna fácil. Exemplo: "usamos `Map<String, Item>` com o código como chave porque `buscarItem` precisa achar um item pelo código sem percorrer a lista inteira".
- [X] **Escrever no README o relato do defeito.** Uma seção curta com: o que acontecia (o sintoma), como vocês reproduziram, qual era a causa e qual teste agora protege contra ele.
- [X] **Assinar a declaração de uso de IA.** O arquivo já está preenchido; faltam os nomes de todos os integrantes no campo de assinatura. Sem a assinatura, a declaração não vale como compromisso da equipe.

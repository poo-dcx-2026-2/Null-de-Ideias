# Devolutiva da Entrega 2 — Null de Ideias

## Feedback

A maior lacuna está na persistência. A gravação inclui apenas os itens, e o carregamento deixa de recuperar exemplares, usuários, empréstimos e reservas. O registro de decisões também precisa explicar as escolhas de design pedidas no enunciado.

## Checklist de ajustes sugeridos

- [ ] Persistir também os exemplares com seu status, os usuários com sua situação, o histórico de empréstimos e as reservas. Garantir IDs únicos após o carregamento.
- [ ] Validar todos os dados carregados antes de substituir o estado em memória.
- [ ] Usar `@TempDir` para testar o ciclo completo de salvar, abrir uma nova instância e carregar todos os registros.
- [ ] Atualizar `docs/decisoes.md` com a escolha entre composição e herança, duas decisões GRASP e o papel da Facade.
- [ ] Identificar na documentação quais regras fazem parte do contrato e quais pertencem à extensão autoral.
- [ ] Integrar mudanças por pull requests com revisões substantivas registradas.

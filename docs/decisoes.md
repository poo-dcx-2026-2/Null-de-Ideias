## Dicisões: Regras de Negócio Adicionais

Além das regras de negócio obrigatórias da Entrega 2, o sistema que gerencia uma biblioteca contém algumas outras regras de negócio. Estas regras foram decididas pela equipe e visam proporcionar um controle mais completo do acervo de filmes, bem como um controle mais organizado de empréstimos, reservas e disponibilidade.


## Regras autorais:

### Classificação Indicativa 

Para cada filme no sistema há uma classificação indicativa. Esta classificação indica a faixa etária mínima recomendável para quem assistir ao filme.
Esta informação é usada pelos mecanismos de elegibilidade de empréstimo para evitar que usuários possam emprestar filmes com classificação indicativa maior do que a faixa etária que lhes é permitida.



### Penalidade por Atraso (Suspensão) 

Os filmes emprestados devem ser devolvidos em até 14 dias.
Se um usuário devolver o filme após esse prazo, ele sofrerá uma penalidade. A penalidade será proporcional ao número de dias em atraso e será imposta na forma de uma suspensão.
Enquanto estiver em suspenso, não poderá fazer novos empréstimos. Quando terminar a pena, volta a poder fazer empréstimos.



### Lista de Espera e Reserva de Filmes 

Se todos os exemplares de um certo filme estiverem emprestados, os usuários que quiserem emprestar o filme podem fazer uma reserva e entrarem em uma fila de espera.
As reservas seguem a política FIFO (First In, First Out). Isso significa que o primeiro usuário a colocar-se na fila é o primeiro a ser atendido.
Quando algum exemplar é devolvido, ele está reservado para o primeiro da fila e nenhum outro usuário pode emprestar o filme antes dele.
Assim que este usuário é atendido, o próximo usuário entra para a primeira posição da fila.



### Controle de Filmes e Exemplares Indisponíveis 

Se um exemplar for baixado do acervo, por exemplo, porque foi perdido, danificado ou retirado, então seu status muda para INDISPONIVEL.
Este exemplar não pode ser usado para novos empréstimos.
O filme continua no acervo mesmo se todos os exemplares estiverem indisponíveis. Neste caso, o acervo contém o filme, mas não há cópias disponíveis para empréstimo.


## Regras do contrato :
 
### Limite de Empréstimos por Usuário 

O sistema impõe um limite máximo de empréstimos ativos por usuário.
limite de emprestimos por usuario (3)
A cada vez que um usuário tentar fazer um empréstimo, o sistema verifica quantos empréstimos ativos ele tem.
Se o número de empréstimos permitidos já tiver sido atingido, o sistema bloqueia a operação e informa que o usuário não pode fazer mais empréstimos até que pelo menos um dos exemplares emprestados seja devolvido.



### Prazo de Devolução e Controle de Atrasos 
Todo empréstimo realizado no sistema possui um prazo limite de devolução de 14 dias corridos, contados a partir do dia em que a operação foi efetuada.
No momento da realização do empréstimo, o sistema calcula e registra automaticamente a data prevista de devolução.
Se a devolução não ocorrer dentro do prazo estipulado, o empréstimo passa ao status de atrasado. A partir desse momento, o usuário fica impedido de realizar novos empréstimos até a regularização do exemplar e o cumprimento da penalidade de suspensão correspondente.



### Justificativa



Estas regras de negócio adicionais completam as regras obrigatórias do sistema. Elas são decisões autorais da equipe e visam melhorar o controle do acervo, assim como assegurar que as reservas serão organizadas, e criar critérios mais precisos para o uso dos serviços da biblioteca.
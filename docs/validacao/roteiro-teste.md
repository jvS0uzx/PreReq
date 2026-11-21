# Roteiro do teste com alunos

Card: #24. Segue a seção Validação de `docs/proposta.tex`. O teste compara abrir um requerimento só com o Portal do Aluno e abrir depois de consultar o PreReq. Ninguém envia pedido de verdade: o participante para antes do botão "Solicitar".

## Resumo

| Item | Definição |
|---|---|
| Participantes | 15 a 20 alunos da UCSal, de cursos e semestres diferentes, com pelo menos 3 calouros e 3 bolsistas (ProUni ou FIES) |
| Duração por participante | cerca de 25 minutos |
| Equipe por sessão | 1 pessoa conduz e lê as situações, 1 pessoa cronometra e anota |
| Material | notebook com o PreReq aberto, o participante logado no próprio Portal do Aluno, cronômetro, `metricas.csv` e os dois questionários de `questionarios.md` |
| Identificação | cada participante recebe um código de `P01` a `P20`. Nome, RA e e-mail não são anotados em lugar nenhum |

## Divisão dos grupos

Os códigos ímpares formam o grupo A e os pares o grupo B. Cada participante resolve as três situações, uma por rodada, e a condição troca de uma rodada para a outra:

| Rodada | Situação | Grupo A | Grupo B |
|---|---|---|---|
| 1 | Situação 1 | só o portal | com o PreReq |
| 2 | Situação 2 | com o PreReq | só o portal |
| 3 | Situação 3 | só o portal | com o PreReq |

Assim cada situação é resolvida pela metade dos participantes em cada condição, e todo participante passa pelas duas. O `metricas.csv` já vem com a condição de cada linha preenchida.

## As três situações

Ler em voz alta, exatamente como está escrito, sem explicar nem dar exemplo. Se o participante perguntar algo, responder só "faça como você faria se estivesse sozinho".

**Situação 1**

> Você faz estágio em uma empresa e recebeu uma proposta de estágio em outra. Para começar no novo, a universidade precisa dar baixa no estágio atual. Você precisa resolver isso esta semana. Mostre como você faria o pedido.

**Situação 2**

> Você começou a trabalhar e o horário do trabalho bate com uma das disciplinas em que está matriculado neste semestre. Você quer deixar de cursar só essa disciplina. Mostre como você faria o pedido.

**Situação 3**

> A empresa do seu novo estágio enviou o Termo de Compromisso de Estágio e disse que a universidade precisa assinar antes de você começar. Mostre como você faria o pedido.

A proposta sugeria como terceira situação a declaração de matrícula para o estágio. Ela foi trocada porque essa categoria ainda está sem documentos cadastrados (ver `conferencia.md`, pergunta 6), e sem documentos não dá para medir a métrica "documentos completos". A assinatura do contrato de estágio tem documento e setor conferidos em protocolo real.

## Gabarito

Conferido com `src/main/resources/dados/` e `conferencia.md` em 21/11/2026. A coluna "conta como acerto" é o que a dupla da sessão marca em `acertou_categoria`.

| Situação | Resposta correta | Conta como acerto | Documentos corretos |
|---|---|---|---|
| 1 | Não existe categoria para encerrar estágio fora do curso de Direito. O caminho é a Coordenação do curso | dizer que não abriria o requerimento e procuraria a coordenação. Escolher `SOLICITAÇÃO DE DISPENSA DE ESTÁGIO CURRICULAR - DIREITO` ou qualquer outra categoria é erro | não se aplica |
| 2 | Não há categoria de trancamento de disciplina na lista. O caminho é confirmar com a Coordenação do curso | dizer que procuraria a coordenação antes de abrir. Escolher `RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO` sem confirmar, ou qualquer outra categoria, é erro | não se aplica |
| 3 | `ASSINATURA DE CONTRATO DE ESTÁGIO - GRADUAÇÃO`, dentro de "Abertura de Requerimento" | escolher essa categoria | Termo de Compromisso de Estágio (TCE) |

As respostas das situações 1 e 2 são deduções a partir do portal, não confirmadas pela universidade (`conferencia.md`, perguntas 1 e 3). Se a CRA responder antes do teste, revisar o gabarito.

## Passo a passo da sessão

1. **Recepção (2 min).** Explicar: "Estamos testando um guia feito por alunos, não você. Não existe resposta errada da sua parte. Nada vai ser enviado para a universidade e não anotamos seu nome nem seu RA." Entregar o código do participante.
2. **Questionário inicial (3 min).** O participante responde o questionário "Antes do teste" no celular ou no notebook, informando o código.
3. **Preparação (1 min).** O participante entra no Portal do Aluno com a própria conta e abre Solicitações, Requerimentos. A dupla não vê a senha. O PreReq fica em outra aba, fechada nas rodadas "só o portal".
4. **Rodada 1 (5 min).** Ler a Situação 1. Disparar o cronômetro ao terminar a leitura. Na condição "com o PreReq", avisar: "Você pode usar esta aba do guia antes de ir para o portal." O participante navega e fala em voz alta o que escolheria. Parar o cronômetro quando ele disser que clicaria em "Solicitar" ou que não abriria o pedido. Se passar de 5 minutos, encerrar a rodada e anotar 300 segundos.
5. **Anotação da rodada.** Antes de passar para a próxima, perguntar: "Que documentos você anexaria?" e "De 1 a 5, quanto você concorda com a frase: eu sabia o que esperar depois de enviar?". Anotar em `metricas.csv`: categoria escolhida (ou "não abriria"), documentos citados, tempo e a nota.
6. **Rodadas 2 e 3 (10 min).** Repetir os passos 4 e 5 com as Situações 2 e 3, trocando a condição conforme a tabela dos grupos.
7. **Questionário final (3 min).** O participante responde o questionário "Depois do teste".
8. **Encerramento (1 min).** Conferir que nenhum requerimento foi enviado no portal, agradecer e mostrar a resposta certa das três situações.

Regras para a dupla:

- não ajudar, não apontar a tela e não reagir à escolha do participante;
- nas rodadas "só o portal", o PreReq fica fechado. Nas rodadas "com o PreReq", o participante decide sozinho quanto usar;
- se o participante clicar em "Solicitar" por engano, cancelar o pedido na hora e anotar na coluna de observações.

## Métricas e metas

As quatro métricas da proposta, calculadas a partir de `metricas.csv`, separando as linhas por `condicao`:

| Métrica | Como calcular | Meta |
|---|---|---|
| Acerto de categoria | linhas com `acertou_categoria = S` divididas pelo total de linhas da condição | acima de 85% com o PreReq |
| Documentos completos | linhas com `documentos_completos = S` divididas pelas linhas em que a coluna não é `NA` | acima de 80% com o PreReq |
| Tempo até o envio | média de `tempo_segundos` | com o PreReq, não pode ser maior que só com o portal |
| Clareza percebida | média de `clareza` | 4 ou mais com o PreReq |

Só a Situação 3 tem documentos no gabarito, então "documentos completos" é medido só nela: no máximo 10 linhas por condição com 20 participantes. É pouco, e o resultado dessa métrica deve ser lido com esse limite.

## Planilha

`metricas.csv` usa `;` como separador e abre direto no Google Planilhas ou no Excel. Uma linha por participante e rodada.

| Coluna | Preenchimento |
|---|---|
| `participante`, `grupo`, `rodada`, `situacao`, `condicao` | já preenchidas |
| `categoria_escolhida` | nome da categoria que o participante escolheria, ou `não abriria` |
| `acertou_categoria` | `S` ou `N`, pelo gabarito |
| `documentos_indicados` | documentos que o participante disse que anexaria, separados por vírgula |
| `documentos_completos` | `S` ou `N` na Situação 3. `NA` nas Situações 1 e 2 (já preenchido) |
| `tempo_segundos` | do fim da leitura até o participante dizer que enviaria ou que não abriria. Máximo 300 |
| `clareza` | nota de 1 a 5 para "eu sabia o que esperar depois de enviar" |
| `observacoes` | qualquer coisa fora do roteiro |

## Depois dos testes

- Calcular as quatro métricas por condição e preencher a tabela "Resultados" de `docs/proposta.tex`, que hoje está com traços.
- Ler as respostas abertas do questionário final e separar o que é ajuste do PreReq.
- Registrar aqui a data das sessões e quantos participantes vieram.

| Data | Participantes | Quem conduziu | Observações |
|---|---|---|---|
| | | | sessões ainda não realizadas |

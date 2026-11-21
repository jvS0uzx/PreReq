# Questionários do teste com alunos

Card: #24. Dois formulários para criar no Google Forms, um antes e um depois da sessão. Nenhum pede nome, RA ou e-mail: desligar a coleta de e-mail nas configurações do formulário. O único identificador é o código do participante (`P01` a `P20`), que liga as respostas às linhas de `metricas.csv`.

Nas perguntas de escala, usar "Escala linear" de 1 a 5, com os rótulos "Discordo totalmente" no 1 e "Concordo totalmente" no 5.

## Antes do teste

Título do formulário: Teste do Guia de Requerimentos - antes

| Nº | Pergunta | Tipo | Opções | Obrigatória |
|---|---|---|---|---|
| 1 | Código do participante | Resposta curta | | sim |
| 2 | Qual é o seu curso? | Resposta curta | | sim |
| 3 | Em que semestre você está? | Lista suspensa | 1º, 2º, 3º, 4º, 5º, 6º, 7º, 8º, 9º, 10º | sim |
| 4 | Você tem bolsa ProUni ou FIES? | Múltipla escolha | ProUni; FIES; Não tenho | sim |
| 5 | Quantos requerimentos você já abriu no Portal do Aluno? | Múltipla escolha | Nenhum; 1 ou 2; 3 a 5; 6 ou mais | sim |
| 6 | Algum requerimento seu já foi indeferido por categoria errada ou por documento faltando? | Múltipla escolha | Sim; Não; Não sei o motivo do indeferimento; Nunca abri requerimento | sim |
| 7 | No último requerimento que você abriu, quanto tempo levou até a resposta? | Múltipla escolha | Até 7 dias; De 8 a 15 dias; De 16 a 30 dias; Mais de 30 dias; Ainda não tive resposta; Nunca abri requerimento | sim |
| 8 | Quando preciso abrir um requerimento, eu sei qual categoria escolher. | Escala linear 1 a 5 | | sim |
| 9 | Eu sei quais documentos preciso anexar. | Escala linear 1 a 5 | | sim |
| 10 | Eu sei quanto tempo o pedido costuma levar. | Escala linear 1 a 5 | | sim |
| 11 | Quando tenho dúvida sobre um requerimento, onde eu procuro ajuda? | Caixas de seleção | Colegas ou grupo de WhatsApp; Coordenação do curso; CRA; Site da universidade; Não procuro, tento sozinho | sim |

## Depois do teste

Título do formulário: Teste do Guia de Requerimentos - depois

| Nº | Pergunta | Tipo | Opções | Obrigatória |
|---|---|---|---|---|
| 1 | Código do participante | Resposta curta | | sim |
| 2 | Com o Guia, foi fácil descobrir qual categoria escolher. | Escala linear 1 a 5 | | sim |
| 3 | Com o Guia, eu sabia quais documentos anexar. | Escala linear 1 a 5 | | sim |
| 4 | Com o Guia, eu sabia o que esperar depois de enviar. | Escala linear 1 a 5 | | sim |
| 5 | Só com o portal, eu sabia o que esperar depois de enviar. | Escala linear 1 a 5 | | sim |
| 6 | Consultar o Guia antes de ir ao portal me atrasou. | Escala linear 1 a 5 | | sim |
| 7 | Eu usaria o Guia antes de abrir um requerimento de verdade. | Escala linear 1 a 5 | | sim |
| 8 | Qual parte do Guia mais ajudou? | Múltipla escolha | Triagem; Checklist de documentos e setores; Modelo de texto; Prazo de referência; Nenhuma | sim |
| 9 | O que faltou ou atrapalhou no Guia? | Parágrafo | | não |

## Como as respostas entram nas métricas

- A métrica "clareza percebida" da proposta vem da nota anotada em `metricas.csv` logo depois de cada rodada, porque ali ela fica separada por condição. As perguntas 4 e 5 do questionário final servem de conferência.
- As perguntas 8 a 10 do questionário inicial e 2 a 4 do final mostram a mudança de percepção do mesmo participante.
- A pergunta 6 do final acompanha a métrica de tempo: se a média passar de 3, o Guia está sendo sentido como uma etapa a mais.
- A pergunta aberta vira lista de ajustes.

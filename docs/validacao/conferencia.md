# Conferência das categorias e regras

Card: #25. Responsável: P6.

Nenhum dado pessoal (nome, RA, número de protocolo) é registrado neste documento.

## Resumo

A conferência com a CRA (Central de Relacionamento ao Aluno) foi tentada pessoalmente nas datas de 13, 16-17/11 e via e-mail no dia 20/11. Não houve resposta até 23/11. Por isso, a conferência foi feita por **análise direta do Portal do Aluno** e dos protocolos concluídos da equipe, sem validação institucional.

Isso significa que as conclusões abaixo são deduções a partir do que o portal mostra, e **não** confirmações da universidade.

| Situação | Perguntas |
|---|---|
| Conclusão a partir do portal | 1, 2, 3, 5 |
| Conclusão parcial | 4, 6 |
| Não foi possível concluir | 7 |
| Confirmado pelo CRA / Registro Acadêmico | nenhuma |

## Fontes usadas

- Portal do Aluno (Solicitações → Requerimentos), consulta somente leitura em 17/11: aba "Disponíveis" (8 tipos) e lista suspensa de "Abertura de Requerimento" (37 categorias).
- Protocolos concluídos da equipe, sem dados pessoais: comprovante de matrícula, reembolso, assinatura de contrato de estágio, e os três casos descritos na proposta (dispensa de estágio, trancamento, transferência interna).
- E-mail enviado à CRA, Osvaldo e Joslene em 20/11, sem resposta.

## Perguntas e conclusões

### 1. Categoria para encerrar um estágio
- Dúvida: qual categoria usar para encerrar (dar baixa em) um estágio?
- Observado: a única categoria de dispensa de estágio na lista é `SOLICITAÇÃO DE DISPENSA DE ESTÁGIO CURRICULAR - DIREITO`. As demais categorias de estágio tratam de outro momento: `ASSINATURA DE CONTRATO DE ESTÁGIO - GRADUAÇÃO`, `CARTA DE APRESENTAÇÃO PARA ESTÁGIO OU VISITA TÉCNICA - GRADUAÇÃO`, `DECLARAÇÃO DE CARGA HORÁRIA PARA ESTÁGIO OBRIGATÓRIO` e `DECLARAÇÃO DE CUMPRIMENTO DE ESTÁGIO CURRICULAR OBRIGATÓRIO`. O caso real da equipe foi indeferido por "campo equivocado" ao usar a categoria do Direito.
- Conclusão: não existe categoria adequada para encerrar estágio fora do curso de Direito. O sistema deve avisar isso e apontar a coordenação do curso ou o CRA como canal.
- Impacto: regra de triagem com `canal_alternativo` preenchido; `quando_nao_usar` da categoria `ASSINATURA DE CONTRATO DE ESTÁGIO - GRADUAÇÃO` já informa que ela serve apenas para iniciar estágio.
- Situação: **conclusão a partir do portal**.

### 2. Categoria para transferência interna
- Dúvida: existe categoria própria para transferência interna?
- Observado: entre os 8 tipos e as 37 categorias só aparece `TRANSFERÊNCIA EXTERNA`.
- Conclusão: não há categoria com o nome "transferência interna" na lista. O caso real da equipe foi deferido em 10 dias, mas a categoria usada nele não pôde ser identificada com segurança. O sistema não deve indicar `TRANSFERÊNCIA EXTERNA` para esse caso e deve apontar a coordenação ou o CRA.
- Pendência: conferir o campo "Categoria do portal (Assunto do atendimento)" no protocolo da equipe para saber qual foi usada.
- Situação: **conclusão a partir do portal**.

### 3. Trancamento de disciplina
- Dúvida: `RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO` é a categoria para trancar disciplinas? Quais setores analisam?
- Observado: é a única categoria de trancamento na lista. No caso real da equipe, o pedido passou primeiro pelo Registro Acadêmico (3 dias) e depois pelo Financeiro (24 dias), para recálculo de parcelas.
- Conclusão: a categoria mais próxima disponível é `RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO`, mas o nome sugere renovação de trancamento já existente, e não foi possível confirmar se ela serve para trancar uma disciplina. Os setores e o efeito (recálculo de parcelas) vêm do protocolo real e podem ser usados.
- Pendência: conferir no protocolo da equipe qual categoria foi usada.
- Situação: **conclusão parcial a partir do portal** (setores e efeito observados; categoria correta não confirmada).

### 4. Comprovante de matrícula x declaração de matrícula e frequência
- Dúvida: qual a diferença entre `COMPROVANTE DE MATRÍCULA - GRADUAÇÃO` e `DECLARAÇÃO DE MATRÍCULA E FREQUÊNCIA NO SEMESTRE ATUAL`?
- Observado: o protocolo de comprovante de matrícula foi concluído em 7 dias, com documento assinado digitalmente e enviado ao e-mail institucional. Não há protocolo da equipe para a declaração de matrícula e frequência, e o portal não descreve as duas categorias.
- Conclusão: pelos nomes, a declaração inclui informação de **frequência** e o comprovante não. Isso é uma dedução pelo nome, não confirmada. As duas ficam cadastradas, com regras de triagem separadas pela palavra "frequência".
- Situação: **conclusão parcial a partir do portal**.

### 5. Reembolso aparece duas vezes
- Dúvida: `REEMBOLSO TODOS OS NIVEIS` aparece duas vezes na aba "Disponíveis". São fluxos distintos?
- Observado: as duas linhas têm o mesmo nome e grupos de atendimento diferentes. O protocolo da equipe passou pelo setor Contas a Receber, com formulário de reembolso assinado em PDF.
- Conclusão: não foi possível distinguir os dois fluxos. Foi mantida uma única linha em `categorias.csv`, para não gerar conflito na carga inicial, que resolve por nome.
- Situação: **conclusão a partir do portal**.

### 6. Documentos e setores das categorias mais usadas
- Dúvida: quais documentos anexar e por quais setores passa cada categoria?
- Observado: só há dados reais de poucas categorias.

| Categoria | Documentos | Setores |
|---|---|---|
| `ASSINATURA DE CONTRATO DE ESTÁGIO - GRADUAÇÃO` | Termo de Compromisso de Estágio (TCE) | Coordenação do curso |
| `REEMBOLSO TODOS OS NIVEIS` | Formulário de reembolso assinado | Contas a Receber |
| `COMPROVANTE DE MATRÍCULA - GRADUAÇÃO` | não identificado | não identificado |
| `RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO` | não identificado | Registro Acadêmico, Financeiro (ver pergunta 3) |

- Conclusão: as demais categorias ficam com `documentos` e `setores` vazios e `A CONFIRMAR` nas colunas de texto. O portal não lista esses dados, que é justamente o problema descrito na proposta.
- Situação: **conclusão parcial a partir do portal**.

### 7. Categorias com mais pedidos incorretos
- Dúvida: quais categorias mais recebem pedidos abertos de forma incorreta?
- Observado: apenas um caso de indeferimento por procedimento, o da dispensa de estágio (pergunta 1). Sem acesso a dados da secretaria, não há como ranquear categorias.
- Conclusão: não foi possível concluir. Os indeferimentos futuros entram pelo registro de indeferimentos do sistema (card #19), que existe para alimentar novas regras.
- Situação: **não foi possível concluir**.

## O que mudou nos dados

| Arquivo | Mudança | Pergunta |
|---|---|---|
| `categorias.csv` | Linha única para `REEMBOLSO TODOS OS NIVEIS` | 5 |
| `categorias.csv` | `quando_nao_usar` da assinatura de estágio: serve apenas para iniciar | 1 |
| `regras.csv` | Regra para encerrar estágio apontando `canal_alternativo` | 1 |
| `regras.csv` | Regra para transferência interna apontando `canal_alternativo` | 2 |
| `regras.csv` | Regras separadas por "frequência" para comprovante e declaração de matrícula | 4 |

Se o banco já tiver sido carregado, as mudanças precisam ser feitas também pelo admin, porque a carga inicial só roda com o banco vazio.

## Categorias conferidas e `dataRevisao`

| Categoria | Conferida por | Data |
|---|---|---|
| `ASSINATURA DE CONTRATO DE ESTÁGIO - GRADUAÇÃO` | análise de protocolo e do portal | [DATA] |
| `REEMBOLSO TODOS OS NIVEIS` | análise de protocolo e do portal | [DATA] |
| `COMPROVANTE DE MATRÍCULA - GRADUAÇÃO` | análise de protocolo e do portal | [DATA] |
| `SOLICITAÇÃO DE DISPENSA DE ESTÁGIO CURRICULAR - DIREITO` | análise da lista do portal | [DATA] |
| `TRANSFERÊNCIA EXTERNA` | análise da lista do portal | [DATA] |
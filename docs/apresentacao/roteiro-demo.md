# Roteiro da demonstração

Card: #26. Duração: 5 a 7 minutos. Percorre os três casos reais da proposta (encerrar estágio, trancar disciplina e transferência interna) nas seis funcionalidades: triagem, checklist, modelo de texto, prazo de referência, minha espera e indeferimento virando regra.

## Antes de começar

- [ ] Abrir a aplicação 1 minuto antes, para o Neon acordar. A primeira página demora alguns segundos.
- [ ] Deixar duas abas prontas: `/` e `/admin` já com o login feito.
- [ ] Conferir em `/prazos` se `RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO` e `REEMBOLSO TODOS OS NIVEIS` têm 3 pedidos cada. Se não tiverem, cadastrar pela tela `/prazos/contribuir` com os blocos de [`historicos-exemplo.md`](historicos-exemplo.md).
- [ ] Conferir em `/admin/regras` que não existe regra com a palavra `desligamento`. Se existir (sobrou de um ensaio), desativar.
- [ ] Conferir em `/admin/indeferimentos` que a fila está vazia.
- [ ] Desligar o Wi-Fi do notebook depois de a página abrir só se a aplicação estiver rodando com H2 (ver "Plano B"). O CSS é local e não depende de internet, mas o Neon depende.
- [ ] Zoom do navegador em 125%.

## Roteiro

Os tempos somam 6min45.

### 1. Abertura (30s)

Tela: `/`.

Fala: "No Portal do Aluno, quem abre um requerimento escolhe entre 37 categorias só pelo nome. Não tem descrição, lista de documentos nem prazo. O PreReq entra antes do portal: ele não abre pedido nenhum e não pede senha."

### 2. Casos 1 e 3: quando o portal não tem a categoria (1min15)

Tela: `/triagem`.

1. Clicar no exemplo "Preciso encerrar meu estágio para começar outro" e em "Descobrir a categoria".
2. Mostrar o resultado: não há categoria no portal, procure a Coordenação do curso.
3. Voltar e digitar "Quero mudar de curso dentro da UCSal". Resultado: não há categoria de transferência interna, não use a Transferência Externa, procure a coordenação.

Fala: "O primeiro é o caso de um integrante da equipe. Ele escolheu a dispensa de estágio, que só vale para o curso de Direito, esperou 32 dias e recebeu 'campo equivocado'. A triagem avisa na hora que a categoria não existe e para onde ir. Na transferência interna é igual: a única categoria parecida é a externa, que é para quem vem de outra instituição."

Padrão: Chain of Responsibility. Regra com categoria, depois regra com canal alternativo, depois a mensagem padrão.

### 3. Checklist e modelo de texto (1min15)

Tela: `/triagem`.

1. Digitar "Preciso assinar o termo de compromisso do meu novo estágio" e enviar.
2. Resultado: `ASSINATURA DE CONTRATO DE ESTÁGIO - GRADUAÇÃO`. Clicar em "Ver checklist".
3. Mostrar o documento (Termo de Compromisso de Estágio e onde conseguir), o setor (Coordenação do curso) e o aviso de quando não usar.
4. No gerador de texto, preencher curso, semestre e motivo, gerar e mostrar o texto pronto para colar no campo "Solicitação".

Padrão: herança e polimorfismo. O aviso e o trecho final do texto mudam conforme o grupo da categoria.

### 4. Caso 2: trancar disciplina e o prazo de referência (1min15)

1. Em `/triagem`, clicar no exemplo "Quero trancar uma disciplina por conflito com o trabalho". Resultado: não há categoria de trancamento de disciplina, procure a coordenação.
2. Abrir `/prazos`. Mostrar a linha de `RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO`: mediana de 24 dias e P75 de 29,5.
3. Clicar na categoria. Mostrar a tabela por setor: Registro Acadêmico com 3 dias e Financeiro com 21.

Fala: "No caso real, o Registro Acadêmico resolveu em 3 dias e o pedido ficou 24 no Financeiro, uma etapa que o aluno nem sabia que existia. Aqui ele vê isso antes de abrir."

Padrão: Strategy. A mediana e o P75 são duas implementações de `CalculoPrazo`, e o service recebe o cálculo por parâmetro.

### 5. De onde vêm os prazos (1min)

Tela: `/prazos/contribuir`.

1. Escolher `RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO` e colar o bloco "Trancamento 1" de `historicos-exemplo.md`.
2. Clicar em "Ver prévia". Mostrar a tabela setor por dias e o aviso "Guardamos só o tipo do requerimento e os dias em cada setor".
3. Não confirmar, para os números da demo não mudarem. Clicar em "Cancelar".

Fala: "O aluno cola o histórico de um pedido concluído. O texto não é salvo. Fica só a categoria, o mês e os dias em cada setor."

Padrão: State. Cada linha do histórico muda o estado da leitura: aberto, em análise num setor, deferido ou indeferido.

### 6. Minha espera (45s)

Tela: `/espera`.

1. Categoria `REEMBOLSO TODOS OS NIVEIS`, data de abertura de 40 dias atrás, setor atual Financeiro.
2. Mostrar os dois avisos: a espera passou do usual (P75 de 26,5 dias), procure a Ouvidoria; e o pedido está no Financeiro, que recalcula as parcelas.

Padrão: Observer. Cada aviso é um observador da situação da espera. O verificador notifica todos e junta o que cada um respondeu.

### 7. Indeferimento virando regra (1min15)

1. Em `/triagem`, digitar "Terminei meu estágio e preciso do desligamento". A triagem não identifica.
2. Em `/indeferimentos/novo`: categoria `SOLICITAÇÃO DE DISPENSA DE ESTÁGIO CURRICULAR - DIREITO`, o que precisava "Terminei meu estágio e preciso do desligamento para começar outro", motivo "campo equivocado". Enviar.
3. Na aba do admin, abrir `/admin/indeferimentos` e clicar em "Converter em regra". O formulário da regra já mostra o relato.
4. Preencher palavras-chave `desligamento, desligar do estágio`, canal alternativo `Coordenação do curso` e salvar.
5. Voltar em `/triagem` e repetir a frase do passo 1. Agora a triagem responde "Procure: Coordenação do curso".

Fala: "O erro de um aluno vira orientação para o próximo, sem mexer em código."

### 8. Fechamento (30s)

Fala: "O aluno continua abrindo o pedido no portal, mas chega lá sabendo a categoria, os documentos, os setores e o prazo. Nenhum dado pessoal é guardado."

## Dados usados na demo

| Categoria | Pedidos | De onde vêm |
|---|---|---|
| `RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO` | 3 | blocos "Trancamento 1 a 3" de `historicos-exemplo.md` |
| `REEMBOLSO TODOS OS NIVEIS` | 3 | blocos "Reembolso 1 a 3" de `historicos-exemplo.md` |

Os seis blocos são exemplos escritos pela equipe no formato do portal. Não são protocolos reais. As quatro amostras reais de `src/test/resources/historicos/` não passam por nenhum setor, então não servem para mostrar a tabela por setor. Os dias do trancamento foram escolhidos perto do caso real da proposta (3 dias no Registro Acadêmico e 24 no Financeiro).

Com esses dados as telas mostram:

| Categoria | Mediana | P75 | Por setor (mediana) |
|---|---|---|---|
| `RENOVAÇÃO TRANCAMENTO - GRADUAÇÃO` | 24,0 | 29,5 | Registro Acadêmico 3,0 e Financeiro 21,0 |
| `REEMBOLSO TODOS OS NIVEIS` | 21,0 | 26,5 | Contas a Receber 7,0 e Financeiro 17,0 |

## Plano B: sem internet

Se o Neon não responder, rodar com H2:

```
./mvnw spring-boot:test-run -Dspring-boot.run.arguments=--server.port=8082
```

Nesse modo a aplicação sobe com os CSV de teste (3 categorias e 2 setores), o login do admin é `admin` com a senha `senha-de-teste`, e o banco começa vazio a cada subida. Os passos 2, 3 e 7 funcionam trocando a categoria da dispensa pela de assinatura de estágio. Os passos 4 a 6 precisam dos seis históricos cadastrados de novo, e o setor Financeiro não existe nos dados de teste, então o segundo aviso do passo 6 não aparece.

## Ensaio

| Data | Onde rodou | Rede | Tempo | Observações |
|---|---|---|---|---|
| 21/11/2026 | H2 com os CSV de `src/main/resources/dados/`, passos por HTTP | ligada | não cronometrado | todos os resultados acima conferidos |
| | Neon, no navegador | desligada depois de abrir | | a fazer |

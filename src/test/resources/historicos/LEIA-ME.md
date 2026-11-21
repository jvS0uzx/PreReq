# Histórico de etapas

Amostras do bloco "Histórico de etapas" copiado de protocolos concluídos no portal da UCSal. Nome, RA, número de protocolo e texto livre são trocados por `XXXX`. Datas e nomes de setor ficam como estão.

## Amostras

| Arquivo | Tipo | Resultado | Dias até o encerramento |
|---|---|---|---|
| `amostra-01.txt` | Não identificado | Encerrado sem deferido nem indeferido | 17 |
| `amostra-02.txt` | Não identificado | Encerrado sem deferido nem indeferido | 7 |
| `amostra-03.txt` | Não identificado | Encerrado sem deferido nem indeferido | 1 |
| `amostra-04.txt` | Não identificado | Encerrado sem deferido nem indeferido | 0 |

Ainda faltam amostras de pedido deferido, indeferido e que passou pelo Financeiro.

## Padrão observado

- A primeira linha é sempre `Histórico de etapas`.
- Cada etapa vem em duas linhas: primeiro `Data de conclusão:DD/MM/AAAA HH:MM:SS`, depois o nome da etapa em maiúsculas.
- Não tem espaço depois de `Data de conclusão:`.
- Os segundos nem sempre têm dois dígitos: aparecem `13:34:9` e `08:19:0`.
- `ABERTURA DO ATENDIMENTO` aparece duas vezes seguidas, com a mesma data e hora.
- O texto termina numa linha de data sem nome de etapa depois.
- Não há linha em branco entre as etapas.

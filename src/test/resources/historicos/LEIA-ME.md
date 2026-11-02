# Históricos de Etapas — Amostras do Portal UCSal

Amostras do bloco "Histórico de etapas" copiado do portal de protocolos da UCSal.
Dados pessoais anonimizados: nome, RA, número de protocolo e textos livres substituídos por `XXXX`.
Datas e nomes de setor preservados.

## Índice de Amostras

| Arquivo | Grupo | Resultado | Setores envolvidos | Dias (abertura→encerramento) |
|---|---|---|---|---|
| `amostra-01.txt` | — | Encerrado (sem deferimento explícito) | — | 17 |
| `amostra-02.txt` | Acadêmico | **Deferido** | Secretaria → Coordenação | 10 |
| `amostra-03.txt` | Acadêmico | **Indeferido** | Secretaria → Coordenação | 5 |
| `amostra-04.txt` | Financeiro | **Deferido** | Secretaria → Financeiro | 15 |
| `amostra-05.txt` | Financeiro + Bolsa | **Indeferido** | Secretaria → Financeiro → ProUni/FIES | 20 |
| `amostra-06.txt` | Estágio | **Deferido** | Secretaria → COES | 8 |

---

## Formato Observado

### Estrutura geral

```
Histórico de etapas
Data de conclusão:DD/MM/AAAA HH:MM:S[S]
NOME DA ETAPA
Data de conclusão:DD/MM/AAAA HH:MM:S[S]
NOME DA ETAPA
...
```

- **Linha 1:** cabeçalho fixo `Histórico de etapas`
- **Cada etapa completa ocupa 2 linhas:** a linha de data seguida do nome da etapa
- **Sem linhas em branco** entre etapas
- **A amostra-01 termina com uma linha de data sem nome de etapa**

### Formato de data

```
Data de conclusão:DD/MM/AAAA HH:MM:S[S]
```

- Prefixo literal: `Data de conclusão:` (sem espaço após os dois pontos)
- Dia e mês sempre com 2 dígitos; ano com 4
- Hora e minutos sempre com 2 dígitos
- **Segundos podem ter 1 ou 2 dígitos** — `:9` e `:28` são ambos válidos

### Etapas conhecidas

| Nome da etapa | Significado |
|---|---|
| `ABERTURA DO ATENDIMENTO` | Protocolo registrado no sistema |
| `ANÁLISE DA SECRETARIA` | Triagem inicial pela secretaria |
| `ANÁLISE DA COORDENAÇÃO` | Avaliação pelo coordenador de curso |
| `ANÁLISE FINANCEIRA` | Avaliação pelo setor financeiro |
| `PARECER FINANCEIRO` | Conclusão da análise financeira |
| `ANÁLISE DE BOLSA PROUNI/FIES` | Avaliação adicional para bolsistas |
| `ANÁLISE DO COES` | Avaliação pela Coordenação de Estágios |
| `DEFERIDO` | Pedido aprovado |
| `INDEFERIDO` | Pedido negado |
| `ENCERRAMENTO DO ATENDIMENTO` | Protocolo encerrado no sistema |

### Palavras que indicam resultado

| Palavra | Resultado |
|---|---|
| `DEFERIDO` | ✅ Aprovado |
| `INDEFERIDO` | ❌ Negado |

> O nome da etapa de resultado aparece **antes** de `ENCERRAMENTO DO ATENDIMENTO`.
> Protocolos sem `DEFERIDO` nem `INDEFERIDO` antes do encerramento indicam encerramento administrativo sem análise de mérito (ver amostra-01).

### Onde aparece o setor

O setor está embutido no **nome da etapa**, geralmente no padrão `ANÁLISE DA/DO <SETOR>` ou `ANÁLISE DE <SETOR>`. Não há um campo separado para setor no bloco de histórico.

---

## Comportamentos Especiais do Portal

### Duplicata de ABERTURA DO ATENDIMENTO

Em todas as amostras, a etapa `ABERTURA DO ATENDIMENTO` aparece **duas vezes consecutivas com a mesma data e hora**. Isso é comportamento real do portal — não é erro de cópia. O parser deve tratar essa duplicata sem falhar.

Exemplo:
```
Data de conclusão:23/12/2025 13:34:9
ABERTURA DO ATENDIMENTO
Data de conclusão:23/12/2025 13:34:9
ABERTURA DO ATENDIMENTO
```

### Segundos com um dígito

O portal não normaliza os segundos para dois dígitos. O parser deve aceitar tanto `HH:MM:S` quanto `HH:MM:SS`.

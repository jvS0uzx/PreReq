# PreReq - Guia de Requerimentos UCSal

O PreReq orienta o aluno da UCSal antes de abrir um requerimento no Portal do Aluno. Ele indica a categoria certa do portal, o que anexar, por quais setores o pedido passa e quanto tempo pedidos parecidos levaram. O pedido oficial continua sendo aberto no portal, como hoje.

O que ele não faz:

- não acessa o Portal do Aluno nem o TOTVS;
- não pede login, senha, nome ou RA do aluno;
- não envia requerimentos.

Trabalho da disciplina Programação Orientada a Objetos Avançada. A proposta completa está em [`docs/proposta.tex`](docs/proposta.tex).

## Como rodar

Requisitos: JDK 21 e acesso a um banco PostgreSQL no Neon. O Maven vem no projeto (`./mvnw`).

1. Copie o `.env.example` para `.env` e preencha:

   | Variável | Para que serve |
   |---|---|
   | `DB_URL`, `DB_USER`, `DB_PASSWORD` | conexão com a sua branch do Neon |
   | `ADMIN_USER`, `ADMIN_PASSWORD` | usuário e senha da área administrativa |

2. Suba a aplicação:

   ```
   ./mvnw spring-boot:run
   ```

3. Abra `http://localhost:8081`. A área administrativa fica em `/admin` e pede o usuário e a senha do `.env`.

Na primeira subida com o banco vazio, a aplicação carrega setores, documentos, categorias e regras dos CSV de `src/main/resources/dados/`. Depois disso os dados são mantidos pela área administrativa, e mudar o CSV não recarrega nada.

Para rodar os testes: `./mvnw verify`. Eles usam H2 em memória e não dependem do Neon nem da internet.

Para ver a aplicação sem Neon, com H2 e os dados de teste:

```
./mvnw spring-boot:test-run -Dspring-boot.run.arguments=--server.port=8082
```

Nesse modo o login do admin é `admin` com a senha `senha-de-teste`.

## O que tem em cada tela

| Rota | O que faz |
|---|---|
| `/triagem` | o aluno descreve o que precisa e recebe a categoria do portal ou o canal certo |
| `/categorias` e `/categorias/{id}` | lista das categorias e o checklist de cada uma: documentos, setores, avisos, prazo de referência e modelo de texto |
| `/prazos` | mediana e P75 de dias por categoria |
| `/prazos/contribuir` | o aluno cola o histórico de etapas de um pedido concluído e contribui com o prazo |
| `/espera` | compara a espera de um pedido aberto com o prazo de referência |
| `/indeferimentos/novo` | relato de pedido negado por procedimento |
| `/admin` | cadastros de setores, documentos, categorias e regras, e a fila de indeferimentos |

## Arquitetura

Spring Boot 4.1 com MVC, Thymeleaf e Pico.css nas telas, Spring Data JPA no acesso ao banco e Spring Security só na área administrativa.

| Pacote | Conteúdo |
|---|---|
| `model/` | entidades JPA e enums |
| `repository/` | interfaces `JpaRepository` |
| `service/` | regras de negócio, com os subpacotes `triagem/`, `prazo/`, `espera/` e `admin/` |
| `controller/` | telas públicas; a área administrativa fica em `controller/admin/` |
| `dto/` | objetos de formulário |
| `exception/` | exceções da aplicação |
| `config/` | segurança e carga inicial |
| `templates/<funcionalidade>/` | views Thymeleaf |

Caminho de uma requisição, usando a triagem como exemplo:

1. O navegador envia `POST /triagem` com a descrição.
2. O `TriagemController` valida o `TriagemForm`. Com erro, volta para o mesmo formulário.
3. O controller chama o `TriagemService`. Controller nunca fala com repository.
4. O service busca as regras ativas no `RegraTriagemRepository`, monta a cadeia de etapas e devolve um `ResultadoTriagem`.
5. O controller coloca o resultado no `Model` e devolve a view `triagem/resultado.html`, que só exibe o que recebeu.

## Padrões de projeto

| Padrão | Onde está | Por quê |
|---|---|---|
| Herança e polimorfismo | `model/TipoRequerimento` e as subclasses `RequerimentoAcademico`, `RequerimentoFinanceiro`, `RequerimentoEstagio` e `RequerimentoDocumental` | cada grupo tem alertas e um trecho próprio no modelo de texto. A tela chama `alertasDoGrupo()` e `montarModeloTexto()` sem saber qual é o grupo |
| Chain of Responsibility | `service/triagem`: `EtapaTriagem`, `EtapaRegraComCategoria`, `EtapaRegraSemCategoria` e `EtapaPadrao` | cada etapa tenta responder a descrição e, se não consegue, passa para a próxima. Primeiro as regras que apontam uma categoria, depois as que apontam um canal alternativo, e por fim a mensagem padrão |
| State | `service/prazo`: `LeituraHistorico` (contexto), `EstadoPedido`, `Aberto`, `EmAnaliseSetor`, `Deferido` e `Indeferido` | a mesma linha do histórico significa coisas diferentes conforme a etapa em que o pedido está. Cada estado decide qual é o próximo e fecha os dias do setor ao sair |
| Strategy | `service/prazo`: `CalculoPrazo`, `CalculoMediana` e `CalculoPercentil` | o `PrazoReferenciaService` recebe o cálculo por parâmetro. Trocar mediana por percentil não muda mais nada no service |
| Observer | `service/espera`: `VerificadorEspera` (sujeito), `ObservadorEspera`, `AlertaPrazoExcedido`, `AlertaSetorFinanceiro` e `AlertaDadosInsuficientes` | cada alerta observa a situação da espera e decide sozinho se avisa. Um alerta novo é só mais uma classe, sem mexer no verificador |

### Por que o grupo de uma categoria não muda na edição

`TipoRequerimento` usa herança em tabela única, e a coluna `grupo` é o discriminador que diz qual subclasse o Hibernate instancia. Trocar o grupo de uma categoria já salva seria trocar a classe do objeto, o que o JPA não faz. Por isso a tela de edição mostra o grupo fixo. Para mudar, desative a categoria e crie outra no grupo certo.

## Privacidade

- O histórico colado em `/prazos/contribuir` não é salvo nem vai para o log. Ficam só a categoria, o resultado, o mês de conclusão e os dias em cada setor.
- A tela `/espera` não guarda nada.
- O relato de indeferimento pede para não informar nome, RA ou número de protocolo.

## Como contribuir

As regras de branch, commit, PR e revisão estão na [issue #1](https://github.com/jvS0uzx/PreReq/issues/1) e no [CONTRIBUTING.md](CONTRIBUTING.md).

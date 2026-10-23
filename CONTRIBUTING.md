# Padrão de desenvolvimento do PreReq

Este documento vale para as seis pessoas da equipe. Se algo aqui não cobrir o seu caso, pergunte na issue da tarefa antes de inventar um padrão novo.

## 1. Preparar a máquina

1. Instale o JDK 21 e o Git. Não precisa instalar Maven: o projeto usa o `./mvnw`.
2. Clone o repositório:
   ```bash
   git clone git@github.com:jvS0uzx/PreReq.git
   cd PreReq
   ```
3. Copie o arquivo de ambiente e preencha com as credenciais que a P1 enviar **em mensagem privada**:
   ```bash
   cp .env.example .env
   ```
4. Crie a sua branch no Neon (console do Neon → Branches → Create branch, com o nome do seu usuário do GitHub) e use a `DB_URL` dela no seu `.env`. Cada pessoa tem o próprio banco; assim uma entidade nova de alguém não altera as tabelas dos outros.
5. Rode:
   ```bash
   ./mvnw spring-boot:run
   ```
   e abra `http://localhost:8081`.
6. Antes de abrir qualquer PR, rode `./mvnw verify`. Ele compila e executa todos os testes usando H2, sem precisar do `.env`.

O `.env` nunca vai para o git. Se uma senha for commitada por engano, avise a P1 na hora para trocá-la no Neon; apagar o commit não basta.

## 2. Fluxo de trabalho no Git

### Uma tarefa, uma branch, um PR

1. Pegue a issue no quadro, confirme que você é o responsável e mova para **In Progress**.
2. Atualize a `main` e crie a branch com o nome que está na issue:
   ```bash
   git switch main
   git pull
   git switch -c feat/10-tela-triagem
   ```
   Formato: `tipo/<número-da-issue>-descricao-curta`, com `tipo` igual ao do commit (`feat`, `fix`, `test`, `docs`, `refactor`, `chore`, `ci`).
3. Faça commits pequenos, cada um com uma mudança que faça sentido sozinha.
4. Antes de abrir o PR, traga o que entrou na `main` e rode os testes:
   ```bash
   git fetch origin
   git rebase origin/main
   ./mvnw verify
   ```
   Conflito se resolve na sua branch, nunca na `main`.
5. Envie a branch e abra o PR:
   ```bash
   git push -u origin "$(git branch --show-current)"
   ```
   Título do PR igual ao da issue. No corpo, `Closes #<número>` para a issue fechar sozinha no merge. O template do PR já traz o checklist.
6. Peça revisão a uma pessoa de **outra frente**. Quem revisa roda a branch na própria máquina antes de aprovar.
7. Com 1 aprovação e o CI verde, faça **Squash and merge** e apague a branch.

### Mensagens de commit

Padrão Conventional Commits, em português, no imperativo, sem ponto final:

```
feat: cria cadeia de responsabilidade da triagem
fix: corrige mediana com quantidade par de registros
test: cobre regra sem categoria na triagem
refactor: extrai normalização de texto para classe própria
docs: descreve variáveis do .env no README
chore: adiciona dependência do Spring Security
```

### Proibido

- Commit ou push direto na `main`.
- `git add .` ou `git add -A` sem olhar o `git status` antes. Adicione os arquivos da sua tarefa pelo nome.
- `git push --force` em branch de outra pessoa. Na sua própria branch, depois de um rebase, use `git push --force-with-lease`.
- Commitar `.env`, senha, connection string, a pasta `target/` ou arquivos da IDE (`.idea/`, `.vscode/`).
- Alterar arquivo de outra frente sem combinar antes na issue dona daquele arquivo (tabela da seção 9).
- PR que mistura duas tarefas. Achou um problema fora da sua tarefa? Abra uma issue nova.

## 3. Arquitetura MVC

```
br.ucsal.prereq
├── model/        entidades JPA e enums
├── repository/   interfaces que estendem JpaRepository
├── service/      regras de negócio; subpacotes por funcionalidade (triagem/, prazo/, espera/, admin/)
├── controller/   @Controller das telas públicas; área administrativa em controller/admin/
├── dto/          objetos de formulário, com Bean Validation
├── exception/    exceções da aplicação
└── config/       SecurityConfig, CargaInicial

src/main/resources/
├── templates/<funcionalidade>/*.html   views Thymeleaf
├── templates/fragments/layout.html     cabeçalho, menu, mensagens e rodapé
├── templates/error/404.html, 500.html
├── static/css/                         pico.min.css e app.css
└── dados/                              CSV da carga inicial
```

O caminho que uma requisição percorre é sempre o mesmo:

```
navegador → Controller → Service → Repository → banco
                ↓
              Model → View (Thymeleaf) → HTML
```

Regras que não se quebram:

- **Controller não acessa Repository.** Ele recebe a requisição, chama um Service e escolhe a view.
- **Regra de negócio fica no Service.** Se um `if` decide algo do domínio (qual categoria, quantos dias, se pode excluir), ele está no Service.
- **View só exibe.** Nada de cálculo ou decisão de negócio no HTML; o Controller entrega tudo pronto no `Model`.
- **Entidade não vai direto para formulário.** O formulário usa um DTO de `dto/`; o Service converte o DTO em entidade.

## 4. Nomes

O domínio é em português. Os sufixos do Spring ficam em inglês, como no próprio framework.

| O quê | Padrão | Exemplo |
|---|---|---|
| Entidade | substantivo no singular | `TipoRequerimento`, `RegraTriagem` |
| Enum | singular, constantes em maiúsculas | `GrupoRequerimento.FINANCEIRO` |
| Repository | `<Entidade>Repository` | `SetorRepository` |
| Service | `<Assunto>Service` | `TriagemService` |
| Controller público | `<Assunto>Controller` | `CategoriaController` |
| Controller admin | `Admin<Assunto>Controller` | `AdminSetorController` |
| DTO de formulário | `<Assunto>Form` | `TriagemForm`, `CategoriaForm` |
| Métodos | verbo no infinitivo | `buscar`, `listarAtivas`, `salvar`, `desativar` |
| Rotas | minúsculas, plural, com hífen | `/categorias/{id}`, `/admin/regras/novo`, `/prazos/contribuir` |
| Templates | `templates/<funcionalidade>/<tela>.html` | `triagem/formulario.html`, `categorias/detalhe.html` |
| Atributos do Model | camelCase em português | `categoria`, `categorias`, `form` |
| Testes | `<ClasseTestada>Test` | `TriagemServiceTest` |
| Métodos de teste | `deve<Resultado>Quando<Situação>` | `deveIndicarEstagioQuandoDescricaoCitaEstagio` |

Sem acento e sem `ç` em nomes de classe, método, variável, rota ou arquivo. Texto que aparece na tela tem acento normalmente.

## 5. Padrão por camada

### Entidade (`model/`)

```java
@Entity
public class Setor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    private String descricao;

    protected Setor() {
    }

    public Setor(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    // getters e setters
}
```

- `id` do tipo `Long` com `GenerationType.IDENTITY`.
- Construtor sem argumentos `protected` (o JPA exige) e um construtor com os campos obrigatórios.
- Getters e setters escritos à mão ou gerados pela IDE. O projeto não usa Lombok.
- Enum sempre com `@Enumerated(EnumType.STRING)`. Com o padrão (`ORDINAL`), reordenar o enum corrompe os dados salvos.
- Datas com `LocalDate` (ou `LocalDateTime` se a hora importar). Nunca `java.util.Date`.
- Não sobrescreva `equals`/`hashCode` sem combinar com a P1.
- Precisa de campo novo numa entidade de outra frente? Comente na issue dona antes.

### Repository (`repository/`)

```java
public interface SetorRepository extends JpaRepository<Setor, Long> {

    Optional<Setor> findByNome(String nome);

    boolean existsByNome(String nome);
}
```

- Prefira consultas derivadas do nome do método (`findBy...`, `existsBy...`, `countBy...`).
- `@Query` em JPQL só quando o nome do método ficar ilegível. SQL nativo, não.
- Repository não tem regra de negócio.

### Service (`service/`)

```java
@Service
@Transactional(readOnly = true)
public class SetorService {

    private final SetorRepository setorRepository;

    public SetorService(SetorRepository setorRepository) {
        this.setorRepository = setorRepository;
    }

    public List<Setor> listar() {
        return setorRepository.findAll(Sort.by("nome"));
    }

    public Setor buscar(Long id) {
        return setorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Setor não encontrado"));
    }

    @Transactional
    public Setor salvar(SetorForm form) {
        if (setorRepository.existsByNome(form.getNome())) {
            throw new RegraNegocioException("Já existe um setor com esse nome");
        }
        return setorRepository.save(new Setor(form.getNome(), form.getDescricao()));
    }
}
```

- Injeção de dependência **pelo construtor**, com campo `final`. Não use `@Autowired` em campo.
- `@Transactional(readOnly = true)` na classe e `@Transactional` nos métodos que gravam.
- A aplicação roda com `spring.jpa.open-in-view=false`: a conexão com o banco fecha quando o Service termina. Tudo o que a view vai mostrar (listas `@ManyToMany`, por exemplo) precisa ser carregado **dentro** do Service, com `@EntityGraph` ou `JOIN FETCH` no repository. Se aparecer `LazyInitializationException`, é isso que faltou.
- Erro de negócio vira exceção (seção 5.7), não `null` nem `boolean` de retorno.
- Os padrões de projeto da proposta (Chain of Responsibility, State, Strategy, Observer) moram nos subpacotes de `service/`.

### DTO de formulário (`dto/`)

```java
public class SetorForm {

    @NotBlank(message = "Informe o nome do setor")
    @Size(max = 100, message = "O nome pode ter até 100 caracteres")
    private String nome;

    private String descricao;

    // getters e setters
}
```

- Classe com construtor vazio, getters e setters. É o formato que o Thymeleaf usa no `th:field`.
- Toda anotação de validação leva `message` em português; a mensagem padrão sai em inglês dependendo da máquina.
- Para resultados internos que não são formulário (`ResultadoTriagem`, `EtapaLida`), use `record`.

### Controller (`controller/`)

```java
@Controller
@RequestMapping("/admin/setores")
public class AdminSetorController {

    private final SetorService setorService;

    public AdminSetorController(SetorService setorService) {
        this.setorService = setorService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("setores", setorService.listar());
        return "admin/setores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new SetorForm());
        return "admin/setores/formulario";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("form") SetorForm form, BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "admin/setores/formulario";
        }
        try {
            setorService.salvar(form);
        } catch (RegraNegocioException e) {
            result.reject("negocio", e.getMessage());
            return "admin/setores/formulario";
        }
        redirect.addFlashAttribute("sucesso", "Setor salvo");
        return "redirect:/admin/setores";
    }
}
```

- `@Controller`, nunca `@RestController`: o projeto devolve páginas, não JSON.
- O método devolve o nome do template (sem `.html`).
- Formulário com erro volta para a mesma view, sem redirect, para manter o que o usuário digitou.
- Formulário salvo com sucesso faz **redirect** (padrão Post/Redirect/Get), assim F5 não reenvia o formulário. A mensagem vai por `addFlashAttribute("sucesso", ...)` ou `("erro", ...)`; o layout já exibe as duas.
- Ação que altera dados é sempre `POST`, inclusive excluir e desativar. `GET` só lê.
- `BindingResult` vem **logo depois** do parâmetro `@Valid`; em outra posição o Spring lança exceção em vez de preencher os erros.

### View (`templates/`)

```html
<!DOCTYPE html>
<html lang="pt-BR" xmlns:th="http://www.thymeleaf.org">
<head th:replace="~{fragments/layout :: head('Setores')}"></head>
<body>
<header th:replace="~{fragments/layout :: menu}"></header>
<main class="container">
    <div th:replace="~{fragments/layout :: mensagens}"></div>

    <h1>Novo setor</h1>
    <form th:action="@{/admin/setores}" th:object="${form}" method="post">
        <p th:if="${#fields.hasErrors('global')}" th:errors="*{global}"></p>

        <label for="nome">Nome</label>
        <input id="nome" th:field="*{nome}" th:aria-invalid="${#fields.hasErrors('nome')} ? 'true'">
        <small th:if="${#fields.hasErrors('nome')}" th:errors="*{nome}"></small>

        <button type="submit">Salvar</button>
    </form>
</main>
<footer th:replace="~{fragments/layout :: rodape}"></footer>
</body>
</html>
```

- Toda página usa os fragments do layout. Ninguém edita `layout.html` sem combinar com o dono.
- Visual: só as classes e elementos do Pico.css. CSS próprio, se precisar, vai em `static/css/app.css`. Sem `style=""` no HTML.
- Links e ações com `@{...}` (`th:href="@{/categorias/{id}(id=${c.id})}"`), nunca o endereço escrito à mão.
- Texto vindo do usuário com `th:text`, que escapa o HTML. `th:utext` está proibido.
- Datas com `${#temporals.format(data, 'dd/MM/yyyy')}`.
- JavaScript só quando inevitável (o botão "copiar" do modelo de texto), pequeno e no próprio template. Sem frameworks.

### Exceções (`exception/`)

Duas exceções, criadas na #4:

- `RecursoNaoEncontradoException`: o registro pedido não existe. Anotada com `@ResponseStatus(HttpStatus.NOT_FOUND)`, faz o Spring mostrar a página `error/404.html`.
- `RegraNegocioException`: o usuário pediu algo que o domínio não permite (nome duplicado, regra com categoria e canal ao mesmo tempo). O Controller captura e mostra a mensagem no formulário.

Não capture `Exception` genérica para esconder erro. Se não sabe tratar, deixe subir.

## 6. Testes

Cada PR com código traz os próprios testes. O mínimo:

| Camada | Como testar | Anotações |
|---|---|---|
| Service e classes de `service/` | teste de unidade, sem Spring, com Mockito para os repositories | `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks` |
| Controller | teste da camada web, com o Service simulado | `@WebMvcTest(XController.class)` e `@MockitoBean` |
| Repository com consulta própria | banco H2 em memória | `@DataJpaTest` |
| Área administrativa | igual ao Controller, com usuário logado | `@WithMockUser` |

- Arquivo de teste no **mesmo pacote** da classe testada, em `src/test/java`.
- Use `@MockitoBean`. O antigo `@MockBean` não existe mais no Spring Boot 4.
- Depois que a #8 (Spring Security) entrar, todo `@WebMvcTest` precisa de `@Import(SecurityConfig.class)`, e toda requisição `POST` no teste leva `.with(csrf())`. Sem isso o teste recebe 401 ou 403.
- Estrutura de cada teste: preparar, executar, conferir (pule uma linha entre as três partes).
- Um comportamento por teste. Teste chamado `deveFuncionar` não diz nada; diga o resultado e a situação.
- Os testes rodam com o H2 de `src/test/resources/application.properties` e não podem depender do Neon nem da internet.
- `./mvnw verify` verde na sua máquina antes do PR. O CI roda o mesmo comando e bloqueia o merge se falhar.

## 7. Configuração e segredos

- Configuração nova vai em `src/main/resources/application.properties` (dono: P1; combine na issue).
- Valor secreto ou que muda de máquina para máquina entra como variável: `${NOME_DA_VARIAVEL}` no `.properties`, o valor real no `.env` e o nome com valor fictício no `.env.example`.
- O teste precisa do mesmo valor? Coloque um fixo em `src/test/resources/application.properties`.

## 8. Banco de dados

- As tabelas são criadas pelo Hibernate a partir das entidades (`ddl-auto=update`). Não crie tabela à mão no Neon.
- O `update` cria tabelas e colunas novas, mas **não** apaga nem renomeia coluna. Se você renomear um campo, a coluna antiga continua lá; avise a equipe no PR para cada um recriar a própria branch do Neon.
- Nomes de tabela e coluna saem em `snake_case` automaticamente (`nomeNoPortal` vira `nome_no_portal`).
- Nunca rode `DROP`, `TRUNCATE` ou `DELETE` sem `WHERE` na branch principal do Neon; ela é a da demonstração.

## 9. Arquivos compartilhados

Estes arquivos são usados por várias frentes e têm um dono. Mudança neles passa por comentário na issue do dono.

| Arquivo | Dono | Observação |
|---|---|---|
| `pom.xml` | P1 | A P2 adiciona o Spring Security na #8; outras dependências, só combinando |
| `src/main/resources/application.properties` | P1 | |
| `src/test/resources/application.properties` | P1 | A P2 acrescenta o usuário admin dos testes na #8 |
| `templates/fragments/layout.html` e `static/css/` | P4 | O menu já nasce com todas as rotas |
| `config/CargaInicial.java` e `resources/dados/` | P1 (código) e P6 (dados) | |
| classes de `model/` e `exception/` | quem criou | Campo novo: comente na issue dona |
| `.github/workflows/` | P1 | |
| `CONTRIBUTING.md` | P1 | Mudança de padrão é decidida pela equipe |

## 10. Estilo de código

- Formatação: 4 espaços, UTF-8, quebra de linha LF. O `.editorconfig` configura a IDE sozinho; use o formatador padrão do VS Code ou do IntelliJ antes de commitar.
- Imports explícitos, sem `import ...*`. Remova os que não são usados.
- Comentário só para explicar o **porquê** de algo que não é óbvio. Não comente o que o código já diz, e não deixe código comentado: o git guarda o histórico.
- Nada de `System.out.println`. Para log, use SLF4J (`private static final Logger log = LoggerFactory.getLogger(...)`), e nunca registre texto digitado pelo aluno, histórico colado ou credenciais.
- Métodos curtos, com um propósito. Se precisou de um comentário para separar partes de um método, provavelmente são dois métodos.
- Prefira `Optional` a devolver `null` nos métodos de busca.
- Sem abstração antecipada: só crie interface ou classe-base quando houver duas implementações de verdade ou quando a proposta pedir o padrão.

## 11. Quando a tarefa está pronta

- [ ] Todos os itens do "Critério de aceite" da issue estão marcados.
- [ ] `./mvnw verify` verde na sua máquina e no CI.
- [ ] Testei a tela no navegador, inclusive o caso de erro do formulário.
- [ ] Sem `.env`, senha, `target/`, `.idea/` ou `.vscode/` no PR.
- [ ] PR revisado e aprovado por alguém de outra frente.
- [ ] Merge por squash, branch apagada e card em **Done** (o `Closes #` move sozinho).

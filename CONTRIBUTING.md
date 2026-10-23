# Padrão de desenvolvimento do PreReq

Vale para toda a equipe. Se algo não estiver coberto aqui, pergunte na issue da tarefa antes de criar um padrão novo.

## 1. Preparar a máquina

- JDK 21 e Git. O Maven vem pelo `./mvnw`.
- `cp .env.example .env` e preencha com as credenciais que a P1 enviar em mensagem privada.
- Cada pessoa usa a própria branch do Neon (nome = seu usuário do GitHub) e coloca a `DB_URL` dela no `.env`.
- Rodar: `./mvnw spring-boot:run`, em `http://localhost:8081`.
- O `.env` nunca vai para o git. Senha commitada por engano: avise a P1 na hora para trocá-la no Neon.

## 2. Git

- Uma tarefa = uma branch = um PR.
- Branch criada da `main` atualizada, com o nome indicado na issue: `tipo/<nº-da-issue>-descricao-curta`.
- Commits pequenos, em Conventional Commits, em português e no imperativo: `feat:`, `fix:`, `test:`, `refactor:`, `docs:`, `chore:`, `ci:`.
- Antes do PR: `git rebase origin/main` e `./mvnw verify` verde. Conflito se resolve na sua branch.
- PR com o título da issue e `Closes #<nº>` no corpo.
- Revisão de 1 pessoa de outra frente, que roda a branch antes de aprovar.
- Merge só com CI verde, por **Squash and merge**, apagando a branch depois.

**Proibido:**
- commit ou push direto na `main`;
- `git add .` / `git add -A` sem conferir o `git status`;
- `push --force` em branch alheia (na sua, depois de rebase, use `--force-with-lease`);
- commitar `.env`, senhas, `target/`, `.idea/` ou `.vscode/`;
- alterar arquivo de outra frente sem combinar na issue dona (seção 9);
- misturar duas tarefas no mesmo PR.

## 3. Arquitetura MVC

| Pacote | Conteúdo |
|---|---|
| `model/` | entidades JPA e enums |
| `repository/` | interfaces `JpaRepository` |
| `service/` | regras de negócio; subpacotes `triagem/`, `prazo/`, `espera/`, `admin/` |
| `controller/` | telas públicas; área administrativa em `controller/admin/` |
| `dto/` | objetos de formulário |
| `exception/` | exceções da aplicação |
| `config/` | segurança e carga inicial |
| `templates/<funcionalidade>/` | views Thymeleaf |

- Controller não acessa Repository: sempre passa pelo Service.
- Toda regra de negócio fica no Service.
- A view só exibe o que veio no `Model`.
- Formulário usa DTO, nunca a entidade.

## 4. Nomes

- Domínio em português; sufixos do Spring em inglês: `TriagemService`, `SetorRepository`, `CategoriaController`, `AdminSetorController`, `TriagemForm`.
- Métodos com verbo no infinitivo: `buscar`, `listar`, `salvar`, `desativar`.
- Rotas em minúsculas e no plural: `/categorias/{id}`, `/admin/regras/novo`.
- Templates em `templates/<funcionalidade>/<tela>.html`.
- Testes: classe `<Classe>Test`, método `deve<Resultado>Quando<Situação>`.
- Sem acento ou `ç` em nomes de código, rotas e arquivos. Texto da tela tem acento normalmente.

## 5. Regras por camada

**Entidade**
- `id` `Long` com `GenerationType.IDENTITY`.
- Construtor vazio `protected`, getters e setters. Sem Lombok.
- Enum sempre com `@Enumerated(EnumType.STRING)`.
- Datas com `LocalDate`/`LocalDateTime`.
- Campo novo em entidade de outra frente: combine na issue dona.

**Repository**
- Consultas derivadas do nome do método (`findBy`, `existsBy`).
- `@Query` só se o nome ficar ilegível. Sem SQL nativo.

**Service**
- Injeção pelo construtor, com campo `final`. Sem `@Autowired` em campo.
- `@Transactional(readOnly = true)` na classe e `@Transactional` nos métodos que gravam.
- Como `open-in-view` está desligado, carregue no Service tudo o que a view vai exibir (`@EntityGraph` ou `JOIN FETCH`). `LazyInitializationException` significa que isso faltou.
- Erro de negócio vira exceção, não `null`.

**DTO**
- Classe com construtor vazio, getters e setters.
- Validação com `message` em português.
- `record` só para resultados internos que não são formulário.

**Controller**
- `@Controller`, nunca `@RestController`.
- Formulário com erro volta para a mesma view.
- Formulário salvo faz redirect e mostra o aviso com `addFlashAttribute("sucesso" | "erro", ...)`.
- Ação que altera dados é sempre `POST`.
- `BindingResult` vem logo depois do parâmetro `@Valid`.

**View**
- Toda página usa os fragments do layout (`head`, `menu`, `mensagens`, `rodape`).
- Só Pico.css; CSS próprio em `static/css/app.css`; sem `style=""`.
- Links com `@{...}`; texto do usuário com `th:text` (`th:utext` proibido).
- Datas com `#temporals.format(data, 'dd/MM/yyyy')`.
- JavaScript só quando inevitável, pequeno e sem framework.

**Exceções** (criadas na #4)
- `RecursoNaoEncontradoException`: registro inexistente; gera a página 404.
- `RegraNegocioException`: pedido que o domínio não permite; o Controller mostra a mensagem no formulário.
- Não capture `Exception` genérica.

## 6. Testes

- Todo PR com código traz testes.
- Service: teste de unidade com Mockito.
- Controller: `@WebMvcTest` com `@MockitoBean` (o `@MockBean` não existe no Spring Boot 4).
- Repository com consulta própria: `@DataJpaTest`.
- Admin: `@WithMockUser`.
- Depois da #8: `@Import(SecurityConfig.class)` nos `@WebMvcTest` e `.with(csrf())` em todo `POST` de teste.
- Teste no mesmo pacote da classe testada, um comportamento por teste.
- Testes usam H2 e não dependem do Neon nem da internet.

## 7. Configuração e segredos

- Propriedade nova em `application.properties`, combinada com a P1.
- Segredo como `${VARIAVEL}` no `.properties`, valor real no `.env` e nome no `.env.example`.
- Valor fixo para testes em `src/test/resources/application.properties`.

## 8. Banco

- Tabelas criadas pelo Hibernate (`ddl-auto=update`). Não crie tabela à mão.
- O `update` não apaga nem renomeia coluna: renomeou campo, avise no PR para cada um recriar a branch do Neon.
- Sem `DROP`, `TRUNCATE` ou `DELETE` sem `WHERE` na branch principal do Neon (é a da demonstração).

## 9. Arquivos compartilhados

| Arquivo | Dono |
|---|---|
| `pom.xml` | P1 (a P2 adiciona o Spring Security na #8) |
| `application.properties` (main e test) | P1 |
| `templates/fragments/layout.html` e `static/css/` | P4 |
| `config/CargaInicial.java` e `resources/dados/` | P1 (código) e P6 (dados) |
| classes de `model/` e `exception/` | quem criou |
| `.github/workflows/` e este arquivo | P1 |

## 10. Estilo

- 4 espaços, UTF-8, LF (o `.editorconfig` configura a IDE).
- Imports explícitos, sem `*`.
- Comentário só para explicar o porquê. Sem código comentado.
- Sem `System.out`; log com SLF4J e nunca com dados do aluno ou credenciais.
- Métodos curtos; `Optional` em vez de `null` nas buscas.
- Sem abstração antecipada: interface ou classe-base só com duas implementações reais ou quando a proposta pede o padrão.

## 11. Tarefa pronta

- [ ] Critérios de aceite da issue atendidos
- [ ] `./mvnw verify` verde local e no CI
- [ ] Testado no navegador, inclusive os erros de formulário
- [ ] Nada de `.env`, senha ou arquivo de IDE no PR
- [ ] Aprovado por alguém de outra frente
- [ ] Squash and merge e branch apagada

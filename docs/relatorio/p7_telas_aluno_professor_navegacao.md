# P7 — Interface: Tela principal, Aluno e Professor

Documentação da parte do P7 no projeto, para ser aproveitada no relatório final
(seções de Conceitos de POO, Bibliotecas e Manual do Usuário).

| Task | Título | Branch |
| --- | --- | --- |
| #15 | Rascunho das telas e esqueleto Swing (Aluno e Professor) | `feature/#15-esqueleto-aluno-professor` |
| #18 | Tela principal e navegação | `feature/#18-tela-principal-navegacao` |
| #19 | Telas de Aluno e Professor ligadas ao núcleo | `feature/#19-telas-aluno-professor` |

As três branches são encadeadas (#18 parte da #15 e #19 parte da #18),
porque cada task continua o trabalho da anterior.

---

## 1. Ponto de partida (análise do repositório em 21/11)

O que já existia na `develop` e foi **reaproveitado sem alteração**:

* `view/Estilo` (P8): fontes, cores, botões, tabela e formulário padrão. As telas do P7 usam
  só esses métodos, por isso ficam com o mesmo visual das abas de Curso, Disciplina e Turma.
* `view/DadosFalsos` (P8): nomes de alunos e professores usados no esqueleto da #15.
* `model/Pessoa`, `Aluno`, `Professor` (P3): construtores, getters e setters usados nas telas.
* `exception/UniversidadeException` (P6/P5): base das exceções do domínio, usada para
  identificar erros de validação ao mostrar a mensagem.

O que **ainda não existia** e do qual as tasks #18 e #19 dependem:

| Classe | Task / responsável | Como o P7 usa |
| --- | --- | --- |
| `model/Matricula` | #10 (P5) | `Aluno` e `Turma` já referenciam a classe: **sem ela a `develop` não compila** |
| `model/Universidade` | #8 (P4) | instância única compartilhada entre as telas |
| `persistence/RepositorioArquivo` | #13 (P6) | `carregar()` ao abrir e `salvar(u)` no menu/botão/fechar |
| `exception/CpfInvalidoException` | #12 (P6) | se for lançada pelo núcleo, a tela mostra a mensagem |

### Métodos que as telas do P7 chamam (contrato com P4 e P6)

```java
// Universidade (#8)
List<Aluno> getAlunos();              // cópia ou lista imutável
List<Professor> getProfessores();
void cadastrarAluno(Aluno a);         // já está no diagrama de classes
void removerAluno(Aluno a);
void cadastrarProfessor(Professor p);
void removerProfessor(Professor p);

// RepositorioArquivo (#13)
new RepositorioArquivo();
Universidade carregar();              // Universidade vazia se o arquivo não existir
void salvar(Universidade u);
```

Se algum nome ficar diferente na versão oficial, basta ajustar a chamada nas classes
`TelaPrincipal`, `AlunoPanel` e `ProfessorPanel`. Os `catch (Exception ex)` das telas aceitam
tanto exceções verificadas (ex.: `CpfInvalidoException`) quanto `IllegalArgumentException`,
então o código continua compilando independentemente de quais exceções os métodos declararem.

Para compilar e testar antes desses merges foram usados *stubs* locais dessas três classes
(mesma prática do `logs_testes.md`). Eles **não foram commitados**.

---

## 2. O que foi feito

### Task #15 — Esqueleto Swing (Aluno e Professor)

* `view/AlunoPanel` e `view/ProfessorPanel` com formulário, `JTable` e botões Salvar, Limpar e Excluir,
  usando dados falsos (sem depender do núcleo).
* `TelaPrincipal` com as abas **Alunos** e **Professores** (os `TODO P7` deixados pelo P8) e um
  `JMenuBar`: *Arquivo*, *Cadastros*, *Acadêmico*, *Ajuda*. Cada item de menu abre a aba correspondente.
* Protótipo em `docs/screenshots/prototipo/` (screenshots e layout).

Critérios de aceite:

- [x] Protótipo das telas (em `docs/screenshots/prototipo/`, falta aprovação do grupo)
- [x] `JFrame` principal com menu
- [x] Telas de aluno e professor com `JTable` e formulário

### Task #18 — Tela principal e navegação

* `TelaPrincipal` carrega a `Universidade` com `RepositorioArquivo.carregar()` ao abrir e guarda
  **uma única instância**, repassada para as telas (`new AlunoPanel(universidade)`, ...).
  Fica disponível em `getUniversidade()`.
* **Salvar** em três lugares: menu *Arquivo → Salvar* (Ctrl+S), botão *Salvar dados* na barra
  inferior e ao fechar a janela. A barra mostra o horário do último salvamento.
* **Fechar a janela pergunta se quer salvar**: *Sim* salva e fecha, *Não* fecha sem salvar e
  *Cancelar* continua no sistema. Se der erro ao salvar, a janela não fecha (para não perder dados).
  *Arquivo → Sair* (Ctrl+Q) passa pela mesma pergunta.
* Interface nova `view/Atualizavel` (`atualizar()`): ao trocar de aba, a aba aberta recarrega suas
  listas. Ex.: um aluno cadastrado em *Alunos* aparece em *Matrículas* sem reiniciar o sistema.
  As telas do P8 podem implementar a interface quando forem ligadas ao núcleo (#20/#21).
* `Main.java` **não foi alterado**: ele continua chamando `new TelaPrincipal()`.

Critérios de aceite:

- [x] Todas as telas acessíveis pelo menu
- [x] Uma única instância de `Universidade` usada em todas as telas
  (Aluno e Professor já recebem; Curso, Disciplina, Turma, Matrícula e Notas passam a receber
  quando o P8 fizer as tasks #20/#21 — há um `TODO P8` no ponto exato)
- [x] Fechar a janela pergunta se quer salvar

### Task #19 — Telas de Aluno e Professor ligadas ao núcleo

* As telas deixam de usar dados falsos e passam a trabalhar com objetos `Aluno` e `Professor`
  guardados na `Universidade`.
* **Cadastro**: valida campos vazios, e-mail sem `@`, data de ingresso inexistente
  (ex.: 31/02), matrícula/SIAPE repetidos e CPF repetido (com ou sem pontuação).
* **Listagem** em `JTable` (Aluno: matrícula, nome, CPF, e-mail, ingresso e quantidade de
  matrículas; Professor: SIAPE, nome, CPF, e-mail, titulação e quantidade de turmas).
* **Edição**: clicar na linha carrega o formulário; matrícula/SIAPE ficam bloqueados (identificador).
* **Exclusão**: pede confirmação; não deixa excluir aluno com matrículas nem professor com turmas.
* **Erros** sempre em `JOptionPane` (nada no console).
* Os dados permanecem depois de salvar e reabrir (via #18).

Critérios de aceite:

- [x] Cadastro com validação de campos vazios
- [x] Listagem em `JTable`
- [x] Edição e exclusão
- [x] Erros mostrados com `JOptionPane`

---

## 3. Conceitos de POO aplicados na interface (para o relatório)

| Conceito | Onde | Trecho |
| --- | --- | --- |
| Herança | `TelaPrincipal extends JFrame`, `AlunoPanel extends JPanel` | as telas herdam todo o comportamento de janela/painel do Swing |
| Interface | `Atualizavel` | `public class AlunoPanel extends JPanel implements Atualizavel` |
| Polimorfismo | `TelaPrincipal.atualizarAbaAtual()` | `if (aba instanceof Atualizavel a) a.atualizar();` a mesma chamada executa a versão de cada tela |
| Encapsulamento | campos `private final` dos painéis | o estado da tela só muda pelos métodos `salvar()`, `excluir()` e `atualizar()` |
| Composição | `TelaPrincipal` cria e contém as abas | os painéis só existem dentro da janela principal |
| Associação | painéis guardam a referência à `Universidade` | todos apontam para o **mesmo** objeto |
| Tratamento de exceções | `salvar()`, `excluir()`, `carregarUniversidade()` | `catch (Exception ex) { mostrarErro(ex); }` com `JOptionPane` |

Exemplo (polimorfismo pela interface):

```java
// TelaPrincipal
abas.addChangeListener(e -> atualizarAbaAtual());

private void atualizarAbaAtual() {
    Component aba = abas.getSelectedComponent();
    if (aba instanceof Atualizavel atualizavel) {
        atualizavel.atualizar();   // AlunoPanel e ProfessorPanel recarregam a própria JTable
    }
}
```

## 4. Bibliotecas usadas

* `javax.swing`: `JFrame`, `JPanel`, `JTabbedPane`, `JMenuBar`/`JMenu`/`JMenuItem`, `JTable`,
  `DefaultTableModel`, `JTextField`, `JComboBox`, `JButton`, `JOptionPane`, `KeyStroke`, `BorderFactory`.
* `java.awt`: `BorderLayout`, `FlowLayout`, `WindowAdapter`/`WindowEvent` (pergunta ao fechar),
  `KeyEvent`/`InputEvent` (atalhos Ctrl+S, Ctrl+Q).
* `java.text.SimpleDateFormat`: leitura e exibição da data de ingresso (`dd/MM/yyyy`, não tolerante).
* `java.time`: horário do último salvamento na barra inferior.

## 5. Manual do usuário (Alunos, Professores e navegação)

Screenshots em `docs/screenshots/manual/`.

**Navegação.** Use as abas ou o menu: *Cadastros* (Alunos, Professores, Cursos, Disciplinas,
Turmas) e *Acadêmico* (Matrículas, Notas).

**Cadastrar aluno.** Aba *Alunos* → preencha matrícula, nome, CPF, e-mail e data de ingresso
(vem preenchida com a data de hoje) → *Salvar*. O aluno aparece na tabela.

**Editar.** Clique no aluno na tabela, altere os campos e clique em *Salvar*.
A matrícula não pode ser alterada. *Limpar* volta para o modo de cadastro.

**Excluir.** Selecione o aluno → *Excluir* → confirme. Alunos com matrículas em turmas não
podem ser excluídos.

**Professores.** Mesmo funcionamento, com SIAPE e titulação (Graduado, Especialista, Mestre,
Doutor, Pós-Doutor). Professores que ministram turmas não podem ser excluídos.

**Salvar.** *Arquivo → Salvar* (Ctrl+S) ou botão *Salvar dados*. Ao fechar, o sistema pergunta
se deve salvar. Os dados ficam em `data/universidade.dat`.

## 6. Testes realizados

Teste automatizado da interface (Swing rodando num display virtual, cliques via `doClick()` e
leitura das mensagens dos `JOptionPane`), usando os stubs locais de `Universidade`,
`Matricula` e `RepositorioArquivo`. **26 de 26 verificações passaram.**

| ID | Cenário | Resultado esperado | Status |
| --- | --- | --- | --- |
| P7-01 | Primeira execução sem `universidade.dat` | Universidade vazia, sem erro | Passou |
| P7-02 | Salvar aluno com campos vazios | Aviso "Preencha todos os campos" | Passou |
| P7-03 | Cadastrar aluno válido | Aparece na `Universidade` e na `JTable`; formulário limpo | Passou |
| P7-04 | Matrícula repetida (maiúsc./minúsc.) | Aviso de matrícula duplicada, nada cadastrado | Passou |
| P7-05 | CPF repetido sem pontuação | Aviso de CPF duplicado | Passou |
| P7-06 | Data 31/02/2026 | Aviso de data inválida | Passou |
| P7-07 | E-mail sem `@` | Aviso de e-mail inválido | Passou |
| P7-08 | Selecionar linha | Formulário preenchido, matrícula bloqueada | Passou |
| P7-09 | Editar nome | Objeto `Aluno` e tabela atualizados, sem duplicar | Passou |
| P7-10 | Excluir sem seleção / cancelar / confirmar | Aviso / mantém / remove | Passou |
| P7-11 | Cadastrar e editar professor (titulação) | Professor salvo e alterado | Passou |
| P7-12 | SIAPE repetido | Aviso de SIAPE duplicado | Passou |
| P7-13 | Item de menu | Abre a aba certa; menu com 4 grupos | Passou |
| P7-14 | Trocar de aba após mudar dados no núcleo | Lista recarregada (`Atualizavel`) | Passou |
| P7-15 | Salvar, fechar e reabrir | Alunos e professores continuam lá | Passou |

Ainda falta testar com as classes oficiais, depois dos merges de #8, #10 e #13.

## 7. Pendências e próximos passos

* Ao entrar `Universidade` (#8), `Matricula` (#10) e `RepositorioArquivo` (#13) na `develop`:
  conferir os nomes dos métodos da seção 1, compilar e abrir os PRs da #18 e da #19.
* Tasks relacionadas, ainda sem responsável, que continuam estas telas:
  #19.1 (componente de busca de aluno), #19.2 (matrícula gerada e curso no cadastro de aluno),
  #19.3 (abas Professores e Departamentos) e #22 (8 abas na ordem final).
  Quando a #11.1 adicionar `codigoMatricula` e `Curso` ao `Aluno`, o campo *Matrícula* da tela vira
  somente leitura, preenchido por `Universidade.gerarMatricula(curso)`.

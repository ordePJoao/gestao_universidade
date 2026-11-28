# Sistema de Gestão de Universidade (`gestao_universidade`)

Sistema acadêmico de **Gestão de Universidade** desenvolvido como Projeto Final da disciplina de **Programação Orientada a Objetos (POO)** da **Universidade Federal de Uberlândia (UFU)** — Faculdade de Computação (FACOM). Permite gerenciar alunos, professores, departamentos, cursos, disciplinas, turmas, matrículas e avaliações, com interface gráfica desktop e persistência binária automática em arquivo.

Projeto 100% Java, com interface gráfica em `javax.swing` e suíte de testes automatizados em **JUnit 5**.

---

## Funcionalidades

* **Gestão de Alunos:** cadastro com geração automática de matrícula (`ANO + CURSO + 4 dígitos`), validação de CPF (11 dígitos), vinculação a curso, busca normalizada (por nome sem acento, CPF ou matrícula), edição e exclusão.
* **Gestão de Professores:** cadastro com identificador SIAPE, titulação e validação de CPF, além de listagem, edição e exclusão.
* **Gestão de Departamentos (Unidades Acadêmicas):** cadastro de departamentos/faculdades, vinculação e desvinculação de professores sem excluir o docente da universidade.
* **Gestão de Cursos e Disciplinas:** cadastro de cursos com carga horária e montagem da grade curricular com disciplinas vinculadas.
* **Gestão de Turmas:** abertura de turmas por disciplina, semestre, professor responsável e controle dinâmico de vagas totais, ocupadas e disponíveis.
* **Matrículas e Controle de Vagas:** matrícula de alunos em turmas com bloqueio de duplicidade (`MatriculaDuplicadaException`), bloqueio de turma lotada (`TurmaCheiaException`) e trancamento/cancelamento de matrícula (`CANCELADA`) liberando vaga automaticamente.
* **Lançamento de Notas e Consolidação:** registro e edição de avaliações (`Prova`, `Trabalho`, `Seminário`, `Lista`), cálculo automático de média aritmética e consolidação da situação acadêmica (`ATIVA`, `APROVADA` para média $\ge 6,0$ ou `REPROVADA` para média $< 6,0$).
* **Persistência Binária Automática:** salvamento automático após operações e atalho global (`Ctrl+S`), com carregamento transparente na inicialização.

---

## Tecnologias e Bibliotecas

* **Java 17 (JDK 17+):** linguagem base do domínio, regras de negócio e persistência.
* **Apache Maven:** gerenciamento de dependências, build e execução de testes.
* **Swing (`javax.swing`) & AWT (`java.awt`):** interface gráfica desktop organizada em 8 abas (`Alunos`, `Professores`, `Departamentos`, `Cursos`, `Disciplinas`, `Turmas`, `Matrículas`, `Notas`) e menus de navegação.
* **Java I/O (`java.io.Serializable`, `ObjectOutputStream`, `ObjectInputStream`):** serialização binária nativa em disco.
* **JUnit 5 (`org.junit.jupiter`):** suíte de testes unitários e de integração (`55` casos de teste em `13` suítes).

---

## Como Executar

### Pré-requisitos
* **JDK 17** ou superior instalado.
* **Apache Maven** (ou IDE com suporte a Maven, como IntelliJ IDEA).

### Passos no Terminal
```bash
# 1. Clonar o repositório oficial
git clone https://github.com/ordePJoao/gestao_universidade.git
cd gestao_universidade

# 2. Compilar, rodar os testes e empacotar o projeto
mvn clean package

# 3. Executar a aplicação desktop
java -jar target/gestao-universidade-1.0.jar
```

Para executar apenas a suíte de testes automatizados:
```bash
mvn test
```

---

## Estrutura do Projeto

```text
gestao_universidade/
├── src/
│   ├── main/java/br/edu/universidade/
│   │   ├── Main.java               # Ponto de entrada da aplicação Swing
│   │   ├── model/                  # Entidades do domínio (Universidade, Pessoa, Aluno, Professor, Departamento, Curso, Disciplina, Turma, Matricula, Avaliacao, StatusMatricula, Exibivel)
│   │   ├── persistence/            # Persistência em arquivo binário (RepositorioArquivo, PersistenciaArquivo)
│   │   ├── exception/              # Hierarquia de exceções customizadas (UniversidadeException e subclasses)
│   │   └── view/                   # Telas e componentes Swing (TelaPrincipal, AlunoPanel, BuscaAlunoPanel, ProfessorPanel, DepartamentoPanel, CursoPanel, DisciplinaPanel, TurmaPanel, MatriculaPanel, NotasPanel, Estilo, Componentes, Atualizavel)
│   └── test/java/br/edu/universidade/
│       └── *Test.java              # 13 suítes de testes unitários e de integração em JUnit 5
├── data/                           # Arquivo binário gerado em execução (data/universidade.dat)
├── docs/
│   ├── logs_testes.md              # Registro e evidências dos 55 casos de teste automatizados
│   ├── relatorio/                  # Relatório técnico final em HTML e PDF (padrão ABNT)
│   ├── screenshots/                # Capturas de tela do manual do usuário
│   └── uml/                        # Diagramas UML de classes e casos de uso
├── pom.xml                         # Configuração Maven e dependências do JUnit 5
└── README.md
```

---

## Conceitos de POO Aplicados

* **Abstração e Herança:** `Pessoa` (classe abstrata) define atributos comuns (`nome`, `cpf`, `email`) e é estendida por `Aluno` e `Professor`.
* **Polimorfismo:** sobrescrita (`@Override`) de `getIdent()` (retornando matrícula em `Aluno` e SIAPE em `Professor`) e implementação polimórfica de `exibirDados()`.
* **Interfaces:** `Exibivel` no pacote `model` (padronizando a exibição textual das entidades) e `Atualizavel` no pacote `view` (sincronizando automaticamente as abas da interface).
* **Agregação e Composição:**
  * *Agregação:* `Universidade`, `Departamento`, `Curso`, `Disciplina` e `Turma` mantêm referências a entidades que possuem ciclo de vida independente.
  * *Composição:* `Matricula` instancia, gerencia e encapsula internamente sua lista de `Avaliacao` (`Collections.unmodifiableList`), de modo que avaliações não existem sem a matrícula.
* **Encapsulamento:** atributos privados/protegidos com validação de invariantes nos construtores e métodos modificadores (*getters* e *setters*).
* **Exceções Customizadas:** hierarquia herdando de `UniversidadeException` tratada na interface gráfica via diálogos amigáveis (`JOptionPane`):
  * `NotaInvalidaException`: lançada quando uma nota informada está fora do intervalo `[0.0, 10.0]`.
  * `MatriculaDuplicadaException`: lançada ao tentar matricular um aluno já inscrito na mesma turma.
  * `TurmaCheiaException`: lançada quando uma turma atinge o limite máximo de vagas.
  * `AlunoNaoEncontradoException`: lançada quando a consulta exata por matrícula não localiza o estudante.
  * `CpfInvalidoException`: lançada quando o CPF informado não contém 11 dígitos numéricos válidos.

---

## Persistência de Dados

O sistema utiliza **serialização binária nativa do Java** (`ObjectOutputStream` e `ObjectInputStream`) através da classe `RepositorioArquivo` para salvar e restaurar o estado completo da aplicação:

* **Arquivo padrão:** `data/universidade.dat`.
* **Grafo de objetos:** a instância raiz `Universidade` persiste em cascata todas as listas e relacionamentos entre departamentos, professores, cursos, disciplinas, alunos, turmas, matrículas e avaliações.
* **Contrato `Serializable`:** todas as entidades do domínio implementam `java.io.Serializable` com `private static final long serialVersionUID = 1L;`.
* **Primeira execução:** caso `data/universidade.dat` ainda não exista na máquina, o sistema cria automaticamente o diretório `data/` e inicia uma `Universidade` vazia sem erros.
* **Controle de versão:** o arquivo `data/universidade.dat` é ignorado pelo Git (`.gitignore`) por ser um artefato local de execução.

---

## Documentação e Evidências

* **Diagramas UML:** `docs/uml/`
* **Relatório Técnico Final (HTML / PDF ABNT):** `docs/relatorio/`
* **Manual e Capturas de Tela:** `docs/screenshots/`
* **Logs e Matriz de Testes:** `docs/logs_testes.md`

---

## Convenção de Trabalho (Git Flow)

* **Branches de funcionalidade:** `feature/#N-descricao` criada a partir de `develop`.
* **Proteção de branches:** nunca realizar commits diretos em `main` ou `develop`; toda integração ocorre via **Pull Request** revisado.
* **Commits:** mensagens curtas, objetivas e no imperativo (ex.: `Adiciona aba Departamentos`).
* **Higiene do repositório:** nenhum arquivo compilado (`target/`) ou dado local de execução (`data/*.dat`) deve ser versionado.
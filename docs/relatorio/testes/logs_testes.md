# Registro e Evidências de Testes — Gestão Universitária

Este documento registra o histórico consolidado dos cenários de teste unitários executados via **JUnit 5 + Maven** (`mvn test`) para validar as entidades do domínio, exceções e persistência antes da integração com a interface Swing.

---

## 1. Suíte Base do Domínio (`src/test/java/br/edu/universidade/DominioTest.java`)
* **Resultado da Execução:** `Tests run: 2, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CT01** | Polimorfismo de Identificação | `Pessoa`, `Aluno`, `Professor` | Instanciar `Aluno` e `Professor` como `Pessoa` e chamar `getIdent()` e `exibirDados()` | Retorna matrícula para `Aluno` e SIAPE para `Professor`, exibindo dados sem exceções | Ativo (Passou) |
| **CT02** | Validação de Notas | `Avaliacao`, `NotaInvalidaException` | Criar notas válidas (`8.5`, `9.0`) e inválidas (`-1.0`, `10.5`, `11.0`) | Aceita notas entre `0.0` e `10.0` e lança `NotaInvalidaException` fora do intervalo | Ativo (Passou) |
| **CT03** | Cálculo de Média e Situação *(Legado / Stub)* | `Matricula`, `Avaliacao`, `Aluno` | Adicionar duas avaliações (`8.0` e `6.0`) e consultar `calcularMedia()` e `getSituacao()` | Média igual a `7.0` e situação retornando `"APROVADO"` | Substituído por `MatriculaLeticiaTest` |

### Observação sobre Integração Parcial (CT03)
Para validar o fluxo de matrícula e cálculo de média antes do merge oficial da classe `Matricula`, foi utilizado inicialmente um *stub* local temporário. Após o merge do **PR #51**, o stub foi removido e o cenário foi expandido na suíte dedicada de `Matricula` abaixo.

---

## 2. Suíte `MatriculaLeticiaTest` — Validação do PR #51 (`src/test/java/br/edu/universidade/MatriculaLeticiaTest.java`)
* **Resultado da Execução:** `Tests run: 8, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR51-CT01** | Inicialização padrão | `Matricula`, `StatusMatricula` | Instanciar `Matricula(aluno, turma)` | Status `ATIVA`, data preenchida (`LocalDate`), lista de avaliações vazia e média `0.0` | Ativo (Passou) |
| **PR51-CT02** | Validação de nulos no construtor | `Matricula` | Instanciar `Matricula(null, turma)` e `Matricula(aluno, null)` | Lança `NullPointerException` impedindo estado inconsistente | Ativo (Passou) |
| **PR51-CT03** | Limites exatos de nota e média | `Matricula`, `Avaliacao` | Adicionar notas `0.0` e `10.0` via `adicionarAvaliacao(tipo, nota)` | Aceita ambos os extremos e calcula média igual a `5.0` | Ativo (Passou) |
| **PR51-CT04** | Bloqueio de avaliações inválidas | `Matricula`, `NotaInvalidaException` | Tentar adicionar nota `-0.1`, nota `10.1` e tipo `null` | Lança `NotaInvalidaException` para notas fora de `[0, 10]` e `NullPointerException` para tipo nulo | Ativo (Passou) |
| **PR51-CT05** | Encapsulamento da composição | `Matricula` | Chamar `getAvaliacoes().clear()` e `getAvaliacoes().remove(0)` | Lança `UnsupportedOperationException` (lista exposta é não modificável) | Ativo (Passou) |
| **PR51-CT06** | Cancelamento direto | `Matricula`, `StatusMatricula` | Chamar `mat.cancelar()` | Altera `status` e `getSituacao()` para `CANCELADA` | Ativo (Passou) |
| **PR51-CT07** | Igualdade (`equals` e `hashCode`) | `Matricula`, `Aluno`, `Turma` | Comparar matrículas de mesmo `(aluno, turma)` vs. alunos ou turmas diferentes | Retorna `true` apenas quando `Aluno` e `Turma` são iguais | Ativo (Passou) |
| **PR51-CT08** | Persistência binária (`Serializable`) | `Matricula`, `Avaliacao` | Gravar `Matricula` com avaliações em `ObjectOutputStream` e restaurar via `ObjectInputStream` | Restaura aluno, data, status e média (`9.0`) sem perda de dados | Ativo (Passou) |

---

## 3. Suíte `PersistenciaTest` — Validação da Issue #13 / #15 (`src/test/java/br/edu/universidade/PersistenciaTest.java`)
* **Resultado da Execução:** `Tests run: 6, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR13-CT01** | Persistência completa em cascata | `RepositorioArquivo`, `Universidade`, `Aluno`, `Professor`, `Curso`, `Disciplina`, `Turma`, `Matricula`, `Avaliacao` | Salvar e carregar `Universidade` populada com todas as entidades e notas em arquivo `.dat` | Restaura todas as listas, buscas por matrícula/SIAPE/código e média calculada (`9.0`) | Ativo (Passou) |
| **PR13-CT02** | Primeira execução (arquivo inexistente) | `RepositorioArquivo`, `Universidade` | Chamar `carregar()` apontando para caminho inexistente | Retorna nova instância de `Universidade` com todas as listas vazias, sem erro | Ativo (Passou) |
| **PR13-CT03** | Criação automática de diretórios | `RepositorioArquivo` | Salvar arquivo em subdiretório ainda não existente (`data/subpasta_nova/...`) | Cria diretórios pais via `mkdirs()` e grava o arquivo com sucesso | Ativo (Passou) |
| **PR13-CT04** | Tratamento de arquivo corrompido | `RepositorioArquivo` | Tentar carregar arquivo com texto inválido não serializado | Lança `IOException` controlada sem derrubar a aplicação | Ativo (Passou) |
| **PR13-CT05** | Validação de argumentos inválidos | `RepositorioArquivo` | Passar `universidade` nula ou `caminho` nulo/vazio para `salvar()` e `carregar()` | Lança `IllegalArgumentException` | Ativo (Passou) |
| **PR13-CT06** | Compatibilidade com alias | `PersistenciaArquivo`, `RepositorioArquivo` | Instanciar `PersistenciaArquivo`, verificar `ARQUIVO_PADRAO` (`data/universidade.dat`) e salvar/carregar | Funciona de forma idêntica a `RepositorioArquivo` | Ativo (Passou) |

---

## 4. Suíte `ExcecaoTest` — Validação da Issue #12 / #53 (`src/test/java/br/edu/universidade/ExcecaoTest.java`)
* **Resultado da Execução:** `Tests run: 5, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CT-EXC-01** | Hierarquia de exceções | `UniversidadeException`, `NotaInvalidaException`, `MatriculaDuplicadaException`, `TurmaCheiaException`, `AlunoNaoEncontradoException`, `CpfInvalidoException` | Verificar via `isAssignableFrom` se todas estendem `UniversidadeException` | Todas as 5 exceções de domínio herdam da classe base `UniversidadeException` | Ativo (Passou) |
| **CT-EXC-02** | Exceção de matrícula duplicada | `MatriculaDuplicadaException` | Instanciar com matrícula `"20261BSI01"` e turma `"POO-2026-1"` | Armazena os identificadores e inclui ambos na mensagem em português | Ativo (Passou) |
| **CT-EXC-03** | Exceção de turma cheia | `TurmaCheiaException` | Instanciar com turma `"POO-2026-1"` e `40` vagas | Armazena código da turma, total de vagas e formata mensagem descritiva | Ativo (Passou) |
| **CT-EXC-04** | Exceção de aluno não encontrado | `AlunoNaoEncontradoException` | Instanciar com matrícula inexistente `"20261BSI99"` | Armazena o identificador buscado e o exibe na mensagem de erro | Ativo (Passou) |
| **CT-EXC-05** | Exceção de CPF inválido | `CpfInvalidoException` | Instanciar com CPF inválido `"123.456"` | Armazena o CPF informado e explica o formato esperado na mensagem | Ativo (Passou) |

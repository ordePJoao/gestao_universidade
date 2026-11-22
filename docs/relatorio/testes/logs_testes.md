# Registro e Evidências de Testes — Gestão Universitária

Este documento registra os cenários de teste unitários executados via **JUnit 5 + Maven** (`mvn test`) para validar as entidades do domínio antes da integração com a interface Swing.

## 1. Testes Automatizados (`src/test/java/br/edu/universidade/DominioTest.java`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CT01** | Polimorfismo de Identificação | `Pessoa`, `Aluno`, `Professor` | Instanciar `Aluno` e `Professor` como `Pessoa` e chamar `getIdent()` e `exibirDados()` | Retorna matrícula para `Aluno` e SIAPE para `Professor`, exibindo dados sem exceções | Ativo (Passou) |
| **CT02** | Validação de Notas | `Avaliacao`, `NotaInvalidaException` | Criar notas válidas (`8.5`, `9.0`) e inválidas (`-1.0`, `10.5`, `11.0`) | Aceita notas entre `0.0` e `10.0` e lança `NotaInvalidaException` fora do intervalo | Ativo (Passou) |
| **CT03** | Cálculo de Média e Situação | `Matricula`, `Avaliacao`, `Aluno` | Adicionar duas avaliações (`8.0` e `6.0`) e consultar `calcularMedia()` e `getSituacao()` | Média igual a `7.0` e situação retornando `"APROVADO"` | `@Disabled` (Passou com stub local) |

## 2. Observação sobre Integração Parcial (CT03)

Para validar o fluxo de matrícula e cálculo de média antes do merge oficial da classe `Matricula`, foi utilizado um *stub* local temporário. O stub foi removido antes do commit para evitar conflitos de merge na branch `develop`, e o caso de teste **CT03** foi mantido registrado com `@Disabled` em `DominioTest.java` para reativação imediata após a entrega da task oficial.

---

## 3. Suíte `MatriculaLeticiaTest` — Validação do PR #51 (`src/test/java/br/edu/universidade/MatriculaLeticiaTest.java`)
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

## 4. Suíte `PersistenciaTest` — Validação da Issue #13 / #15 (`src/test/java/br/edu/universidade/PersistenciaTest.java`)
* **Resultado da Execução:** `Tests run: 6, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR13-CT01** | Persistência completa em cascata | `RepositorioArquivo`, `Universidade`, `Aluno`, `Professor`, `Curso`, `Disciplina`, `Turma`, `Matricula`, `Avaliacao` | Salvar e carregar `Universidade` populada com todas as entidades e notas em arquivo `.dat` | Restaura todas as listas, buscas por matrícula/SIAPE/código e média calculada (`9.0`) | Ativo (Passou) |
| **PR13-CT02** | Primeira execução (arquivo inexistente) | `RepositorioArquivo`, `Universidade` | Chamar `carregar()` apontando para caminho inexistente | Retorna nova instância de `Universidade` com todas as listas vazias, sem erro | Ativo (Passou) |
| **PR13-CT03** | Criação automática de diretórios | `RepositorioArquivo` | Salvar arquivo em subdiretório ainda não existente (`data/subpasta_nova/...`) | Cria diretórios pais via `mkdirs()` e grava o arquivo com sucesso | Ativo (Passou) |
| **PR13-CT04** | Tratamento de arquivo corrompido | `RepositorioArquivo` | Tentar carregar arquivo com texto inválido não serializado | Lança `IOException` controlada sem derrubar a aplicação | Ativo (Passou) |
| **PR13-CT05** | Validação de argumentos inválidos | `RepositorioArquivo` | Passar `universidade` nula ou `caminho` nulo/vazio para `salvar()` e `carregar()` | Lança `IllegalArgumentException` | Ativo (Passou) |
| **PR13-CT06** | Compatibilidade com alias | `PersistenciaArquivo`, `RepositorioArquivo` | Instanciar `PersistenciaArquivo`, verificar `ARQUIVO_PADRAO` (`data/universidade.dat`) e salvar/carregar | Funciona de forma idêntica a `RepositorioArquivo` | Ativo (Passou) |
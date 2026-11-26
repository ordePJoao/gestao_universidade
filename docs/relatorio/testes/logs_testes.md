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

---

## 5. Suíte `DepartamentoTest` — Validação da Issue #7 / #56 (`src/test/java/br/edu/universidade/DepartamentoTest.java`)
* **Resultado da Execução:** `Tests run: 4, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR56-CT01** | Inicialização e getters/setters | `Departamento` | Instanciar `Departamento("FACOM - Faculdade de Computação")` e alterar nome via `setNome()` | Inicializa com lista vazia de professores e atualiza o nome corretamente | Ativo (Passou) |
| **PR56-CT02** | Bloqueio de professor duplicado | `Departamento`, `Professor` | Chamar `adicionarProfessor(p1)`, `adicionarProfessor(p2)` e repetir `adicionarProfessor(p1)` | Mantém tamanho `2` na lista, ignorando a inserção duplicada | Ativo (Passou) |
| **PR56-CT03** | Independência da agregação | `Professor`, `Departamento` | Instanciar `Professor` sem associá-lo a nenhum `Departamento` | Instancia normalmente com identificador SIAPE válido | Ativo (Passou) |
| **PR56-CT04** | Persistência binária (`Serializable`) | `Departamento`, `Professor` | Gravar `Departamento` com professor em `ObjectOutputStream` e restaurar via `ObjectInputStream` | Restaura nome e lista de professores sem perda de dados | Ativo (Passou) |
---

## 6. Suíte `UniversidadeDepartamentoTest` — Validação da Issue #8 / #57 (`src/test/java/br/edu/universidade/UniversidadeDepartamentoTest.java`)
* **Resultado da Execução:** `Tests run: 2, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR57-CT01** | Gerenciamento e encapsulamento de departamentos | `Universidade`, `Departamento` | Adicionar departamentos distintos, tentar duplicado/nulo, tentar `clear()` na lista retornada e remover departamento | Ignora nulos/duplicados, lança `UnsupportedOperationException` na lista exposta e remove corretamente | Ativo (Passou) |
| **PR57-CT02** | Persistência em cascata de departamentos | `Universidade`, `Departamento`, `Professor`, `RepositorioArquivo` | Salvar e carregar `Universidade` contendo `Departamento` com `Professor` associado | Restaura departamento e lista de professores vinculada sem perda de dados | Ativo (Passou) |
---

## 7. Suíte `TestPR58` — Validação da Task #11.1 / Issue #30 (`src/test/java/br/edu/universidade/TestPR58.java`)
* **Resultado da Execução:** `Tests run: 4, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR58-CT01** | Geração automática de código de matrícula | `Universidade`, `Aluno`, `Curso` | Cadastrar `Aluno` com `Curso` (`BSI`) e sem matrícula prévia | Gera código no formato `ANO + BSI + 4 dígitos` e vincula ao aluno | Ativo (Passou) |
| **PR58-CT02** | Unicidade de códigos gerados | `Universidade`, `Aluno`, `Curso` | Cadastrar `30` alunos consecutivos no mesmo curso (`BCC`) | Gera 30 códigos distintos sem colisão ou duplicidade | Ativo (Passou) |
| **PR58-CT03** | Preservação de matrícula pré-definida | `Universidade`, `Aluno` | Cadastrar `Aluno` instanciado com código explícito (`20261BCC99`) | Mantém o código informado originalmente sem sobrescrever | Ativo (Passou) |
| **PR58-CT04** | Validação de parâmetros nulos | `Universidade`, `Aluno` | Chamar `gerarMatricula(null)` e `cadastrarAluno(null)` | Lança `IllegalArgumentException` para curso nulo e retorna `false` para aluno nulo | Ativo (Passou) |
---

## 8. Suíte `TestPR59` — Validação da Task #11.2 / Issue #31 (`src/test/java/br/edu/universidade/TestPR59.java`)
* **Resultado da Execução:** `Tests run: 4, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR59-CT01** | Busca por nome normalizado | `Universidade`, `Aluno` | Chamar `buscarAluno("jose")` e `buscarAluno("JO")` | Ignora acentos e maiúsculas/minúsculas, retornando os alunos correspondentes | Ativo (Passou) |
| **PR59-CT02** | Busca por CPF (com e sem pontuação) | `Universidade`, `Aluno` | Buscar por `"555.666.777-88"`, `"55566677788"` e termo numérico curto `"555"` | Localiza o aluno independente da máscara de CPF e ignora termos menores que 11 dígitos | Ativo (Passou) |
| **PR59-CT03** | Busca por matrícula e termos inválidos | `Universidade`, `Aluno` | Chamar `buscarAluno("2026bsi0001")`, `"inexistente"`, `""` e `null` | Retorna o aluno correspondente para matrícula válida e lista vazia para termos inválidos | Ativo (Passou) |
| **PR59-CT04** | Busca exata com exceção | `Universidade`, `Aluno`, `AlunoNaoEncontradoException` | Chamar `buscarPorMatricula("2026BSI0002")` e `buscarPorMatricula("2026BSI9999")` | Retorna o `Aluno` quando existe e lança `AlunoNaoEncontradoException` quando não encontra | Ativo (Passou) |

---

## 9. Suíte `TestPR62` — Validação da Task #10.1 / Issue #54 (`src/test/java/br/edu/universidade/TestPR62.java`)
* **Resultado da Execução:** `Tests run: 7, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR62-CT01** | Estado inicial sem avaliações | `Matricula`, `StatusMatricula` | Instanciar `Matricula(aluno, turma)` e verificar `MEDIA_APROVACAO`, `getStatus()`, `getSituacao()` e `calcularMedia()` | Constante igual a `6.0`, status `ATIVA`, situação `"ATIVA"` e média `0.0` | Ativo (Passou) |
| **PR62-CT02** | Aprovação no corte exato (`6.0`) | `Matricula`, `Avaliacao`, `StatusMatricula` | Adicionar avaliação `"P1"` com nota `6.0` | Calcula média `6.0` e consolida status e situação para `APROVADA` | Ativo (Passou) |
| **PR62-CT03** | Reprovação abaixo da média (`< 6.0`) | `Matricula`, `Avaliacao`, `StatusMatricula` | Adicionar avaliação `"P1"` com nota `5.9` | Calcula média `5.9` e consolida status e situação para `REPROVADA` | Ativo (Passou) |
| **PR62-CT04** | Recálculo dinâmico ao alterar nota | `Matricula`, `Avaliacao`, `StatusMatricula` | Lançar notas `8.0` e `6.0` (`APROVADA`) e alterar primeira nota para `2.0` via `alterarNota()` | Recalcula média para `4.0` e atualiza status consolidado para `REPROVADA` | Ativo (Passou) |
| **PR62-CT05** | Sincronização de cancelamento em `Aluno` | `Aluno`, `Matricula`, `Turma`, `StatusMatricula` | Associar matrícula ao aluno e chamar `aluno.cancelarMatricula(m)` e `aluno.cancelarMatricula(null)` | Marca matrícula como `CANCELADA`, remove da lista do aluno e ignora `null` sem exceção | Ativo (Passou) |
| **PR62-CT06** | Proteção de status `CANCELADA` | `Matricula`, `StatusMatricula` | Aprovar matrícula (`10.0`), cancelar via `cancelar()`, chamar `consolidarSituacao()` e tentar lançar nova nota (`8.0`) | Preserva `CANCELADA` sem sobrescrever e lança `IllegalStateException` ao tentar adicionar avaliação | Ativo (Passou) |
| **PR62-CT07** | Validação de notas e contrato `Exibivel` | `Matricula`, `NotaInvalidaException`, `Exibivel`, `Aluno`, `Curso`, `Disciplina`, `Turma` | Tentar notas `10.5` e `-0.5` e invocar `exibirDados()` polimorficamente nas entidades | Lança `NotaInvalidaException` mantendo `ATIVA` e retorna descrição formatada não vazia em todas as classes | Ativo (Passou) |

---

## 10. Suíte `TestPR65` — Validação da Task #19.2 / Issue #33 (`src/test/java/br/edu/universidade/TestPR65.java`)
* **Resultado da Execução:** `Tests run: 6, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR65-CT01** | Estrutura visual e campo matrícula somente leitura | `AlunoPanel`, `Atualizavel`, `BuscaAlunoPanel`, `Curso` | Instanciar `AlunoPanel(uni)` e inspecionar componentes, contrato `Atualizavel`, combo de cursos e botões | Implementa `Atualizavel`, integra `BuscaAlunoPanel`, mantém `txtMatricula` não editável (`"(gerada ao salvar)"`) e exibe botões `Salvar`, `Limpar` e `Excluir` | Ativo (Passou) |
| **PR65-CT02** | Cadastro com curso e geração automática de matrícula | `AlunoPanel`, `Universidade`, `Aluno`, `Curso` | Preencher dados válidos, selecionar curso `BCC` e invocar `salvar()` | Cadastra o aluno na `Universidade`, gera matrícula contendo `"BCC"` e atualiza a tabela | Ativo (Passou) |
| **PR65-CT03** | Rejeição de dados inválidos (e-mail e data) | `AlunoPanel`, `Universidade` | Tentar salvar com e-mail sem `@` (`"carlos.ufu.br"`) e data inexistente (`"31/02/2026"`) | Exibe aviso e impede o cadastro na `Universidade` *(nota: validação de formato de CPF ainda pendente em `Pessoa`)* | Ativo (Passou) |
| **PR65-CT04** | Bloqueio de CPF duplicado e campos obrigatórios | `AlunoPanel`, `Universidade`, `Aluno` | Tentar cadastrar aluno com CPF já existente (com máscara) e com campo `Nome` vazio | Bloqueia duplicidade e impede gravação de campos obrigatórios vazios | Ativo (Passou) |
| **PR65-CT05** | Edição de aluno selecionado na tabela | `AlunoPanel`, `Universidade`, `Aluno`, `Curso` | Selecionar linha na `tblAlunos`, alterar nome e curso para `BSI` e invocar `salvar()` | Atualiza o registro existente e a tabela sem duplicar o aluno | Ativo (Passou) |
| **PR65-CT06** | Bloqueio de exclusão com matrícula ativa | `AlunoPanel`, `Universidade`, `Aluno`, `Matricula`, `Turma` | Tentar excluir aluno com matrícula ativa e depois cancelar a matrícula via `cancelarMatricula()` | Impede exclusão enquanto houver matrícula vinculada e libera após o cancelamento | Ativo (Passou) |

---

## 11. Suíte `TestPR66` — Validação da Task #14 (`src/test/java/br/edu/universidade/TestPR66.java`)
* **Resultado da Execução:** `Tests run: 3, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR66-CT01** | Execução completa do `TesteConsole.main` | `TesteConsole`, `Universidade`, `Aluno`, `Professor`, `Curso`, `Disciplina`, `Turma`, `Matricula`, `RepositorioArquivo` | Executar `TesteConsole.main(new String[]{})` capturando `System.out` | Percorre as 3 etapas (fluxo feliz, exceções e persistência temporária), não registra `[FALHA]` e imprime `TUDO OK` | Ativo (Passou) |
| **PR66-CT02** | Validação das regras auxiliares de matrícula | `TesteConsole`, `Aluno`, `Turma`, `Matricula`, `MatriculaDuplicadaException`, `TurmaCheiaException` | Invocar `matricular(aluno, turma)` para matrícula válida, repetida e com turma lotada (`1` vaga) | Realiza a primeira matrícula e lança `MatriculaDuplicadaException` e `TurmaCheiaException` nos cenários inválidos | Ativo (Passou) |
| **PR66-CT03** | Persistência e restauração em arquivo temporário | `TesteConsole`, `RepositorioArquivo`, `Universidade`, `Matricula`, `NotaInvalidaException` | Lançar notas (`8.0`, `6.0`), validar rejeição de nota `11.0`, salvar em arquivo temporário (`Files.createTempFile`) e recarregar | Restaura entidades e média `7.0` sem alterar a pasta oficial `data/` | Ativo (Passou) |
---

## 12. Suíte `TestPR68` — Validação da Task #19.3 / Issue #34 (`src/test/java/br/edu/universidade/TestPR68.java`)
* **Resultado da Execução:** `Tests run: 4, Failures: 0, Errors: 0` (`BUILD SUCCESS`)

| ID | Caso de Teste | Classes Envolvidas | Entrada / Ação | Resultado Esperado | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **PR68-CT01** | Remoção de professor e `toString()` em `Departamento` | `Departamento`, `Professor` | Chamar `toString()`, `removerProfessor()` para professor não vinculado e para professor vinculado | Retorna o nome do departamento em `toString()`, devolve `false` para não vinculado e `true` removendo o professor existente | Ativo (Passou) |
| **PR68-CT02** | Estrutura visual da aba Departamentos | `DepartamentoPanel`, `Atualizavel`, `Professor` | Instanciar `DepartamentoPanel(uni)` e inspecionar interface `Atualizavel`, combo de professores e botões | Implementa `Atualizavel`, popula `cbProfessor` e exibe os botões `Salvar`, `Limpar`, `Excluir`, `Adicionar professor` e `Remover professor` | Ativo (Passou) |
| **PR68-CT03** | Cadastro de departamento e bloqueio de duplicidade | `DepartamentoPanel`, `Universidade`, `Departamento` | Tentar salvar nome vazio, cadastrar `"FACOM"` e tentar cadastrar `"facom"` | Rejeita nome vazio, cadastra o departamento na tabela e bloqueia nomes duplicados ignorando maiúsculas/minúsculas | Ativo (Passou) |
| **PR68-CT04** | Vinculação e desvinculação de professores no departamento | `DepartamentoPanel`, `Departamento`, `Professor` | Selecionar departamento na tabela, vincular professor, tentar vincular novamente e remover da lista | Atualiza contagem na tabela para `1`, bloqueia professor repetido e remove professor zerando a contagem | Ativo (Passou) |
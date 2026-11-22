# gestao_universidade

Sistema de Gestão de Universidade desenvolvido como Projeto Final de Programação Orientada a Objetos. Permite gerenciar alunos, professores, departamentos, cursos, disciplinas, turmas, matrículas e avaliações, com interface gráfica e persistência em arquivo.

Projeto 100% Java, com interface em `javax.swing`.

## Funcionalidades

* Cadastro, listagem, edição e exclusão de alunos, professores, cursos, disciplinas e turmas
* Matrícula de alunos em turmas, com controle de vagas
* Lançamento de notas e cálculo de média
* Tratamento de erros com exceções customizadas
* Salvamento e carregamento automático dos dados

## Tecnologias

* Java 17
* Maven
* Swing (`javax.swing`)
* Serialização nativa (`java.io`)

## Como executar

Requisitos: JDK 17 ou superior e Maven instalados.

```bash
git clone https://github.com/SEU-USUARIO/gestao-universidade-poo.git
cd gestao-universidade-poo
mvn clean package
java -jar target/gestao-universidade-1.0.jar
```

## Estrutura do projeto

```
src/main/java/br/edu/universidade/
├── Main.java
├── model/          Entidades do domínio (Pessoa, Aluno, Professor, Curso, Turma, Matricula...)
├── service/        Regras de negócio
├── persistence/    Salvar e carregar dados
├── exception/      Exceções customizadas
└── view/           Telas Swing
data/               Arquivo de dados gerado em execução
docs/               UML, relatório, screenshots e apresentação
```

## Conceitos de POO aplicados

* **Herança:** `Pessoa` (abstrata) é a base de `Aluno` e `Professor`
* **Polimorfismo:** `getIdent()` e `exibirDados()` se comportam de forma diferente em cada subclasse
* **Interface:** `Exibivel`
* **Agregação:** `Universidade`, `Curso` e `Disciplina`
* **Composição:** `Matricula` e `Avaliacao`
* **Encapsulamento:** atributos privados com acesso por getters e setters
* **Exceções customizadas:** pacote `exception` com hierarquia baseada em `UniversidadeException`:
  * `NotaInvalidaException`: lançada quando uma nota informada não está no intervalo de 0.0 a 10.0.
  * `MatriculaDuplicadaException`: lançada ao tentar matricular um aluno que já possui vínculo ativo na mesma turma.
  * `TurmaCheiaException`: lançada quando uma turma não possui vagas disponíveis para nova matrícula.
  * `AlunoNaoEncontradoException`: lançada quando uma consulta por identificador ou matrícula não localiza o aluno.
  * `CpfInvalidoException`: lançada quando um CPF informado não atende aos requisitos e dígitos válidos.

## Persistência de Dados

O sistema utiliza **serialização binária nativa do Java** (`ObjectOutputStream` e `ObjectInputStream`) para salvar e carregar o estado da aplicação.

* **Arquivo de armazenamento:** `data/universidade.dat`
* **Objeto raiz:** A classe `Universidade` atua como ponto de entrada único, persistindo em cascata todas as listas e entidades vinculadas a ela.
* **Regra para classes de domínio:** Todas as classes de modelo armazenadas direta ou indiretamente em `Universidade` (como `Aluno`, `Professor`, `Curso`, `Disciplina`, etc.) devem obrigatoriamente implementar `java.io.Serializable` e declarar o identificador de versão:

  ```java
  private static final long serialVersionUID = 1L;
  ```

* **Primeira execução:** se o arquivo não existir, o sistema inicia com uma `Universidade` vazia.
* **Controle de versão:** o arquivo `data/universidade.dat` é ignorado pelo Git (`.gitignore`), pois é gerado em cada máquina.

## Convenção de trabalho

* Uma branch por task: `feature/#N-descricao`
* Nunca commitar direto na `main` ou na `develop`
* Pull Request para `develop`, revisado por outra pessoa
* Mensagens de commit curtas e no imperativo: `Adiciona classe Aluno`
* Nenhum dado de execução ou credencial no repositório

## Documentação

* UML de classes: `docs/uml/`
* Relatório em PDF: `docs/relatorio/`
* Screenshots: `docs/screenshots/`

______________________________________________

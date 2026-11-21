# Protótipo das telas de Aluno e Professor (task #15, P7)

Esqueleto Swing com **dados falsos**, sem depender do núcleo (Sprint 1).
Segue o padrão visual da classe `view/Estilo` (combinado com P8).

| Tela | Arquivo |
| --- | --- |
| Aba Alunos | `aba_alunos.png` |
| Aba Professores | `aba_professores.png` |

## Layout (igual às abas de Curso, Disciplina e Turma)

```
+--------------------------------------------------------------+
| Arquivo  Cadastros  Acadêmico  Ajuda                (menu)   |
| [Alunos][Professores][Cursos][Disciplinas][Turmas][...]      |
+--------------------------------------------------------------+
| Título da tela                                               |
| +-- Cadastro de ... ---------------------------------------+ |
| | Campo 1: [_______________]                               | |
| | Campo 2: [_______________]                               | |
| +----------------------------------------------------------+ |
| [Salvar] [Limpar] [Excluir]                                  |
| +-- ... cadastrados (JTable) ------------------------------+ |
| | col 1 | col 2 | col 3 | ...                              | |
| +----------------------------------------------------------+ |
+--------------------------------------------------------------+
```

* Clicar numa linha da tabela carrega o registro no formulário (edição).
  O identificador (matrícula / SIAPE) fica bloqueado durante a edição.
* **Salvar** cadastra (nada selecionado) ou atualiza (linha selecionada).
* **Excluir** pede confirmação.
* Menu **Cadastros** e **Acadêmico** abrem a aba correspondente.

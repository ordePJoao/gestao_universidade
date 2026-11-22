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

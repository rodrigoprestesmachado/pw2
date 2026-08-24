# Registro de Evidencias - Checkstyle e PMD

Este arquivo registra somente comandos, resultados e alteracoes efetivamente
observados durante a atividade. Deve ser consolidado no relatorio final.

## Contexto do repositorio

- Repositorio local: `C:\projetos\validacao_e_verificacao_de_sistemas\pw2`
- Branch em uso: `dev`, criada com rastreamento de `origin/dev`.
- Diretorio de trabalho da atividade: `exemplos/books`.
- Modulos em escopo: `catalog`, `management` e `users`.

## Linha de base - catalog

### Comando inicial

```bash
mvn verify
```

### Resultado inicial

`BUILD FAILURE` no modulo `catalog`, antes da configuracao de PMD e
Checkstyle.

- Plugin que interrompeu o ciclo: `maven-surefire-plugin:3.2.5:test`.
- Teste: `CatalogWSTest.testHelloEndpoint`.
- Evidencia: a requisicao `GET /hello` esperava HTTP 200 e recebeu HTTP 404.

### Diagnostico realizado

Arquivos inspecionados:

- `src/test/java/dev/rpmhub/CatalogWSTest.java`
- `src/main/java/dev/rpmhub/web/CatalogWS.java`

O teste original verificava a rota `/hello` e a resposta `Hello from Quarkus
REST`. A classe `CatalogWS` nao possui essa rota; ela expoe
`GET /catalog/ping`, que retorna `Pong`.

### Correcao aplicada manualmente

No teste `CatalogWSTest`, foram atualizados:

- Caminho: `/hello` para `/catalog/ping`.
- Corpo esperado: `Hello from Quarkus REST` para `Pong`.

Nenhuma rota de producao foi adicionada.

### Validacao apos a correcao

```bash
mvn verify
```

Resultado: `BUILD SUCCESS` em `2026-08-23T17:54:30-03:00`.

Observacao: a mensagem `Tests are skipped` veio da etapa `failsafe:verify`.
O teste unitario que antes falhava foi executado pelo Surefire e nao impediu o
ciclo.

## Uso de LLM - classificacao pela Tabela 1 do enunciado

| Etapa | Prompt/necessidade | Resumo da resposta recebida | Atividade e justificativa |
| --- | --- | --- | --- |
| Preparacao | "Por onde deveriamos comecar?" | Estabelecer a linha de base: obter o repositorio, confirmar a branch `dev`, identificar os tres modulos e executar `mvn verify` antes de configurar ferramentas. | `#1 Search information`: consulta por informacoes iniciais e fontes de verificacao. |
| Diagnostico | Resultado de `mvn verify` com HTTP 404 no teste de catalog. | Diferenciar falha funcional de analise estatica; inspecionar o teste e a classe que expoe as rotas antes de alterar o codigo. | `#3 Check their answers`: o resultado observado foi confrontado com a interpretacao do estudante. |
| Correcao pontual | Qual comportamento o teste deveria validar? | A rota existente e sem autenticacao e `GET /catalog/ping`, com resposta `Pong`; atualizar somente as duas expectativas do teste e validar novamente. | `#8 Ask ideas for their improvement`: buscou uma ideia pontual para melhorar a verificacao. |
| Diagnostico | Resultado de `mvn verify` com HTTP 404 no teste de management. | Identificar que as rotas de negocio exigem autenticacao; usar o endpoint de liveness local e validar o campo JSON `status` igual a `UP`. | `#3 Check their answers`: validacao da leitura feita a partir das rotas e do resultado do teste. |
| Conceito | "Isso ocorre em qualquer projeto Java ou depende do Surefire?" | Diferenciar projetos Java em geral de projetos Maven: o Surefire executa testes na fase `test` e, nos empacotamentos Maven comuns, essa associacao e padrao; os relatorios existem quando o Surefire gera arquivos. | `#11 Ask for definitions`: esclarecimento de conceito e escopo de uma ferramenta. |
| Conceito | "Executar o teste pela IDE agora produz o mesmo erro?" | A mesma classe de teste ainda chama `/hello`, portanto a falha HTTP 404 deve se repetir; a diferenca e que a IDE pode exibir o resultado sem criar o relatorio do Surefire, salvo quando ela delega a execucao ao Maven. | `#11 Ask for definitions`: comparacao conceitual entre execucao pela IDE e pelo Maven. |
| Conceito | "Preciso compilar ou fazer o build antes de executar testes pela IDE?" | A IDE normalmente compila automaticamente antes de executar um teste. No Maven, `mvn test` ja inclui as fases de compilacao e teste; `mvn verify` percorre essas fases e ainda executa a fase `verify`. | `#11 Ask for definitions`: explicacao do ciclo e das responsabilidades de compilacao. |
| Conceito | "Quais sao as fases do Maven e o que precede `verify`?" | Explicar que `mvn <fase>` executa todas as fases anteriores do ciclo `default`; `verify` ocorre apos compilacao, testes, empacotamento e possiveis testes de integracao. | `#11 Ask for definitions`: definicao de fases e de ciclo de vida. |
| Conceito | "Visao breve dos ciclos clean e site." | `clean` remove artefatos gerados, normalmente `target`; `site` gera documentacao e relatorios de projeto, geralmente em `target/site`, quando os plugins de relatorio estao configurados. | `#11 Ask for definitions`: definicao dos ciclos Maven. |
| Conceito | "Nao entendi o ciclo site." | Tratar `site` como a geracao de um pequeno site HTML local que reune documentacao e relatorios; ele nao publica nada e nao e necessario para a atividade. | `#11 Ask for definitions`: reformulacao de uma definicao ainda nao compreendida. |
| Correcao pontual | Como adaptar o teste de `users` a uma API que gera JWT? | Enviar JSON por `POST /users/getJwt`, validar HTTP 200 e um corpo nao vazio, pois cada token gerado varia. | `#8 Ask ideas for their improvement`: ideia de assercao mais adequada ao comportamento variavel. |
| Conceito | "Checkstyle e PMD entram em `dependencies` ou em `build/plugins`?" | Diferenciar bibliotecas usadas pelo codigo da aplicacao de ferramentas executadas pelo Maven; Checkstyle e PMD pertencem a `build/plugins`. | `#11 Ask for definitions`: definicao da diferenca entre dependencia e plugin. |
| Conceito | "Dependencias nao entram no build e na compilacao?" | Sim: dependencias participam do build como bibliotecas disponiveis no classpath. Elas nao criam uma etapa de build; plugins sao programas que o Maven invoca para executar objetivos, como compilar, testar ou analisar codigo. | `#11 Ask for definitions`: esclarecimento conceitual complementar. |
| Conceito | "Plugin permite ao Maven executar algo manualmente ou no ciclo de build?" | Confirmar que cada plugin oferece objetivos executaveis diretamente ou vinculaveis a uma fase; eles podem verificar, gerar artefatos, compilar ou empacotar. Checkstyle e PMD, neste trabalho, analisam e relatam, sem alterar o codigo-fonte. | `#11 Ask for definitions`: definicao de plugin e de goal. |
| Conceito | "Isso tem relacao com a criacao do JAR?" | Explicar que o JAR tambem e produzido por um plugin, normalmente `maven-jar-plugin` na fase `package`; Checkstyle e PMD sao outros plugins e nao criam o JAR. | `#11 Ask for definitions`: relacao entre plugin, fase e artefato. |
| Conceito | "Overview dos goals Maven." | Definir goal como uma acao concreta de um plugin, invocavel diretamente no formato `plugin:goal` ou vinculada a uma fase. Distinguir goals de relatorio (`checkstyle`, `pmd`) de goals de validacao (`check`, `pmd:check`). | `#11 Ask for definitions`: definicao de goal e sua terminologia. |
| Conceito | "Um goal e executado sem ser chamado ou configurado no pom?" | Explicar que um goal roda quando e chamado diretamente, configurado em uma execucao ou associado por padrao ao empacotamento Maven. Nao existe um goal universal padrao; Checkstyle e PMD precisam de configuracao explicita para rodar em `mvn verify`. | `#11 Ask for definitions`: esclarecimento do mecanismo de vinculacao. |
| Conceito | "`mvn test` chama `surefire:test`?" | Confirmar que, para o empacotamento Maven comum do projeto, executar a fase `test` percorre as fases anteriores e aciona o goal padrao `surefire:test`. | `#11 Ask for definitions`: definicao aplicada da relacao fase-goal. |
| Conceito | "Por que configurar `phase` e `goal` em `executions` se a fase ja executa goals?" | Esclarecer que uma fase executa somente os goals previamente vinculados a ela. `executions` cria a vinculacao explicita de um goal adicional, como Checkstyle ou PMD, a uma fase do ciclo. | `#11 Ask for definitions`: explicacao da estrutura de `executions`. |
| Conceito | "`check` e goal de `verify` ou de uma ferramenta?" | Distinguir fase de goal: `verify` e uma fase; `check` e goal do plugin. O nome completo identifica a ferramenta, por exemplo `checkstyle:check` ou `pmd:check`. | `#11 Ask for definitions`: distincao terminologica central. |
| Conceito | "Mostrar no pom a ligacao entre phase e goal." | Demonstrar apenas o esqueleto XML: um plugin contem uma execucao; a fase define quando rodar e o goal define qual acao daquele plugin executar. | `#2 Get examples`: exemplo parcial usado para comparar a estrutura, sem solicitar o `pom.xml` completo. |
| Conceito | "Pode haver varias executions, fases e goals?" | Confirmar que um plugin pode ter varias execucoes em fases distintas e uma execucao pode listar mais de um goal; cada associacao deve ter finalidade clara. | `#11 Ask for definitions`: explicacao de possibilidades da configuracao. |
| Configuracao | "O ruleset PMD pode ser local em vez de uma URL?" | Confirmar que o plugin aceita rulesets de URL ou do sistema de arquivos. Para um ruleset customizado local e portavel, usar caminho baseado em `${project.basedir}` e versionar a copia, preservando a referencia da origem. | `#7 Get insight into complex problems`: comparacao de alternativas de ruleset e seus impactos. |
| Configuracao | "O ruleset copiado em `target/pmd/rulesets` pode ser usado como arquivo local?" | Ele nao deve ser referenciado diretamente em `target`, pois sera removido por `mvn clean`; mas seu conteudo pode ser copiado para fora de `target`, versionado e usado como snapshot local, registrando URL de origem e data. | `#7 Get insight into complex problems`: analise de trade-off entre artefato temporario e configuracao versionavel. |
| Configuracao | "Explicar os parametros de falha do PMD." | Explicar `failOnViolation`, `maxAllowedViolations`, `failurePriority` e `printFailingErrors`, relacionando sete violacoes de prioridade 3 ao limite configurado de cinco. | `#11 Ask for definitions`: os parametros foram definidos antes do uso na configuracao. |
| Configuracao | "Quais regras iniciar no Checkstyle?" | Propor um conjunto compacto de estilo: tabs, nova linha ao fim do arquivo, importacao com asterisco, chaves e espacos. Manter documentacao no PMD para evitar duplicacao de regras. | `#9 Make lists or outlines`: pedido e resposta organizados como uma lista de regras iniciais. |
| Conformidade com o enunciado | Pedido de configuracao Checkstyle completa. | Nao fornecer o arquivo completo; orientar a construcao incremental de modulos de regra, preservando autoria e rastreabilidade do estudante. | `#2 Get examples`, mas pedido recusado por exceder a regra do enunciado; nao deve ser apresentado como exemplo de configuracao aceito. |

### Limites da classificacao

As atividades `#18 Anticipate ChatGPT's outputs` e `#19 Grade ChatGPT's
outputs` nao foram realizadas no formato exigido pelo enunciado: nao houve uma
previsao escrita anterior a Etapa 2 nem uma avaliacao propria formal enviada
para confirmacao na Etapa 5. Elas nao devem ser atribuidas retroativamente.
As atividades `#10 Summarize their own work` e `#14 Get feedback for their
work` serao registradas apenas depois de o estudante escrever o rascunho do
relatorio final e solicitar feedback de clareza e organizacao. A Etapa 7 nao
tem linha nesta tabela porque foi realizada sem LLM.

### Feedback do rascunho final

- Atividade da Tabela 1: `#14 Get feedback for their work`.
- Prompt: envio do rascunho autoral do relatorio final para avaliacao de
  clareza, organizacao e concisao.
- Resposta resumida: o texto tem sequencia logica e cobre os pontos centrais;
  recomenda-se reduzir repeticoes entre resultados, falha controlada e
  conclusao, manter a tabela de LLM visualmente compacta e conferir o limite
  de uma pagina na formatacao final.

## Linha de base - management

### Comando inicial

```bash
mvn verify
```

### Resultado inicial

`BUILD FAILURE` em `2026-08-23T18:08:30-03:00`, antes da configuracao de PMD
e Checkstyle.

- Plugin que interrompeu o ciclo: `maven-surefire-plugin:3.2.5:test`.
- Resumo: 1 teste executado e 1 falha.
- Teste: `ManagementWSTest.testHelloEndpoint` (linha 16).
- Evidencia: a requisicao esperava HTTP 200 e recebeu HTTP 404.
- Causa funcional: o teste-template usava `/hello`, rota inexistente no
  modulo.

### Diagnostico e correcao aplicada manualmente

- `ManagementWS` expoe apenas rotas de negocio em `/bookManagement` e elas
  exigem o papel `User`.
- `Live` usa `@Liveness`; como nao ha configuracao que substitua a rota de
  health, o endpoint adequado para teste isolado e `GET /q/health/live`.
- O teste foi atualizado para chamar `/q/health/live` e validar o campo JSON
  `status` com valor `UP`.

### Validacao apos a correcao

```powershell
mvn verify
```

Resultado: `BUILD SUCCESS` em `2026-08-23T18:21:24-03:00`.

Observacao: `Tests are skipped` foi emitido por `failsafe:verify`; o teste
unitario foi executado anteriormente pelo Surefire.

## Linha de base - users

### Comando inicial

```powershell
mvn verify
```

### Resultado inicial

`BUILD FAILURE`, antes da configuracao de PMD e Checkstyle.

- Plugin que interrompeu o ciclo: `maven-surefire-plugin:3.2.5:test`.
- Resumo: 1 teste executado e 1 falha.
- Teste: `UsersWSTest.testHelloEndpoint` (linha 16).
- Evidencia: a requisicao esperava HTTP 200 e recebeu HTTP 404.
- Causa funcional: o teste-template usava `GET /hello`, rota inexistente no
  modulo.

### Diagnostico e correcao aplicada manualmente

- A classe `Users` expoe `POST /users/getJwt` e consome JSON com `email` e
  `fullName`.
- O teste foi atualizado para enviar JSON com `Content-Type: application/json`
  por `POST /users/getJwt`.
- A resposta e um JWT variavel; por isso, o teste valida HTTP 200 e corpo nao
  vazio, sem comparar um token fixo.

### Validacao apos a correcao

```powershell
mvn verify
```

Resultado: `BUILD SUCCESS` em `2026-08-23T19:01:08-03:00`.

Observacao: `Tests are skipped` foi emitido por `failsafe:verify`; o teste
unitario foi executado anteriormente pelo Surefire.

## Experimento - ciclo site

No modulo `users`, foi executado o comando abaixo para compreender o ciclo de
documentacao do Maven:

```powershell
mvn site
```

Resultado: `BUILD SUCCESS` em `2026-08-23T18:47:29-03:00`, apos
10 minutos e 30 segundos, principalmente pelo download inicial de plugins e
dependencias. O aviso `Modified invalid anchor name` foi emitido pelo gerador
XHTML e nao interrompeu o comando.

Este experimento nao substitui os relatorios isolados nem a validacao em
`mvn verify` exigidos pela atividade.

## Inspecao inicial do pom - catalog

O `pom.xml` do modulo `catalog` foi inspecionado antes de qualquer alteracao.

- O modulo e independente: nao declara `parent` nem agregador local.
- A secao `build/plugins` ja contem plugins do Quarkus, compilacao, Surefire e
  Failsafe.
- Nao ha configuracao de `maven-checkstyle-plugin` nem de `maven-pmd-plugin`.
- Portanto, os dois plugins devem ser configurados na secao `build/plugins` de
  cada modulo em escopo, sem substituir os plugins existentes.

## Origem das regras

- Nenhum `checkstyle.xml` ou `pmd.xml` existe no repositorio clonado.
- O material de apoio fornece a URL do ruleset `pmd.xml` no repositorio
  `rodrigoprestesmachado/tpack`.
- Nao foi encontrada configuracao Checkstyle correspondente nesse repositorio;
  sera necessario definir uma configuracao local para Checkstyle antes de
  executa-lo.

## PMD - catalog

### Configuracao inicial

- Plugin: `org.apache.maven.plugins:maven-pmd-plugin:3.21.2`.
- Ruleset usado inicialmente: URL remota indicada no material de apoio.
- Neste momento, o plugin ainda nao possui `execution` vinculada ao ciclo
  Maven.

### Estado da configuracao

Foi executado `mvn verify` em `2026-08-23T19:52:28-03:00` e o resultado foi
`BUILD SUCCESS`. Como o PMD ainda nao possui uma `execution` vinculada a
`verify`, esse comando nao executou o goal PMD e nao gerou `target/pmd.xml`.

O relatorio isolado `mvn pmd:pmd` foi executado posteriormente.

### Relatorio isolado

```powershell
mvn pmd:pmd
```

O arquivo `target/pmd.xml` foi gerado em `2026-08-23T19:58:00.859` com PMD
6.55.0. Foram identificadas 7 violacoes da regra `CommentRequired`, todas de
prioridade 3:

- `Book.java`: comentario da classe e de quatro campos.
- `CatalogWS.java`: comentario do campo `books` e do construtor publico.

O relatorio foi gerado isoladamente; o PMD ainda nao esta vinculado a
`mvn verify` nesta etapa.

### Migracao para ruleset local

- Foi criada a copia local `catalog/pmd.xml` a partir do ruleset resolvido pelo
  PMD, preservando a descricao `Tpack PMD rules`.
- O `pom.xml` passou a referenciar `${project.basedir}/pmd.xml`.
- Apos `mvn clean pmd:pmd`, foram recriados `target/pmd.xml` e
  `target/site/pmd.html`, confirmando que a analise nao depende mais da URL
  remota.

### Validacao vinculada ao ciclo

O goal `pmd:check` foi vinculado explicitamente a fase `verify`, com:

- `failOnViolation=true`
- `maxAllowedViolations=5`
- `failurePriority=3`
- `printFailingErrors=true`

Ao executar `mvn verify`, o resultado foi `BUILD FAILURE` em
`2026-08-23T20:10:50-03:00`.

- Goal executado: `pmd:3.21.2:check`.
- Resultado: 7 violacoes de prioridade 3, acima do limite de 5.
- Arquivo de diagnostico indicado pelo Maven: `target/pmd.xml`.

Essa e a evidencia da falha controlada do PMD. O limite nao sera elevado para
ocultar as violacoes; elas deverao ser corrigidas apos consolidar as regras do
Checkstyle.

## Checkstyle - catalog

### Execucao exploratoria sem configuracao no pom

Foi executado `mvn checkstyle:checkstyle` antes de adicionar o plugin ao
`pom.xml`.

- Maven resolveu `maven-checkstyle-plugin:3.6.0` para a chamada direta.
- Foi aplicado o ruleset padrao `sun_checks.xml`.
- O relatorio apontou 35 erros, mas terminou com `BUILD SUCCESS`, pois o goal
  `checkstyle` gera relatorio e nao e o goal de validacao.

Essa execucao nao e a evidencia final da atividade, pois nao usou o
`checkstyle.xml` local nem a versao definida no material.

### Relatorio isolado com configuracao local

O plugin `maven-checkstyle-plugin:3.1.1` foi adicionado ao `pom.xml` com
`configLocation=checkstyle.xml`.

```powershell
mvn checkstyle:checkstyle
```

Resultado: `BUILD SUCCESS` em `2026-08-23T20:31:32-03:00`.

O relatorio `target/checkstyle-result.xml` foi gerado com Checkstyle 8.29. Os
arquivos analisados aparecem sem elementos de erro, portanto nao houve
violacoes para as cinco regras locais configuradas.

### Validacao do Checkstyle

O goal `checkstyle:check` foi vinculado explicitamente a fase `verify` no
`pom.xml`. A validacao isolada foi executada em seguida:

```powershell
mvn checkstyle:check
```

Resultado: `BUILD SUCCESS` em `2026-08-23T20:40:25-03:00`, com a mensagem
`You have 0 Checkstyle violations.` Isso confirma que o plugin usa o
`checkstyle.xml` local e que o goal de bloqueio esta funcional. A execucao
completa de `mvn verify` ainda interrompera no PMD enquanto as sete violacoes
de documentacao permanecerem abertas.

### Correcao parcial das violacoes do PMD

Foram adicionados os comentarios Javadoc requeridos a classe `Book` e aos seus
quatro campos. A nova execucao abaixo foi realizada:

```powershell
mvn pmd:check
```

Resultado: `BUILD SUCCESS` em `2026-08-23T20:45:40-03:00`, com 2 violacoes
restantes, ambas em `CatalogWS.java`: o campo `books` (linha 30) e o
construtor publico (linha 32). O Maven nao bloqueou a execucao porque
`maxAllowedViolations=5`; isso nao representa ausencia de problemas e as duas
violacoes serao corrigidas antes da validacao final.

### PMD sem violacoes no catalog

Foram documentados o campo `books` e o construtor publico de `CatalogWS`.
Em seguida, foi executado:

```powershell
mvn pmd:check
```

Resultado: `BUILD SUCCESS` em `2026-08-23T20:47:53-03:00`, sem mensagens
`PMD Failure`. Portanto, as sete violacoes iniciais foram corrigidas. O trecho
`pmd:check > :pmd` e esperado: o goal `check` executa a analise que atualiza o
relatorio antes de decidir se a validacao deve falhar.

### Verificacao integrada do catalog

Foi executado:

```powershell
mvn verify
```

Resultado: `BUILD SUCCESS` em `2026-08-23T20:49:10-03:00`.

- Testes unitarios: 1 executado, 0 falhas, 0 erros.
- PMD 6.55.0: executado por `pmd:check`, sem mensagens de falha.
- Checkstyle 3.1.1: executado por `checkstyle:check`, com 0 violacoes.

Esta e a evidencia de que as duas ferramentas estao integradas ao ciclo
`verify` do modulo `catalog` e nao dependem apenas de invocacoes manuais.

## PMD e Checkstyle - management

Os arquivos locais `pmd.xml` e `checkstyle.xml`, assim como os blocos dos
plugins PMD e Checkstyle, foram replicados para o modulo `management`. Foi
executado:

```powershell
mvn clean verify
```

Resultado: os testes unitarios passaram (1 executado, 0 falhas e 0 erros),
mas o `pmd:check` encerrou o ciclo com `BUILD FAILURE` em
`2026-08-23T20:54:47-03:00`, antes da execucao do Checkstyle.

O PMD encontrou 14 violacoes `CommentRequired`, todas de prioridade 3, acima
do limite de 5. A distribuicao inicial foi:

- `CatalogRC.java`: classe e dois metodos publicos (3).
- `PingRC.java`: classe e metodo publico (2).
- `Live.java`: classe (1).
- `Ready.java`: classe (1).
- `Book.java`: classe e quatro campos (5).
- `ManagementWS.java`: classe e campo (2).

O relatorio correspondente foi gerado em `management/target/pmd.xml` como
parte da execucao de `verify`.

### Verificacao integrada do management

As violacoes apontadas pelo PMD foram corrigidas e o arquivo
`application.properties` recebeu a quebra de linha final requerida pelo
Checkstyle. Foi executado novamente:

```powershell
mvn clean verify
```

Resultado: `BUILD SUCCESS` em `2026-08-23T20:59:25-03:00`.

- Testes unitarios: 1 executado, 0 falhas e 0 erros.
- PMD 6.55.0: executado por `pmd:check`, sem mensagens `PMD Failure`.
- Checkstyle 3.1.1: executado por `checkstyle:check`, com 0 violacoes.

O Maven exibiu avisos de realocacao das dependencias Quarkus
`quarkus-rest-client` e `quarkus-rest-client-jackson`. Sao avisos de
atualizacao futura das dependencias, preexistentes e independentes desta
configuracao de analise estatica; nao impedem o build.

## PMD e Checkstyle - users

Os arquivos locais de regras e os dois plugins foram replicados para o modulo
`users`. Foi executado:

```powershell
mvn clean verify
```

Resultado parcial: os testes unitarios passaram (1 executado, 0 falhas e 0
erros) e o PMD 6.55.0 foi executado sem mensagens `PMD Failure`. Em seguida,
o Checkstyle 3.1.1 interrompeu o ciclo com `BUILD FAILURE` em
`2026-08-23T22:10:01-03:00` por uma unica violacao:

- `src/main/resources/application.properties`, linha 1:
  `NewlineAtEndOfFile`.

Essa e uma falha controlada do Checkstyle: o arquivo nao termina com quebra
de linha. A correcao sera adicionar apenas a quebra de linha final e repetir
`mvn clean verify`.

### Verificacao integrada do users

Depois de adicionar a quebra de linha final em `application.properties`, foi
executado novamente `mvn clean verify`.

Resultado: `BUILD SUCCESS` em `2026-08-23T22:12:00-03:00`.

- Testes unitarios: 1 executado, 0 falhas e 0 erros.
- PMD 6.55.0: executado por `pmd:check`, sem mensagens `PMD Failure`.
- Checkstyle 3.1.1: executado por `checkstyle:check`, com 0 violacoes.

Os tres modulos (`catalog`, `management` e `users`) agora possuem PMD e
Checkstyle integrados a fase `verify` e concluem o ciclo com sucesso.

### Artefatos de relatorio do users

Foi confirmado que a execucao integrada gerou os relatorios em `users/target`:

```text
pmd.xml                  315 bytes  23/08/2026 22:13:03
checkstyle-result.xml    360 bytes  23/08/2026 22:13:05
```

Embora `mvn verify` tenha produzido esses arquivos de diagnostico, o enunciado
exige tambem a execucao isolada de `mvn pmd:pmd` e
`mvn checkstyle:checkstyle` em cada modulo. A execucao isolada do `users`
permanece pendente nesta etapa documental.

## Conferencia do estado de entrega

Foi executado `git status --short` no diretorio `exemplos/books`, na branch
`dev`. O estado lista as alteracoes esperadas para a atividade:

- Arquivos locais `pmd.xml` e `checkstyle.xml` em cada modulo.
- `pom.xml` dos tres modulos com os plugins configurados.
- Comentarios Javadoc adicionados para corrigir o PMD.
- Testes ajustados aos endpoints existentes.
- `application.properties` corrigidos para a regra de quebra de linha final.
- Este registro de evidencias como arquivo novo.

Nenhum diretorio ou arquivo `target` apareceu no status, o que confirma que
artefatos de build e relatorios nao serao incluidos no controle de versao.
O status `AM` em `catalog/checkstyle.xml` indica que o arquivo ja foi adicionado
ao indice, mas recebeu uma alteracao posterior ainda nao adicionada; essa
diferenca deve ser revisada antes de qualquer commit.

### Correcao do indice Git

A diferenca de `catalog/checkstyle.xml` mostrou que a versao no indice era um
arquivo vazio, enquanto a configuracao completa estava apenas no diretorio de
trabalho. Apos revisar o diff, foi executado:

```bash
git add -- catalog/checkstyle.xml
git status --short catalog/checkstyle.xml
```

Resultado: `A  catalog/checkstyle.xml`. A versao completa da configuracao esta
agora no indice, sem alteracoes pendentes para esse arquivo.

## Auditoria dos requisitos do enunciado

Foi confirmado que a Etapa 5 exige, alem de `mvn verify`, os goals isolados
`mvn pmd:pmd` e `mvn checkstyle:checkstyle` nos tres modulos. O `catalog` ja
teve ambos executados durante a configuracao inicial. Para completar a
evidencia, ainda devem ser executados os dois goals isolados em `management` e
em `users`, com inspecao dos respectivos arquivos XML em `target`.

Tambem permanece pendente a falha controlada final da Etapa 7: reintroduzir
uma violacao de proposito, observar o `BUILD FAILURE`, desfazer a alteracao e
confirmar `BUILD SUCCESS` novamente. As falhas anteriores durante a
configuracao sao evidencias uteis, mas esta validacao final deve ser registrada
separadamente.

### Relatorios isolados do management

Foram executados no modulo `management`:

```powershell
mvn pmd:pmd
mvn checkstyle:checkstyle
```

Ambos terminaram com `BUILD SUCCESS`, respectivamente em
`2026-08-23T22:24:03-03:00` e `2026-08-23T22:24:51-03:00`. A inspecao pontual
abaixo nao retornou nenhuma linha:

```powershell
Select-String -Path target\pmd.xml, target\checkstyle-result.xml `
  -Pattern '<violation|<error'
```

Portanto, os relatorios isolados de PMD e Checkstyle do `management` existem e
nao registram violacoes.

### Relatorios isolados do users

Foram executados no modulo `users`:

```powershell
mvn pmd:pmd
mvn checkstyle:checkstyle
```

Ambos terminaram com `BUILD SUCCESS`, respectivamente em
`2026-08-23T22:25:58-03:00` e `2026-08-23T22:26:20-03:00`. A inspecao de
`target/pmd.xml` e `target/checkstyle-result.xml` nao encontrou elementos
`<violation>` nem `<error>`. Os relatorios isolados do `users` estao limpos.

## Etapa 7 - validacao final sem LLM

Esta etapa foi executada pelo estudante sem orientacao de LLM, conforme exige
o enunciado. No modulo `management`, foram removidos temporariamente
comentarios requeridos pelo ruleset PMD. A execucao de `mvn verify` produziu
`BUILD FAILURE` em `2026-08-23T22:31:17-03:00`:

- PMD identificou 6 violacoes `CommentRequired` de prioridade 3.
- Como 6 e maior que `maxAllowedViolations=5`, o goal `pmd:check` bloqueou o
  ciclo corretamente.

Depois da restauracao manual do codigo, uma nova execucao de `mvn verify`
terminou com `BUILD SUCCESS` em `2026-08-23T22:32:30-03:00`, com PMD sem
falhas e Checkstyle com 0 violacoes. Isso demonstra que a falha e a recuperacao
do build sao controladas pelas regras configuradas, sem suprimir regras nem
alterar o limite de violacoes.

## Pendencias

- Escrever o rascunho autoral do relatorio final de, no maximo, uma pagina.
- Registrar apenas depois do rascunho as atividades `#10` e `#14`, por meio de
  uma solicitacao de feedback de clareza e organizacao.
- Atualizar o indice Git para incluir as ultimas alteracoes deste registro
  antes do commit final.

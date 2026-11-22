---
layout: default
title: Configuration
parent: Micro Serviços Intermediário
nav_order: 8
---

# Configuration 🛠️

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/configuration/slides/index.html#/"
        title="Configuration"
        width="90%" height="500" style="border:none;">
    </iframe>
</center>

## Por que precisamos nos preocupar com configuração? 🤔

Imagine que você comprou uma televisão nova. Você certamente não gostaria de ter
que abrir o aparelho com uma chave de fenda e soldar novos fios na placa de
circuito toda vez que quisesse aumentar o volume, conectar ao Wi-Fi da sua casa
ou plugar o aparelho em uma tomada de 110V ou 220V. Você espera que a TV venha
com botões, controles remotos e seletores externos prontos para ajuste.
{: .fs-3 }

Com microsserviços em contêineres e na nuvem, o princípio é exatamente o mesmo.
Um dos pilares mais fundamentais das aplicações modernas (descrito na metodologia
[The Twelve-Factor App](https://12factor.net/pt_br/config)) afirma que **o código
compilado deve ser estritamente o mesmo em qualquer ambiente**. O que muda de
um estágio para outro não é o código-fonte, mas a **configuração** injetada
pelo ambiente em que ele roda:
{: .fs-3 }

* Em **desenvolvimento**, seu serviço conecta ao banco de dados no `localhost` sem senha.
* Em **testes automatizados**, ele aponta para um banco em memória temporário.
* Em **produção**, ele se conecta ao cluster de banco de dados corporativo, com credenciais seguras e conexões criptografadas.
{: .fs-3 }

No ecossistema Java moderno, o Quarkus utiliza a especificação
[MicroProfile Config](https://github.com/eclipse/microprofile-config), implementada
pelo [SmallRye Config](https://github.com/smallrye/smallrye-config). Ela nos dá
uma forma declarativa e elegante de injetar propriedades diretamente nos nossos
objetos, lendo valores de arquivos, variáveis de ambiente, parâmetros de inicialização
ou contêineres sem que precisemos escrever uma única linha de código para abrir
ou processar arquivos manualmente.
{: .fs-3 }

## O kit de ferramentas de configuração 🧰

Vamos conhecer os principais recursos de configuração que utilizaremos:
{: .fs-3 }

* **`@ConfigProperty`** (*"Injetando o botão"*): anotação que busca a propriedade configurada e a entrega pronta para o seu código Java, com conversão automática de tipo (String, int, boolean, etc.).
* **`application.properties`** (*"O painel padrão"*): arquivo central onde declaramos os valores padrão versionados junto ao código do serviço.
* **Perfis (`%dev`, `%test`, `%prod`)** (*"Uma roupa para cada ocasião"*): seletores que alteram valores específicos de acordo com a fase de desenvolvimento, sem duplicar arquivos desnecessariamente.
* **Arquivos `.env` e Variáveis de Ambiente** (*"O cofre e os ajustes locais"*): valores que ficam fora do versionamento do Git para guardar senhas, tokens e ajustes locais da sua máquina.
* **Arquivos YAML** (*"Hierarquia visual"*): uma alternativa limpa e legível para quem prefere estruturar configurações em árvore ao invés de chaves planas pontuadas.
{: .fs-3 }

## Preparando o ambiente 🛠️

Para praticar todos os conceitos deste tópico, você pode criar um projeto Quarkus
novinho em folha com suporte a endpoints REST:
{: .fs-3 }

```sh
mvn io.quarkus.platform:quarkus-maven-plugin:3.40.1.2:create \
    -DprojectGroupId=dev.pw2 \
    -DprojectArtifactId=config-quickstart \
    -Dextensions='rest' \
    -DclassName="dev.pw2.ConfigResource" \
    -Dpath="/config"

cd config-quickstart
code .
```

A extensão `rest` (ou `quarkus-rest`) traz o essencial para expor endpoints
HTTP e já inclui o suporte nativo a injeção CDI e MicroProfile Config.
{: .fs-3 }

## 1. Onde ficam as configurações por padrão? 📁

No Quarkus, o local canônico para declarar configurações da aplicação é o arquivo:
{: .fs-3 }

```text
src/main/resources/application.properties
```

Esse arquivo é empacotado dentro do artefato da aplicação (JAR/imagem nativa).
Vamos adicionar duas propriedades de exemplo:
{: .fs-3 }

```properties
pw2.mensagem=Ola, bem-vindo ao mundo dos microsservicos!
pw2.autor=Rodrigo
```

## 2. Injetando configurações com `@ConfigProperty` 💉

Para usar essas propriedades no código, usamos a anotação `@ConfigProperty`
em campos de beans CDI (como nossos recursos REST):
{: .fs-3 }

```java
package dev.pw2;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/config")
public class ConfigResource {

    @Inject
    @ConfigProperty(name = "pw2.mensagem")
    String mensagem;

    @Inject
    @ConfigProperty(name = "pw2.autor", defaultValue = "Anonimo")
    String autor;

    @Inject
    @ConfigProperty(name = "pw2.versao")
    Optional<String> versao;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String exibir() {
        return mensagem + " (Autor: " + autor + ", Versao: " + versao.orElse("1.0-snapshot") + ")";
    }
}
```

Vamos decifrar o que acontece passo a passo:
{: .fs-3 }

1. O Quarkus intercepta os campos marcados com `@ConfigProperty`.
2. O campo `mensagem` recebe exatamente o valor da chave `pw2.mensagem` definida no `application.properties`.
3. O campo `autor` busca a chave `pw2.autor`. Se ela não existisse, assumiria o valor `"Anonimo"` definido em `defaultValue`.
4. O campo `versao` é do tipo `Optional<String>`. Como a chave `pw2.versao` não foi declarada no arquivo, o Quarkus não falha: ele simplesmente injeta um `Optional.empty()`.
5. No método `exibir()`, usamos `versao.orElse("1.0-snapshot")` para fornecer um valor amigável de contingência.
{: .fs-3 }

💡 **Dica:** No Quarkus, quando você anota um campo com `@ConfigProperty`, a anotação `@Inject` é opcional, mas muitos desenvolvedores a mantêm por clareza e aderência estrita à especificação CDI.
{: .fs-3 }

### E se uma propriedade não existir? 💥

Esta é uma das principais dúvidas de quem está começando:
{: .fs-3 }

| Situação no Código | Comportamento do Quarkus |
|---|---|
| `@ConfigProperty(name = "chave")` sem valor e sem `defaultValue` | **A aplicação não sobe!** Falha na inicialização lançando `DeploymentException`. |
| Com `defaultValue = "padrao"` | Inicializa com o valor padrão informado. |
| Com o tipo `Optional<T>` | Inicializa com sucesso, recebendo `Optional.empty()`. |
{: .fs-3 }

⚠️ **Atenção:** O comportamento de falhar logo na inicialização (*fail-fast*)
quando uma propriedade obrigatória falta é uma funcionalidade deliberada e muito
segura! É infinitamente melhor o serviço avisar que falta a URL do banco antes
de começar a atender clientes do que falhar silenciosamente com um `NullPointerException`
quando o primeiro usuário tentar se autenticar.
{: .fs-3 }

## 3. Lendo configurações de forma programática 🔍

Em alguns casos pontuais — como classes utilitárias que não são gerenciadas pelo
CDI ou quando a chave que você precisa ler é calculada dinamicamente —, você pode
obter a configuração diretamente usando a classe `ConfigProvider`:
{: .fs-3 }

```java
import org.eclipse.microprofile.config.ConfigProvider;

String dbHost = ConfigProvider.getConfig()
    .getValue("banco.host", String.class);

Optional<Integer> dbPorta = ConfigProvider.getConfig()
    .getOptionalValue("banco.porta", Integer.class);
```

## 4. Sobrescrevendo em runtime e ordem de precedência ⚡

Imagine que seu serviço foi compilado e você precisa executá-lo em um servidor
específico sem alterar o arquivo `application.properties` que está dentro do JAR.
Você pode passar qualquer propriedade na inicialização via parâmetro de sistema
`-D`:
{: .fs-3 }

```sh
./mvnw quarkus:dev -Dpw2.mensagem="Ola direto da linha de comando!"
```

Ou através de **variáveis de ambiente** do sistema operacional:
{: .fs-3 }

```sh
PW2_MENSAGEM="Ola via variavel de ambiente" ./mvnw quarkus:dev
```

### Qual valor ganha? Ordem de precedência 🏆

Quando o mesmo nome de propriedade é definido em locais diferentes, o Quarkus
adota uma ordem de precedência rígida (do maior para o menor):
{: .fs-3 }

1. **Parâmetros de linha de comando (`-Dchave=valor`)**: maior prioridade de todas.
2. **Variáveis de ambiente do sistema operacional** (ex: `PW2_MENSAGEM=...`).
3. **Arquivo `.env`** na raiz do projeto (carregado em desenvolvimento).
4. **`application.properties`** (específico de perfil, ex: `%dev.`).
5. **`application.properties`** (padrão geral).
{: .fs-3 }

## 5. Perfis de configuração: dev, test e prod 🎭

Durante o dia a dia, precisamos de valores diferentes para o banco de dados local
e o banco de dados da nuvem. O Quarkus resolve isso de forma brilhante com
**perfis de configuração** (*profiles*).
{: .fs-3 }

Por padrão, existem três perfis automáticos:
{: .fs-3 }

* **`dev`**: ativado automaticamente quando você executa `./mvnw quarkus:dev`.
* **`test`**: ativado automaticamente durante a execução de testes (`./mvnw test`).
* **`prod`**: o modo padrão ao empacotar ou executar o executável final (`java -jar ...`).
{: .fs-3 }

No arquivo `application.properties`, usamos o prefixo `%nome-do-perfil.` para
atribuir valores exclusivos àquele ambiente:
{: .fs-3 }

```properties
# Configuracao padrao (vale para todos que nao sobrescreverem)
pw2.banco.url=jdbc:postgresql://localhost:5432/meubanco_dev

# Configuracao especifica para quando a aplicacao rodar em producao
%prod.pw2.banco.url=jdbc:postgresql://db.minhaempresa.com:5432/meubanco_prod

# Configuracao especifica para quando rodar testes automatizados
%test.pw2.banco.url=jdbc:h2:mem:testdb
```

Também é possível criar **perfis customizados**:
{: .fs-3 }

```properties
quarkus.http.port=8080
%homologacao.quarkus.http.port=8090
```

E ativá-lo na inicialização com `-Dquarkus.profile=homologacao`.
{: .fs-3 }

## 6. O arquivo `.env`: guardando segredos locais 🔐

Você nunca deve comitar senhas de banco, tokens de API ou chaves privadas no
repositório Git. O arquivo `.env` serve justamente para guardar esses segredos
e ajustes de máquina local.
{: .fs-3 }

### Onde colocar o `.env`?

Ao contrário do `application.properties` (que fica em `src/main/resources`),
o `.env` deve ficar **na raiz do projeto** (ao lado do `pom.xml`):
{: .fs-3 }

```text
meu-projeto/
├── .env                          ← AQUI (adicione ao seu .gitignore!)
├── pom.xml
└── src/main/resources/
    └── application.properties    ← Configurações versionadas no Git
```

### Regra de conversão de nomes

As variáveis no `.env` seguem o padrão das variáveis de ambiente:
**letras maiúsculas e pontos substituídos por sublinhados (`_`)**:
{: .fs-3 }

* `pw2.mensagem` vira `PW2_MENSAGEM`
* `quarkus.http.port` vira `QUARKUS_HTTP_PORT`
* `banco.dados.senha` vira `BANCO_DADOS_SENHA`
{: .fs-3 }

Exemplo de `.env`:
{: .fs-3 }

```properties
PW2_MENSAGEM="Mensagem super secreta do meu ambiente local"
QUARKUS_HTTP_PORT=9090
DB_SENHA=senhaUltraSecreta123
```

### Expressões e valores de contingência `${VAR:padrao}`

Você pode conectar o `application.properties` às variáveis do sistema ou do `.env`
usando a sintaxe de expressão:
{: .fs-3 }

```properties
banco.senha=${DB_SENHA:senha_padrao_dev}
servico.externo.url=${URL_SERVICO:http://localhost:8081}
```

Se a variável `DB_SENHA` existir no `.env` ou no sistema, o Quarkus usa o seu
valor. Se não existir, ele adota `senha_padrao_dev`.
{: .fs-3 }

## 7. O namespace reservado `quarkus.` e configurações de build 🚨

O próprio Quarkus é configurado pelo mesmo sistema de propriedades! Todas as
chaves que configuram o framework e suas extensões começam com `quarkus.`:
{: .fs-3 }

```properties
# Altera a porta do servidor HTTP do Quarkus
quarkus.http.port=8082

# Altera o nivel de log do console
quarkus.log.console.level=DEBUG
```

⚠️ **Atenção:** **Nunca** use o prefixo `quarkus.` para suas propriedades de
negócio (como `quarkus.minha-taxa`). Esse namespace é reservado exclusivamente
para o framework.
{: .fs-3 }

Além disso, note que algumas opções do Quarkus são de **tempo de compilação/build**
(marcadas com um ícone de cadeado 🔒 na documentação oficial). Essas propriedades
são gravadas permanentemente no bytecode durante o build e não podem ser
alteradas em tempo de execução sem recompilar a aplicação.
{: .fs-3 }

## 8. Arquivos YAML: uma alternativa visual 📄

Se você prefere a sintaxe hierárquica do YAML ao invés de propriedades pontuadas,
basta adicionar a extensão `quarkus-config-yaml`:
{: .fs-3 }

```sh
./mvnw quarkus:add-extension -Dextensions='quarkus-config-yaml'
```

E criar o arquivo `src/main/resources/application.yml`. Compare a elegância
dos dois formatos:
{: .fs-3 }

Em `application.properties`:
{: .fs-3 }

```properties
pw2.servico.nome=Notificacoes
pw2.servico.timeout=5000
quarkus.http.port=8080
```

Em `application.yml`:
{: .fs-3 }

```yaml
pw2:
  servico:
    nome: Notificacoes
    timeout: 5000

quarkus:
  http:
    port: 8080
```

---

## Colocando tudo em prática: dominando a configuração 🎯

Estes exercícios foram planejados para serem feitos em sequência, de forma simples
e direta, para construir a sua confiança passo a passo.
{: .fs-3 }

### Exercício 1: Sua primeira propriedade injetada 🟢

**Objetivo:** Declarar uma propriedade no `application.properties` e exibi-la via endpoint REST.
{: .fs-3 }

**Passo 1:** Abra o arquivo `src/main/resources/application.properties` e adicione:
{: .fs-3 }

```properties
app.boas-vindas=Bem-vindo ao curso de Microsservicos!
```

**Passo 2:** Crie a classe `src/main/java/dev/pw2/BoasVindasResource.java`:
{: .fs-3 }

```java
package dev.pw2;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/boas-vindas")
public class BoasVindasResource {

    @ConfigProperty(name = "app.boas-vindas")
    String mensagem;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String saudar() {
        return mensagem;
    }
}
```

**Teste:**
{: .fs-3 }

```sh
curl http://localhost:8080/boas-vindas
```

**Resultado esperado:**
{: .fs-3 }

```text
Bem-vindo ao curso de Microsservicos!
```

---

### Exercício 2: Tratando propriedades opcionais com segurança 🛡️

**Objetivo:** Aprender a usar `defaultValue` e `Optional<T>` para evitar que a aplicação quebre se a configuração faltar.
{: .fs-3 }

**Passo 1:** No mesmo `BoasVindasResource.java`, injete duas novas propriedades:
{: .fs-3 }

```java
    @ConfigProperty(name = "app.departamento", defaultValue = "Tecnologia")
    String departamento;

    @ConfigProperty(name = "app.cupom")
    java.util.Optional<String> cupom;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String saudar() {
        return mensagem + " | Depto: " + departamento + " | Cupom: " + cupom.orElse("Nenhum cupom ativo");
    }
```

**Teste:**
Note que nós **não** declaramos `app.departamento` nem `app.cupom` no `application.properties`. Faça a requisição:
{: .fs-3 }

```sh
curl http://localhost:8080/boas-vindas
```

**Resultado esperado:**
A aplicação responde normalmente usando o valor padrão e o fallback do Optional!
{: .fs-3 }

```text
Bem-vindo ao curso de Microsservicos! | Depto: Tecnologia | Cupom: Nenhum cupom ativo
```

---

### Exercício 3: Sobrescrita em tempo de execução com `-D` ⚡

**Objetivo:** Alterar o valor de uma propriedade em runtime sem modificar uma única linha de código.
{: .fs-3 }

**Passo 1:** No terminal onde você inicia o Quarkus, execute com o parâmetro `-D`:
{: .fs-3 }

```sh
./mvnw quarkus:dev -Dapp.boas-vindas="Ola via System Property!"
```

**Teste:**
{: .fs-3 }

```sh
curl http://localhost:8080/boas-vindas
```

**Resultado esperado:**
A mensagem exibida passa a ser `Ola via System Property!`, demonstrando que o parâmetro de inicialização tem prioridade sobre o arquivo estático.
{: .fs-3 }

---

### Exercício 4: Perfis na prática (`%dev` e `%prod`) 🎭

**Objetivo:** Configurar comportamentos distintos para o modo de desenvolvimento e o modo de produção.
{: .fs-3 }

**Passo 1:** Adicione em `src/main/resources/application.properties`:
{: .fs-3 }

```properties
app.ambiente=Ambiente padrao
%dev.app.ambiente=Voce esta em MODO DESENVOLVIMENTO (Dev Mode ativo)
%prod.app.ambiente=Voce esta em PRODUCAO (Seguranca maxima)
```

**Passo 2:** Injete no recurso:
{: .fs-3 }

```java
    @ConfigProperty(name = "app.ambiente")
    String ambiente;
```

E adicione ao retorno de `saudar()`.
{: .fs-3 }

**Teste:**
Com o Quarkus rodando via `./mvnw quarkus:dev`, chame o endpoint e veja a mensagem de desenvolvimento.
Depois, simule o perfil de produção rodando:
{: .fs-3 }

```sh
./mvnw quarkus:dev -Dquarkus.profile=prod
```

**Resultado esperado:**
O Quarkus ativa as configurações marcadas com `%prod`, exibindo o texto de produção automaticamente!
{: .fs-3 }

---

### Exercício 5: Isolando credenciais com o arquivo `.env` 🔐

**Objetivo:** Usar um arquivo `.env` para sobrescrever valores locais sem comitar segredos no Git.
{: .fs-3 }

**Passo 1:** Crie um arquivo com o nome `.env` na **raiz** do projeto (onde está o `pom.xml`):
{: .fs-3 }

```properties
APP_BOAS_VINDAS="Mensagem carregada com sucesso do arquivo .env!"
APP_CUPOM="DESCONTO-DEV-10"
```

**Teste:**
Inicie normalmente o Quarkus (`./mvnw quarkus:dev`) e faça o teste:
{: .fs-3 }

```sh
curl http://localhost:8080/boas-vindas
```

**Resultado esperado:**
O Quarkus detecta automaticamente o arquivo `.env` local e substitui as propriedades correspondentes, preenchendo tanto a mensagem quanto o cupom que antes estava vazio!
{: .fs-3 }

---

## Desafio opcional: migrando o serviço Books para YAML 🏋️

Agora que você domina as propriedades e perfis, vamos praticar a migração
estrutural no projeto de gerenciamento de livros da disciplina:
{: .fs-3 }

```shell
git clone -b dev https://github.com/rodrigoprestesmachado/pw2
code pw2/exemplos/books/users
```

O arquivo `src/main/resources/application.properties` do serviço `users` contém:
{: .fs-3 }

```properties
# HTTP
quarkus.http.port=8080

# HTTPS
quarkus.http.ssl-port=8443
quarkus.http.ssl.certificate.key-store-file=keystore.jks
quarkus.http.ssl.certificate.key-store-password=password
quarkus.tls.trust-all=true

# JWT Sign Key Location
smallrye.jwt.sign.key.location=privateKey.pem
```

Siga os passos:
{: .fs-3 }

1. Abra o projeto `exemplos/books/users`.
2. Adicione a dependência `quarkus-config-yaml` no `pom.xml`.
3. Crie `src/main/resources/application.yml` convertendo a sintaxe pontuada para árvore YAML:
{: .fs-3 }

```yaml
quarkus:
  http:
    port: 8080
    ssl-port: 8443
    ssl:
      certificate:
        key-store-file: keystore.jks
        key-store-password: password
  tls:
    trust-all: true

smallrye:
  jwt:
    sign:
      key:
        location: privateKey.pem
```

4. Renomeie ou esvazie o arquivo `application.properties` anterior.
5. Inicie o serviço com `./mvnw quarkus:dev` e valide se a porta e as chaves foram carregadas com sucesso.
{: .fs-3 }

---

## Teste seus conhecimentos 🧠

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/configuration/questions.html"
        title="Configuration"
        width="90%" height="500"
        style="border:none;">
    </iframe>
</center>

## Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.
* Guia Oficial de Configuração do Quarkus: [https://quarkus.io/guides/config-reference](https://quarkus.io/guides/config-reference)
* Especificação Eclipse MicroProfile Config: [https://github.com/eclipse/microprofile-config](https://github.com/eclipse/microprofile-config)
* SmallRye Config: [https://github.com/smallrye/smallrye-config](https://github.com/smallrye/smallrye-config)
{: .fs-3 }

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>

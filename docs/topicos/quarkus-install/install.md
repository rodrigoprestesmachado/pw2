---
layout: default
title: Instalação do Quarkus
nav_order: 2
---

# Instalação do Quarkus

Essa página tem como o objetivo de orientar sobre a instalação do Quarkus.

## Configurando o ambiente

Antes de utilizar o Quarkus você necessita instalar o Java e o Maven na sua
máquina. Caso você esteja com essas duas ferramentas instaladas, por favor,
pule as próximas duas subseções.

### Java ♨️

Antes de iniciarmos, se faz necessário verificar se você possui uma JVM
(*Java Virtual Maquine*) e um JDK (*Java Development Kit*) 11 ou superior
instalado na sua máquina:

    java -version

Para verificar se o JDK que está instalado digite:

    javac -version

Se os comandos acima retornarem a versão do Java e do compilador, significa que
tem que a sua máquina possui o primeiro requisito para rodar o Quarkus.

Porém, se você não tiver o Java instalado na sua máquina, recomendo que você
utilize o gerenciador de SDKs [SDKMAN](https://sdkman.io) para instalar o Java.

Para instalá-lo, abra um terminal e copie e cole o seguinte comando:

    curl -s "https://get.sdkman.io" | bash

Com o SDKMAN instalado, utilize o seguinte comando para instalar o Java:

    sdk install java

O SDKMAN é uma ferramenta que permite a instalação de diversas versões do Java,
tais como: OpenJDK, Oracle JDK, Temurin, GraalVM, etc. Assim, a grande vantagem
de se utilizar o SDKMAN é que ele alterar a versão do Java que está sendo
utilizada na sua máquina de maneira descomplicada. Para saber mais sobre o
SDKMAN, por favor, consulte a [documentação](https://sdkman.io) da ferramenta.

### Maven 🌐

O Maven é uma ferramenta de automação de compilação utilizada principalmente
para projetos Java. Ele é utilizado para gerar um artefato (geralmente um
arquivo `.jar` ou `.war`) a partir do código fonte do projeto. Além disso,
o Maven gerencia as dependências do projeto, o ciclo de vida do *build*, teste e
a até mesmo a distribuição do projeto.

Para instalar o Maven utilizando o SDKMAN, digite o seguinte comando:

    sdk install maven

## Quarkus CLI ⚙️

Outra forma bastante útil de se trabalhar com o Quarkus é por meio de sua
interface de linha de comando (_Command Line Interface_). Para instalar o
Quarkus CLI, por meio do SDKMAN, digite o seguinte comando:

    sdk install quarkus

Faça um teste para ver se o Quarkus foi instalado corretamente:

    quarkus --version

Se você digitar `quarkus --help` será possível verificar todas as
[funcionalidades do CLI](https://quarkus.io/guides/cli-tooling#using-the-cli),
entre elas: criar um projeto (app ou linha de comando), fazer um *build*, rodar
um projeto em modo de desenvolvimento, entre outros.

## Quarkus no VSCode 🖥️

A [extensão](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-quarkus)
do Quarkus para o VSCode é uma ferramenta que permite que você crie,
desenvolva, gerencie dependências, teste, etc. de aplicações Quarkus diretamente
do seu editor de código. Para instalar a extensão, abra o VSCode e digite
`Quarkus` na barra de pesquisa de extensões. A extensão oficial do Quarkus
é a primeira opção que aparece na lista.

## Quarkus IO

Uma das formas mais fáceis de iniciar um projeto com o Quarkus é acessar o site
[https://code.quarkus.io](https://code.quarkus.io) que disponibiliza uma
ferramenta para configurar e baixar um projeto Quarkus inicial.

Nesse site você pode escolher se quer que o seu projeto tenha as suas
dependências e ciclo de *build* gerenciado por meio do
[Maven](https://maven.apache.org) ou [Gradle](https://gradle.org). Também é
possível escolher as dependências necessárias para o projeto, como por exemplo,
[RESTEasy JAX-RS](https://quarkus.io/guides/rest-json),
[Hibernate com o Panache](https://quarkus.io/guides/hibernate-orm-panache),
[Smallrye JWT](https://quarkus.io/guides/security-jwt) entre muitas outras.
Caso você necessite de novas dependências para o seu projeto não se preocupe,
pois, existem pelo menos mais duas formas de adicionar essas dependências
(VSCode, Quarkus CLI, etc.) no tempo de desenvolvimento do sistema.

<center>
    <img src="img/quarkusio.jpg" alt="Ilustração do site code.quarkus.io" width="400"/>
    <br>
    Fig 1 - Site Quarkus.io
</center>

Depois de configurar, o site irá permitir que você faça um *download* do projeto
 no formato `.zip`. Para executar o projeto, basta descompactar e, na raiz do
 projeto (localização do arquivo pom.xml) executar o comando do Maven:

    ./mvnw compile quarkus:dev

## Codespace 🚀

Outra forma de se trabalhar com o Quarkus é por meio do [Codespace](https://github.com/features/codespaces)
do GitHub. O Codespace é um ambiente de desenvolvimento que roda na nuvem e
que permite que você desenvolva, teste e depure o seu código
diretamente do seu navegador. O Codespace utiliza o [Visual Studio Code](https://code.visualstudio.com)
como editor de código e permite que você instale diversas extensões como a do
Quarkus.

Assim, depois de criar uma máquina no Codespace, você pode acessá-lo por meio do
navegador. Logo, crie um diretório chamado `devcontainer`, dentro desse diretório
adicione um arquivo chamado `devcontainer.json`. Dentro desse arquivo, coloque a
configuração do container para que o Quarkus possa ser executado no Codespace.
[Quarkus Codespace](https://gist.github.com/rodrigoprestesmachado/84feb44d39bb944f4581cbb8c55e032d).

A configuração acima possui o Java 21, Maven, Docker e o Quarkus CLI. Além disso,
o VSCode irá instalar várias extensões, entre elas o Java Extension Pack e o
Quarkus.

* Uma dica, uma máquina com pelo menos 4 cores e 16GB de memória RAM fará com que
  o seu desenvolvimento seja mais confortável.

* Uma segunda dica é abrir a porta 8080 no Codespace para que você possa acessar
  a aplicação por meio do navegador. Para isso, procure a aba `Ports` no Codespace
  e adicione a porta 8080.

# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>
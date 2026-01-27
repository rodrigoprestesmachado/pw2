<!-- .slide: data-background-opacity="0.3" data-background-image="img/title.jpg" data-transition="convex" -->
# Configuration
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Introdução
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Quarkus é um framework Java projetado para aplicativos nativos da nuvem.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- A configuração desempenha um papel crucial uma vez que os aplicativos Quarkus
são executados em contêineres, a configuração é essencial para garantir que o
aplicativo
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- O Quarkus suporta configuração baseada em propriedades e injeção de
  configuração.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Arquivos de Configuração
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- O Quarkus usa arquivos `application.properties` ou `application.yml` para
  configuração.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

- Esses arquivos estão geralmente localizados em `src/main/resources`.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Propriedades Comuns
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- `quarkus.http.port`: Define a porta HTTP do servidor.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- `quarkus.datasource.url`: URL da fonte de dados para integração com banco de
  dados.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- `quarkus.log.console.enable`: Ativa ou desativa a saída de log no console.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Profiles
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- Os profiles permitem diferentes configurações para diferentes ambientes.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- Exemplo: `application-dev.properties` para ambiente de desenvolvimento e `application-prod.properties` para produção.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- Também pode ser utilizado todas a propriedades em um único arquivo
  `application.properties` e definir o perfil por meio dos prefixos
  %dev, %prod e %test em cada propriedade.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Injeção de Configuração
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- O Quarkus suporta injeção de configuração em beans CDI (_Contexts and
  Dependency Injection_).
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->

- Use a anotação `@ConfigProperty` para injetar propriedades configuradas.
<!-- .element: style="margin-bottom:60px; font-size: 25px; color:white" -->


<!-- .slide: data-background="white" data-transition="convex" -->
## Injeção de Configuração
<!-- .element: style="margin-bottom:80px; font-size: 50px; color:black; font-family: Marker Felt;" -->

```java
@Inject
@ConfigProperty(name = "minha.propriedade")
String minhaPropriedade;
```
<!-- .element: style="margin-bottom:60px; font-size: 24px; color:black" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Referências
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.
<!-- .element: style="margin-bottom:50px; font-size: 25px; color:white" -->

* SMALLRYE FAULT TOLERANCE disponível em: [https://quarkus.io/guides/smallrye-fault-tolerance](https://quarkus.io/guides/smallrye-fault-tolerance)
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
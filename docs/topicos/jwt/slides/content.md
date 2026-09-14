<!-- .slide: data-background-opacity="0.3" data-background-image="img/title.jpg" data-transition="convex" -->
# Json Web Token (JWT)
<!-- .element: style="margin-bottom:100px; font-size: 60px; color:white; font-family: Marker Felt;" -->

Pressione 'F' para tela cheia
<!-- .element: style="margin-bottom:10px; font-size: 15px; color:white" -->

[versão em pdf](?print-pdf)
<!-- .element: style="margin-bottom 25px; font-size: 15px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## O que é JWT?
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* JWT é um padrão aberto (RFC 7519) que define um método compacto e autônomo para transmitir informações entre partes como um objeto JSON.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Estrutura do JWT
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Um JWT é composto por três partes separadas por pontos:
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

  - **Header**: contém o tipo do token (JWT) e o algoritmo de hashing usado.
  <!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

  - **Payload**: contém as claims (reivindicações), que são informações sobre a
    entidade e metadados adicionais.
  <!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

  - **Signature**: é a assinatura do token, usada para verificar a integridade
  do token e garantir que não tenha sido alterado.
  <!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Funcionamento do JWT
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

1. **Login**: o cliente envia usuário e senha para um serviço de autenticação.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

2. **Emissão**: o servidor valida as credenciais e gera (assina) um JWT.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

3. **Uso**: o cliente guarda o token e o envia em cada requisição seguinte.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

4. **Verificação**: o servidor valida a assinatura do JWT para autorizar a solicitação.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Funcionamento do JWT
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/funcionamento.puml">
        <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/dev/docs/topicos/jwt/funcionamento.puml" alt="Funcionamento do JWT" width="40%" height="40%"/>
    </a>
    <br/>
</center>


<!-- .slide: data-background="#21093D" data-transition="convex" -->
## Por que utilizar JWT?
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

- **Segurança**: padrão seguro e amplamente utilizado para autenticação e
autorização de usuários.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

- **Escalabilidade**: padrão leve e eficiente, fácil de integrar em qualquer
aplicação.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

- **Interoperabilidade**: padrão aberto e amplamente suportado por diversas
linguagens e *frameworks*.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->

- **Flexibilidade**: permite incluir informações adicionais no token, como
nome do usuário e papéis.
<!-- .element: style="margin-bottom:70px; font-size: 25px; color:white" -->


<!-- .slide: data-background="#21093D" data-transition="convex" -->
# Referências 📚
<!-- .element: style="margin-bottom:50px; font-size: 50px; color:white; font-family: Marker Felt;" -->

* Usando JWT RBAC. Disponível em: [https://quarkus.io/guides/security-jwt](https://quarkus.io/guides/security-jwt)
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

* Alex Soto Bueno; Jason Porter; Quarkus Cookbook: Kubernetes-Optimized Java Solutions. Editora: O'Reilly Media, 2020.
<!-- .element: style="margin-bottom:40px; font-size: 20px; color:white" -->

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">CC BY 4.0 DEED</a>
</center>
<!-- .element: style="margin-top:100px; font-size: 15px; font-family: Bradley Hand" -->
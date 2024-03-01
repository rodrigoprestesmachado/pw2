---
layout: default
title: Web Services
nav_order: 3
---

# Web Services 🌐

<center>
    <iframe src="https://pw2.rpmhub.dev/topicos/webservices/slides/index.html#/"
        title="Web Services" width="90%" height="500" style="border:none;">
    </iframe>
</center>


## Resumo 📝

Um Web Service é uma tecnologia que permite a comunicação entre diferentes
sistemas de software pela internet, mais especificamente, pela World Wide Web.
Ele é projetado para facilitar a interoperabilidade entre aplicativos,
permitindo que eles compartilhem dados e funcionalidades de forma eficiente
e segura, independentemente da plataforma ou linguagem de programação utilizada.

Na essência, um Web Service disponibiliza uma interface acessível pela web,
utilizando padrões abertos e protocolos como HTTP, XML e JSON para trocar
informações entre sistemas. Isso significa que um sistema pode solicitar
dados ou serviços a partir de outro sistema, enviar informações e até mesmo
realizar operações remotas, tudo isso através de chamadas HTTP.

Existem diferentes tipos de Web Services, sendo os mais comuns:

1. **XML Web Services**: Utiliza dois padrões principais: SOAP e WSDL.
   O SOAP (_Simple Object Access Protocol_) é um protocolo baseado em XML para
   troca de mensagens entre sistemas, enquanto o WSDL
   (_Web Services Description Language_) é uma linguagem baseada em XML para
   descrever a interface de um Web Service. Os Web Services baseados em XML são
   mais complexos e pesados, mas oferecem suporte a funcionalidades avançadas,
   como segurança e transações.

2. **REST (Representational State Transfer)**: O REST é um estilo arquitetural
   que utiliza os próprios métodos HTTP, como GET, POST, PUT e DELETE, para
   realizar operações sobre recursos. Os Web Services RESTful são geralmente
   mais simples de implementar e mais leves que os serviços SOAP. Devido à sua
   simplicidade, atualmente, existe uma grande adesão a este estilo de
   Web Service, por essa razão, o REST será o foco desta disciplina.

Os Web Services são amplamente utilizados na integração de sistemas,
permitindo que diferentes aplicações se comuniquem e cooperem entre si de forma
transparente. Eles são essenciais para o desenvolvimento de sistemas
distribuídos e aplicações que dependem da troca de dados com outros sistemas
pela internet.

Além disso, os Web Services desempenham um papel fundamental na construção de
arquiteturas de sistemas como a de micro serviços. Micro serviços são uma
abordagem arquitetural na qual um aplicativo é construído como um conjunto de
pequenos serviços independentes, cada um executando um processo específico e
comunicando-se através de APIs leves, geralmente baseadas
em serviços REST ou protocolos de mensagens assíncronas. Cada serviço é
desenvolvido, implantado e dimensionado de forma independente, permitindo maior
flexibilidade, escalabilidade e facilidade de manutenção em comparação com
arquiteturas monolíticas. Essa modularidade dos micro serviços facilita a
evolução contínua do sistema, tornando-os uma escolha popular para aplicações
modernas e distribuídas.

---
Para saber mais sobre Web Services: consulte o o capítulo 7 do livro [Desenvolvimento de software, v.3 programação de sistemas web orientada a objetos em Java](https://biblioteca.ifrs.edu.br/pergamum_ifrs/biblioteca_s/acesso_login.php?cod_acervo_acessibilidade=5020683&acesso=aHR0cHM6Ly9pbnRlZ3JhZGEubWluaGFiaWJsaW90ZWNhLmNvbS5ici9ib29rcy85Nzg4NTgyNjAzNzEw&label=acesso%20restrito) para compreender detalhes sobre a implementação de Web
Services em Java.

---

## Exercício 📝

Desenvolva um Web Service em Rest utilizando o framework Quarkus que permita
realizar as seguintes conversões de unidades de medida:

1. **Conversão de Quilômetro por hora para Milhas por hora**:
   - Este método deve aceitar requisições do tipo POST e produzir os resultados
   em formato de texto.
   - A fórmula de conversão a ser utilizada é: 1 quilômetro por hora equivale a
   0.621371 milhas por hora.

1. **Conversão de Nós para Quilômetros por hora**:
   - Este método deve aceitar requisições do tipo GET e retornar os resultados
   em formato JSON.
   - A fórmula de conversão a ser aplicada é: 1 nó equivale a 1.852 quilômetros
   por hora.

Certifique-se de implementar corretamente os casos de teste do exercício.

```java
@QuarkusTest
public class ConversionResourceTest {

    @Test
    public void testConversionKmMiles() {
        given()
            .contentType(ContentType.TEXT)
            // 50 quilômetros por hora
            .body("50")
        .when()
            .post("/Conversion/km-to-miles")
        .then()
            .statusCode(200)
            // O resultado esperado para 50 km/h
            .body(equalTo("31.06855"));
    }

    @Test
    public void testConversionKnotsKm() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/Conversion/knots-to-km/1")
        .then()
            .statusCode(200)
            // O valor esperado para 1 nó em km/h é 1.852
            .body("valor", is(1.852f));
    }
}
```

## RESTFul Web Services (legado)

<center>
<iframe width="560" height="315" src="https://www.youtube.com/embed/PU8EhAHptlQ" title="RESTFul Web Services" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
</center>

## XML Web Services (legado)

<center>
<iframe width="560" height="315" src="https://www.youtube.com/embed/2nP7rzaIw5Y" title="XML Web Services" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
</center>

Dúvidas na configuração do XML Web Service? seguem os arquivos de configuração [server.xml](server.xml) e [pom.xml](pom.xml) utilizados no vídeo.

# Referências 📚

* Alex Soto Bueno; Jason Porter; [Quarkus Cookbook: Kubernetes-Optimized Java Solutions.](https://www.amazon.com.br/gp/product/B08D364VMD/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=B08D364VMD&linkCode=as2&tag=rpmhub-20&linkId=2f82a4bb959a1797ec9791e0af68d1af) Editora: O'Reilly Media, 2020.

* [The Jakarta® EE Tutorial](https://eclipse-ee4j.github.io/jakartaee-tutorial/#the-lifecycles-of-enterprise-beans)

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>
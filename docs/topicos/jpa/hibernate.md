# Hibernate no Quarkus 💾

Esse tutorial tem o objetivo de demonstrar como utilizar o [Hibernate](https://hibernate.org) dentro de um contexto de um RESTFul Web Service (JAX-RS) no [Quarkus](https://quarkus.io). Assim, esse documento apresenta dicas úteis para a implementação de mapeamento objeto relacional, conversão de objetos Java para JSON e consultas por meio da API Critéria.

⚠️ A JPA (Java Persistence API) é uma especificação que o [Hibernate](https://hibernate.org), [Eclipse Link](https://www.eclipse.org/eclipselink/), [Open JPA](http://openjpa.apache.org), [entre outros](https://en.wikibooks.org/wiki/Java_Persistence/Persistence_Products), respeitam/implementam.

No Hibernate, existe uma segunda API chamada de _Hibernate Native API_ que implementa mais funcionalidades do que aqueles especificados pela JPA, a figura 1 mostra uma visão geral sobre o Hibernate.

<center>
    <img src="https://docs.jboss.org/hibernate/orm/5.6/userguide/html_single/images/architecture/data_access_layers.svg" alt="Diagrama de classes" width="30%" height="30%"/> <br/>
    Figura 1 - Visão geral sobre o Hibernate
</center>

# Como implementar?

Imagine que tenhamos que implementar um RESTful Web Service para um sistema bate-papo (_chat_), assim, como ilustração, considere o seguinte diagrama de classes:

<center>
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/master/docs/topicos/jpa/uml.puml" alt="Diagrama de classes" width="25%" height="25%"/> <br/>
    Figura 2 - Diagrama de classes
</center>

💡 Uma dica, a última versão do código desse tutorial no VS Code abra um terminal e digite:

    git clone -b dev https://github.com/rodrigoprestesmachado/pw2
    code pw2/exemplos/hibernate

## Etapa 1: projeto Quarkus

Independente do que desejamos implementar, o primeiro passo é criar um projeto no Quarkus, no Linux/Unix, abra um terminal e digite:

    mvn io.quarkus.platform:quarkus-maven-plugin:2.3.0.Final:create \
    -DprojectGroupId=dev.pw2 \
    -DprojectArtifactId=hibernate \
    -DclassName="dev.pw2.UserWS" \
    -Dpath="/user/list"

Se você estiver no Windows (cmd):

    mvn io.quarkus.platform:quarkus-maven-plugin:2.3.0.Final:create -DprojectGroupId=dev.pw2 -DprojectArtifactId=hibernate -DclassName="dev.pw2.UserWS" -Dpath="/user/list"

Depois de criar, abra o projeto no VS Code por meio do comando:

    code hibernate

Na sequência, abra o arquivo `pom.xml` e adicione as seguintes dependências:

```xml
 <!-- Hibernate -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm</artifactId>
</dependency>
<!-- Panache: auxilia na implementação de consultas com o banco de dados -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
<!-- MYSQL JDBC driver -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-mysql</artifactId>
</dependency>
<!-- 
    Resteasy Jackson: converte objetos Java para JSON no momento de um retorno 
    de um RESTful Web Services (JAX-RS) escrito por meio do Resteasy.
--> 
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId> quarkus-resteasy-jackson</artifactId>
</dependency>
```

Caso você deseje configurar o seu projeto por meio da [extensão](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-quarkus) do Quarkus para VS Code ou por meio do [CLI](https://quarkus.io/guides/cli-tooling), aqui está a lista de dependências que serão necessárias:

    quarkus-hibernate-orm
    quarkus-hibernate-orm-panache
    quarkus-jdbc-mysql
    quarkus-resteasy
    quarkus-resteasy-jackson

## Etapa 2 - Mapeamento objeto relacional

Um vez que tenhamos o nosso projeto configurado, o próximo passo é implementar as classes e fazer o mapeamento com o banco de dados relacional. Para persistir um objeto Java devemos iniciar utilizando a anotação [`@Entity`](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#annotations-jpa-entity), como mostra o exemplo da classe `User` abaixo:

```java
package dev.pw2.model;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class User extends PanacheEntity {

    private String name;
}
```

Note que utilizamos a anotação `@Entity` e herdamos da classe `PanacheEntity`. A classe `PanacheEntity` fornece um conjunto de métodos que facilitam a criação consultas no banco de dados. Quando herdamos de `PanacheEntity`, abrimos mão  de controlar o identificador da entidade/objeto (chave primária do banco - `id`), porém, se você quiser ter o controle sobre o `id`, herde da classe `PanacheEntityBase`.

Quando **não** utilizamos a classe `PanacheEntity`, devemos controlar o identificador da entidade por meio da anotação [`@Id`](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#annotations-jpa-id). Consequentemente, também necessitamos informar como os valores de chave primária são gerados por meio da anotação [`@GeneratedValue`](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#annotations-jpa-generatedvalue). Nesse caso, a classe `User` ficaria como código parecido com:

```java
@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id
    private String name;
}
```

O diagrama de classes do nosso exemplo possui, um relacionamento unidirecional entre a classe `User` e `Message` de um para muitos (`@OneToMany`) e um relacionamento bidirecional entre a classe `User` e `Channel` de muitos para muitos (`@ManyToMany`). Assim, as próximas seções abordam esses dois assuntos.

### one-to-many

O mapeamento do tipo [one-to-many](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Mapping/Relationship_Mappings/Collection_Mappings/OneToMany) são usados para representar o relacionamento entre um objeto e uma coleção de objetos de destino.

<center>
<a href="https://wiki.eclipse.org/File:Onetomany_map_fig.gif">
<img src="https://wiki.eclipse.org/images/9/9e/Onetomany_map_fig.gif" width="35%" height="35%" />
</a> <br/>
Figura 3 - Exemplo de relacionamento um para muitos da documentação do EclipseLink
</center>

A anotação `@OneToMany`possui os seguintes atributos:

* cascade - por padrão, o JPA não coloca em cascata nenhuma operação de persistência para o destino da associação. Assim, as opções de cascateamento são: ALL, MERGE, PERSIST, REFRESH, REMOVE.
* fetch - por padrão, o EclipseLink usa ou tipo de busca javax.persitence.FetchType.LAZY: esta é uma dica para o provedor de persistência de que os dados devem ser buscados lentamente quando são acessados pela primeira vez (se possível). Defina, o fetch para FetchType.EAGER se o requisito necessitar que os dados sejam buscados imediatamente.
* mappedBy - por padrão, se o relacionamento for unidirecional, o provedor de persistência EclipseLink determina o campo que possui o relacionamento. Porém, se o relacionamento for bidirecional, se faz necessário definir o defina o elemento mappedBy no lado inverso.
* targetEntity - por padrão, se você estiver usando uma coleção (`Collection`) usando genéricos (_generics_), o Eclipse Link saberá a entidade de destino associada a partir do tipo de objeto que está sendo referenciado. Porém, se sua coleção não usa genéricos, você deve especificar a classe de entidade que é o destino da associação por meio do targetEntity no lado proprietário da associação.

Como exemplo, vamos implementar o relacionamento unidirecional entre as classes `User` e `Message`:

```java
package dev.pw2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class User extends PanacheEntity {

    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    // name = nome da coluna que irá armazenar a chave estrangeira
    // na tabela Message
    @JoinColumn(name = "user_id")
    private List<Message> messages;

    public User() {
        this.messages = new ArrayList<>();
    }

    // 🚨 os métodos foram omitidos

}

```

Como se trata de um [relacionamento unidirecional](https://en.wikibooks.org/wiki/Java_Persistence/OneToMany#Unidirectional_OneToMany,_No_Inverse_ManyToOne,_No_Join_Table_(JPA_2.x_ONLY)), utilizamos a anotação [`@JoinColumn`](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#annotations-jpa-joincolumn) para indicar a coluna que armazena a chave estrangeira da tabela Message.

## many-to-many

Os mapeamentos [many-to-many](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Mapping/Relationship_Mappings/Collection_Mappings/ManyToMany) representam relacionamentos entre uma coleção de objetos de origem e uma coleção de objetos de destino. Neste caso, exigem a criação de uma tabela intermediária para gerenciar as associações entre os registros de origem e de destino.

<center>
<a href="https://wiki.eclipse.org/File:Mmmapfig.gif">
<img src="https://wiki.eclipse.org/images/e/ef/Mmmapfig.gif" width="35%" height="35%" />
</a><br/>
Figura 4 - Exemplo de relacionamento muitos para muitos da documentação do EclipseLink
</center>

A anotação `@ManyToOne` possui os seguintes atributos:

* cascade - por padrão, a JPA não cascateia nenhuma operação de persistência para o destino da associação. Se você quiser algumas ou todas as operações de persistência em cascata para o destino da associação, defina o valor deste atributo: ALL, MERGE, PERSIST, REFRESH, REMOVE.
* fetch - por padrão, o EclipseLink usa ou tipo de busca javax.persitence.FetchType.LAZY. Defina, o fetch para FetchType.EAGER se o requisito necessitar que os dados sejam buscados imediatamente.
* mappedBy - Por padrão, se o relacionamento for unidirecional, o EclipseLink determina o campo que possui o relacionamento. Se o relacionamento for bidirecional, defina o elemento mappedBy no lado inverso (não proprietário) da associação para o nome do campo ou propriedade que possui o relacionamento.
* targetEntity - por padrão, se a sua coleção (`Collection`) estiver usando genéricos (_generics_), o Eclipse Link saberá a entidade de destino associada a partir do tipo de objeto que está sendo referenciado. Porém, se sua coleção não usa genéricos, você deve especificar a classe de entidade que é o destino da associação por meio do targetEntity no lado proprietário da associação.

Exemplo de relacionamento one-to-many bidirecional entre a classe `User` e `Channel` respectivamente:

```java
package dev.pw2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class User extends PanacheEntity {

    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    // name = nome da coluna que irá armazenar a chave estrangeira
    // na tabela Message
    @JoinColumn(name = "user_id")
    private List<Message> messages;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Channel> channels;

    public User() {
        this.messages = new ArrayList<>();
        this.channels = new ArrayList<>();
    }

    // 🚨 os métodos foram omitidos

```

```java
package dev.pw2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Channel extends PanacheEntity {

    private String hash;

    @ManyToMany(mappedBy = "channels", fetch = FetchType.EAGER)
    private List<User> users;

    public Channel() {
        this.users = new ArrayList<>();
    }

    // 🚨 os métodos foram omitidos
}
```

## Etapa 3 - RESTful Web Service

Agora que já realizamos o mapeamento objeto relacional, iremos modificar a classe `UserWS` para poderemos realmente persistir os dados dos usuários no banco de dados. Um exemplo possível para a classe `UserWS` pode ser:

```java
package dev.pw2;

import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import dev.pw2.model.User;

@Path("/user")
@Transactional
// 1 - Podemos delegar o controle de transação utilizando a anotação
// @Transactional nos métodos ou no nível da classe.
public class UserWS {

    @GET
    @Path("/save/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public User save(@PathParam("name") String name) {
        User user = new User();
        user.setName(name);
        // 2 - O método do Panache `persist` possibilita persistir um objeto.
        user.persist();
        return user;
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public List<User> list() {
        // 3 - O método `listAll` recupera todos os objetos da classe User.
        return User.listAll();
    }

    @GET
    @Path("/list/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public User list(@PathParam("id") Long id) {
        // 4 - O método do Panache `findById` recupera um objeto da classe User.
        return User.findById(id);
    }
}
```

🚨 Atenção: observe os comentários na classe para entender os principais comandos dessa implementação.

Se você chegou até aqui, basta executar o projeto quarkus no terminal:

    ./mvnw compile quarkus:dev

Como não fizemos nenhuma configuração de banco de dados, o Quarkus irá automaticamente baixar uma imagem e irá executar um banco de dados MySQL 🐬 por meio do Docker 🐳. Mas como o Quarkus sabe que ele deve baixar um container do MySQL? isso ocorre por meio da dependência `quarkus-jdbc-mysql` que foi adicionada ao projeto. 🚨 Note que é importante ter o Docker instalado na sua máquina para que esse recurso funcione adequadamente.

Depois que o banco estiver ativo e rodando, você poderá testar as URLs 🌐 para ver se você consegue salvar e recuperar um objeto da classe `User`:

    http://localhost:8080/user/save/{name}

    http://localhost:8080/user/list

    http://localhost:8080/user/list/{id}

💡 Uma dica, utilize a extensão [Thunder Client](https://marketplace.visualstudio.com/items?itemName=rangav.vscode-thunder-client) do VS Code para fazer requisições HTTP para o seu Web Service.

Alternativamente, você também poderá configurar as conexões com o seu banco de dados por meio do arquivo `src/main/resources/application.properties`, como por exemplo:

    quarkus.datasource.db-kind=mysql
    quarkus.datasource.username=hibernate
    quarkus.datasource.password=hibernate
    quarkus.datasource.jdbc.url=jdbc:mysql://localhost:3306/hibernate
    quarkus.hibernate-orm.database.generation = drop-and-create

Se você necessitar de um banco de dados MySQL, você poderá utilizar o Docker compose abaixo. Crie um arquivo na raiz do seu projeto chamado `docker-compose.yml` e cole o conteúdo abaixo. Depois de criar o arquivo, abra um terminar e, no mesmo diretório do seu projeto, execute o comando para iniciar o container:

    docker-compose up -d

```yml
version: "3.7"
volumes:
  database:
services:
  db:
    image: mysql:latest
    container_name: hibernate
    ports:
      - 3306:3306
    volumes:
      - database:/var/lib/mysql
    environment:
      - MYSQL_ROOT_PASSWORD=hibernate
      - MYSQL_DATABASE=hibernate
      - MYSQL_USER=hibernate
      - MYSQL_PASSWORD=hibernate
```

💡 Dica, caso você necessite de um cliente para visualizar os dados do MySQL, instale a extensão [MySQL](https://marketplace.visualstudio.com/items?itemName=cweijan.vscode-mysql-client2) do Visual Studio Code.

## Etapa 4 - JSON Binding

Jakarta EE inclui suporte para a especificação [Jakarta JSON Binding](https://eclipse-ee4j.github.io/jakartaee-tutorial/#json-binding)(JSON-B), que fornece uma API que pode serializar objetos Java para objetos JSON e desserializar JSON para Java.

O processamento de JSON-B funciona bem com o JAX-RS (RESTful Web Services), basta acionar a anotação `@Produces(MediaType.APPLICATION_JSON)` (veja a implementação da classe `UserWS acima) em um método ou classe. Porém, em relacionamentos bidirecionais, existe um problema comum de geração de referência recursiva.

     javax.json.bind.JsonbException: Recursive reference has been found in class

Nesses casos, a forma menos difícil de resolver essa questão é utilizam a biblioteca [Jackon](https://github.com/FasterXML/jackson) e as anotações [`@JsonBackReference` e `@JsonManagedReference`](https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion#managed-back-reference), por exemplo, observe a alteração realizada no relacionamento bidirecional entre `User` e `Channel`:

```java
// 🚨  vários trechos do código dessa classe foram omitidos
@Entity
public class User extends PanacheEntity {

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Channel> channels;

}
```

```java
// 🚨  vários trechos do código dessa classe foram omitidos
@Entity
public class Channel extends PanacheEntity {

    @ManyToMany(mappedBy = "channels", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<User> users;

}
```

# Criteria

Consultas em Hibernate/JPA também podem serem realizadas por meio da API [Criteria](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Querying/Criteria)

`CriteriaBuilder` é a interface principal para a API Criteria. O `CriteriaBuilder` pode ser obtido por meio do `EntityManager` ou `EntityManagerFactory` usando o método `getCriteriaBuilder()`. `CriteriaBuilder` é utilizado para construir objetos de consulta  `CriteriaQuery` e suas expressões.

`CriteriaQuery` define uma consulta no banco de dados. Um `CriteriaQuery` é usado com a API `EntityManager` e `createQuery()` para criar uma consulta em JPA. Veja um exemplo:

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery cq = cb.createQuery();
```

A partir de uma `CriteriaQuery`podemos parametrizar diverso tipos de consulta por meio de um `Root`. Existem diversos tipos de cláusulas que podem ser usadas em uma consulta, como por exemplo: `where(Expression), where(Predicate...)`, `select(Selection)`, `from(Class)`, `orderBy(Order...), orderBy(List<Order>)`, `groupBy(Expression...), groupBy(List<Expression>)`, `subquery(Class)`, etc.

A cláusula `where` é normalmente a parte principal da consulta, pois define as condições (predicado) que filtram o que é retornado. Um predicado é obtido usando uma operação de comparação ou uma operação lógica do `CriteriaBuilder`. Para saber mais, por favor, acesse [Criteria API](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Querying/Criteria) no Eclipse Link. Por exemplo, para buscar um único objeto da base de dados:

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery cq = cb.createQuery();

// Define os critérios da busca
Root<User> e = cq.from(User.class);
cq.where(cb.equal(e.get("id"), 1));

// Realiza a consulta
Query query = em.createQuery(cq);
User user = (User) query.getSingleResult();
```

Outro exemplo, para buscarmos todos os registros de uma tabela e transformá-los em objetos:

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery cq = cb.createQuery();

Root<User> e = cq.from(User.class);

Query query = em.createQuery(cq);
List<User> users = (List<User>) query.getResultList();
```

Um `selection` pode ser qualquer expressão de objeto, expressão de atributo, função, sub-seleção, construtor ou função de agregação. As funções de agregação podem incluir informações resumidas sobre um conjunto de objetos. Essas funções podem ser usadas para retornar um único resultado ou podem ser usadas com um groupBy para retornar vários resultados.

As funções agregadas são definidas no `CriteriaBuilder` e incluem, por exemplo, `max(Expression)`, `greatest(Expression)`, `avg(Expression) `, `count(Expression)`, etc. Um exemplo:

```java
Root<User> e = cq.from(User.class);
cq.select(cb.count(e));

Query query = em.createQuery(cq);
List<User> users = (List<User>) query.getResultList();
```

# Referências 📚

SIMPLIFIED HIBERNATE ORM WITH PANACHE. Disponível em: [https://quarkus.io/guides/hibernate-orm-panache](https://quarkus.io/guides/hibernate-orm-panache)

<center>
<a href="https://rpmhub.dev" target="blanck"><img src="../imgs/logo.png" alt="Rodrigo Prestes Machado" width="3%" height="3%" border=0 style="border:0; text-decoration:none; outline:none"></a><br/>
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Atribuição 4.0 Internacional</a>
</center>
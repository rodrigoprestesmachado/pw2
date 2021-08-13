# Configurações de JPA (Java Persistence API) ☕ e Docker 🐳

## Configurando um projeto JakartaEE/Microprofile no Docker

[Docker](https://www.docker.com) é um serviço no nível do sistema operacional (Linux) que permite o empacotamento e a distribuição de sistemas pode meio de pacotes chamado _containers_. Os containers são isolados uns dos outros e normalmente utilizam apenas os recursos necessários para a execução de um sistema. Os trechos abaixo mostram como criar um _container_ a partir de um projeto do [MicroProfile](https://microprofile.io) no [Open Liberty](https://openliberty.io).

<center>
<iframe width="560" height="315" src="https://www.youtube.com/embed/tFiG3aw4ET4" title="Docker com Microprofile e Open Liberty" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
</center>

### Dockerfile

O trecho de código abaixo mostra um exemplo de dockerfile utilizado para construirmos um _container_ para a nossa aplicação. O dockerfile abaixo irá tentar instalar um [driver JDBC](https://dev.mysql.com/downloads/connector/j/) do MySQL no container, assim, baixe um driver e instale no diretório `src/main/liberty/config/resources/` do projeto.

```yaml
# Baixa uma imagem do Open Liberty do dockerhub
FROM open-liberty:21.0.0.6-full-java11-openj9

# Descrição do container
LABEL \
    org.opencontainers.image.authors="Rodrigo Prestes Machado" \
    description="Exemplo de JPA"

# Copia um driver jdbc para o container
COPY --chown=1001:0 src/main/liberty/config/resources/mysql-connector-java-8.0.26.jar /config/resources/

# Copia os arquivos de configuração do open liberty para o container
COPY --chown=1001:0 src/main/liberty/config/* /config/

# Copia a aplicação e realiza o deploy
COPY --chown=1001:0 target/jpa.war /config/dropins/

# Executing
RUN configure.sh
````

### Docker-compose

```yaml
version: '3.7'
volumes:
    database:
services:
    db:
        image: mysql:latest
        container_name: jap-mysql
        ports:
            - 3306:3306
        volumes:
            - database:/var/lib/mysql
        environment:
            - MYSQL_ROOT_PASSWORD=jpa
            - MYSQL_DATABASE=jpa
            - MYSQL_USER=jpa
            - MYSQL_PASSWORD=jpa
    phpMyAdmin:
            image: phpmyadmin/phpmyadmin:latest
            container_name: jpa-phpmyadmin
            environment:
                - PMA_ARBITRARY=1
            links:
                - db
            ports:
                - 8080:80
            volumes:
                - /sessions
    app:
        build: .
        depends_on:
            - "db"
            - "phpMyAdmin"
        container_name: jpa-app
        ports:
            - 9080:9080
            - 9443:9443
```
--

## Configurando um projeto com JPA no Open Liberty

### Open Liberty

As seguintes modificações devem ser realizadas no arquivo `server.xml` do [Open Liberty](https://openliberty.io). Primeiro, devemos adicionar o suporte para JPA:

```xml
<featureManager>
    <feature>microProfile-4.0</feature>
    <feature>jpa-2.2</feature>
</featureManager>
```

Segundo, podemos configurar uma fonte de dados para o JPA:

```xml
<!-- Carrega o driver no open liberty -->
<library id="MySQLLib" name="MySQL Connector">
    <fileset dir="${server.config.dir}/resources" id="mysql-connector-jar" includes="mysql-*.jar" />
</library>

<!-- Cria uma fonte de dados -->
<dataSource jndiName="jdbc/JPADS">
    <jdbcDriver libraryRef="MySQLLib" />
    <properties serverName="localhost" portNumber="3306" databaseName="jpa" user="jpa" password="jpa" />
</dataSource>
```

### Maven

Para compilar nosso projeto com o [Maven](https://maven.apache.org) temos que adicionar as seguintes configurações. Primeiro, acrescentar um gerenciador de dependências:

```xml
<dependencyManagement>
    <dependencies>
    <dependency>
        <groupId>io.openliberty.features</groupId>
        <artifactId>features-bom</artifactId>
        <version>21.0.0.6</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
    </dependencies>
</dependencyManagement>
```

Segundo, adicionar a dependência do JPA propriamente dita:

```xml
<dependency>
    <groupId>io.openliberty.features</groupId>
    <artifactId>jpa-2.2</artifactId>
    <type>esa</type>
    <scope>provided</scope>
</dependency>
```

### Persistence

Finalmente, podemos criar um arquivo `persistence.xml` para [configurar o JPA](https://openliberty.io/docs/21.0.0.6/reference/feature/jpa-2.2.html). Por padrão, o Open Liberty utiliza o [EclipseLink](https://www.eclipse.org/eclipselink/) como implementação padrão de JPA.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2" xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
                        http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
    <persistence-unit name="JPADS" transaction-type="JTA">
        <jta-data-source>jdbc/JPADS</jta-data-source>
        <properties>
            <property name="eclipselink.ddl-generation" value="create-tables" />
            <property name="eclipselink.ddl-generation.output-mode" value="both" />
        </properties>
    </persistence-unit>
</persistence>
````

## Hello World JPA

Imagine que tenhamos o seguinte diagrama de classes:

<center>
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/rodrigoprestesmachado/pw2/master/docs/topicos/jpa/uml.puml" alt="Diagrama de classes" width="20%" height="20%"/>
</center>

O mapeamento da classe `User` deveria ser algo parecido com:
### Entity

```java
@Entity
public class User {

    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String email;

}
```

### Web Services

Para persistir o objeto `User` em um Web Service, podemos utilizar a classe `EntityManager` anotada com `@PersistenceContext` e, por meio do método `persist`, salvarmos o estado de um objeto.


```java

@Path("/hello")
@Singleton
@Transactional
public class HelloController {

    @PersistenceContext(unitName = "JPADS")
    private EntityManager em;

    @GET
    public void sayHello() {

        User user = new User();
        user.setName("Rodrigo");
        user.setEmail("rodrigo@teste.com");
        em.persist(user);

    }
}
```
--
## Mapeamentos em JPA

Essa seção apresenta as duas principais anotações utilizadas para o mapeamento em [JPA](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA).
### OneToMany

Mapeamento do tipo [one-to-many](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Mapping/Relationship_Mappings/Collection_Mappings/OneToMany)
são usados para representar o relacionamento entre um objeto e uma coleção de objetos de destino.

<center>
<a href="https://wiki.eclipse.org/File:Onetomany_map_fig.gif">
<img src="https://wiki.eclipse.org/images/9/9e/Onetomany_map_fig.gif" width="35%" height="35%" />
</a>
</center>

* cascade - por padrão, o JPA não coloca em cascata nenhuma operação de persistência para o destino da associação. Assim, as opções de cascateamento são: ALL, MERGE, PERSIST, REFRESH, REMOVE.
* fetch - por padrão, o EclipseLink usa ou tipo de busca javax.persitence.FetchType.LAZY: esta é uma dica para o provedor de persistência de que os dados devem ser buscados lentamente quando são acessados pela primeira vez (se possível). Defina, o fetch para FetchType.EAGER se o requisito necessitar que os dados sejam buscados imediatamente.
* mappedBy - por padrão, se o relacionamento for unidirecional, o provedor de persistência EclipseLink determina o campo que possui o relacionamento. Porém, se o relacionamento for bidirecional, se faz necessário definir o defina o elemento mappedBy no lado inverso.
* targetEntity - por padrão, se você estiver usando uma coleção (`Collection`) usando genéricos (_generics_), o Eclipse Link saberá a entidade de destino associada a partir do tipo de objeto que está sendo referenciado. Porém, se sua coleção não usa genéricos, você deve especificar a classe de entidade que é o destino da associação por meio do targetEntity no lado proprietário da associação.

Exemplo de relacionamento one-to-many unidirecional entre a classe `User` e `Message` (ver diagrama de classes acima).

```java
@Entity
public class User {

    @Id
    @GeneratedValue
    private long id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Message> messages;
```

### ManyToMany

Os mapeamentos [many-to-many](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Mapping/Relationship_Mappings/Collection_Mappings/ManyToMany) representam relacionamentos entre uma coleção de objetos de origem e uma coleção de objetos de destino. Neste caso, exigem a criação de uma tabela intermediária para gerenciar as associações entre os registros de origem e de destino.

<center>
<a href="https://wiki.eclipse.org/File:Mmmapfig.gif">
<img src="https://wiki.eclipse.org/images/e/ef/Mmmapfig.gif" width="35%" height="35%" />
</a>
</center>

* cascade - por padrão, a JPA não cascateia nenhuma operação de persistência para o destino da associação. Se você quiser algumas ou todas as operações de persistência em cascata para o destino da associação, defina o valor deste atributo: ALL, MERGE, PERSIST, REFRESH, REMOVE.
* fetch - por padrão, o EclipseLink usa ou tipo de busca javax.persitence.FetchType.LAZY. Defina, o fetch para FetchType.EAGER se o requisito necessitar que os dados sejam buscados imediatamente.
* mappedBy - Por padrão, se o relacionamento for unidirecional, o EclipseLink determina o campo que possui o relacionamento. Se o relacionamento for bidirecional, defina o elemento mappedBy no lado inverso (não proprietário) da associação para o nome do campo ou propriedade que possui o relacionamento.
* targetEntity - por padrão, se a sua coleção (`Collection`) estiver usando genéricos (_generics_), o Eclipse Link saberá a entidade de destino associada a partir do tipo de objeto que está sendo referenciado. Porém, se sua coleção não usa genéricos, você deve especificar a classe de entidade que é o destino da associação por meio do targetEntity no lado proprietário da associação.

Exemplo de relacionamento one-to-many bidirecional entre a classe `User` e `Channel`

```java
@Entity
public class Channel {

    @Id
    @GeneratedValue
    private long id;
    private String hash;

    @ManyToMany(mappedBy = "channels")
    private List<User> users;

    public Channel() {
        this.users = new ArrayList<>();
    }
}
```

```java
@Entity
public class User {

    @Id
    @GeneratedValue
    private long id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Message> messages;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Channel> channels;

    public User() {
        this.messages = new ArrayList<Message>();
        this.channels = new ArrayList<Channel>();
    }
}
```

## Criteria

Consultas em JPA podem serem realizadas por meio da API [Criteria](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Querying/Criteria)

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

## JSON

Jakarta EE inclui suporte para a especificação Jakarta [JSON Binding](https://eclipse-ee4j.github.io/jakartaee-tutorial/#json-binding), que fornece uma API que pode serializar objetos Java para documentos JSON e deserializar documentos JSON para objetos Java.


O processamento de JSON-B funciona bem com o jax-rs (web services), basta acionar a anotação `@Produces(MediaType.APPLICATION_JSON)` em um método ou classe. Porém, em relacionamentos bidirecionais, existe um problema comum de geração de referência recursiva.

     javax.json.bind.JsonbException: Recursive reference has been found in class

Nesses casos, a forma mais fácil de resolver essa questão é utiliza o [Jackon](https://github.com/FasterXML/jackson) e as anotações `@JsonBackReference` e `@JsonManagedReference`, por exemplo, observe o relacionamento bidirecional entre `User` e `Channel`:

```java
@Entity
public class User {

    @JsonBackReference
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Channel> channels;

}

```java
@Entity
public class Channel {

    @JsonManagedReference
    @ManyToMany(mappedBy = "channels")
    private List<User> users;

}
```


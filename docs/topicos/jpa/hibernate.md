# Hibernate no Quarkus  👊

Primeiro, crie um projeto Quarkus:

    mvn io.quarkus.platform:quarkus-maven-plugin:2.3.0.Final:create \
    -DprojectGroupId=dev.rpmhub \
    -DprojectArtifactId=hibernate-example \
    -DclassName="dev.rpmhub.GreetingResource" \
    -Dpath="/user"

Se você estiver no Windows (cmd):

    mvn io.quarkus.platform:quarkus-maven-plugin:2.3.0.Final:create -DprojectGroupId=dev.rpmhub -DprojectArtifactId=hibernate-example -DclassName="dev.rpmhub.GreetingResource" -Dpath="/start"

Abra o projeto no VS Code: `code hibernate-example`.

Em seguida, abra o arquivo `pom.xml` e adicione as seguintes dependências:

```xml
 <!-- Hibernate ORM specific dependencies -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm</artifactId>
</dependency>
<!-- MYSQL JDBC driver -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-mysql</artifactId>
</dependency>
<!-- Panache -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
<!-- Resteasy JSON-B --> 
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy-jsonb</artifactId>
</dependency>
```

Crie uma classe de negócio chamada User:

```java
package dev.rpmhub.model;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class User extends PanacheEntity  {
    
    private String name;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
```

Note que nessa classe utilizamos a anotação `@Entity` e herdamos da classe `PanacheEntity`. A classe `PanacheEntity` fornece um conjunto de métodos que facilitam a criação de entidades no banco de dados. Além disso, delegamos o gerenciamento do identificador da entidade (id) para a classe `PanacheEntity`.

Modifique a classe que implementa o Web Service. `GreetingResource`, para conter métodos capazes de persistir e recuperar os dados de um usuário. ⚡ Olhe os comentários na classe para entender os principais comandos.

```java 
package dev.rpmhub;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import dev.rpmhub.model.User;

@Path("/user")
public class GreetingResource {

    // 1 - Primeiro é necessário injetar o EntityManager
    @Inject
    EntityManager em;

    // 2 - Podemos deletar o controle de transação utilizando a anotação @Transactional nos métodos ou no nível da classe.
    @GET
    @Path("/save")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String save() {
        User user = new User();
        user.setName("Rodrigo");
        // 3 - O método do Panache `persist` possibilita persistir um objeto.
        user.persist();  
        return "saved";
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public List<User> list() {
       // 4 - O método `findAll` recupera todos os objetos da classe User.
       return User.findAll().list();
    }

    @GET
    @Path("/list/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public User list(@PathParam("id") Long id) {
        // 5 - O método do Panache `findById` recupera um objeto da classe User.
       return User.findById(id);
    }

}
```

Se você chegou até aqui, basta executar o projeto quarkus no terminal:

    ./mvnw compile quarkus:dev

Como não fizemos nenhuma configuração de banco de dados, o Quarkus irá automaticamente baixar uma imagem e irá executar um banco de dados MySQL 🐬 por meio do Docker 🐳. Mas como o Quarkus sabe que ele deve baixar um container do MySQL? isso ocorre por meio da dependência `quarkus-jdbc-mysql` que foi adicionada ao projeto. 🚨 Note que é importante ter o Docker instalado na sua máquina para que esse recurso funcione adequadamente.

Depois que o banco estiver ativo e rodando, você poderá testar as URLs para ver se você consegue salvar um usuário e recuperar um usuário, são elas: 🌐

    http://localhost:8080/user/save

    http://localhost:8080/user/list

    http://localhost:8080/user/list/{id}

Alternativamente, você pode configurar o seu banco de dados por meio do arquivo `src/main/resources/application.properties`:

    quarkus.datasource.db-kind=mysql 
    quarkus.datasource.username=hibernate
    quarkus.datasource.password=hibernate
    quarkus.datasource.jdbc.url=jdbc:mysql://localhost:3306/hibernate
    quarkus.hibernate-orm.database.generation = drop-and-create

Se você necessitar de um banco de dados, você poderá utilizar o Docker compose abaixo:

```xml
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

Caso você necessite de um cliente para visualizar os dados do MySQL, instale a extensão [MySQL](https://marketplace.visualstudio.com/items?itemName=cweijan.vscode-mysql-client2) do Visual Studio Code.
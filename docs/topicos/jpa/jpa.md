# Configurações de JPA (Java Persistence API) ☕ e Docker 🐳

## Docker

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
            - 9444:9444
```

## JPA no Open Liberty


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

### Entity

```java
package edu.ifrs.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

```java
package edu.ifrs.jpa;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
@Singleton
@Transactional
public class HelloController {

    @PersistenceContext(name = "jdbc/JPADS")
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


   


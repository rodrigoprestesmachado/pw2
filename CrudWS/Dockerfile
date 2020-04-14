FROM jboss/wildfly:19.0.0.Final

LABEL \
    org.opencontainers.image.authors="Rodrigo Prestes Machado" \
    description="Wildfly image to run PW2 CrudWS example"

# creating a pw2 wildfly user and password
RUN /opt/jboss/wildfly/bin/add-user.sh pw2 pw2 --silent

# configuring datasource
COPY --chown=jboss:root wildfly/mysql-connector-java-8.0.19.jar /opt/jboss/wildfly/standalone/deployments
COPY --chown=jboss:root wildfly/standalone.xml /opt/jboss/wildfly/standalone/configuration

# deploying CrudWS application
COPY --chown=jboss:root target/CrudWS.war /opt/jboss/wildfly/standalone/deployments

# starting wildfly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

#! /bin/bash 

JBOSS_HOME=/opt/jboss/wildfly
JBOSS_CLI=$JBOSS_HOME/bin/jboss-cli.sh

$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0
#sleep 10
#$JBOSS_CLI --connect --user=pw2 --password=pw2 --file=$JBOSS_HOME/bin/wildfly.cli

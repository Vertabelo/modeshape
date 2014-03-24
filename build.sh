#!/bin/bash

export MAVEN_HOME=~/bin/apache-maven-3.2.1/bin/; export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=512m"; ~/bin/apache-maven-3.2.1/bin/mvn clean install -s alfa-settings.xml

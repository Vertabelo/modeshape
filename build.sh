#!/bin/bash

export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=512m"
mvn clean install -s alfa-settings.xml

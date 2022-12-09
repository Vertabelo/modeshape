SHELL=/bin/bash

MVN=~/work/vertabelo-tools/apache-maven-3.2.3/bin/mvn

export MODSHAPE_VERSION=$(shell cat VERSION)

export MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=512m 
export JAVA_HOME=$(HOME)/work/vertabelo-tools/jdk-11.0.8+10/


build:
	$(MVN) install 


clean:
	rm -fr */target/


update-pom-xml:
	sed -i '' -e 's|<version>\(3.7.1.Final-ep.*\)</version>|<version>$(MODSHAPE_VERSION)</version>|g' *.xml */*.xml

push:
	cp -r ~/.m2/repository/org/modeshape/modeshape-sequencer-ddl/$(MODSHAPE_VERSION) ~/work/vertabelo-repos/maven/org/modeshape/modeshape-sequencer-ddl/
	cp -r ~/.m2/repository/org/modeshape/modeshape-sequencers/$(MODSHAPE_VERSION) ~/work/vertabelo-repos/maven/org/modeshape/modeshape-sequencers/
	


.PHONY: build clean copy-jars update-pom-xml push 



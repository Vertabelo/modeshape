

MVN=~/work/vertabelo-tools/apache-maven-3.2.3/bin/mvn

export MODSHAPE_VERSION=$(shell cat VERSION)

export MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=512m 
export JAVA_HOME=$(HOME)/work/vertabelo-tools/jdk1.8.0_91/


build:
	$(MVN) install 


clean:
	rm -fr */target/

copy-jars:
	cp -r ~/.m2/repository/org/modeshape/modeshape-sequencer-ddl/$(MODSHAPE_VERSION) ~/work/vertabelo-repos/maven/org/modeshape/modeshape-sequencer-ddl/


update-pom-xml:
	sed -i 's|<version>\(3.7.4.Final-ep.*\)</version>|<version>$(MODSHAPE_VERSION)</version>|g' *.xml */*.xml

push:
	./push-to-nexus.sh


.PHONY: build clean copy-jars update-pom-xml push 



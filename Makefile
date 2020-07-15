

MVN=~/work/vertabelo-tools/apache-maven-3.2.3/bin/mvn

export MODSHAPE_VERSION=$(shell cat VERSION)

export MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=512m 
export JAVA_HOME=$(HOME)/work/vertabelo-tools/jdk1.8.0_91/


build:
	$(MVN) install 


clean:
	rm -fr */target/

copy-jars:
	mkdir -p ~/.m2/repository/org/modeshape/modeshape-sequencer-ddl
	mkdir -p ~/.maven/repository/modeshape/src/
	cp ~/.m2/repository/org/modeshape/modeshape-sequencer-ddl/3.7.1.Final-ep*/modeshape-sequencer-ddl-3.7.1.Final-ep*.jar ~/.maven/repository/modeshape/jars/
	cp ~/.m2/repository/org/modeshape/modeshape-sequencer-ddl/3.7.1.Final-ep*/modeshape-sequencer-ddl-3.7.1.Final-ep*-sources.jar ~/.maven/repository/modeshape/src/


update-pom-xml:
	sed -i 's|<version>\(3.7.1.Final-ep.*\)</version>|<version>$(MODSHAPE_VERSION)</version>|g' *.xml */*.xml

push:
	./push-to-nexus.sh


.PHONY: build update-jars update-pom-xml-version



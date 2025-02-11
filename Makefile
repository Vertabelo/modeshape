SHELL=/bin/bash

export MODSHAPE_VERSION=$(shell cat VERSION)

export MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=512m 

build:
	mvn install

clean:
	rm -fr */target/

update-pom-xml:
	sed -i -e 's|<version>\(3.7.1.Final-ep.*\)</version>|<version>$(MODSHAPE_VERSION)</version>|g' *.xml */*.xml

.PHONY: build clean copy-jars update-pom-xml



#!/bin/bash

version=ep12-dev

scp modeshape-common/target/modeshape-common-3.7.1.Final-${version}.jar alfa:/var/www/maven/modeshape/jars/
scp modeshape-common/target/modeshape-common-3.7.1.Final-${version}-sources.jar alfa:/var/www/maven/modeshape/src/modeshape-common-3.7.1.Final-${version}.zip

scp modeshape-jcr-api/target/modeshape-jcr-api-3.7.1.Final-${version}.jar alfa:/var/www/maven/modeshape/jars/
scp modeshape-jcr-api/target/modeshape-jcr-api-3.7.1.Final-${version}-sources.jar alfa:/var/www/maven/modeshape/src/modeshape-jcr-api-3.7.1.Final-${version}.zip

scp sequencers/modeshape-sequencer-ddl/target/modeshape-sequencer-ddl-3.7.1.Final-${version}.jar alfa:/var/www/maven/modeshape/jars/
scp sequencers/modeshape-sequencer-ddl/target/modeshape-sequencer-ddl-3.7.1.Final-${version}-sources.jar alfa:/var/www/maven/modeshape/src/modeshape-sequencer-ddl-3.7.1.Final-${version}.zip



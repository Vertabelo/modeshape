#!/bin/bash
set -ex

SERVER="https://nexus.lan.e-point.pl/repository/epoint/org/modeshape/modeshape-sequencer-ddl"
URL="$SERVER"

REPO="epoint"
if [ ! -f ~/.nexus_pass ]; then
    echo "No credentail file found ~/.nexus_pass"
    echo "Example content"
    echo "user=<your_nexus(ldap)_login_name_here>"
    echo "password=<your_nexus(ldap)_password_here>"
    exit 1
fi
source ~/.nexus_pass
USER="$user:$password"

group=org.modeshape
artifact=modeshape-sequencer-ddl
version=3.7.1.Final-ep23
classifier=
ext=jar
jarFilename=modeshape-sequencer-ddl-$version.$ext
sourceJarFilename=modeshape-sequencer-ddl-$version-sources.$ext

pushd modeshape-sequencer-ddl/target

curl --write-out "\nStatus: %{http_code}\n" \
    --request PUT \
    -v  \
    -u $USER \
    --upload-file $jarFilename \
    "$URL/$version/$jarFilename"

curl --write-out "\nStatus: %{http_code}\n" \
    --request PUT \
    -v \
    -u $USER \
    --upload-file $sourceJarFilename \
    "$URL/$version/$sourceJarFilename"

popd

#!/bin/bash
set -ex

SERVER="https://nexus.lan.e-point.pl/service/local/artifact/maven/content"
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
version=3.7.1.Final-ep21-dev
classifier=
ext=jar
jarFilename=modeshape-sequencer-ddl/target/modeshape-sequencer-ddl-$version.$ext
sourceJarFilename=modeshape-sequencer-ddl/target/modeshape-sequencer-ddl-$version-sources.$ext


curl --write-out "\nStatus: %{http_code}\n" \
    --request POST \
    --user $USER \
    -F "r=$REPO" \
    -F "g=$group" \
    -F "a=$artifact" \
    -F "v=$version" \
    -F "p=$ext" \
    -F "file=@$jarFilename" \
    "$URL"

curl --write-out "\nStatus: %{http_code}\n" \
    --request POST \
    --user $USER \
    -F "r=$REPO" \
    -F "g=$group" \
    -F "a=$artifact" \
    -F "v=$version" \
    -F "c=source" \
    -F "p=$ext" \
    -F "file=@$sourceJarFilename" \
    "$URL"
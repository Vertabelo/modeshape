find . -type f -print0 | xargs -0 sed -i 's|<version>3.7.1.Final-ep</version>|<version>3.7.1.Final-ep2</version>|g'

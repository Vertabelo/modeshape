find . -type f -print0 | xargs -0 sed -i 's|<version>3.7.1.Final-ep3</version>|<version>3.7.1.Final-ep4</version>|g'

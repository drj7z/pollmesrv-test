#!/bin/sh

client=${1:-1}

java -cp target/pollmesrv-test-0.0.1-SNAPSHOT.jar:../pollmesrv/target/classes:../pollmesrv/target/dependency/* net.ddns.drj7z.pollme.pollmesrv_test.App "${client}" 2>&1 | tee tmp-out/$1.txt

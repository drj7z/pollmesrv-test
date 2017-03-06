#!/bin/sh

client=${1:-1}
serverHost="${2:-localhost}"

sleepTime=$(printf '0.%03d' $((RANDOM % 250)))
echo "##### - ##### sleeping ${sleepTime}"
sleep ${sleepTime}
echo "##### - ##### GOOOOOO!!!!!!!!!"

java -cp target/pollmesrv-test-0.0.1-SNAPSHOT.jar:../pollmesrv/target/classes:../pollmesrv/target/dependency/* net.ddns.drj7z.pollme.pollmesrv_test.App "${client}" "${serverHost}" 2>&1 | tee tmp-out/$1.txt

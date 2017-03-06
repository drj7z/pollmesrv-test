#!/bin/sh

run ()
{
	local running
	java -cp target/pollmesrv-test-0.0.1-SNAPSHOT.jar:../pollmesrv/target/classes:../pollmesrv/target/dependency/* net.ddns.drj7z.pollme.pollmesrv_test.App $1
#	running=$!
	
#	echo "running & waiting: ${running}: $1."
#	wait $!
	echo "${running} completed: $1."
}

rm -fr tmp-out
mkdir -p tmp-out

clients=${1:-64}

echo "##### running ${clients} clients..."

i=0
while [ ${i} -lt ${clients} ] ; do
	i=$((i + 1))
	echo "client: ${i}..."
	{ run ${i} 2>&1 | tee ./tmp-out/$i.txt ; } &
#	sleep 0.125
	echo "...done: client: ${i}."
done

echo "##### ...done: running ${clients} clients."


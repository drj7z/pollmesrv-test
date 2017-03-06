#!/bin/sh

rm -fr tmp-out
mkdir -p tmp-out

clients=${1:-64}

echo "##### running ${clients} clients..."

i=0
while [ ${i} -lt ${clients} ] ; do
	i=$((i + 1))
	echo "launching client: ${i}."
	./test-client.sh ${i} & 
	sleep 0.25
done

echo "##### ...done: running ${clients} clients."

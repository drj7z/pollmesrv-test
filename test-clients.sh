#!/bin/sh

rm -fr tmp-out
mkdir -p tmp-out

clients=${1:-64}

serverHost="${2:-localhost}"

echo "##### running ${clients} clients..."

i=0
while [ ${i} -lt ${clients} ] ; do
	i=$((i + 1))
	echo "launching client: ${i}."
	./test-client.sh ${i} ${serverHost} & 
#	sleep 0.25
done

echo "##### ...done: running ${clients} clients."

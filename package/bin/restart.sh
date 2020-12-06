#!/bin/bash --login

programJar=opentsdb-collector-*

cd `dirname $0`;

./stop.sh

sleep 3

nohup java -jar -Xmx1024m -Xms1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$(pwd)/aiops.hprof -Dfile.encoding=UTF-8 $(pwd)/${programJar}  > /dev/null 2>&1 &

sleep 3

jps
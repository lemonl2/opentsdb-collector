#!/bin/sh --login

programJar=opentsdb-collector-*

cd `dirname $0`;

jps | grep ${programJar} | awk "{print \$1}" | xargs kill >/dev/null 2>&1

jps
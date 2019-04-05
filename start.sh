#!/bin/bash
if [ $# -eq 0 ]
 then
   echo "Usage: ./start.sh -i filename -cs cachesize -bs blocksize -w setnumber [-vs victim cache size]"
   exit 1
fi

if [ $# -eq 8 ]
 then
   java -jar  CacheSimulator.jar $1 $2 $3 $4 $5 $6 $7 $8
fi

if [ $# -eq 10 ]
 then
   java -jar  CacheSimulator.jar $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10}
fi
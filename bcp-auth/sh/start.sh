#!/bin/sh

WORK_HOME=/datafile/NokiaSAS/bcp-auth
PROC_NAME=bcp-auth-0.0.1-SNAPSHOT.jar

trap "" 1 2 3
trap "exit 1" 15

PROC_NUMBER=`ps -ef |grep $PROC_NAME|grep -v grep |wc -l`
if [ $PROC_NUMBER = 0 ]
then
  cd $WORK_HOME
  nohup java -jar $PROC_NAME >/dev/null 2>&1 &
else
  echo $PROC_NAME has running!
fi

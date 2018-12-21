#!/bin/sh

WORK_HOME=/datafile/NokiaSAS/bcp-auth
STOKEN_NAME=$WORK_HOME/conf
PROC_NAME=bcp-auth-0.0.1-SNAPSHOT.jar

trap "" 1 2 3
trap "exit 1" 15

PROC_NUMBER=`ps -ef |grep $PROC_NAME|grep -v grep |wc -l`
if [ $PROC_NUMBER = 0 ]
then
  echo $PROC_NAME has not running!
else
  PROC_ID=`ps -ef |grep $PROC_NAME|grep -v grep|awk '{print $2}'`
  kill $PROC_ID
  echo kill $PROC_NAME over,proccess=$PROC_ID
fi

exit 0;
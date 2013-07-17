#!/bin/bash

PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set PRG_HOME if not already set
[ -z "$PRG_HOME" ] && PRG_HOME=`cd "$PRGDIR/.." ; pwd`

echo Using HOME DIR $PRG_HOME

java -cp .:$PRG_HOME/lib/*:$PRG_HOME/lib/sigar-bin/lib/* edu.usc.pgroup.floe.startup.Container $PRG_HOME/conf/Container.properties $1 $2


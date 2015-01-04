#!/bin/bash

if [ -z "$SIMBS_HOME" -o ! -d "$SIMBS_HOME" ]; then
  PRG="$0"
  # need this for relative symlinks
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG=`dirname "$PRG"`"/$link"
    fi
  done

  SIMBS_HOME=`dirname "$PRG"`/..

  # make it fully qualified
  SIMBS_HOME=`cd "$SIMBS_HOME" > /dev/null && pwd`
fi

java -classpath $SIMBS_HOME/lib/hsqldb.jar org.hsqldb.util.DatabaseManagerSwing

#!/bin/bash

if [ -z "${SIMBS_HOME}" -o ! -d "${SIMBS_HOME}" ]; then
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
  SIMBS_HOME=`cd "${SIMBS_HOME}" > /dev/null && pwd`
fi

java -cp ${SIMBS_HOME}/lib/hsqldb.jar org.hsqldb.Server -database.0 "${SIMBS_HOME}/res/data/bookdb;user=book_admin;password=123456" -dbname.0 bookdb

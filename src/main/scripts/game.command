#!/usr/bin/env sh
SCRIPT="$0"
if [ "X" = "X$SCRIPT" ]; then
    echo '$0 is empty, trying $PWD'
    BASE="$PWD"
else
    BASE=`dirname "$SCRIPT"`
fi
java -jar -Djava.library.path="$BASE/lib/" "$BASE/ld31-0.1-SNAPSHOT.jar" &

#!/bin/bash
echo ">> Parsing Current...";
PARSER_DIRECTORY="../parser";
PARSER_CLASSPATH=$(cat "$PARSER_DIRECTORY/classpath")
CLASS=$(cat "parser_class")
CHANGE_DIRECTORY=$(cat "change_directory");
RES_DIR="res-current"
ARGS="input/gtfs.zip $CHANGE_DIRECTORY$RES_DIR/raw current_"
java -Xms2048m -Xmx8192m -Dfile.encoding=UTF-8 \
-classpath \
bin:\
$PARSER_CLASSPATH \
$CLASS \
$ARGS;
RESULT=$?;
echo ">> Parsing Current... DONE";
exit $RESULT;

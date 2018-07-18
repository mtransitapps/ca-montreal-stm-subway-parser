#!/bin/bash
source ../commons/commons.sh
echo ">> Parsing...";
PARSER_DIRECTORY="../parser";
PARSER_CLASSPATH=$(cat "$PARSER_DIRECTORY/classpath");
CLASS=$(cat "parser_class");
java -Xms2048m -Xmx8192m -Dfile.encoding=UTF-8 \
-classpath \
bin:\
$PARSER_CLASSPATH \
$CLASS;
checkResult $? false;
echo ">> Parsing... DONE";

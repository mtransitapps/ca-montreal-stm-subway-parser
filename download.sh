#!/bin/bash
source ../commons/commons.sh
echo ">> Downloading..."
URL=`cat input_url`;
mkdir -p input;
download "${URL}" "input/gtfs.zip";
checkResult $?;
if [ -e "input_url_next" ]; then
	URL=`cat input_url_next`;
	download "${URL}" "input/gtfs_next.zip";
	checkResult $?;
fi
echo ">> Downloading... DONE"

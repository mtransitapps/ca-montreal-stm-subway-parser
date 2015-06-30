#!/bin/bash
echo ">> Downloading..."
URL=`cat input_url`;
FILENAME=$(basename "$URL");
if [ -e input/gtfs.zip ]; then
    mv input/gtfs.zip $FILENAME;
    wget --header="User-Agent: MonTransit" -N $URL;
else
    wget --header="User-Agent: MonTransit" -S $URL;
fi;
if [ -e $FILENAME ]; then
	mv $FILENAME input/gtfs.zip;
fi;
echo ">> Downloading... DONE"
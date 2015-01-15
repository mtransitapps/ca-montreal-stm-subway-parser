#!/bin/bash
echo ">> Downloading & Parsing..."
./download.sh
./parse.sh
./list_change.sh
echo ">> Downloading & Parsing... DONE"
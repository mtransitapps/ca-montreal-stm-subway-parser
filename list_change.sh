#!/bin/bash
echo ">> Listing change...";
TARGET=$(cat "change_directory")
git -C $TARGET status | grep "res/raw"
git -C $TARGET diff res/values/gtfs_rts_values_gen.xml
echo ">> Listing change... DONE";
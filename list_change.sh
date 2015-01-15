#!/bin/bash
echo ">> Listing change...";
TARGET=$(cat "change_directory")
git -C $TARGET status | grep "res/raw"
echo ">> Listing change... DONE";
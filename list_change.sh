#!/bin/bash
source ../commons/commons.sh
echo "> Listing change...";
TARGET=$(cat "change_directory");
RESULT=$(git -C $TARGET status);
checkResult $? false;
RESULT=$(echo $RESULT | grep "res/raw");
STATUS=$?;
if [ $STATUS == 1 ]; then STATUS=0; fi; # grep returns 1 when no result
checkResult $STATUS false;
git -C $TARGET diff res/values/gtfs_rts_values_gen.xml;
checkResult $?;
RESULT=$(git -C $TARGET diff-index  --name-only HEAD -- "res/raw" | wc -l);
if [ "$RESULT" -gt 0 ]; then
	echo "> SCHEDULE CHANGED > MANUAL FIX!";
	git -C $TARGET status | grep "res/raw" | head -n 7;
	exit -1;
fi
git -C $TARGET diff res-current/values/current_gtfs_rts_values_gen.xml;
checkResult $?;
RESULT=$(git -C $TARGET diff-index  --name-only HEAD -- "res-current/raw" | wc -l);
if [ "$RESULT" -gt 0 ]; then
	echo "> SCHEDULE CHANGED > MANUAL FIX!";
	git -C $TARGET status | grep "res-current/raw" | head -n 7;
	exit -1;
fi
if [ -d $TARGET/res-next ]; then
	git -C $TARGET status | grep "res-next" | head -n 1;
	if [ -d $TARGET/res-next/values ]; then
		git -C $TARGET diff res-next/values/next_gtfs_rts_values_gen.xml;
		checkResult $?;
	fi
	if [ -d $TARGET/res-next/raw ]; then
		RESULT=$(git -C $TARGET diff-index  --name-only HEAD -- "res-next/raw" | wc -l);
		if [ "$RESULT" -gt 0 ]; then
			echo "> SCHEDULE CHANGED > MANUAL FIX!";
			git -C $TARGET status | grep "res-next/raw" | head -n 7;
			exit -1;
		fi
	fi
fi
echo "> Listing change... DONE";

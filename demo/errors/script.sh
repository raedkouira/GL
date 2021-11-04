#!/bin/bash

for FILE in "$@"
do
    if [ -d $FILE ];
    then
        for DIR_FILE in $FILE*
        do
            cat $DIR_FILE
            decac $DIR_FILE
        done
    else
        cat $FILE
        decac $FILE
    fi
done

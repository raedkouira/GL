#!/bin/bash

LAUNCHER_FILE="/src/test/script/launchers/test_context"
CAT_HEADER_FILE="src/test/deca/context/header_deca.txt"
CAT_FOOT_FILE="src/test/deca/context/footer_deca.txt"

mvn compile
for FILE in "$@"
do
    echo "$FILE"
    if [ -d $FILE ];
    then
        for DIR_FILE in $FILE*
        do
            cat $CAT_HEADER_FILE
            cat $DIR_FILE
            cat $CAT_FOOT_FILE
            echo "$DIR_FILE"
            ./$LAUNCHER_FILE $DIR_FILE
        done
    else
        cat $CAT_HEADER_FILE
        cat $FILE
        cat $CAT_FOOT_FILE
        echo "$FILE"
        ./$LAUNCHER_FILE $FILE
    fi
done


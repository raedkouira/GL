#! /bin/bash

cd "$(dirname "$0")"/../../.. || exit 1
PATH=./src/test/script/launchers:"$PATH"

./src/test/script/test-all.sh lex
./src/test/script/test-all.sh synt
./src/test/script/test-all.sh context
./src/test/script/test-all.sh codegen

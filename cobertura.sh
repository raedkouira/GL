#! /bin/bash

cd "$(dirname "$0")" || exit 1
PATH=./src/test/script/launchers:"$PATH"

mvn cobertura:clean
mvn cobertura:cobertura
firefox target/site/cobertura/index.html

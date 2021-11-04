#! /bin/bash

if [[ $1 == "sin" ]]; then
    ./parseAndPrint.py curves/sinDeca.txt curves/sinJava.txt
fi

if [[ $1 == "atan" ]]; then
    ./parseAndPrint.py curves/atanDeca.txt curves/atanJava.txt
fi

if [[ $1 == "asin" ]]; then
    ./parseAndPrint.py curves/asinDeca.txt curves/asinJava.txt
fi

if [[ $1 == "ulp" ]]; then
    ./parseAndPrint.py curves/ulpDeca.txt curves/ulpBasic.txt
fi

if [[ $1 == "errsin" ]]; then
    ./parseAndPrint.py curves/errSinDeca.txt
fi

if [[ $1 == "erratan" ]]; then
    ./parseAndPrint.py curves/errAtanDeca.txt
fi

if [[ $1 == "errasin" ]]; then
    ./parseAndPrint.py curves/errAsinDeca.txt
fi

if [[ $1 == "errulp" ]]; then
    ./parseAndPrint.py curves/errUlpDeca.txt
fi

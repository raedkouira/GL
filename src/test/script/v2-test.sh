#! /bin/bash

cd "$(dirname "$0")"/../../.. || exit 1
PATH=./src/test/script/launchers:"$PATH"

# COUNTERS
passedLex=0
totalLex=0

passedSynt=0
totalSynt=0

passedContext=0
totalContext=0

passedCodegen=0
totalCodegen=0

# PARAMS

# display the .deca before each file
catPgm=false

# display the error output even if the test is a success
catEOutput=false

# write all the file that didn't pass in a toFix.log
writeIssues=false

# stop the tests if one fails
exitOne=false

# Step A
# display the lexing output (i.e. tokens and their location)
lexOutput=false

# display the syntax output (i.e. a tree with no decorations)
syntOutput=false

# Step B
# display the context output (i.e. a tree with decorations)
contextOutput=false

# Step C
# display the result
codegenOutput=false

# display the assembly
catAss=false

# tests to do
lex=false
synt=false
context=false
codegen=false

# file (in case there is one)
file=""

# in case the file is given, must precise if it's valid or invalid
fileCategory="none"

# PARSING Options

for arg in $@
do
    if [[ $arg == "-l" ]];
    then
        lex=true

    elif [[ $arg == "-s" ]];
    then
        synt=true

    elif [[ $arg == "-ct" ]];
    then
        context=true

    elif [[ $arg == "-cg" ]];
    then
        codegen=true

    elif [[ $arg == "-pgm" ]];
    then
        catPgm=true

    elif [[ $arg == "-err" ]];
    then
        catEOutput=true

    elif [[ $arg == "-log" ]];
    then
        writeIssues=true
        if [[ -f toFix.log ]]; then
            rm toFix.log
        fi
        touch toFix.log

    elif [[ $arg == "-ex1" ]];
    then
        exitOne=true

    elif [[ $arg == "-lO" ]];
    then
        lexOutput=true

    elif [[ $arg == "-sO" ]];
    then
        syntOutput=true

    elif [[ $arg == "-ctO" ]];
    then
        contextOutput=true

    elif [[ $arg == "-cgO" ]];
    then
        codegenOutput=true

    elif [[ $arg == "-ass" ]];
    then
        catAss=true

    elif [[ $arg == "-v" ]];
    then
        fileCategory="valid"

    elif [[ $arg == "-nv" ]];
    then
        fileCategory="invalid"

    elif [[ ${arg: -5} == ".deca" ]];
    then
        if [[ -z $file ]];
        then
            file=$arg
        else
            echo "2 or more .deca files, exiting..."
            exit 1
        fi
    else
        echo "invalid command arguments, please use one among"
        echo "-l"
        echo "-s"
        echo "-ct"
        echo "-cg"
        echo "-pgm"
        echo "-err"
        echo "-log"
        echo "-ex1"
        echo "-lO"
        echo "-sO"
        echo "-ctO"
        echo "-cgO"
        echo "-ass"
        echo ".deca file"
        echo "-v"
        echo "-nv"
        exit 1
    fi
done

if [[ "$fileCategory" == "none" && -n $file ]];
then
    echo "file given but no category, please precise valid (-v) or (-nv)"
    echo "exiting..."
    exit 1
fi

if [[ $lex = false && $synt = false && $context = false && $codegen = false ]];
then
    echo "one among -l, -s, -ct, -cg should be used, exiting..."
    exit 1
fi

# COMMON FUNCTIONS

displayProg () { # $1 = file
    if [[ "$catPgm" == true ]];
    then
        echo "Program :"
        cat $1
    fi
}

displayEOutput () { # $1 = eoutput
    if [[ "$catEOutput" == true ]];
    then
        echo "Eoutput:"
        echo $1
    fi
}

writeIssues () { # $1 = the issue
    if [[ "$writeIssues" == true ]];
    then
        echo $1 >> ./toFix.log
        echo "" >> ./toFix.log
    fi
}

exitUn () {
    if [[ "$exitOne" == true ]];
    then
        echo "exiting..."
        exit 1
    fi
}

ifThenEcho () { # $1 = condition, $2 = printable
    if [[ $1 == true ]];
    then
        echo $2
    fi
}

red () { # $1 = string
    echo -en "\e[91m$1"
    echo -e "\e[39m"
}

green () { # $1 = string
    echo -en "\e[92m$1"
    echo -e "\e[39m"
}

yellow () { # $1 = string
    echo -en "\e[93m$1"
    echo -e "\e[39m"
}

blink () { # $1 = string
    echo -en "\e[96m$1"
    echo -e "\e[0m"
}

# MAIN
# LEX

lexValid () { # $1 = file
    displayProg "$1"
    eoutput=$(test_lex $1 2>&1 > /dev/null | head -n 1)
    displayEOutput "$eoutput"
    if [[ "$lexOutput" == true ]];
    then
        out=$(test_lex $1)
        echo "lexOutput:"
        echo "$out"
    fi
    eoutput=$(echo "$eoutput" | grep 'deca:[0-9][0-9]*:')
    if [[ -n "$eoutput" ]];
    then
        red "failed $1"
        writeIssues "$1 unexpected error:"
        writeIssues "$eoutput"
        exitUn
    else
        green "passed $1"
        passedLex=$((passedLex + 1))
    fi
    totalLex=$((totalLex + 1))
}


lexInvalid () {

    displayProg "$1"
    eoutput=$(test_lex $1 2>&1 > /dev/null | head -n 1)
    displayEOutput "$eoutput"
    eoutput=$(echo "$eoutput" | grep 'deca:[0-9][0-9]*:')
    if [[ -n "$eoutput" ]];
    then
        green "passed $1"
        passedLex=$((passedLex + 1))
    else
        red "failed $1"
        writeIssues "$1 no error found"
        exitUn
    fi
    totalLex=$((totalLex + 1))
}


if [[ "$lex" == true ]];
then
    if [[ -n $file && "$fileCategory" == "valid" ]]
    then
        lexValid "$file"

    elif [[ -n $file && "$fileCategory" == "invalid" ]]
    then

        lexInvalid "$file"

    else
        # valid
    	for f in ./src/test/deca/syntax/valid/lex/*.deca
    		do
                lexValid $f
    		done

    	# invalid
    	for f in ./src/test/deca/syntax/invalid/lex/*.deca
    		do
                lexInvalid $f
    		done
    fi
fi

# SYNT

syntValid () { # $1 = file

    displayProg "$1"
    eoutput=$(test_synt $1 2>&1 > /dev/null | head -n 1)
    displayEOutput "$eoutput"
    if [[ "$syntOutput" == true ]];
    then
        out=$(test_synt $1)
        echo "syntOutput:"
        echo "$out"
    fi
    eoutput=$(echo "$eoutput" | grep 'deca:[0-9][0-9]*:')
    if [[ -n "$eoutput" ]];
    then
        red "failed $1"
        writeIssues "$1 unexpected error:"
        writeIssues "$eoutput"
        exitUn
    else
        green "passed $1"
        passedSynt=$((passedSynt + 1))
    fi
    totalSynt=$((totalSynt + 1))
}


syntInvalid () {

    displayProg "$1"
    eoutput=$(test_synt $1 2>&1 > /dev/null | head -n 1)
    displayEOutput "$eoutput"
    eoutput=$(echo "$eoutput" | grep 'deca:[0-9][0-9]*:')
    if [[ -n "$eoutput" ]];
    then
        green "passed $1"
        passedSynt=$((passedSynt + 1))
    else
        red "failed $1"
        writeIssues "$1 no error found"
        exitUn
    fi
    totalSynt=$((totalSynt + 1))
}

if [[ "$synt" == true ]];
then
    if [[ -n $file && "$fileCategory" == "valid" ]]
    then
        syntValid "$file"

    elif [[ -n $file && "$fileCategory" == "invalid" ]]
    then

        syntInvalid "$file"

    else
        # valid
    	for f in ./src/test/deca/syntax/valid/synt/*.deca
    		do
                syntValid $f
    		done

    	# invalid
    	for f in ./src/test/deca/syntax/invalid/synt/*.deca
    		do
                syntInvalid $f
    		done
    fi
fi


# CONTEXT

contextValid () { # $1 = file

    displayProg "$1"
    eoutput=$(test_context $1 2>&1 > /dev/null | head -n 1)
    displayEOutput "$eoutput"
    if [[ "$contextOutput" == true ]];
    then
        out=$(test_context $1)
        echo "contextOutput:"
        echo "$out"
    fi
    eoutput=$(echo "$eoutput" | grep 'deca:[0-9][0-9]*:')
    if [[ -n "$eoutput" ]];
    then
        red "failed $1"
        writeIssues "$1 unexpected error :"
        writeIssues "$eoutput"
        exitUn
    else
        green "passed $1"
        passedContext=$((passedContext + 1))
    fi
    totalContext=$((totalContext + 1))
}


contextInvalid () {

    displayProg "$1"
    eoutput=$(test_context $1 2>&1 > /dev/null | head -n 1)
    displayEOutput "$eoutput"
    eoutput=$(echo "$eoutput" | grep 'deca:[0-9][0-9]*:')
    if [[ -n "$eoutput" ]];
    then
        green "passed $1"
        passedContext=$((passedContext + 1))
    else
        red "failed $1"
        writeIssues "$1 no error found:"
        writeIssues "$eoutput"
        exitUn
    fi
    totalContext=$((totalContext + 1))
}


if [[ "$context" == true ]];
then
    if [[ -n $file && "$fileCategory" == "valid" ]]
    then
        contextValid "$file"

    elif [[ -n $file && "$fileCategory" == "invalid" ]]
    then

        contextInvalid "$file"

    else
        # valid
    	for f in ./src/test/deca/context/valid/*.deca
    		do
                contextValid $f
    		done

    	# invalid
    	for f in ./src/test/deca/context/invalid/*.deca
    		do
                contextInvalid $f
    		done
    fi
fi

# CODEGEN

codegenValid () {

    displayProg "$1"
    tmp="$1"
    a=${tmp:: -5}.ass
    dec=$(decac $1 2>&1 | head -n 1)
    displayEOutput "$dec"

    if [[ $catAss == "true" ]];
    then
        echo
        cat $a
    fi

    if [ ! -f $a ]; then
        yellow "Fichier .ass non généré $1"
        writeIssues "$1 pas de .ass :"
        writeIssues "$dec"
        exitUn
    else
        result=$(ima $a)

        if [[ "$catAss" == true ]];
        then
            echo "Assembly Program:"
            cat $a
        fi

        rm $a

        if [[ "$codegenOutput" == true ]];
        then
            echo "Ima output:"
            echo "$result"
        fi
        expresult=$(grep @result $1 | cut -c12-)
        if [[ "$result" == "$expresult" ]];
        then
            green "passed $1"
            passedCodegen=$((passedCodegen + 1))
        else
            red "failed $1"
            writeIssues "$1 unexpected result :"
            writeIssues "expected : $expresult"
            writeIssues "obtained : $result"
            exitUn
        fi
    fi
    totalCodegen=$((totalCodegen + 1))
}

codegenInvalid () {
    codegenValid "$1"
}

if [[ "$codegen" == true ]];
then
    if [[ -n $file && "$fileCategory" == "valid" ]]
    then
        codegenValid "$file"

    elif [[ -n $file && "$fileCategory" == "invalid" ]]
    then

        codegenInvalid "$file"

    else
        # valid
    	for f in ./src/test/deca/codegen/valid/*.deca
    		do
                codegenValid $f
    		done

    	# invalid
    	for f in ./src/test/deca/codegen/invalid/*.deca
    		do
                codegenInvalid $f
    		done
    fi
fi

# SUMMARY

if [[ "$lex" == true ]];
then
    blink "$passedLex passed out of $totalLex for the lexer"
fi
if [[ "$synt" == true ]];
then
    blink "$passedSynt passed out of $totalSynt for the parser"
fi
if [[ "$context" == true ]];
then
    blink "$passedContext passed out of $totalContext for the context"
fi
if [[ "$codegen" == true ]];
then
    blink "$passedCodegen passed out of $totalCodegen for the codegen"
fi
if [[ "$writeIssues" == true ]];
then
    echo "check toFix.log in your current directory for more details about the failed tests"
fi

exit 0

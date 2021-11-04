lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

// Deca lexer rules.

// Reserved words
ASM: 'asm';
CLASS: 'class';
EXTENDS: 'extends';
ELSE: 'else';
FALSE: 'false';
IF: 'if';
INSTANCEOF: 'instanceof';
NEW: 'new';
NULL: 'null';
READINT: 'readInt';
READFLOAT: 'readFloat';
PRINTLNX: 'printlnx';
PRINTLN: 'println';
PRINTX: 'printx';
PRINT: 'print';
PROTECTED: 'protected';
RETURN: 'return';
THIS: 'this';
TRUE: 'true';
WHILE: 'while';


// Symboles
LT: '<';
GT: '>';
EQUALS: '=';
PLUS: '+';
MINUS: '-';
SLASH: '/';
TIMES: '*';
EXCLAM: '!';
PERCENT: '%';
DOT: '.';
COMMA: ',';
OPARENT: '(';
CPARENT: ')';
OBRACE: '{';
CBRACE: '}';
SEMI: ';';
EQEQ: '==';
NEQ: '!=';
GEQ: '>=';
LEQ: '<=';
AND: '&&';
OR: '||';

// Include
fragment FILENAME: (LETTER | DIGIT  | '.' | '-' | '_')+;
INCLUDE: '#include' (' ')* '"' FILENAME '"' {
    doInclude(getText());
    };

// Identifiers
fragment LETTER: 'a'..'z' | 'A'..'Z';
IDENT: (LETTER | '$' | '_') (LETTER | DIGIT | '$' | '_')*;

// Litterals
fragment POSITIVE_DIGIT: '1'..'9';
INT: '0' | POSITIVE_DIGIT DIGIT*;
DIGIT: '0'..'9';

// Floats
fragment NUM: DIGIT+;
fragment SIGN: '+' | '-' ;
fragment DIGITHEX: DIGIT | 'a'..'f' | 'A'..'F';
fragment EXP: ('E' | 'e') SIGN? NUM;
fragment NUMHEX:  DIGITHEX+;
fragment DEC: NUM '.' NUM;
fragment FLOATDEC: (DEC | DEC EXP) ('F' | 'f')?;
fragment FLOATHEX: '0' ('x' | 'X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN? NUM ('F' | 'f')?;
FLOAT: FLOATDEC | FLOATHEX;

// Strings
fragment STRING_CAR: ~('"' | '\\' | '\n');
STRING: '"' (STRING_CAR | '\\"' | '\\\\')* '"'
		{setText(getText().substring(1, getText().length()-1));
		 setText(getText().replaceAll("\\\\\"", "\""));
		}; // remove quotes
MULTI_LINE_STRING: '"' (STRING_CAR | '\n' | '\\"' | '\\\\')* '"'
		{setText(getText().substring(1, getText().length()-1));
		 setText(getText().replaceAll("\\\\\"", "\""));
		}; // remove quotes

// Comments
COMMENT:
    '//' ~('\n'|'\r')* { skip(); }
	| '/*' .*? '*/' { skip();};


// Separators
WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {skip();}
    ;


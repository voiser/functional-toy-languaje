grammar Grammar;

file
    : 'module' name=ID imp* block
    ;

imp
    : 'import' ID ('.' ID)? '.' what=(ID|CLASSID)
    ;

block
    : (expression | forward)+
    ;

expression
    : value
    | apply
    | anonapply
    | fn
    | ref
    | cond
    | match
    | '(' exp=expression ')'
    | list
    | map
    | objapply=expression '.' apply
    | objfield=expression '.' ID
    | left=expression binop=('=='|'!=') right=expression
    | left=expression binop=('*' | '/') right=expression
    | left=expression binop=('+' | '-') right=expression
    | klass
    | instantiation
    | interf
    | defsimple=ID '=' defsimple2=expression
    | defn=ID '(' ')'                            '=' body=expression 
    | defn=ID '(' fnargpair (',' fnargpair)* ')' '=' body=expression 
    ;

value
    : m='-'? INTEGER
    | m='-'? FLOAT
    | STRING
    | boolfalse='false'
    | booltrue='true'
    ;

fn
    : '{' block '}'
    | '{' fnargpair (',' fnargpair)* '=>' block '}'
    ;

fnargpair
    : ID tydef?
    ;

apply
    : ID '(' ')'
    | ID '(' expression ( ',' expression )* ')'
    ;

anonapply
    : fn '(' ')'
    | fn '(' expression ( ',' expression )* ')'
    ;

ref
    : ID
    ;

cond
    : 'if' condition=expression 'then' exptrue=expression 'else' expfalse=expression
    ;

forward
    : (nat='native')? id=ID ('(' natid=ID ')')? ':' ty=tydef
    ;

tydef
    : CLASSID | ID ('+' tydef)* | tydef '[' tydef (',' tydef)* ']' | tydef (',' tydef)* '->' tydef  | '(' tydef ')'
    ;

list
    : '[' expression (',' expression)* ']'
    ;

map
    : '[' mappair (',' mappair)* ']'
    ;

mappair
    : mapkey=expression ':' mapvalue=expression
    ;

klass 
	: 'class' CLASSID '(' (klassvar (',' klassvar)*)? ')' ('is' klassparent (',' klassparent)*)? ('{' block '}')?
	;

klassvar
	: ID (ty=tydef)?
	;

klassparent
	: CLASSID | ID | klassparent '[' klassparent (',' klassparent)* ']'
	;

instantiation
    : CLASSID '(' (expression (',' expression)*)? ')'
    ;

interf
    : 'interface' CLASSID ('[' tydef (',' tydef)* ']')? ('is' klassparent (',' klassparent)*)? ('{' forward* '}')?
    ;

match
    : 'if' source=expression 'is' matchexp 'then' exptrue=expression 'else' expfalse=expression
    ;

matchexp
    : v=ID? CLASSID '(' (matchexp (',' matchexp)*)? ')'
    | ID
    ;

ID
    : [a-z][a-zA-Z0-9_\-']*
    ;

CLASSID
    : [A-Z][a-zA-Z]*
    ;

INTEGER
    : [0-9]+
    ;

FLOAT
    : [0-9]* '.' [0-9]+
    ;

STRING
    : '"' ~["]* '"'
    ;

WS
    : [ \t\r\n]+ -> skip
    ;

COMMENT
    : '/*' ~[/]* '*/' -> skip
    ;

COMMENT2
    :'//' .*? '\n' -> skip
    ;

// NOTE TO SELF:
//   -REARRANGE STATES BELOW
//   -ADD COMMENT STATES
//   -ADD LOGIC TO PRODUCE SYMBOL TABLE

class Toy {
    public static void main(String args[]) throws java.io.IOException {
        Yylex yy = new Yylex(System.in);
        Yytoken t;
        
        while ((t = yy.yylex()) != null) {
            System.out.print(t);
        }
    }
}

class Yytoken {
    private int index;
    private String type;
    
    public Yytoken(int index, String type) {
        this.index = index;
        this.type = type;
    }
    
    public String toString() {    
        if (type.equals("newline"))
            return "\n";

        return type + " ";
    }
}

%%

%line
%char
%state COMMENT

ALPHA=[A-Za-z]
DIGIT=[0-9]
WHITE_SPACE_CHAR=[\ \t\b]
NEWLINE=[\n\012]

%%

<YYINITIAL> {WHITE_SPACE_CHAR} { }
<YYINITIAL> {NEWLINE} { return (new Yytoken(-1, "newline")); }

<YYINITIAL> "boolean"                                       { return (new Yytoken(0, "boolean")); }
<YYINITIAL> "else"                                          { return (new Yytoken(0, "else")); }
<YYINITIAL> "implements"                                    { return (new Yytoken(0, "implements")); }
<YYINITIAL> "newarray"                                      { return (new Yytoken(0, "newarray")); }
<YYINITIAL> "return"                                        { return (new Yytoken(0, "return")); }
<YYINITIAL> "+"                                             { return (new Yytoken(0, "plus")); }
<YYINITIAL> "%"                                             { return (new Yytoken(0, "mod")); }
<YYINITIAL> ">="                                            { return (new Yytoken(0, "greaterequal")); }
<YYINITIAL> "|"                                             { return (new Yytoken(0, "or")); }
<YYINITIAL> ","                                             { return (new Yytoken(0, "comma")); }
<YYINITIAL> "["                                             { return (new Yytoken(0, "leftbracket")); }
<YYINITIAL> {DIGIT}+                                        { return (new Yytoken(0, "intconstant")); }
<YYINITIAL> "break"                                         { return (new Yytoken(0, "break")); }
<YYINITIAL> "extends"                                       { return (new Yytoken(0, "extends")); }
<YYINITIAL> "int"                                           { return (new Yytoken(0, "int")); }
<YYINITIAL> "null"                                          { return (new Yytoken(0, "null")); }
<YYINITIAL> "string"                                        { return (new Yytoken(0, "string")); }
<YYINITIAL> "-"                                             { return (new Yytoken(0, "minus")); }
<YYINITIAL> "<"                                             { return (new Yytoken(0, "less")); }
<YYINITIAL> "=="                                            { return (new Yytoken(0, "equal")); }
<YYINITIAL> "!"                                             { return (new Yytoken(0, "not")); }
<YYINITIAL> "."                                             { return (new Yytoken(0, "period")); }
<YYINITIAL> "]"                                             { return (new Yytoken(0, "rightbracket")); }
<YYINITIAL> {DIGIT}+"."{DIGIT}*                             { return (new Yytoken(0, "doubleconstant")); }
<YYINITIAL> "class"                                         { return (new Yytoken(0, "class")); }
<YYINITIAL> "for"                                           { return (new Yytoken(0, "for")); }
<YYINITIAL> "interface"                                     { return (new Yytoken(0, "interface")); }
<YYINITIAL> "println"                                       { return (new Yytoken(0, "println")); }
<YYINITIAL> "void"                                          { return (new Yytoken(0, "void")); }
<YYINITIAL> "*"                                             { return (new Yytoken(0, "multiplication")); }
<YYINITIAL> "<="                                            { return (new Yytoken(0, "lessequal")); }
<YYINITIAL> "!="                                            { return (new Yytoken(0, "notequal")); }
<YYINITIAL> "="                                             { return (new Yytoken(0, "assignop")); }
<YYINITIAL> "("                                             { return (new Yytoken(0, "leftparen")); }
<YYINITIAL> "{"                                             { return (new Yytoken(0, "leftbrace")); }
<YYINITIAL> \"(\\\"|[^\n\"]|\\{WHITE_SPACE_CHAR}+\\)*\"     { return (new Yytoken(0, "stringconstant")); }
<YYINITIAL> "double"                                        { return (new Yytoken(0, "double")); }
<YYINITIAL> "if"                                            { return (new Yytoken(0, "if")); }
<YYINITIAL> "new"                                           { return (new Yytoken(0, "new")); }
<YYINITIAL> "readln"                                        { return (new Yytoken(0, "readln")); }
<YYINITIAL> "while"                                         { return (new Yytoken(0, "while")); }
<YYINITIAL> "/"                                             { return (new Yytoken(0, "division")); }
<YYINITIAL> ">"                                             { return (new Yytoken(0, "greater")); }
<YYINITIAL> "&&"                                            { return (new Yytoken(0, "and")); }
<YYINITIAL> ";"                                             { return (new Yytoken(0, "semicolon")); }
<YYINITIAL> ")"                                             { return (new Yytoken(0, "rightparen")); }
<YYINITIAL> "}"                                             { return (new Yytoken(0, "rightbrace")); }
<YYINITIAL> ("true"|"false")                                { return (new Yytoken(0, "booleanconstant")); }
<YYINITIAL> {ALPHA}({ALPHA}|{DIGIT}|_)*                     { return (new Yytoken(0, "id")); }
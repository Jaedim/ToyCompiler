// NOTE TO SELF:
//   -ADD LOGIC TO PRODUCE SYMBOL TABLE

/********
* Notes:
*   -States below should be organized to their respective sections:
*       -Keywords and then constants (then id should always be last)
*       -Brace symbols
*       -Boolean operators
*       -Mathematical operators
*       -Miscellaneous
*   -Keyword states should be organized alphabetically to ensure that
*       keywords with similar characters at the start are not predisposed
*   -
*********/

class Toy {
    public static void main(String args[]) throws java.io.IOException {
        // Enable debugging output or not (default: false)
        Info.debugMode = true;

        Yylex yy = new Yylex(System.in);
        Yytoken t;
        
        while ((t = yy.yylex()) != null) {
            System.out.print(t);
        }
    }
}

// Used only to print debugging info or lexical errors in the input code.
// This information will not be passed to the semantic parser.
// To disable info output, set "debugMode" to false at start of main method.
class Info {
    public static boolean debugMode = false;

    public enum Error {
        UNCLOSED_STRING
    };

    public void throwError(Error e) {
        if (!debugMode) return;

        if (e == Error.UNCLOSED_STRING) {
            System.out.println("UNCLOSED_STRING_ERROR");
        }
    }
}

// This class is required for JLex to work.
// Handles the token generation as stated below in the subroutines.
class Yytoken {
    private int index;
    private String type;
    
    // The constructor used for most subroutines.
    public Yytoken(int index, String type) {
        this.index = index;
        this.type = type;
    }

    // Default constructor that's used to generate newlines in the
    //   output.
    public Yytoken() {
        this.index = -1;
        this.type = "newline";
    }
    
    // Used for outputting information for 
    public String toString() {    
        if (type.equals("newline"))
            return "\n";

        return type + " ";
    }
}

%%

%line
%char
%state ONECOMMENT
%state COMMENT
%state STRING
%state FSLASH

ALPHA=[A-Za-z]
DIGIT=[0-9]
HEX=[A-Fa-f0-9]
WHITE_SPACE_CHAR=[\ \t\b]
NEWLINE=[\n\012]

%%

<YYINITIAL> {NEWLINE} { return (new Yytoken()); }
<YYINITIAL> {WHITE_SPACE_CHAR} { }

<YYINITIAL> "boolean"                                  { return (new Yytoken( 0, "boolean")); }
<YYINITIAL> "break"                                    { return (new Yytoken( 1, "break")); }
<YYINITIAL> "class"                                    { return (new Yytoken( 2, "class")); }
<YYINITIAL> "double"                                   { return (new Yytoken( 3, "double")); }
<YYINITIAL> "else"                                     { return (new Yytoken( 4, "else")); }
<YYINITIAL> "extends"                                  { return (new Yytoken( 5, "extends")); }
<YYINITIAL> "for"                                      { return (new Yytoken( 6, "for")); }
<YYINITIAL> "if"                                       { return (new Yytoken( 7, "if")); }
<YYINITIAL> "implements"                               { return (new Yytoken( 8, "implements")); }
<YYINITIAL> "interface"                                { return (new Yytoken( 9, "interface")); }
<YYINITIAL> "int"                                      { return (new Yytoken(10, "int")); }
<YYINITIAL> "newarray"                                 { return (new Yytoken(11, "newarray")); }
<YYINITIAL> "new"                                      { return (new Yytoken(12, "new")); }
<YYINITIAL> "null"                                     { return (new Yytoken(13, "null")); }
<YYINITIAL> "println"                                  { return (new Yytoken(14, "println")); }
<YYINITIAL> "readln"                                   { return (new Yytoken(15, "readln")); }
<YYINITIAL> "return"                                   { return (new Yytoken(16, "return")); }
<YYINITIAL> "string"                                   { return (new Yytoken(17, "string")); }
<YYINITIAL> "void"                                     { return (new Yytoken(18, "void")); }
<YYINITIAL> "while"                                    { return (new Yytoken(19, "while")); }
<YYINITIAL> ("true"|"false")                           { return (new Yytoken(20, "booleanconstant")); }
<YYINITIAL> {DIGIT}+"."{DIGIT}*(("E+"|"e+"){DIGIT}+)?  { return (new Yytoken(21, "doubleconstant")); }
<YYINITIAL> "0x"{HEX}+|"0X"{HEX}+                      { return (new Yytoken(22, "intconstant")); }
<YYINITIAL> {DIGIT}+                                   { return (new Yytoken(23, "intconstant")); }
<YYINITIAL> {ALPHA}({ALPHA}|{DIGIT}|_)*                { return (new Yytoken(24, "id")); }

<YYINITIAL> "("                                        { return (new Yytoken(25, "leftparen")); }
<YYINITIAL> ")"                                        { return (new Yytoken(26, "rightparen")); }
<YYINITIAL> "{"                                        { return (new Yytoken(27, "leftbrace")); }
<YYINITIAL> "}"                                        { return (new Yytoken(28, "rightbrace")); }
<YYINITIAL> "["                                        { return (new Yytoken(29, "leftbracket")); }
<YYINITIAL> "]"                                        { return (new Yytoken(30, "rightbracket")); }

<YYINITIAL> "=="                                       { return (new Yytoken(31, "equal")); }
<YYINITIAL> "!="                                       { return (new Yytoken(32, "notequal")); }
<YYINITIAL> ">="                                       { return (new Yytoken(33, "greaterequal")); }
<YYINITIAL> "<="                                       { return (new Yytoken(34, "lessequal")); }
<YYINITIAL> ">"                                        { return (new Yytoken(35, "greater")); }
<YYINITIAL> "<"                                        { return (new Yytoken(36, "less")); }
<YYINITIAL> "!"                                        { return (new Yytoken(37, "not")); }
<YYINITIAL> "&&"                                       { return (new Yytoken(38, "and")); }
<YYINITIAL> "||"                                       { return (new Yytoken(39, "or")); }

<YYINITIAL> "="                                        { return (new Yytoken(40, "assignop")); }
<YYINITIAL> "+"                                        { return (new Yytoken(41, "plus")); }
<YYINITIAL> "-"                                        { return (new Yytoken(42, "minus")); }
<YYINITIAL> "*"                                        { return (new Yytoken(43, "multiplication")); }
<YYINITIAL> "%"                                        { return (new Yytoken(44, "mod")); }
<YYINITIAL> "/"                                        { yybegin(FSLASH); }
<YYINITIAL> \"                                         { yybegin(STRING); }

<YYINITIAL> ","                                        { return (new Yytoken(46, "comma")); }
<YYINITIAL> "."                                        { return (new Yytoken(47, "period")); }
<YYINITIAL> ";"                                        { return (new Yytoken(48, "semicolon")); }


<FSLASH> ([^("*"|"/")])     { yybegin(YYINITIAL); return (new Yytoken(45, "division")); }
<FSLASH> "/"                { yybegin(ONECOMMENT); }
<FSLASH> "*"                { yybegin(COMMENT); }


<ONECOMMENT> [^(\n\012)]    { }
<ONECOMMENT> {NEWLINE}      { yybegin(YYINITIAL); }

<COMMENT> [^"*/"]           { }
<COMMENT> "*/"[\n\012]      { yybegin(YYINITIAL); }
<COMMENT> "*/"              { yybegin(YYINITIAL); }


<STRING> [\n\"]         { yybegin(YYINITIAL); return (new Yytoken(49, "stringconstant")); }
<STRING> [^(\"|\n)]     {  }
<STRING> \n             { 
                          yybegin(YYINITIAL); System.out.println(""); 
                          new Info().throwError(Info.Error.UNCLOSED_STRING);
                        }
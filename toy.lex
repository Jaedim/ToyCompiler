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

import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

class Toy {
    private int tokenCounter = 0; // Used for keyword / identifier seperators

    public static void main(String args[]) throws java.io.IOException {
        // Enable debugging output or not (default: false)
        Info.debugMode = true;

        Scanner keywords = new Scanner(new File("./toy.keywords"));
        FileReader inputCode = new FileReader(new File("./toy.code"));
        FileWriter outputFile = new FileWriter(new File("./output.txt"));
        Trie trieTable = new Trie();

        Yylex yy = new Yylex(inputCode);
        Yytoken t;

        while (keywords.hasNextLine()) {
            trieTable.setIdentifier(keywords.nextLine());
            trieTable.storeIntoTrie();
        }

        trieTable.printTable();
        
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

// Trie data structure
class Trie {
    private int switchArr[] =  new int[52]; // A-Z & a-z
    private char symbolArr[] = new char[200]; // Arbitrary value for now
    private int nextArr[] =    new int[200]; // Arbitrary value for now
    private int lastPos = 0; // Position of first empty spot in next/symbol arrays

    private String identifier;
    private int currSymIndex;
    private int ptr;
    private int seperator;

    public Trie() {
        resetId();
        ptr = 0;
        seperator = 0;
        
        for (int i = 0; i < switchArr.length; i++)
            switchArr[i] = -1;

        for (int i = 0; i < symbolArr.length; i++)
            symbolArr[i] = 0;

        for (int i = 0; i < nextArr.length; i++)
            nextArr[i] = -1;
    }

    public void setIdentifier(String id) {
        identifier = id;
        currSymIndex = 0;
    }

    // Stores given identifier into symbol table
    // NOTE: setIdentifier() must be used first
    public void storeIntoTrie() {
        if (identifier.length() == 0) return;

        int valueOfSymbol = getNextSymbol();
        int ptr = switchArr[valueOfSymbol];
        
        if (ptr == -1) {
            create();
        }
        else {
            valueOfSymbol = getNextSymbol();
            boolean exit = false;
            while (!exit) {
                if (symbolArr[ptr] == valueOfSymbol) {
                    if (currSymIndex != identifier.length()) {
                        ptr += 1;
                        valueOfSymbol = getNextSymbol();
                        currSymIndex++;
                    }
                    else {
                        exit = true;
                    }
                }
                else {
                    if (nextArr[ptr] != -1) {
                        ptr = nextArr[ptr];
                        valueOfSymbol = getNextSymbol();
                    }
                    else {
                        create();
                        exit = true;
                    }
                }
            }
        }

        resetId();
    }

/*
valueOfSymbol = getNextSymbol();  // get first symbol in id
ptr = switch [valueOfSymbol];     // set pointer to point at symbol location in switch array
if ptr is undefined then Create() // new identifier if symbol does not exist in switch array
else {                            // else (if it points to something)
    valueOfSymbol = getNextSymbol();    // get next symbol in id
    exit = false;                       // exit flag used for characters flagged as end points
    while not exit {                    // while not at an exit point...
        if (symbol [ptr] == valueOfSymbol)          // if symbol at position ptr is same as symbol in id
        then if valueOfSymbol is not the endmarker      // check if symbol in id is endmarker
            then { ptr = ptr + 1;                       // if not, increment pointer by one
                valueOfSymbol = getNextSymbol(); }      // get next symbol in id
            else { exit = true; }       // if symbol is endmarker, set exit flag to true
        else if next [ptr] is defined   // see if next array contains a pointer
            then ptr = next [ptr]       // set pointer to point to next set in symbol array
            else { Create(); exit = true; } // new identifier (pointer added to next array)
    } //while
} //if
*/
    // Returns symbol "value" (according to switch array)
    private int getNextSymbol() {
        if (currSymIndex > identifier.length()) return -1;

        char c = identifier.charAt(currSymIndex);

        int offset = 0;
        if (c >= 'a' && c <= 'z')
            offset = 26;

        offset -= Character.getNumericValue('a'); // A and a have the same value

        return Character.getNumericValue(c) + offset;
    }

    private void create() {
        int firstChPos = Character.getNumericValue(identifier.charAt(0));
        firstChPos -= Character.getNumericValue('a');
        
        if (identifier.charAt(0) >= 'a' && identifier.charAt(0) <= 'z')
            firstChPos += 26;
        
        int storeLength = 0;

        if (switchArr[firstChPos] == -1) {
            switchArr[firstChPos] = lastPos;
            ptr = switchArr[firstChPos];
            lastPos += identifier.length() - 1;
            storeLength = lastPos - ptr;
        }
        else {
            ptr = switchArr[firstChPos];
            while (nextArr[ptr] != -1) ptr = nextArr[ptr];
            nextArr[ptr] = lastPos;
            ptr = nextArr[ptr];
            lastPos += identifier.length() - 1 - currSymIndex;
            storeLength = lastPos - ptr;
            System.out.println("id: " + identifier);
            System.out.println("ptr|lastPos|sl: " + ptr + "|" + lastPos + "|" + storeLength);
        }

        for (int i = 0 + currSymIndex; i < storeLength; i++)
            symbolArr[i + ptr] = identifier.charAt(i + 1);

        //symbolArr[lastPos] = (char) seperator;
        symbolArr[lastPos] = '@';
        lastPos++;
    }

    public void printTable() {
        int maxWidth = 68;
        printSwitchTable(maxWidth);
        System.out.println();
        printRestOfTable(maxWidth);
        System.out.println();
    }

    private void printSwitchTable(int maxWidth) {
        String printTop = "";
        String printBot = "";

        for (int i = 0; i < switchArr.length; i++) {
            int spacePadBot = (String.valueOf(switchArr[i])).length();
            int spacing = Math.max(spacePadBot, 2); // 2 = minimum spacing
            String topAddChar = "";
            String botAddChar = "";

            if (printTop.equals("")) { // printTop & printBot are empty by this conditional
                printTop = "        ";
                printBot = "switch: ";
            }

            if (i < 26) topAddChar += (char)('A' + i);
            else        topAddChar += (char)('a' + i - 26);

            botAddChar = String.valueOf(switchArr[i]);

            printTop += insertSpacing(topAddChar, spacing) + " ";
            printBot += insertSpacing(botAddChar, spacing) + " ";

            // Due to padding, all print* variables should be the same length
            if (printTop.length() >= maxWidth || i == switchArr.length - 1) {
                System.out.println(printTop);
                System.out.println(printBot);
                System.out.println();
                printTop = printBot = "";
            }
        }
    }

    private void printRestOfTable(int maxWidth) {
            String printTop = "";
            String printMid = "";
            String printBot = "";
        for (int i = 0; i < symbolArr.length; i++) {
            if (printTop.equals("")) { // All print* are empty by this conditional
                printTop = "        ";
                printMid = "symbol: ";
                printBot = "next:   ";
            }
        
            int spacePadTop = (String.valueOf(i)).length();
            int spacePadBot = (String.valueOf(nextArr[i])).length();
            int spacing = Math.max(spacePadTop, spacePadBot);
            spacing = Math.max(2, spacing); // 2 = minimum spacing
            
            String topAddChar = "";
            String midAddChar = "";
            String botAddChar = "";

            topAddChar = String.valueOf(i);
            midAddChar = String.valueOf(symbolArr[i]);
            if (nextArr[i] != -1) botAddChar = String.valueOf(nextArr[i]);

            printTop += insertSpacing(topAddChar, spacing) + " ";
            printMid += insertSpacing(midAddChar, spacing) + " ";
            printBot += insertSpacing(botAddChar, spacing) + " ";

            if (printTop.length() >= maxWidth || i == symbolArr.length - 1 || symbolArr[i + 1] == 0) {
                System.out.println(printTop);
                System.out.println(printMid);
                System.out.println(printBot);
                System.out.println();

                printTop = printMid = printBot = "";

                if (symbolArr[i + 1] == 0) break;
            }
        }
    }

    private String insertSpacing(String str, int amount) {
        for (int i = str.length(); i < amount; i++) {
            str = " " + str;
        }

        return str;
    }

    public void incrementSeperator() {
        seperator++;
    }

    private void resetId() {
        identifier = "";
        currSymIndex = 0;
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
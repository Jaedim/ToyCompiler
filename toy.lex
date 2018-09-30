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

// Trie data structure
class Trie {
    private int switchArr[] =  new int[52]; // A-Z & a-z
    private char symbolArr[] = new char[200]; // Arbitrary value for now
    private int nextArr[] =    new int[200]; // Arbitrary value for now
    private int lastPos = 0; // Position of first empty spot in next/symbol arrays

    private String identifier;
    private int valueOfSymbol;
    private int currSymIndex;
    private int ptr;
    private int seperator;

    public Trie() {
        ptr = 0;
        seperator = 0;
        lastPos = 0;
        valueOfSymbol = 0;
        
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
        valueOfSymbol = getNextSymbolVal();
        boolean exit = false;
        ptr = switchArr[valueOfSymbol];
        
        if (ptr == -1) { // If symbol does not yet exist in table:
            currSymIndex++; // Consume first symbol,
            insertIdentifier(); // insert full symbol into table, and
            return; // exit after inserting full symbol into table
        }

        currSymIndex++;
        valueOfSymbol = getNextSymbolVal();
        while (!exit) { // Partial symbol handling
            char c = symbolArr[ptr];
            int symbolArrVal = Character.getNumericValue(c);
            if (c >= 'a' && c <= 'z') symbolArrVal += 26 - 10;

            if (symbolArrVal == valueOfSymbol) { // if same char
                if (currSymIndex < identifier.length()) {
                    ptr++;
                    currSymIndex++;
                    valueOfSymbol = getNextSymbolVal();
                }
                else { // Reached end, symbol is already in table
                    exit = true;
                }
            }
            else { // if not same char
                if (nextArr[ptr] != -1) {
                    ptr = nextArr[ptr]; // If capable jump, go to it
                }
                else { // Insert partial symbol into table if no jump is possible
                    insertIdentifier();
                    exit = true;
                }
            }
        }
    }

    private int getNextSymbolVal() {
        if (identifier.length() == 0) return -1;

        char c = identifier.charAt(currSymIndex);
        int out = Character.getNumericValue(c);

        if (c >= 'a' && c <= 'z') out += 26 - 10;

        return out;
    }

    private void insertIdentifier() {
        // If does not exist in switch array
        if (ptr == -1) { // full symbol insertion
            switchArr[valueOfSymbol] = lastPos;
            ptr = switchArr[valueOfSymbol];
        }
        else { // partial symbol insertion
            nextArr[ptr] = lastPos;
            ptr = nextArr[ptr];
        }

        int idLen = identifier.length() - currSymIndex;
        lastPos += idLen;

        for (int i = 0; i < idLen; i++)
            symbolArr[ptr + i] = identifier.charAt(currSymIndex + i);

        symbolArr[lastPos] = '@';
        lastPos += 1;
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
<STRING> \n             { yybegin(YYINITIAL); System.out.println(""); }
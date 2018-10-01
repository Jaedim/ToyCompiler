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
*********/

/**
* Member names:
* Min-Jae Yi
**/

import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

class Toy {
    private int tokenCounter = 0; // Used for keyword / identifier seperators

    public static void main(String args[]) throws java.io.IOException {

        if (args.length == 2) {
            String keywords_file = "";
            String code_file = "";
            
            if (args[0].charAt(0) != '/') keywords_file = "./" + args[0]; // relative
            else                          keywords_file = args[0]; // absolute path

            if (args[1].charAt(0) != '/') code_file = "./" + args[1]; // relative
            else                          code_file = args[1]; // absolute path

            Scanner keywords = new Scanner(new File(keywords_file));
            FileReader inputCode = new FileReader(new File(code_file));
            FileWriter outputFile = new FileWriter(new File(code_file + ".output"));
            Trie trieTable = new Trie();

            Yylex yy = new Yylex(inputCode);
            Yytoken t;
            String output = "";

            while (keywords.hasNextLine()) {
                trieTable.setIdentifier(keywords.nextLine());
                trieTable.storeIntoTrie();
            }
            
            while ((t = yy.yylex()) != null) {
                output += t;

                if (t.getType().equals("id")) {
                    trieTable.setIdentifier(t.getText());
                    trieTable.storeIntoTrie();
                }
            }

            trieTable.printTable();
            trieTable.printTable(outputFile);
            
            System.out.println(output);
            outputFile.write(output);
            outputFile.flush(); // Write fully to file
            outputFile.close();
        }
        else if (args.length == 0) { 
            Yylex yy = new Yylex(System.in);
            Yytoken t;

            while ((t = yy.yylex()) != null)
                System.out.println(t);
        }
        else {
            System.out.println("Exactly 0 or 2 arguments are required.");
            System.out.println("If 2 arguments are given, they must be a keyword file and a code file.");
            System.out.println("Keyword file and code file must be specified as arguments in this order:");
            System.out.println("\t<keywords_file> <code_file>");
            return; // Exit if args is not 2 (required)
        }
    }
}

// Trie data structure
class Trie {
    private int switchArr[] =  new int[52]; // A-Z & a-z
    private char symbolArr[] = new char[2000]; // Arbitrary value for now
    private int nextArr[] =    new int[2000]; // Arbitrary value for now
    private int lastPos = 0; // Position of first empty spot in next/symbol arrays

    private String identifier;
    private int valueOfSymbol;
    private int currSymIndex;
    private int ptr;
    private int seperator;

    int maxWidth; // Maximum output width allowed (default 68 to match example)

    public Trie() {
        ptr = 0;
        seperator = 0;
        lastPos = 0;
        valueOfSymbol = 0;

        maxWidth = 68;
        
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
        else if (identifier.length() == 1) {
            return;
        }

        currSymIndex++;
        valueOfSymbol = getNextSymbolVal();
        while (!exit) { // Partial symbol handling
            char c = symbolArr[ptr];
            int symbolArrVal = Character.getNumericValue(c);
            if (c >= 'a' && c <= 'z') symbolArrVal += 26 - 10;
            else                      symbolArrVal -= 10;

            if (symbolArrVal == valueOfSymbol) { // if same char
                if (currSymIndex < identifier.length()-1) {
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
        else                      out -= 10;

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

    public void setOutputWidth(int width) {
        maxWidth = width;
    }

    public void printTable() throws java.io.IOException {
        printSwitchTable(maxWidth, null);
        System.out.println();
        printRestOfTable(maxWidth, null);
        System.out.println();
    }

    public void printTable(FileWriter outFile) throws java.io.IOException {
        printSwitchTable(maxWidth, outFile);
        outFile.write("\n");
        printRestOfTable(maxWidth, outFile);
        outFile.write("\n");
        outFile.flush();
    }

    private void printSwitchTable(int maxWidth, FileWriter file)
                 throws java.io.IOException {
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
                if (file == null) {
                    System.out.println(printTop);
                    System.out.println(printBot);
                    System.out.println();
                }
                else {
                    file.write(printTop + "\n");
                    file.write(printBot + "\n");
                    file.write("\n");
                }
                printTop = printBot = "";
            }
        }
    }

    private void printRestOfTable(int maxWidth, FileWriter file) throws java.io.IOException {
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
                if (file == null) {
                    System.out.println(printTop);
                    System.out.println(printMid);
                    System.out.println(printBot);
                    System.out.println();
                }
                else {
                    file.write(printTop + "\n");
                    file.write(printMid + "\n");
                    file.write(printBot + "\n");
                    file.write("\n");
                }

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
    private String text;
    private String type;
    
    // The constructor used for most subroutines.
    public Yytoken(int index, String text, String type) {
        this.index = index;
        this.text = text;
        this.type = type;
    }

    // Default constructor that's used to generate newlines in the
    //   output.
    public Yytoken() {
        this.index = -1;
        this.type = "newline";
    }
    
    // Used for outputting information
    public String toString() {    
        if (type.equals("newline"))
            return "\n";

        return type + " ";
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
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

<YYINITIAL> "boolean"                                        { return (new Yytoken( 0, yytext(), "boolean")); }
<YYINITIAL> "break"                                          { return (new Yytoken( 1, yytext(), "break")); }
<YYINITIAL> "class"                                          { return (new Yytoken( 2, yytext(), "class")); }
<YYINITIAL> "double"                                         { return (new Yytoken( 3, yytext(), "double")); }
<YYINITIAL> "else"                                           { return (new Yytoken( 4, yytext(), "else")); }
<YYINITIAL> "extends"                                        { return (new Yytoken( 5, yytext(), "extends")); }
<YYINITIAL> "for"                                            { return (new Yytoken( 6, yytext(), "for")); }
<YYINITIAL> "if"                                             { return (new Yytoken( 7, yytext(), "if")); }
<YYINITIAL> "implements"                                     { return (new Yytoken( 8, yytext(), "implements")); }
<YYINITIAL> "interface"                                      { return (new Yytoken( 9, yytext(), "interface")); }
<YYINITIAL> "int"                                            { return (new Yytoken(10, yytext(), "int")); }
<YYINITIAL> "newarray"                                       { return (new Yytoken(11, yytext(), "newarray")); }
<YYINITIAL> "new"                                            { return (new Yytoken(12, yytext(), "new")); }
<YYINITIAL> "null"                                           { return (new Yytoken(13, yytext(), "null")); }
<YYINITIAL> "println"                                        { return (new Yytoken(14, yytext(), "println")); }
<YYINITIAL> "readln"                                         { return (new Yytoken(15, yytext(), "readln")); }
<YYINITIAL> "return"                                         { return (new Yytoken(16, yytext(), "return")); }
<YYINITIAL> "string"                                         { return (new Yytoken(17, yytext(), "string")); }
<YYINITIAL> "void"                                           { return (new Yytoken(18, yytext(), "void")); }
<YYINITIAL> "while"                                          { return (new Yytoken(19, yytext(), "while")); }
<YYINITIAL> ("true"|"false")                                 { return (new Yytoken(20, yytext(), "booleanconstant")); }
<YYINITIAL> {DIGIT}+"."{DIGIT}*(("E+"|"e+")("-")?{DIGIT}+)?  { return (new Yytoken(21, yytext(), "doubleconstant")); }
<YYINITIAL> "0x"{HEX}+|"0X"{HEX}+                            { return (new Yytoken(22, yytext(), "intconstant")); }
<YYINITIAL> {DIGIT}+                                         { return (new Yytoken(23, yytext(), "intconstant")); }
<YYINITIAL> {ALPHA}({ALPHA}|{DIGIT}|_)*                      { return (new Yytoken(24, yytext(), "id")); }

<YYINITIAL> "("                                              { return (new Yytoken(25, yytext(), "leftparen")); }
<YYINITIAL> ")"                                              { return (new Yytoken(26, yytext(), "rightparen")); }
<YYINITIAL> "{"                                              { return (new Yytoken(27, yytext(), "leftbrace")); }
<YYINITIAL> "}"                                              { return (new Yytoken(28, yytext(), "rightbrace")); }
<YYINITIAL> "["                                              { return (new Yytoken(29, yytext(), "leftbracket")); }
<YYINITIAL> "]"                                              { return (new Yytoken(30, yytext(), "rightbracket")); }

<YYINITIAL> "=="                                             { return (new Yytoken(31, yytext(), "equal")); }
<YYINITIAL> "!="                                             { return (new Yytoken(32, yytext(), "notequal")); }
<YYINITIAL> ">="                                             { return (new Yytoken(33, yytext(), "greaterequal")); }
<YYINITIAL> "<="                                             { return (new Yytoken(34, yytext(), "lessequal")); }
<YYINITIAL> ">"                                              { return (new Yytoken(35, yytext(), "greater")); }
<YYINITIAL> "<"                                              { return (new Yytoken(36, yytext(), "less")); }
<YYINITIAL> "!"                                              { return (new Yytoken(37, yytext(), "not")); }
<YYINITIAL> "&&"                                             { return (new Yytoken(38, yytext(), "and")); }
<YYINITIAL> "||"                                             { return (new Yytoken(39, yytext(), "or")); }

<YYINITIAL> "="                                              { return (new Yytoken(40, yytext(), "assignop")); }
<YYINITIAL> "+"                                              { return (new Yytoken(41, yytext(), "plus")); }
<YYINITIAL> "-"                                              { return (new Yytoken(42, yytext(), "minus")); }
<YYINITIAL> "*"                                              { return (new Yytoken(43, yytext(), "multiplication")); }
<YYINITIAL> "%"                                              { return (new Yytoken(44, yytext(), "mod")); }
<YYINITIAL> "/"                                              { yybegin(FSLASH); }
<YYINITIAL> \"                                               { yybegin(STRING); }

<YYINITIAL> ","                                              { return (new Yytoken(46, yytext(), "comma")); }
<YYINITIAL> "."                                              { return (new Yytoken(47, yytext(), "period")); }
<YYINITIAL> ";"                                              { return (new Yytoken(48, yytext(), "semicolon")); }


<FSLASH> ([^("*"|"/")])     { yybegin(YYINITIAL); return (new Yytoken(45, yytext(), "division")); }
<FSLASH> "/"                { yybegin(ONECOMMENT); }
<FSLASH> "*"                { yybegin(COMMENT); }


<ONECOMMENT> [^(\n\012)]    { }
<ONECOMMENT> {NEWLINE}      { yybegin(YYINITIAL); return (new Yytoken()); }

<COMMENT> [^"*/"]           { }
<COMMENT> "*/"[\n\012]      { yybegin(YYINITIAL); }
<COMMENT> "*/"              { yybegin(YYINITIAL); }


<STRING> [\n\"]             { yybegin(YYINITIAL); return (new Yytoken(49, yytext(), "stringconstant")); }
<STRING> [^(\"|\n)]         {  }
<STRING> \n                 { yybegin(YYINITIAL); System.out.println("UNMATCHED_STRING"); }

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
*   -Does not yet save string literals anywhere yet (just simply consumes
*       them and returns the entire string as a single token)
*********/

/*
* Member names:
* Min-Jae Yi
* Kenneth Ung
* Dongri Zhu
*/

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
* Main class used to receive command line input and start the whole
* lexical analyzer program.
*
* The program either takes 0 or 2 arguments.
*   -If 0, the program will simply take in lines of Toy-like code and return
*       back its tokens in string form immediately following user pressing enter.
*   -If 2, the program will require them be in the order of $KEYWORD_FILE and then
*       $CODE_FILE. It will then echo all $CODE_FILE input as tokens in string
*       form and print out the trie table for it. All of this output is also stored
*       into an output file named $CODE_FILE + ".output".
**/
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

/**
* Class used for everything involving the trie data structure. It can create one
* and print out its information upon request.
* 
* NOTE:
* The method used is transcribed from pseudocode and follows that logic. Due to
* that, the way some things are accomplished may be needlessly complicated.
**/
class Trie {
    private int switchArr[];
    private ArrayList<Character> symbolArr;
    private ArrayList<Integer> nextArr;

    private String identifier;
    private char delimiter;

    // Maximum output width allowed (default 68 to match example)
    private int maxWidth;

    /**
    * Default constructor of 0 arguments. Used for initializing the
    * member variables with proper initial values.
    **/
    public Trie() {
        identifier = "";
        delimiter = '@';
        maxWidth = 68;

        symbolArr = new ArrayList<>();
        nextArr = new ArrayList<>();
        switchArr = new int[52]; // A-Z & a-z
        for (int i = 0; i < switchArr.length; i++)
            switchArr[i] = -1;
    }

    /**
    * Resets identifier state and sets target identifier to work with.
    * @param id Identifier to add to trie.
    **/
    public void setIdentifier(String id) {
        identifier = id;
    }

    /**
    * Stores the identifier into the trie, so long as there is a target
    * identifier to work with. That means setIdentifier() is required to
    * be called first.
    **/
    public void storeIntoTrie() {
        if (identifier.length() == 0) return;

        char c = identifier.charAt(0);
        int cv = getSymbolVal(c);

        if (switchArr[cv] == -1)
            switchArr[cv] = symbolArr.size();

        int iter = switchArr[cv];

        // Handle following symbols in symbol/next array
        //     Will either insert new symbols into trie or will sit
        //     at some place in the trie if can't insert anything
        for (int i = 1; i < identifier.length(); i++) {
            c = identifier.charAt(i);
            cv = getSymbolVal(c);

            if (iter >= symbolArr.size()) {
                symbolArr.add(c);
                nextArr.add(-1);
            }

            if (c == symbolArr.get(iter)) {
                iter += 1;
            }
            else { // If symbol not same
                if (nextArr.get(iter) != -1) { // See where to jump
                    iter = nextArr.get(iter);
                    i -= 1;
                }
                else { // If jump is not defined
                    symbolArr.add(c);
                    int pos = iter;
                    iter = symbolArr.size();
                    nextArr.set(pos, iter-1);
                    nextArr.add(-1);
                }
            }
        }

        // Handle when identifier may already exist in table
        if (iter < symbolArr.size()) {
            if (symbolArr.get(iter) == delimiter) {
                return;
            }
            else {
                if (nextArr.get(iter) == -1) {
                    nextArr.set(iter, symbolArr.size());
                }
                else {
                    if (symbolArr.get(nextArr.get(iter)) == delimiter)
                        return;
                }

            }
        }
        
        symbolArr.add(delimiter);
        nextArr.add(-1);

    }

    /**
    * Retrieves the next symbol from the identifier.
    **/
    private int getSymbolVal(char c) {
        if (identifier.length() == 0) return -1;

        int out = Character.getNumericValue(c);

        if (c >= 'a' && c <= 'z') out += 26 - 10;
        else                      out -= 10;

        return out;
    }

    /**
    * Sets the output width that is allowed. Should the trie table exceed
    * this width, it will be forced to continue to print to another line.
    * Default width is 68 characters.
    * @param width Width to force table to
    **/
    public void setOutputWidth(int width) {
        maxWidth = width;
    }

    /**
    * Prints the table, starting with the switch table and then the symbol
    * table and next table together.
    **/
    public void printTable() throws java.io.IOException {
        printSwitchTable(maxWidth, null);
        System.out.println();
        printRestOfTable(maxWidth, null);
        System.out.println();
    }

    /**
    * Prints the table to the output file, starting with the switch table and
    * then the symbol table and next table together.
    **/
    public void printTable(FileWriter outFile) throws java.io.IOException {
        printSwitchTable(maxWidth, outFile);
        outFile.write("\n");
        printRestOfTable(maxWidth, outFile);
        outFile.write("\n");
        outFile.flush();
    }

    /**
    * Handles printing just the switch table so the printTable() method isn't
    * too crowded or long.
    * @param maxWidth Maximimum width the table cannot exceed
    * @param file File to print to (can be null for none)
    **/
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
    
    /**
    * Handles printing the symbol and next tableso the printTable() method
    * isn't too crowded or long.
    * @param maxWidth Maximum width the table cannot exceed
    * @param file File to print to (can be null for none)
    **/
    private void printRestOfTable(int maxWidth, FileWriter file) throws java.io.IOException {
            String printTop = "";
            String printMid = "";
            String printBot = "";
        for (int i = 0; i < symbolArr.size(); i++) {
            if (printTop.equals("")) { // All print* are empty by this conditional
                printTop = "        ";
                printMid = "symbol: ";
                printBot = "next:   ";
            }
        
            int spacePadTop = (String.valueOf(i)).length();
            int spacePadBot = (String.valueOf(nextArr.get(i)).length());
            int spacing = Math.max(spacePadTop, spacePadBot);
            spacing = Math.max(2, spacing); // 2 = minimum spacing
            
            String topAddChar = "";
            String midAddChar = "";
            String botAddChar = "";

            topAddChar = String.valueOf(i);
            midAddChar = String.valueOf(symbolArr.get(i));
            if (nextArr.get(i) != -1) botAddChar = String.valueOf(nextArr.get(i));

            printTop += insertSpacing(topAddChar, spacing) + " ";
            printMid += insertSpacing(midAddChar, spacing) + " ";
            printBot += insertSpacing(botAddChar, spacing) + " ";

            if (printTop.length() >= maxWidth || i == symbolArr.size() - 1) {
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
            }
        }
    }

    /**
    * Returns white space according to the parameters given.
    **/
    private String insertSpacing(String str, int amount) {
        for (int i = str.length(); i < amount; i++) {
            str = " " + str;
        }

        return str;
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
    
    // Returns token type as a string that is printable
    public String toString() {    
        if (type.equals("newline"))
            return "\n";

        return type + " ";
    }

    // Returns token type as a string
    public String getType() {
        return type;
    }

    // Returns interpreted text exactly as is
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

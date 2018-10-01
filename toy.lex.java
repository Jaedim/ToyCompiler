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
            System.out.println();
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


class Yylex {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int FSLASH = 4;
	private final int ONECOMMENT = 1;
	private final int STRING = 3;
	private final int YYINITIAL = 0;
	private final int COMMENT = 2;
	private final int yy_state_dtrans[] = {
		0,
		76,
		78,
		82,
		84
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NOT_ACCEPT,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NOT_ACCEPT,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NOT_ACCEPT,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NOT_ACCEPT,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NOT_ACCEPT,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NOT_ACCEPT,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NOT_ACCEPT,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NOT_ACCEPT,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NOT_ACCEPT,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NOT_ACCEPT,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"54:8,2:2,1,54:21,2,42,51,54:2,49,45,54,35,36,48,29,52,47,27,50,30,26:9,54,5" +
"3,44,41,43,54:2,31:4,28,31,33:17,32,33:2,39,54,40,54,34,54,7,3,11,13,6,17,2" +
"3,25,18,33,10,5,19,8,4,20,33,9,12,16,14,24,21,15,22,33,37,46,38,54:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,159,
"0,1:3,2,3,1:8,4,5,6,7,1:7,8,9,1:6,10,8,11,12,8:17,1:3,13,1:6,14,15,16,17,18" +
",19,12,20,21,22,17,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41" +
",42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66" +
",67,68,8,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90," +
"91,92,93,94,95,8,96,97,98,99,100,101,102,103,104")[0];

	private int yy_nxt[][] = unpackFromString(105,55,
"1,2,3,4,149:2,117,149,97,152,149,153,154,155,149:2,118,98,65,149,156,157,14" +
"9:2,119,149,5,6,149,7,66,149:3,-1,8,9,10,11,12,13,14,15,16,17,64,68,18,19,2" +
"0,21,22,23,24,-1:59,149,158,149:4,120,149:16,121,-1,149,-1,121,149:3,121,-1" +
":46,5,26,-1:2,5,-1:65,27,-1:54,28,-1:54,29,-1:54,30,-1:16,149:23,121,-1,149" +
",-1,121,149:3,121,-1:26,72,-1:19,26,-1,72,-1,26,-1:27,149:4,140,149:18,121," +
"-1,149,-1,121,149:3,121,-1:23,149:3,151,149:19,121,-1,149,-1,121,149:3,121," +
"-1:23,36,-1:2,36:2,-1:3,36,-1,36,-1:3,36,-1:8,36,-1,36,-1,36:2,-1:24,58,-1:" +
"98,31,-1:12,149:5,73,149:8,25,149,128,149:6,121,-1,149,-1,121,149:3,121,-1:" +
"35,70,-1:10,5,26,-1:2,5,-1,70,-1:48,67,-1:3,67,-1:70,32,-1:11,149:18,33,149" +
":4,121,-1,149,-1,121,149:3,121,-1:23,149:6,34,149:16,121,-1,149,-1,121,149:" +
"3,121,-1:49,74,-1:28,149:13,35,149:9,121,-1,149,-1,121,149:3,121,-1:23,149:" +
"3,37,149:19,121,-1,149,-1,121,149:3,121,-1:20,1,54,55:33,-1:2,55:18,-1:3,14" +
"9:2,38,149:20,121,-1,149,-1,121,149:3,121,-1:20,1,56:47,80,56,-1,56:4,-1:3," +
"149:3,39,149:19,121,-1,149,-1,121,149:3,121,-1:70,57,-1:7,149:10,40,149:12," +
"121,-1,149,-1,121,149:3,121,-1:20,1,59,60:33,-1:2,60:9,-1,60:4,59,60:3,-1:3" +
",149:7,41,149:15,121,-1,149,-1,121,149:3,121,-1:20,1,61:34,-1:2,61:9,-1,61," +
"62,61,63,61:4,-1:3,149:9,42,149:13,121,-1,149,-1,121,149:3,121,-1:23,149:3," +
"43,149:19,121,-1,149,-1,121,149:3,121,-1:23,149:5,44,149:17,121,-1,149,-1,1" +
"21,149:3,121,-1:23,149:5,45,149:17,121,-1,149,-1,121,149:3,121,-1:23,149:20" +
",46,149:2,121,-1,149,-1,121,149:3,121,-1:23,149:3,47,149:19,121,-1,149,-1,1" +
"21,149:3,121,-1:23,149:5,48,149:17,121,-1,149,-1,121,149:3,121,-1:23,149:9," +
"49,149:13,121,-1,149,-1,121,149:3,121,-1:23,149:5,50,149:17,121,-1,149,-1,1" +
"21,149:3,121,-1:23,149:19,51,149:3,121,-1,149,-1,121,149:3,121,-1:23,149:3," +
"52,149:19,121,-1,149,-1,121,149:3,121,-1:23,149:9,53,149:13,121,-1,149,-1,1" +
"21,149:3,121,-1:23,149:3,69,149:7,100,149:11,121,-1,149,-1,121,149:3,121,-1" +
":23,149,71,149:2,127,149:18,121,-1,149,-1,121,149:3,121,-1:23,149:9,75,149:" +
"13,121,-1,149,-1,121,149:3,121,-1:23,149:2,77,149:20,121,-1,149,-1,121,149:" +
"3,121,-1:23,149:11,79,149:11,121,-1,149,-1,121,149:3,121,-1:23,149:15,81,14" +
"9:7,121,-1,149,-1,121,149:3,121,-1:23,149:4,83,149:18,121,-1,149,-1,121,149" +
":3,121,-1:23,149:9,85,149:13,121,-1,149,-1,121,149:3,121,-1:23,149:9,79,149" +
":13,121,-1,149,-1,121,149:3,121,-1:23,149:2,86,149:20,121,-1,149,-1,121,149" +
":3,121,-1:23,149:2,87,149:20,121,-1,149,-1,121,149:3,121,-1:23,149:6,88,149" +
":16,121,-1,149,-1,121,149:3,121,-1:23,149:5,89,149:17,121,-1,149,-1,121,149" +
":3,121,-1:23,149:2,90,149:20,121,-1,149,-1,121,149:3,121,-1:23,149:4,91,149" +
":18,121,-1,149,-1,121,149:3,121,-1:23,149:10,92,149:12,121,-1,149,-1,121,14" +
"9:3,121,-1:23,149:2,93,149:20,121,-1,149,-1,121,149:3,121,-1:23,149:4,94,14" +
"9:18,121,-1,149,-1,121,149:3,121,-1:23,149:8,95,149:14,121,-1,149,-1,121,14" +
"9:3,121,-1:23,149:13,96,149:9,121,-1,149,-1,121,149:3,121,-1:23,149:2,99,14" +
"9:9,122,149:10,121,-1,149,-1,121,149:3,121,-1:23,149:6,101,149:16,121,-1,14" +
"9,-1,121,149:3,121,-1:23,149,102,149:21,121,-1,149,-1,121,149:3,121,-1:23,1" +
"49:3,103,149:19,121,-1,149,-1,121,149:3,121,-1:23,149:13,132,149:9,121,-1,1" +
"49,-1,121,149:3,121,-1:23,149:4,133,149:8,134,149:9,121,-1,149,-1,121,149:3" +
",121,-1:23,149:4,104,149:18,121,-1,149,-1,121,149:3,121,-1:23,149:6,135,149" +
":16,121,-1,149,-1,121,149:3,121,-1:23,149:11,136,149:11,121,-1,149,-1,121,1" +
"49:3,121,-1:23,149:2,105,149:20,121,-1,149,-1,121,149:3,121,-1:23,149:17,15" +
"0,149:5,121,-1,149,-1,121,149:3,121,-1:23,149:15,137,149:7,121,-1,149,-1,12" +
"1,149:3,121,-1:23,149:15,106,149:7,121,-1,149,-1,121,149:3,121,-1:23,149:2," +
"138,149:20,121,-1,149,-1,121,149:3,121,-1:23,149:3,139,149:19,121,-1,149,-1" +
",121,149:3,121,-1:23,149:10,107,149:12,121,-1,149,-1,121,149:3,121,-1:23,14" +
"9:11,108,149:11,121,-1,149,-1,121,149:3,121,-1:23,149:15,109,149:7,121,-1,1" +
"49,-1,121,149:3,121,-1:23,110,149:22,121,-1,149,-1,121,149:3,121,-1:23,149:" +
"5,142,149:17,121,-1,149,-1,121,149:3,121,-1:23,149:3,111,149:19,121,-1,149," +
"-1,121,149:3,121,-1:23,149:5,112,149:17,121,-1,149,-1,121,149:3,121,-1:23,1" +
"49:6,143,149:16,121,-1,149,-1,121,149:3,121,-1:23,149:3,145,149:19,121,-1,1" +
"49,-1,121,149:3,121,-1:23,149:13,113,149:9,121,-1,149,-1,121,149:3,121,-1:2" +
"3,149:6,114,149:16,121,-1,149,-1,121,149:3,121,-1:23,149:14,146,149:8,121,-" +
"1,149,-1,121,149:3,121,-1:23,149:16,147,149:6,121,-1,149,-1,121,149:3,121,-" +
"1:23,149:4,115,149:18,121,-1,149,-1,121,149:3,121,-1:23,149:3,148,149:19,12" +
"1,-1,149,-1,121,149:3,121,-1:23,149:5,116,149:17,121,-1,149,-1,121,149:3,12" +
"1,-1:23,149:2,141,149:20,121,-1,149,-1,121,149:3,121,-1:23,149:6,144,149:16" +
",121,-1,149,-1,121,149:3,121,-1:23,149:3,123,149:19,121,-1,149,-1,121,149:3" +
",121,-1:23,149:2,124,149:20,121,-1,149,-1,121,149:3,121,-1:23,149:13,125,14" +
"9:9,121,-1,149,-1,121,149:3,121,-1:23,149,126,149:21,121,-1,149,-1,121,149:" +
"3,121,-1:23,149:6,129,149:16,121,-1,149,-1,121,149:3,121,-1:23,149:22,130,1" +
"21,-1,149,-1,121,149:3,121,-1:23,149,131,149:21,121,-1,149,-1,121,149:3,121" +
",-1:20");

	public Yytoken yylex ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {
				return null;
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return (new Yytoken()); }
					case -3:
						break;
					case 3:
						{ }
					case -4:
						break;
					case 4:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -5:
						break;
					case 5:
						{ return (new Yytoken(23, yytext(), "intconstant")); }
					case -6:
						break;
					case 6:
						{ return (new Yytoken(47, yytext(), "period")); }
					case -7:
						break;
					case 7:
						{ return (new Yytoken(41, yytext(), "plus")); }
					case -8:
						break;
					case 8:
						{ return (new Yytoken(25, yytext(), "leftparen")); }
					case -9:
						break;
					case 9:
						{ return (new Yytoken(26, yytext(), "rightparen")); }
					case -10:
						break;
					case 10:
						{ return (new Yytoken(27, yytext(), "leftbrace")); }
					case -11:
						break;
					case 11:
						{ return (new Yytoken(28, yytext(), "rightbrace")); }
					case -12:
						break;
					case 12:
						{ return (new Yytoken(29, yytext(), "leftbracket")); }
					case -13:
						break;
					case 13:
						{ return (new Yytoken(30, yytext(), "rightbracket")); }
					case -14:
						break;
					case 14:
						{ return (new Yytoken(40, yytext(), "assignop")); }
					case -15:
						break;
					case 15:
						{ return (new Yytoken(37, yytext(), "not")); }
					case -16:
						break;
					case 16:
						{ return (new Yytoken(35, yytext(), "greater")); }
					case -17:
						break;
					case 17:
						{ return (new Yytoken(36, yytext(), "less")); }
					case -18:
						break;
					case 18:
						{ return (new Yytoken(42, yytext(), "minus")); }
					case -19:
						break;
					case 19:
						{ return (new Yytoken(43, yytext(), "multiplication")); }
					case -20:
						break;
					case 20:
						{ return (new Yytoken(44, yytext(), "mod")); }
					case -21:
						break;
					case 21:
						{ yybegin(FSLASH); }
					case -22:
						break;
					case 22:
						{ yybegin(STRING); }
					case -23:
						break;
					case 23:
						{ return (new Yytoken(46, yytext(), "comma")); }
					case -24:
						break;
					case 24:
						{ return (new Yytoken(48, yytext(), "semicolon")); }
					case -25:
						break;
					case 25:
						{ return (new Yytoken( 7, yytext(), "if")); }
					case -26:
						break;
					case 26:
						{ return (new Yytoken(21, yytext(), "doubleconstant")); }
					case -27:
						break;
					case 27:
						{ return (new Yytoken(31, yytext(), "equal")); }
					case -28:
						break;
					case 28:
						{ return (new Yytoken(32, yytext(), "notequal")); }
					case -29:
						break;
					case 29:
						{ return (new Yytoken(33, yytext(), "greaterequal")); }
					case -30:
						break;
					case 30:
						{ return (new Yytoken(34, yytext(), "lessequal")); }
					case -31:
						break;
					case 31:
						{ return (new Yytoken(38, yytext(), "and")); }
					case -32:
						break;
					case 32:
						{ return (new Yytoken(39, yytext(), "or")); }
					case -33:
						break;
					case 33:
						{ return (new Yytoken(12, yytext(), "new")); }
					case -34:
						break;
					case 34:
						{ return (new Yytoken( 6, yytext(), "for")); }
					case -35:
						break;
					case 35:
						{ return (new Yytoken(10, yytext(), "int")); }
					case -36:
						break;
					case 36:
						{ return (new Yytoken(22, yytext(), "intconstant")); }
					case -37:
						break;
					case 37:
						{ return (new Yytoken( 4, yytext(), "else")); }
					case -38:
						break;
					case 38:
						{ return (new Yytoken(13, yytext(), "null")); }
					case -39:
						break;
					case 39:
						{ return (new Yytoken(20, yytext(), "booleanconstant")); }
					case -40:
						break;
					case 40:
						{ return (new Yytoken(18, yytext(), "void")); }
					case -41:
						break;
					case 41:
						{ return (new Yytoken( 1, yytext(), "break")); }
					case -42:
						break;
					case 42:
						{ return (new Yytoken( 2, yytext(), "class")); }
					case -43:
						break;
					case 43:
						{ return (new Yytoken(19, yytext(), "while")); }
					case -44:
						break;
					case 44:
						{ return (new Yytoken(15, yytext(), "readln")); }
					case -45:
						break;
					case 45:
						{ return (new Yytoken(16, yytext(), "return")); }
					case -46:
						break;
					case 46:
						{ return (new Yytoken(17, yytext(), "string")); }
					case -47:
						break;
					case 47:
						{ return (new Yytoken( 3, yytext(), "double")); }
					case -48:
						break;
					case 48:
						{ return (new Yytoken( 0, yytext(), "boolean")); }
					case -49:
						break;
					case 49:
						{ return (new Yytoken( 5, yytext(), "extends")); }
					case -50:
						break;
					case 50:
						{ return (new Yytoken(14, yytext(), "println")); }
					case -51:
						break;
					case 51:
						{ return (new Yytoken(11, yytext(), "newarray")); }
					case -52:
						break;
					case 52:
						{ return (new Yytoken( 9, yytext(), "interface")); }
					case -53:
						break;
					case 53:
						{ return (new Yytoken( 8, yytext(), "implements")); }
					case -54:
						break;
					case 54:
						{ yybegin(YYINITIAL); }
					case -55:
						break;
					case 55:
						{ }
					case -56:
						break;
					case 56:
						{ }
					case -57:
						break;
					case 57:
						{ yybegin(YYINITIAL); }
					case -58:
						break;
					case 58:
						{ yybegin(YYINITIAL); }
					case -59:
						break;
					case 59:
						{ yybegin(YYINITIAL); return (new Yytoken(49, yytext(), "stringconstant")); }
					case -60:
						break;
					case 60:
						{  }
					case -61:
						break;
					case 61:
						{ yybegin(YYINITIAL); return (new Yytoken(45, yytext(), "division")); }
					case -62:
						break;
					case 62:
						{ yybegin(COMMENT); }
					case -63:
						break;
					case 63:
						{ yybegin(ONECOMMENT); }
					case -64:
						break;
					case 65:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -65:
						break;
					case 66:
						{ return (new Yytoken(23, yytext(), "intconstant")); }
					case -66:
						break;
					case 67:
						{ return (new Yytoken(21, yytext(), "doubleconstant")); }
					case -67:
						break;
					case 69:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -68:
						break;
					case 71:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -69:
						break;
					case 73:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -70:
						break;
					case 75:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -71:
						break;
					case 77:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -72:
						break;
					case 79:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -73:
						break;
					case 81:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -74:
						break;
					case 83:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -75:
						break;
					case 85:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -76:
						break;
					case 86:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -77:
						break;
					case 87:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -78:
						break;
					case 88:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -79:
						break;
					case 89:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -80:
						break;
					case 90:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -81:
						break;
					case 91:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -82:
						break;
					case 92:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -83:
						break;
					case 93:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -84:
						break;
					case 94:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -85:
						break;
					case 95:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -86:
						break;
					case 96:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -87:
						break;
					case 97:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -88:
						break;
					case 98:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -89:
						break;
					case 99:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -90:
						break;
					case 100:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -91:
						break;
					case 101:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -92:
						break;
					case 102:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -93:
						break;
					case 103:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -94:
						break;
					case 104:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -95:
						break;
					case 105:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -96:
						break;
					case 106:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -97:
						break;
					case 107:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -98:
						break;
					case 108:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -99:
						break;
					case 109:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -100:
						break;
					case 110:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -101:
						break;
					case 111:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -102:
						break;
					case 112:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -103:
						break;
					case 113:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -104:
						break;
					case 114:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -105:
						break;
					case 115:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -106:
						break;
					case 116:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -107:
						break;
					case 117:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -108:
						break;
					case 118:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -109:
						break;
					case 119:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -110:
						break;
					case 120:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -111:
						break;
					case 121:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -112:
						break;
					case 122:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -113:
						break;
					case 123:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -114:
						break;
					case 124:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -115:
						break;
					case 125:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -116:
						break;
					case 126:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -117:
						break;
					case 127:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -118:
						break;
					case 128:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -119:
						break;
					case 129:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -120:
						break;
					case 130:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -121:
						break;
					case 131:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -122:
						break;
					case 132:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -123:
						break;
					case 133:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -124:
						break;
					case 134:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -125:
						break;
					case 135:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -126:
						break;
					case 136:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -127:
						break;
					case 137:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -128:
						break;
					case 138:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -129:
						break;
					case 139:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -130:
						break;
					case 140:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -131:
						break;
					case 141:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -132:
						break;
					case 142:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -133:
						break;
					case 143:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -134:
						break;
					case 144:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -135:
						break;
					case 145:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -136:
						break;
					case 146:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -137:
						break;
					case 147:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -138:
						break;
					case 148:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -139:
						break;
					case 149:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -140:
						break;
					case 150:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -141:
						break;
					case 151:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -142:
						break;
					case 152:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -143:
						break;
					case 153:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -144:
						break;
					case 154:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -145:
						break;
					case 155:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -146:
						break;
					case 156:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -147:
						break;
					case 157:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -148:
						break;
					case 158:
						{ return (new Yytoken(24, yytext(), "id")); }
					case -149:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}

// NOTE TO SELF:
//   -REARRANGE STATES BELOW
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
    public Yytoken() {
        this.index = -1;
        this.type = "newline";
    }
    public String toString() {    
        if (type.equals("newline"))
            return "\n";
        return type + " ";
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
		63,
		65,
		69,
		73
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
		/* 61 */ YY_NOT_ACCEPT,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NOT_ACCEPT,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NOT_ACCEPT,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NOT_ACCEPT,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NOT_ACCEPT,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
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
		/* 150 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"50:8,2:2,1,50:21,2,32,49,50:2,19,43,50,39,45,38,18,23,30,33,42,25:10,50,44," +
"31,21,20,50:2,47:26,24,50,34,50,48,50,7,3,35,28,6,36,29,41,10,47,26,5,11,8," +
"4,12,47,15,9,13,17,37,14,27,16,47,40,22,46,50:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,151,
"0,1:3,2,1:2,3,4,1:3,5,1,6,7,1:10,8,1:2,9,1:3,10,11,8:18,1:4,12,1:3,13,14,15" +
",16,17,18,19,20,21,22,12,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39" +
",40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,8," +
"64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88," +
"89,90,8,91,92,93,94,95,96,97,98,99")[0];

	private int yy_nxt[][] = unpackFromString(100,51,
"1,2,3,4,141:2,109,141,89,144,62,141,145,110,146,147,141:2,5,6,7,8,9,10,11,1" +
"2,141:2,148,141,13,14,15,16,17,149,90,111,18,19,20,141,21,61,22,23,24,141,-" +
"1,25,-1:55,141,150,141:10,112,141:2,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5" +
",141,113,-1:23,27,-1:50,28,-1:54,12,-1:7,29,-1:38,30,-1:50,31,-1:32,141:15," +
"-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:27,29,-1:28,141:4,132,14" +
"1:10,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:3,143,141:11," +
"-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:4,71:47,57,71,-1:43,32,-" +
"1:10,141:5,66,141:2,116,141:6,-1:7,113,141:4,-1:5,141,26,141,-1:3,141,-1:5," +
"141,113,-1:2,1,53,54:37,-1,54:5,-1,54:5,-1:3,141:11,33,141:3,-1:7,113,141:4" +
",-1:5,141:3,-1:3,141,-1:5,141,113,-1:2,1,55:37,67,55:3,-1,55:8,-1:3,141:10," +
"34,141:4,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:44,56,-1:11,141" +
":12,35,141:2,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:2,1,-1,71:4" +
"7,57,71,-1:3,141:3,36,141:11,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,11" +
"3,-1:5,141:2,37,141:12,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:2" +
",1,58:21,-1,58:15,59,-1,58:2,60,58:2,-1,58:5,-1:3,141:3,38,141:11,-1:7,113," +
"141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:15,-1:7,113,141:2,39,141,-1" +
":5,141:3,-1:3,141,-1:5,141,113,-1:5,141:15,-1:7,113,40,141:3,-1:5,141:3,-1:" +
"3,141,-1:5,141,113,-1:5,141:3,41,141:11,-1:7,113,141:4,-1:5,141:3,-1:3,141," +
"-1:5,141,113,-1:5,141:6,42,141:8,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,14" +
"1,113,-1:5,141:15,-1:7,113,141:3,43,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,1" +
"41:5,44,141:9,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:5,45" +
",141:9,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:3,46,141:11" +
",-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:5,47,141:9,-1:7,1" +
"13,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:6,48,141:8,-1:7,113,141:" +
"4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:5,49,141:9,-1:7,113,141:4,-1:5," +
"141:3,-1:3,141,-1:5,141,113,-1:5,141:13,50,141,-1:7,113,141:4,-1:5,141:3,-1" +
":3,141,-1:5,141,113,-1:5,141:3,51,141:11,-1:7,113,141:4,-1:5,141:3,-1:3,141" +
",-1:5,141,113,-1:5,141:6,52,141:8,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,1" +
"41,113,-1:5,141:3,64,141:10,92,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141," +
"113,-1:5,141,68,141:2,122,141:10,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,14" +
"1,113,-1:5,141:6,70,141:8,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-" +
"1:5,141:2,72,141:12,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,14" +
"1:14,74,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:7,75,141:7" +
",-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:4,76,141:10,-1:7," +
"113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:2,77,141:12,-1:7,113,14" +
"1:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:6,78,141:8,-1:7,113,141:4,-1:" +
"5,141:3,-1:3,141,-1:5,141,113,-1:5,141:6,74,141:8,-1:7,113,141:4,-1:5,141:3" +
",-1:3,141,-1:5,141,113,-1:5,141:5,79,141:9,-1:7,113,141:4,-1:5,141:3,-1:3,1" +
"41,-1:5,141,113,-1:5,141:2,80,141:12,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:" +
"5,141,113,-1:5,141:12,81,141:2,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141," +
"113,-1:5,141:2,82,141:12,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1" +
":5,141:4,83,141:10,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141" +
":15,-1:7,113,141:2,84,141,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:2,85,14" +
"1:12,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:4,86,141:10,-" +
"1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:15,-1:7,113,141:4,-" +
"1:5,87,141:2,-1:3,141,-1:5,141,113,-1:5,141:10,88,141:4,-1:7,113,141:4,-1:5" +
",141:3,-1:3,141,-1:5,141,113,-1:5,141:2,91,141:12,-1:7,113,141,114,141:2,-1" +
":5,141:3,-1:3,141,-1:5,141,113,-1:5,141:12,93,141:2,-1:7,113,141:4,-1:5,141" +
":3,-1:3,141,-1:5,141,113,-1:5,141,94,141:13,-1:7,113,141:4,-1:5,141:3,-1:3," +
"141,-1:5,141,113,-1:5,141:3,95,141:11,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1" +
":5,141,113,-1:5,141:10,124,141:4,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,14" +
"1,113,-1:5,141:12,125,141:2,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113" +
",-1:5,141:9,142,141:5,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5," +
"141:7,126,141:7,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:7," +
"96,141:7,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:4,127,141" +
":5,128,141:4,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:14,12" +
"9,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:4,97,141:10,-1:7" +
",113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:2,98,141:12,-1:7,113,1" +
"41:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:2,130,141:12,-1:7,113,141:4," +
"-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:3,131,141:11,-1:7,113,141:4,-1:5," +
"141:3,-1:3,141,-1:5,141,113,-1:5,141:7,99,141:7,-1:7,113,141:4,-1:5,141:3,-" +
"1:3,141,-1:5,141,113,-1:5,141:5,134,141:9,-1:7,113,141:4,-1:5,141:3,-1:3,14" +
"1,-1:5,141,113,-1:5,141:15,-1:7,113,141:2,100,141,-1:5,141:3,-1:3,141,-1:5," +
"141,113,-1:5,141:14,101,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:" +
"5,102,141:14,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:3,103" +
",141:11,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:5,104,141:" +
"9,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:12,135,141:2,-1:" +
"7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:3,137,141:11,-1:7,113" +
",141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:10,105,141:4,-1:7,113,141:" +
"4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:12,106,141:2,-1:7,113,141:4,-1:" +
"5,141:3,-1:3,141,-1:5,141,113,-1:5,141:15,-1:7,113,141:4,-1:5,141,138,141,-" +
"1:3,141,-1:5,141,113,-1:5,141:8,139,141:6,-1:7,113,141:4,-1:5,141:3,-1:3,14" +
"1,-1:5,141,113,-1:5,141:4,107,141:10,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:" +
"5,141,113,-1:5,141:3,140,141:11,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141" +
",113,-1:5,141:5,108,141:9,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-" +
"1:5,141:2,133,141:12,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,1" +
"41:12,136,141:2,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:10" +
",115,141:4,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:12,117," +
"141:2,-1:7,113,141:4,-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141:15,-1:7,113," +
"141:4,-1:5,141:3,-1:3,118,-1:5,141,113,-1:5,141:3,119,141:11,-1:7,113,141:4" +
",-1:5,141:3,-1:3,141,-1:5,141,113,-1:5,141,120,141:13,-1:7,113,141:4,-1:5,1" +
"41:3,-1:3,141,-1:5,141,113,-1:5,141:2,121,141:12,-1:7,113,141:4,-1:5,141:3," +
"-1:3,141,-1:5,141,113,-1:5,141,123,141:13,-1:7,113,141:4,-1:5,141:3,-1:3,14" +
"1,-1:5,141,113,-1:2");

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
						{ return (new Yytoken(0, "id")); }
					case -5:
						break;
					case 5:
						{ return (new Yytoken(0, "plus")); }
					case -6:
						break;
					case 6:
						{ return (new Yytoken(0, "mod")); }
					case -7:
						break;
					case 7:
						{ return (new Yytoken(0, "greater")); }
					case -8:
						break;
					case 8:
						{ return (new Yytoken(0, "assignop")); }
					case -9:
						break;
					case 9:
						{ return (new Yytoken(0, "or")); }
					case -10:
						break;
					case 10:
						{ return (new Yytoken(0, "comma")); }
					case -11:
						break;
					case 11:
						{ return (new Yytoken(0, "leftbracket")); }
					case -12:
						break;
					case 12:
						{ return (new Yytoken(0, "intconstant")); }
					case -13:
						break;
					case 13:
						{ return (new Yytoken(0, "minus")); }
					case -14:
						break;
					case 14:
						{ return (new Yytoken(0, "less")); }
					case -15:
						break;
					case 15:
						{ return (new Yytoken(0, "not")); }
					case -16:
						break;
					case 16:
						{ return (new Yytoken(0, "period")); }
					case -17:
						break;
					case 17:
						{ return (new Yytoken(0, "rightbracket")); }
					case -18:
						break;
					case 18:
						{ return (new Yytoken(0, "multiplication")); }
					case -19:
						break;
					case 19:
						{ return (new Yytoken(0, "leftparen")); }
					case -20:
						break;
					case 20:
						{ return (new Yytoken(0, "leftbrace")); }
					case -21:
						break;
					case 21:
						{ yybegin(FSLASH); }
					case -22:
						break;
					case 22:
						{ return (new Yytoken(0, "semicolon")); }
					case -23:
						break;
					case 23:
						{ return (new Yytoken(0, "rightparen")); }
					case -24:
						break;
					case 24:
						{ return (new Yytoken(0, "rightbrace")); }
					case -25:
						break;
					case 25:
						{ yybegin(STRING); }
					case -26:
						break;
					case 26:
						{ return (new Yytoken(0, "if")); }
					case -27:
						break;
					case 27:
						{ return (new Yytoken(0, "greaterequal")); }
					case -28:
						break;
					case 28:
						{ return (new Yytoken(0, "equal")); }
					case -29:
						break;
					case 29:
						{ return (new Yytoken(0, "doubleconstant")); }
					case -30:
						break;
					case 30:
						{ return (new Yytoken(0, "lessequal")); }
					case -31:
						break;
					case 31:
						{ return (new Yytoken(0, "notequal")); }
					case -32:
						break;
					case 32:
						{ return (new Yytoken(0, "and")); }
					case -33:
						break;
					case 33:
						{ return (new Yytoken(0, "new")); }
					case -34:
						break;
					case 34:
						{ return (new Yytoken(0, "int")); }
					case -35:
						break;
					case 35:
						{ return (new Yytoken(0, "for")); }
					case -36:
						break;
					case 36:
						{ return (new Yytoken(0, "else")); }
					case -37:
						break;
					case 37:
						{ return (new Yytoken(0, "null")); }
					case -38:
						break;
					case 38:
						{ return (new Yytoken(0, "booleanconstant")); }
					case -39:
						break;
					case 39:
						{ return (new Yytoken(0, "void")); }
					case -40:
						break;
					case 40:
						{ return (new Yytoken(0, "break")); }
					case -41:
						break;
					case 41:
						{ return (new Yytoken(0, "while")); }
					case -42:
						break;
					case 42:
						{ return (new Yytoken(0, "class")); }
					case -43:
						break;
					case 43:
						{ return (new Yytoken(0, "string")); }
					case -44:
						break;
					case 44:
						{ return (new Yytoken(0, "readln")); }
					case -45:
						break;
					case 45:
						{ return (new Yytoken(0, "return")); }
					case -46:
						break;
					case 46:
						{ return (new Yytoken(0, "double")); }
					case -47:
						break;
					case 47:
						{ return (new Yytoken(0, "boolean")); }
					case -48:
						break;
					case 48:
						{ return (new Yytoken(0, "extends")); }
					case -49:
						break;
					case 49:
						{ return (new Yytoken(0, "println")); }
					case -50:
						break;
					case 50:
						{ return (new Yytoken(0, "newarray")); }
					case -51:
						break;
					case 51:
						{ return (new Yytoken(0, "interface")); }
					case -52:
						break;
					case 52:
						{ return (new Yytoken(0, "implements")); }
					case -53:
						break;
					case 53:
						{ yybegin(YYINITIAL); }
					case -54:
						break;
					case 54:
						{ }
					case -55:
						break;
					case 55:
						{ }
					case -56:
						break;
					case 56:
						{ yybegin(YYINITIAL); }
					case -57:
						break;
					case 57:
						{ yybegin(YYINITIAL); return (new Yytoken(0, "stringconstant")); }
					case -58:
						break;
					case 58:
						{ yybegin(YYINITIAL); return (new Yytoken(0, "division")); }
					case -59:
						break;
					case 59:
						{ yybegin(COMMENT); }
					case -60:
						break;
					case 60:
						{ yybegin(ONECOMMENT); }
					case -61:
						break;
					case 62:
						{ return (new Yytoken(0, "id")); }
					case -62:
						break;
					case 64:
						{ return (new Yytoken(0, "id")); }
					case -63:
						break;
					case 66:
						{ return (new Yytoken(0, "id")); }
					case -64:
						break;
					case 68:
						{ return (new Yytoken(0, "id")); }
					case -65:
						break;
					case 70:
						{ return (new Yytoken(0, "id")); }
					case -66:
						break;
					case 72:
						{ return (new Yytoken(0, "id")); }
					case -67:
						break;
					case 74:
						{ return (new Yytoken(0, "id")); }
					case -68:
						break;
					case 75:
						{ return (new Yytoken(0, "id")); }
					case -69:
						break;
					case 76:
						{ return (new Yytoken(0, "id")); }
					case -70:
						break;
					case 77:
						{ return (new Yytoken(0, "id")); }
					case -71:
						break;
					case 78:
						{ return (new Yytoken(0, "id")); }
					case -72:
						break;
					case 79:
						{ return (new Yytoken(0, "id")); }
					case -73:
						break;
					case 80:
						{ return (new Yytoken(0, "id")); }
					case -74:
						break;
					case 81:
						{ return (new Yytoken(0, "id")); }
					case -75:
						break;
					case 82:
						{ return (new Yytoken(0, "id")); }
					case -76:
						break;
					case 83:
						{ return (new Yytoken(0, "id")); }
					case -77:
						break;
					case 84:
						{ return (new Yytoken(0, "id")); }
					case -78:
						break;
					case 85:
						{ return (new Yytoken(0, "id")); }
					case -79:
						break;
					case 86:
						{ return (new Yytoken(0, "id")); }
					case -80:
						break;
					case 87:
						{ return (new Yytoken(0, "id")); }
					case -81:
						break;
					case 88:
						{ return (new Yytoken(0, "id")); }
					case -82:
						break;
					case 89:
						{ return (new Yytoken(0, "id")); }
					case -83:
						break;
					case 90:
						{ return (new Yytoken(0, "id")); }
					case -84:
						break;
					case 91:
						{ return (new Yytoken(0, "id")); }
					case -85:
						break;
					case 92:
						{ return (new Yytoken(0, "id")); }
					case -86:
						break;
					case 93:
						{ return (new Yytoken(0, "id")); }
					case -87:
						break;
					case 94:
						{ return (new Yytoken(0, "id")); }
					case -88:
						break;
					case 95:
						{ return (new Yytoken(0, "id")); }
					case -89:
						break;
					case 96:
						{ return (new Yytoken(0, "id")); }
					case -90:
						break;
					case 97:
						{ return (new Yytoken(0, "id")); }
					case -91:
						break;
					case 98:
						{ return (new Yytoken(0, "id")); }
					case -92:
						break;
					case 99:
						{ return (new Yytoken(0, "id")); }
					case -93:
						break;
					case 100:
						{ return (new Yytoken(0, "id")); }
					case -94:
						break;
					case 101:
						{ return (new Yytoken(0, "id")); }
					case -95:
						break;
					case 102:
						{ return (new Yytoken(0, "id")); }
					case -96:
						break;
					case 103:
						{ return (new Yytoken(0, "id")); }
					case -97:
						break;
					case 104:
						{ return (new Yytoken(0, "id")); }
					case -98:
						break;
					case 105:
						{ return (new Yytoken(0, "id")); }
					case -99:
						break;
					case 106:
						{ return (new Yytoken(0, "id")); }
					case -100:
						break;
					case 107:
						{ return (new Yytoken(0, "id")); }
					case -101:
						break;
					case 108:
						{ return (new Yytoken(0, "id")); }
					case -102:
						break;
					case 109:
						{ return (new Yytoken(0, "id")); }
					case -103:
						break;
					case 110:
						{ return (new Yytoken(0, "id")); }
					case -104:
						break;
					case 111:
						{ return (new Yytoken(0, "id")); }
					case -105:
						break;
					case 112:
						{ return (new Yytoken(0, "id")); }
					case -106:
						break;
					case 113:
						{ return (new Yytoken(0, "id")); }
					case -107:
						break;
					case 114:
						{ return (new Yytoken(0, "id")); }
					case -108:
						break;
					case 115:
						{ return (new Yytoken(0, "id")); }
					case -109:
						break;
					case 116:
						{ return (new Yytoken(0, "id")); }
					case -110:
						break;
					case 117:
						{ return (new Yytoken(0, "id")); }
					case -111:
						break;
					case 118:
						{ return (new Yytoken(0, "id")); }
					case -112:
						break;
					case 119:
						{ return (new Yytoken(0, "id")); }
					case -113:
						break;
					case 120:
						{ return (new Yytoken(0, "id")); }
					case -114:
						break;
					case 121:
						{ return (new Yytoken(0, "id")); }
					case -115:
						break;
					case 122:
						{ return (new Yytoken(0, "id")); }
					case -116:
						break;
					case 123:
						{ return (new Yytoken(0, "id")); }
					case -117:
						break;
					case 124:
						{ return (new Yytoken(0, "id")); }
					case -118:
						break;
					case 125:
						{ return (new Yytoken(0, "id")); }
					case -119:
						break;
					case 126:
						{ return (new Yytoken(0, "id")); }
					case -120:
						break;
					case 127:
						{ return (new Yytoken(0, "id")); }
					case -121:
						break;
					case 128:
						{ return (new Yytoken(0, "id")); }
					case -122:
						break;
					case 129:
						{ return (new Yytoken(0, "id")); }
					case -123:
						break;
					case 130:
						{ return (new Yytoken(0, "id")); }
					case -124:
						break;
					case 131:
						{ return (new Yytoken(0, "id")); }
					case -125:
						break;
					case 132:
						{ return (new Yytoken(0, "id")); }
					case -126:
						break;
					case 133:
						{ return (new Yytoken(0, "id")); }
					case -127:
						break;
					case 134:
						{ return (new Yytoken(0, "id")); }
					case -128:
						break;
					case 135:
						{ return (new Yytoken(0, "id")); }
					case -129:
						break;
					case 136:
						{ return (new Yytoken(0, "id")); }
					case -130:
						break;
					case 137:
						{ return (new Yytoken(0, "id")); }
					case -131:
						break;
					case 138:
						{ return (new Yytoken(0, "id")); }
					case -132:
						break;
					case 139:
						{ return (new Yytoken(0, "id")); }
					case -133:
						break;
					case 140:
						{ return (new Yytoken(0, "id")); }
					case -134:
						break;
					case 141:
						{ return (new Yytoken(0, "id")); }
					case -135:
						break;
					case 142:
						{ return (new Yytoken(0, "id")); }
					case -136:
						break;
					case 143:
						{ return (new Yytoken(0, "id")); }
					case -137:
						break;
					case 144:
						{ return (new Yytoken(0, "id")); }
					case -138:
						break;
					case 145:
						{ return (new Yytoken(0, "id")); }
					case -139:
						break;
					case 146:
						{ return (new Yytoken(0, "id")); }
					case -140:
						break;
					case 147:
						{ return (new Yytoken(0, "id")); }
					case -141:
						break;
					case 148:
						{ return (new Yytoken(0, "id")); }
					case -142:
						break;
					case 149:
						{ return (new Yytoken(0, "id")); }
					case -143:
						break;
					case 150:
						{ return (new Yytoken(0, "id")); }
					case -144:
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

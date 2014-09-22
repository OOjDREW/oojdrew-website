
package jDREW.util;

import java.io.*;


/**
############################################################################# <br>
#          National Research Council -- IIT - e-Business Fredericton <br>
# <br>
# PROJECT:      jDREW <br>
# AUTHOR(S):    Bruce Spencer <br>
# DATE CREATED: Jun 04 2002 <br>
# LAST MOD:      <br>
# LAST MOD BY:   <br>
# COPYRIGHT:    NRC <br>
# USAGE:        Open source code <br>
# COMMENTS:     none <br>
# VERSION:      1.1  <br>
# <br>
############################################################################# <br>

   <code>ParserBasic</code> contains constants used by the parsers ({@link DCFileParser}, {@link RuleMLFileParser}) as delimiters. 
   
*/
public class ParserBasic{
	/**
	 * "(" */
  public static final String LEFTPAREN = "(";
  /**
	 * ")" */
  public static final String RIGHTPAREN = ")";
  /**
	* "[" */
  public static final String LEFTBRACKET = "[";
   /**
	* "]" */
  public static final String RIGHTBRACKET = "]";
   /**
	* "." */
  public static final String PERIOD = ".";
   /**
	* "," */
  public static final String COMMA = ",";
   /**
	* "%" */
  public static final String PERCENT = "%";
   /**
	* "&#43" */
  public static final String PLUS = "+";
   /**
	* "-" */
  public static final String MINUS = "-";
   /**
	* "\t" */
  public static final String TAB = "\t";
   /**
	* " " */
  public static final String SPACE = " ";
   /**
	* "<" */
  public static final String LESS = "<";
   /**
	* ">" */
  public static final String GREATER = ">";
   /**
	* ":" */
  public static final String COLON = ":";
   /**
	* TAB &#43 SPACE */
  public static final String WHITE = TAB + SPACE;
   /**
	* "'" */
  public static final String SINGLE_QUOTE = "'";
  /**The valid delimiter is "(", ")", "[", "]", ".", ",", "%",
     "&#43", "-", "\t"(tab), " ", "&lt"(less than), "&gt"(greater than), ":", "'".*/
  public static final String DELIMITERS = 
    LEFTPAREN + RIGHTPAREN + LEFTBRACKET + 
    RIGHTBRACKET + PERIOD + COMMA + PERCENT + PLUS + MINUS + 
    TAB + SPACE + LESS + GREATER + COLON + SINGLE_QUOTE;
     /**
	* "\" */
  public static final String ESCAPE = "\\";

  /**
     <code>delimiter</code> returns true if the String argument
     given is a {@link #DELIMITERS delimiter}. 

     @param s - a <code>String</code> value
     @return a <code>boolean</code> value */
  public static boolean delimiter(String s){
    return s.length() == 1 & 
      DELIMITERS.indexOf(s.charAt(0))>=0;
  }

  /**
     <code>containsDelimiters</code> returns true if s contains {@link #DELIMITERS delimiters}.

     @param s - a <code>String</code> value
     @return a <code>boolean</code> value
  */
  public static boolean containsDelimiters(String s){
    for(int i = 0; i < s.length(); i++)
      if (DELIMITERS.indexOf(s.charAt(i))>=0) 
	return true;
    return false;
  }

}




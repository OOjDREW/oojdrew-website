
package jDREW.util;



import java.io.*;
import java.util.*;

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

  Create a stream of tokens and delimiters from a file or a stream.
  Provides peekToken as well as nextToken.
  Delimiters are always one character Strings.
  Comment lines in files are ignored.  They are denoted as %...<eol>
  Items within single quotes are taken as one token.
  A token type is also provided, taking possible values of
  STRING, QUOTED_STRING and DELIMITER.

  White space is ignored (Tab and space characters) and
  newline characters are ignored.

  
 */

public class TokenStream  {

  /** An inner class for the tokens and their types */
  public class Token{
  	
  	/** 
  	 * Creates a Token instance.
  	 * @param tokenName a <code> String </code> value specifying the name of the token.
  	 * @param tokenType a <code> int </code> value specifying the type of the token. Three types of tokens are {@link TokenStream#STRING}, {@link TokenStream#QUOTED_STRING} and {@link TokenStream#DELIMITER}.
  	 */
    public Token(String tokenName, int tokenType){
      this.tokenName = tokenName;
      this.tokenType = tokenType;
    }
	/** 
  	 * Creates a {@link TokenStream#STRING} type Token instance.
  	 * @param tokenName a <code> String </code> value specifying the name of the token.
  	 */
    public Token (String tokenName){
      this(tokenName, STRING);
    }
	/**
	 * The name of this Token that is specifyed by the constructor
	 */
    String tokenName;
    /**
	 * The type of this Token that is specifyed by the constructor
	 */
    int tokenType;
	/**
	 * Returns the value of the {@link #tokenName}. 
	 * @return the value of the {@link #tokenName}. 
	 */
    public String getTokenName(){
      return tokenName;
    }
/**
	 * Returns the value of the {@link #tokenType}. 
	 * @return the value of the {@link #tokenType}. 
	 */
    public int getTokenType(){
      return tokenType;
    }
/**
   *    <code>toString()</code> is a method to get a string representatin of
   *        this Token which is composed of the {@link #tokenName} and {@link #tokenType}. 
   *
   *    @retrun A <code>String</code> value containing the string representation
   *        of the DefiniteClause.
   */
    public String toString(){
      String tokenTypeName = "";
      if(tokenType == 0)
	tokenTypeName = "STRING";
      else if(tokenType == 1)
	tokenTypeName = "QUOTED_STRING";
      else if (tokenType == 2)
	tokenTypeName = "DELIMITER";
      return "Token[ tokenName = " + tokenName +
	", tokenType = " + tokenTypeName + "]";
    }
  }
  /** A constant variable denote that the token stream is built on a file	*/
  public static final int FILE_MODE = 0;
  /** A constant variable denote that the token stream is built on a String that only holds one formular*/
  public static final int STRING_MODE = 1;
  //added by jkp
  /** A constant variable denote that the token stream is built on a Stream	that holds mulitple formulae*/
  public static final int STREAM_MODE = 2;
  //end of "added by jkp"
  /** 
   * An <int>int</int> value indicating the current mode of the token stream. The value can be {@link #FILE_MODE}, {@link #STRING_MODE} or {@link #STREAM_MODE}.
   */
  private int mode;

  /** Token types, indicating a plain String */
  public static final int STRING = 0;
  /** Token types, indicating a string with single quotes on both ends */
  public static final int QUOTED_STRING = 1;
  /** Token types, indicating a delimiter specified by the parameter <code> delimiters </code> in the  {@link #TokenStream(String,String,int) constructor} of the <code> TokenStream </code>*/
  public static final int DELIMITER = 2;
 /** 
  * Used with the {@link #br} to read the {@link #STREAM_MODE} input.
  */
  private StringReader sr;//added by jkp
  /** 
  * Used with the {@link #br} to read the {@link #FILE_MODE} input.
  */
  private FileReader fr;
  /** 
  * Used with the {@link #fr} to read the {@link #FILE_MODE} input or Used with the {@link #sr} to read the {@link #STREAM_MODE} input.
  */ 
  private BufferedReader br;
  /**
   * Stores the current read line.
   */
  private String st;
  /**
   * A <code> StringTokenizer </code> object instantialized in the {@link #TokenStream(String,String,int) constructor} of the TokenStream 
   * 
   */
  private StringTokenizer stt;
  /**
   * A <code> boolean </code> variable indicating if there exists an unconsumed token returned by the previous call of {@link #hasMoreTokens}.
   */
  private boolean peekedAhead;
  /** 
   * A temporary <code> Token </code> variable used in various places in the TokenStream class.
   */ 
  private Token token;
  /**
   * A <code> boolean </code> variable indicating whether the end of the input stream has been reached.
   */
  private boolean atEOF;
  /**
   * Holds a string of delimiters used for the purpose of tokenization.
   */
  private String delimiters;

  /**
     Useful for error reporting.  Keeps track of the current read line.
   */
  private int lineNumber;
  /**
     return the line number of the current read line.
   */
  public int getLineNumber () {
    return lineNumber;
  }

  /**
   * Generates a TokenStream instance.
     @param inString If the <code>mode</code> is {@link #FILE_MODE}, the <code>inString</code> is the name of the file to be tokenized,
     if the <code>mode</code> is {@link #STRING_MODE} or {@link #STREAM_MODE},the <code>inString</code> is the string itself that contains a formula or formulae.
     @param delimiters a String of characters that each acts as a delimiter. for example {@link ParserBasic#DELIMITERS}.
     @param mode an <code> int </code> value specifying the type of the input.
     @throws IOException if the input specifyed by the <code> inString </code> is not available or accessible.
  */
  public TokenStream(String inString, String delimiters, int mode) throws IOException {
    this.delimiters = delimiters;
    this.mode = mode;
    if(mode == FILE_MODE){
      fr = new FileReader(inString);
      br = new BufferedReader(fr);
      st = br.readLine();
      lineNumber = 1;
      atEOF = false;
    }
    else if(mode == STRING_MODE){
      st = inString;
      lineNumber = 0;
      atEOF = true;
    }
    //added by jkp
    else if(mode == STREAM_MODE){
      sr = new StringReader(inString);
      br = new BufferedReader(sr);
      st = br.readLine();
      lineNumber = 1;
      atEOF = false;
    }
    //end of "added by jkp"
    stt = new StringTokenizer(st, delimiters, true);
    peekedAhead = false;
  }



  /**
   * Returns the next non-white, non-commented token in the file.
     @return the next non-white, non-commented token in the file.
     Delimiters are single-character strings, as defined in
     ParserConstants. A single-quote-delimited string is also a token.
     @return true is the stream has more tokens
     @see ParserBasic#DELIMITERS
   */
  public boolean hasMoreTokens() throws IOException, ParseException{
    if (peekedAhead){
      return true;
    }
    else if (stt.hasMoreTokens()){
      String tempTokenName = stt.nextToken();
      if(tempTokenName.compareTo(ParserBasic.SINGLE_QUOTE)==0){
	StringBuffer sb = new StringBuffer();
	boolean quoting = true;
	while(quoting && stt.hasMoreTokens()){
	  tempTokenName = stt.nextToken();
	  if(tempTokenName.compareTo(ParserBasic.SINGLE_QUOTE)==0)
	    quoting = false;
	  else{
	    sb.append(tempTokenName);
	  }
	}
	if(quoting)
	  throw new ParseException
	    ("End of token stream within a quoted expression.");
	token = new Token(new String(sb), QUOTED_STRING);
	peekedAhead = true;
	return true;
      }
      else if(tempTokenName.equals(ParserBasic.TAB)
	      ||tempTokenName.equals(ParserBasic.SPACE)){
	//skip white space
	peekedAhead=false;
	return hasMoreTokens();
      }
      else if (tempTokenName.equals(ParserBasic.PERCENT)) {
	//throw out current stringTokenize
	//no processing of comments is done
	stt = new StringTokenizer("");
	return hasMoreTokens();
      }
      else {
	peekedAhead = true;
	int tempTokenType = STRING;
	if(ParserBasic.delimiter(tempTokenName))
	  tempTokenType = DELIMITER;
	token = new Token(tempTokenName, tempTokenType);
	return true;
      }
    }
    else if (mode == STRING_MODE) {
      return false;
    }
    //mode = FILE_MODE or STREAM_MODE
    else if (atEOF) {
      return false;
    }
    else {//get the next line
      st = br.readLine();
      if (st == null) {
      	if(mode==FILE_MODE)//added by jkp
			fr.close();
		atEOF = true;
		return false;
      }
      else {
		lineNumber++;
		stt = new StringTokenizer(st, delimiters, true);
		return hasMoreTokens();
      }
    }
  }

  /**
   * Returns the next token but do not consume the next token in the input stream
     @return the next token but do not consume the next token in the input stream
   */
  public Token peekToken() throws IOException, ParseException{
    if (hasMoreTokens())
      return token;
    else{
      throw new NoSuchElementException("No token can be found.");
    }
  }
      

  /**
   * Returns the next token and consume the next token in the input stream
     @return the next token and consume the next token in the input stream
   */
  public Token nextToken() throws IOException, ParseException {
    if(hasMoreTokens()){
      peekedAhead = false;
      return token;
    }
    else{
      throw new NoSuchElementException("No token can be found.");
    }
  }

  public static void main(String args[]){
    try{
      TokenStream 
      t = 
	new TokenStream
	  ("top < - regular('Ho nda'). %fref", 
	   ParserBasic.DELIMITERS, STRING_MODE);
      while(t.hasMoreTokens()){
	System.out.println("Token is:" + t.nextToken());
      }
    } catch (IOException e){
      e.printStackTrace();
    } catch (ParseException e){
      e.printStackTrace();
    }
  }

} // TokenStream

  

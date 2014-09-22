
package jDREW.util;



import java.util.*;
import jDREW.TEST.*; 
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

   DCFileParser is constucted given a file name to parse.  It
   creates a Vector of DefiniteClauses contained in that file.
   As it does so, it populates a SymbolTable with the symbols it
   finds.

   The Definite Clauses that are created are stored in a private
   Vector and available via the iterator, or getIputClause.

  Context-free grammar for DC files
  <pre>
    file ::= EOF
    file ::= defclause file
    defclause ::= atom '.'
    defclause ::= atom ':-' '.'
    defclause ::= atom ':-' atom atomlist 
    atomlist ::= '.'
    atomlist ::= ',' atom atomlist 
    atom ::= term            
    %it is not really a term, but is parsed as such
    term ::= functor
    term ::= functor '(' termlist
    termlist ::= ')'
    termlist ::= term termlistcont
    termlistcont ::= ')'
    termlistcont ::= ',' term termlistcont
    </pre>
    

    DCFileParsers are not "re-entrant".  In other words, if
    parsing a file is interrupted and we start parsing a
    different file, then continuing parsing the old file is
    messed up. Of course if one wants to parse a different file,
    one should create a new DCFileParser for just that file.

  */
public class DCFileParser {


  /**
     Creates a new <code>DCFileParser</code> instance into which 
     definite clauses can be placed.

     @param symbolTable - a <code>{@link SymbolTable}</code> value 
     that is used to hold the symbols generated during the parsing of the file
  */
  public DCFileParser(SymbolTable symbolTable) {
    inputClauses = new Vector();
    this.symbolTable = symbolTable;
  }

  /**
     The variable <code>inputClauses</code> is a Vector of
     DefiniteClause's.  A clause parsed from the file is then
     placed into the vector into the next availabile position, as
     returned by <code>{@link #inputClausesSize}</code>.  
  */
  private Vector inputClauses; 


  /**
     The variable <code>symbolTable</code> refers to the
     {@link SymbolTable} that was given at construction time and that is
     receiving symbols as they are being parsed.  */
  private SymbolTable symbolTable;

  /**
     <code>inputClausesSize</code> returns the current size of
     the {@link #inputClauses} Vector, which is taken as the next available
     position for inserting a new clause.  This is correct
     because Vectors index from zero.

     @return an <code>int</code> value representing the size*/
  public int inputClausesSize(){
    return inputClauses.size();
  }
  
  /**
     <code>getInputClause</code> returns the DefiniteClause at
     position i in clauses.  It also does the cast to
     DefiniteClause from the Vector.

     @param i - an <code>int</code> value denote the position of the definite clause in the {@link #inputClauses} vector.
     @return a <code>DefiniteClause</code> value */
  public DefiniteClause getInputClause(int i){
    return (DefiniteClause) inputClauses.get(i);
  }

  /**
     <code>iterator</code> returns the iterator through the
     clauses that were read.

     @return an <code>Iterator</code> value, giving all
     DefiniteClauses that were read.  */
  public Iterator iterator(){
    return inputClauses.iterator();
  }



//Added by jkp
  
/**
     <code>parseDCStream</code> performs a parse of a definite
     clause stream and places the symbols it encounters into the
     symbolTable.

     @param stream - a <code>String</code> value that holds the definite clauses being parsed.
     
     @return an <code>String</code> value, giving the error message,
     null is returned if there is no error  */
  public String parseDCStream(String stream){
    try{
      t = new TokenStream(stream,
			  ParserBasic.DELIMITERS,
			  TokenStream.STREAM_MODE);

      Count c = parseDCFile();
      return null;

    } catch (IOException e){
      return ("IO Error while reading DC Stream at line number " +
			 t.getLineNumber() + " " + e.getMessage());

    } catch (ParseException e){
      return ("IO Error while parsing DC Stream at line number " +
			 t.getLineNumber() + " " + e.getMessage());
    }

  }

  /**
     <code>parseDefiniteClause</code> parses out a definite clause but
     from a String instead of from the current token stream. 
     @param clauseString - a <code>String</code> value holds a single definite clause
     @return a <code>DefiniteClause</code> value
     @exception ParseException if an error occurs */
  public DefiniteClause parseQueryClause(String clauseString) 
    throws ParseException {
    try{
      t = new TokenStream
	("$top:-" + clauseString, ParserBasic.DELIMITERS, 
	 TokenStream.STRING_MODE);
      currentClause = new Vector();
      varTable = new Hashtable();
      varCount = 0;
      atomCount = 0;
      Count c = parseAtom(); //Consume "$top" that was added to the string
      insistDelimiter(ParserBasic.COLON);//Consume ":"
      insistDelimiter(ParserBasic.MINUS);//Consume "-"
      c.plus(parseAtom().plus(
			      parseAtomList()));
    }
    catch (IOException e){
      //can't happen - TokenStreams from Strings aren't liable to cause file errors.
    }
    DefiniteClause dc = 
      new DefiniteClause(currentClause, varTable, symbolTable);
    varTable = null;
    currentClause = null;
    return dc;
  }


  /**
     <code>parseDefiniteClause</code> parses out definite clauses but
     from a String instead of from the current token stream. 
     @param clauseString - a <code>String</code> value holds definite clauses.
     @return a <code>DefiniteClause</code> value
     @exception ParseException if an error occurs */
  public DefiniteClause parseDefiniteClause(String clauseString) 
    throws ParseException {
    try{
      t = new TokenStream
	(clauseString, ParserBasic.DELIMITERS, 
	 TokenStream.STRING_MODE);
      currentClause = new Vector();
      varTable = new Hashtable();
      varCount = 0;
      atomCount = 0;
      Count c = parseAtom(); 
      if(!matchDelimiter(ParserBasic.PERIOD)){
	insistDelimiter(ParserBasic.COLON);
	insistDelimiter(ParserBasic.MINUS);
	if(!matchDelimiter(ParserBasic.PERIOD)){
	  c.plus(parseAtom().plus(
				  parseAtomList()));
	}
      }
    }
    catch (IOException e){
      //can't happen - TokenStreams from Strings aren't liable to cause file errors.
    }
    DefiniteClause dc = 
      new DefiniteClause(currentClause, varTable, symbolTable);
    varTable = null;
    currentClause = null;
    return dc;
  }



  /**
     <code>parseDCFile</code> performs the parse of a definite
     clause file and places the symbols it encounters into the
     symbolTable.

     @param fileName - a <code>String</code> value for the name
     of the file in ".dc" format to parse */
  public void parseDCFile(String fileName){
    try{
      t = new TokenStream(fileName,
			  ParserBasic.DELIMITERS, 
			  TokenStream.FILE_MODE);
      
      Count c = parseDCFile();

    } catch (IOException e){
      if(t != null)
	System.err.println("IO Error while reading DC File " +
			   fileName + " at line number " + 
			   t.getLineNumber() + " " + e.getMessage());
      else
	System.err.println("IO Error while reading DC File " +
			   fileName + " " + e.getMessage());
      System.exit(-1);
    } catch (ParseException e){
      System.err.println("Error while parsing DC File " +
			 fileName + " at line number " + 
			 t.getLineNumber() + " " + e.getMessage());
      System.exit(-1);
    }

  }
  /**
   * An <code> Hashtable </code> variable containing the variable name of distinct variables of the clause currently being parsed.
   */
  private Hashtable varTable;
  /**
   * An <code> int </code> variable containing the number of distinct variables we have seen so far as the current clause is being parsed.
   */
  private int varCount;
  /**
   * An <code> int </code> variable containing the number of atoms in the clause that is currently being parsed.
   */
  private int atomCount;
  /**
   * An {@link TokenStream} variable that holds the tokenized stream of the clauses. All the parsing operations are carried out on this tokenized stream.
   */ 
  private TokenStream t;
  /**
   * An <code> Vector </code> variable containing the flatterm table of the clause that is currently being parsed.
   */
  private Vector currentClause;

  /** 
      This is similar to interSymbol from {@link SymbolTable}, but
      the hash table vhTable of variables is created anew for
      each clause, and there is no need to have a table of print
      names for variables since we do not intend to print them
      with their original names.  Thus internVar is a quick way
      to count the number of distinct vars we have seen so far in
      the clause, and to identify the ones in common. The count
      starts at 1.
      @param symbol the newly read token identifying a variable, which
      is decided by its upper case first letter and the fact that it
      is not a quoted string
      @return an integer identifying this variable's position in
      the list of variables seen so far in this clause.  This
      list is chronologically ordered from 1.  
      
      Note: In Dec 2001 we decided to store the print names
      of the variables.
  */
  private int internVar(String symbol){
    Object varPos = varTable.get(symbol);
    if(varPos==null){ //This is a new variable.
      varCount++;
      varTable.put(symbol, new Integer(varCount));
      return varCount;
    }
    else
      return ((Integer)varPos).intValue();
  }


  /**
     <code>parseDCFile</code> starts a recursive descent parser to
     pull out the DefiniteClauses from a file.

     The overall strategy of counters here is to be able to
     return multiple counts or statistics from the parsing
     routines.  In particular we want to know the number of
     symbols (not number of characters) of a given term (or
     clause, or atom) and also the arity of that term. Thus two
     counters are used as we proceed down the term.  This pair of
     counters is returned from each method call.

     @return a <code>Count</code> value
     @exception ParseException if an error occurs
     @exception IOException if an error occurs */
  private Count parseDCFile() throws ParseException,IOException {
    if(!t.hasMoreTokens())
      return new Count(0);
    else {
      return parseDefiniteClause().plus(parseDCFile());
    }
  }
 
  


  /**
     <code>parseAtomList</code> performs a recursive descent parse of
     a list of atoms, separated by commas and terminated with a period.

     @return a <code>Count</code> value telling the number of symbols
     in the atoms list
     @exception ParseException if an error occurs
     @exception IOException if an error occurs
  */
  private Count parseAtomList() throws ParseException,IOException {
    if(matchDelimiter(ParserBasic.PERIOD))
      return new Count(0);
    else{
      insistDelimiter(ParserBasic.COMMA);
      return 
	parseAtom().plus(
			 parseAtomList());
    }
  }
  /**
     <code>parseAtom</code> performs a recursive descent parse of a single
     atom.  It also adds one to the atomCount, which eventually is reported 
     to the clause.

     @return a <code>Count</code> value
     @exception ParseException if an error occurs
     @exception IOException if an error occurs
  */
  private Count parseAtom() throws ParseException,IOException {
    atomCount++;
    return parseTerm();
  }

  /**
     <code>parseTerm</code> performs a recursive descent of a
     term.  This also involves discovering two things: the number
     of symbols in the term, and the number of arguments for this
     term.  The number of symbols is used in the structure that
     represents terms.  The number of arguments, or arity, is
     used in the symbol table.

     @return a <code>Count</code> value
     @exception ParseException if an error occurs
     @exception IOException if an error occurs */
  private  Count parseTerm() throws ParseException,IOException {
    if(peek().getTokenType() == TokenStream.DELIMITER) 
      throw new ParseException("Unexpected Delimiter " + peek());
    TokenStream.Token token = next();
    if(Character.isUpperCase(token.getTokenName().charAt(0)) && 
        token.getTokenType() != TokenStream.QUOTED_STRING){
      //the term is a variable
      int i = internVar(token.getTokenName());
      int[] entry = {-i, 1};
      currentClause.addElement(entry);
      return new Count(1,1);
    }
    else {
      //the term is not a variable
      Count c; // will count both the length of this term and the arity
      int[] entry = {0, 0}; //added to current clause, we will update
      //these two positions in this array so the current clause stores
      //the symbol table index and the length
      currentClause.addElement(entry);
      if(matchDelimiter(ParserBasic.LEFTPAREN)){
	c = parseTermList().plus(1,0);//add one to reflect this token
	int i = symbolTable.
	  internSymbol(token.getTokenName(), c.secondValue());//put arity in symbol
	entry[0]=i;
	entry[1]=c.firstValue();//number of symbols in this term
      }
      else {
	//this term is simply a constant -- no arguments
	c = new Count(1,0);
	int i = symbolTable.internSymbol(token.getTokenName(), 0);
	//put arity in symbol table
	entry[0] = i;
	entry[1] = 1;//just one symbol in the term
      }
      c.assignSecond(1);//now that we have the inner arity of this term term, we
      //set the arity to 1 so that we report that just one term has been parsed
      return c;
    }
  }

  /**
     <code>parseDefiniteClause</code> performs a recursive descent
     along the token stream t, creating a new Clause.  Since
     variables in clausal logic are clause-local, we create a new
     Hashtable for variables.  Instead of creating a
     DefiniteClause directly in the descent, we create a Vector,
     currentClause, of symbols.  When the end of the clause is
     encountered, we create the DefiniteClause, which also
     contains the count of atoms and the number of variables.

     @return a <code>Count</code> value
     @exception ParseException if an error occurs
     @exception IOException if an error occurs */
  private Count parseDefiniteClause() throws ParseException,IOException {
    currentClause = new Vector();
    varTable = new Hashtable();
    varCount = 0;
    atomCount = 0;
    Count c = parseAtom();
    if(!matchDelimiter(ParserBasic.PERIOD)){
      insistDelimiter(ParserBasic.COLON);
      insistDelimiter(ParserBasic.MINUS);
      if(!matchDelimiter(ParserBasic.PERIOD)){
	c.plus(parseAtom().plus(
				parseAtomList()));
      }
    }
    inputClauses.add(new DefiniteClause(currentClause, varTable, symbolTable));
    varTable = null;
    currentClause = null;
    return c; 
  }


  
  /**
     <code>parseTermList</code> performs a recursive descent of a
     list of terms, starting with a term, separated by commas and
     terminated with a right parenthesis.

     @return a <code>Count</code> value storing both number of symbols
     and number of terms parsed.
     @exception ParseException if an error occurs
     @exception IOException if an error occurs */
  private Count parseTermList() throws ParseException,IOException {
    if(matchDelimiter(ParserBasic.RIGHTPAREN))
      return new Count(0, 0);
    else {
      return 
	parseTerm().plus(
			 parseTermListCont());
    }
  }

  /**
     <code>parseTermListCont</code> performs a recursive descent of a
     list of terms, comma or right parenthesis, separated by commas and
     terminated with a right parenthesis.

     @return a <code>Count</code> value
     @exception ParseException if an error occurs
     @exception IOException if an error occurs
  */
  private  Count parseTermListCont() throws ParseException,IOException {
    if(matchDelimiter(ParserBasic.RIGHTPAREN))
      return new Count(0, 0);
    else {
      insistDelimiter(ParserBasic.COMMA);
      return 
	parseTerm().plus(
			 parseTermListCont());
    }
  }

  /**
     <code>match</code> tells if the next token is a match with the argument.

     @param s - a <code>String</code> value
     @return a <code>boolean</code> value
     @exception ParseException if an error occurs
     @exception IOException if an error occurs
  */
  private  boolean matchDelimiter(String s) throws ParseException, IOException{
    if(!t.hasMoreTokens())throw new 
      ParseException("Unexpectedly reached the end of the token stream.");
    TokenStream.Token tempToken = t.peekToken();
    if(tempToken.getTokenName().equals(s) & 
       tempToken.getTokenType() == TokenStream.DELIMITER){
      t.nextToken();
      return true;
    }
    else
      return false;
  }

  /**
     <code>insist</code> performs a check that the next token is a match
     with the argument.  Otherwise an exception is thrown.

     @param s - a <code>String</code> value
     @exception ParseException if an error occurs
     @exception IOException if an error occurs
  */
  private  void insistDelimiter(String s) throws ParseException, IOException{
    TokenStream.Token tempToken = t.nextToken();
    if(tempToken.getTokenName().equals(s) &&
       tempToken.getTokenType() == TokenStream.DELIMITER)
      return;
    else
      throw new ParseException("Expected " + s + 
			       " in input, but found " + tempToken + 
			       " on line " + t.getLineNumber());
  }

  /**
     <code>next</code> tells the next token in the token input
     stream, consuming it.

     @return a <code>String</code> value
     @exception ParseException if an error occurs
     @exception IOException if an error occurs */
  private TokenStream.Token next() throws ParseException, IOException{
    if(!t.hasMoreTokens())throw new 
      ParseException("Unexpectedly reached the end of the token stream.");
    return t.nextToken();
  }

  /**
     <code>peek</code> tells the next token in the token input
     stream, not consuming it.  It is assumed that one token look-ahead 
     is sufficient.

     @return a <code>String</code> value
     @exception ParseException if an error occurs
     @exception IOException if an error occurs */
  private  TokenStream.Token peek() throws ParseException, IOException{
    if(!t.hasMoreTokens())throw new 
      ParseException("Unexpectedly reached the end of the token stream.");
    return t.peekToken();
  }

  public  static void main(String args[]){
    SymbolTable st = new SymbolTable();
    DCFileParser fp = new DCFileParser(st);
    fp.parseDCFile(EnvTool.localPath+"TEST\\test.dc");
    
    System.out.println("SymbolTable");
    for(int i = 0; i < st.size(); i++){
      System.out.println(i + "=>" + st.printName(i));
    }
    System.out.println("Reconstruction:");
    for(int i = 0; i < fp.inputClauses.size(); i++){
      boolean debugging = false;
      DefiniteClause clause = (DefiniteClause) fp.inputClauses.get(i);
      if(debugging){
	for(int j = 0; j < clause.symbols.length; j++){
	  if(clause.symbols[j][0] < 0){
	    System.out.println("V" + (-1-clause.symbols[j][0]));
	    
	  }
	  else
	    System.out.println(fp.symbolTable.printName(clause.symbols[j][0]) + 
			       " " + clause.symbols[j][1]);
	}
	System.out.println("End of clause " + i);
      }
      System.out.println(clause.toString());
    }

/* modified by jkp
    fp.parseDCFile("gina.dc");
    

    System.out.println("SymbolTable");
    for(int i = 0; i < st.size(); i++){
      System.out.println(i + "=>" + st.printName(i));
    }
    System.out.println("Reconstruction:");
    Iterator it = fp.iterator();
    while(it.hasNext()){
      System.out.println(it.next());

    }*/
  }

}

/** 
    Count is a multi-place object that stores counts.  In this
    file it is used by the parser to keep track, in the first
    argument,  of the length of items in a clause, and in the
    second argument the arity of the current function symbol
    or predicate symbol.

    */

class Count{
  /** An <code> int </code> variable used to store index values  */
  private int i;
  /** An <code> int </code> variable used to store index values  */
  private int j;

  /** Create a counter with a specified first index value, and
     the default value of zero in the other indexes */
  Count(int i) {
    this.i = i;
  }
  /** Create a counter with a specified first and second index values, and
     the defalut value of zero in the other indexes */
  Count(int i, int j) {
    this.i = i;
    this.j = j;
  }
  /** Project the first value */
  int firstValue(){
    return i;
  }
  /** Project the second value */
  int secondValue(){
    return j;
  }
  /** Add the indexes of two counters, item by item*/
  Count plus(Count addend){
    i+=addend.firstValue();
    j+=addend.secondValue();
    return this;
  }
  /** Add a given value to the first item */
  Count plus(int i){
    this.i+=i;
    return this;
  }
  /** Add a given value to the first and second items */
  Count plus(int i, int j){
    this.i+=i;
    this.j+=j;
    return this;
  }
  /** Give a value to the second index */
  Count assignSecond(int j){
    this.j=j;
    return this;
  }
}





package jDREW.util;



import java.util.*;
import java.io.*;
import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*;


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

   RuleMLFileParser is constucted given a file name to parse.  It
   creates a Vector of DefiniteClauses contained in that file.
   As it does so, it populates a SymbolTable with the symbols it
   finds.

   The Definite Clauses that are created are stored in a private
   Vector and available via the iterator, or getIputClause.

  */
public class RuleMLFileParser {

/**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String IND = "ind";
  /**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String VAR = "var";
  /**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String _OPR = "_opr";
  /**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String REL = "rel";
  /**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String IMP = "imp";
  /**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String FACT = "fact";
  /**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String _HEAD = "_head";
  /**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String _BODY = "_body";
  /**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String ATOM = "atom";
  /**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String AND = "and";
  /**
   * Constant variable that is used to identify and parse the RuleML tag.
*/
  public static final String RULEBASE = "rulebase";

  /**
     Creates a new <code>RuleMLFileParser</code> instance into which 
     definite clauses can be placed.

     @param symbolTable - a <code>SymbolTable</code> that will be used to hold all the symbols of the DefiniteClauses being parsed.
  */
  public RuleMLFileParser(SymbolTable symbolTable) {
    inputClauses = new Vector();
    this.symbolTable = symbolTable;
  }

  /**
     The variable <code>inputClauses</code> is a Vector of
     DefiniteClause's.  A clauses parsed from the file is then
     placed into the vector into the next availabile position, as
     returned by {@link #inputClausesSize}.  
  */
  private Vector inputClauses; 


  /**
     The variable <code>symbolTable</code> refers to the
     SymbolTable that was given at construction time and that is
     receiving symbols as they are being parsed.  */
  private SymbolTable symbolTable;

  /**
     <code>inputClausesSize</code> returns the current size of
     the inputClauses Vector, which is taken as the next available
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

     @param i - an <code>int</code> value
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

  /**
     <code>parseDefiniteClause</code> parses out a definite clause but
     from a String instead of from the current token stream. 
     At this moment the query comes in as a Prolog-like definite clause.
     @param clauseString - a <code>String</code> value
     @return a <code>DefiniteClause</code> value
     @exception ParseException if an error occurs */
  public DefiniteClause parseQueryClause(String clauseString) 
    throws ParseException {
    DCFileParser dcfp = new DCFileParser(symbolTable);
    return dcfp.parseQueryClause(clauseString);
  }
 /**
  * Compares two Strings ignoring the case.
  * @param s1 -the first String being compared.
  * @param s1 -the second String being compared.
  * @return true if two strings are equal.
  */
  private boolean compare(String s1, String s2) {
    return s1==null?s2==null:s1.equalsIgnoreCase(s2);
  }

  /**
     <code>parseRuleMLFile</code> performs a parse of a definite
     clause file and places the symbols it encounters into the
     symbolTable.

     @param fileName - a <code>String</code> value for the name
     of the file in ".ruleml" format to parse. */
  public void parseRuleMLFile(String fileName) throws JDOMException{
    // Build w/ SAX and Xerces, no validation
    SAXBuilder b = new SAXBuilder();
    // Create the document
    Document doc = null;
    try{
        doc = b.build(new File(fileName));
    }catch(Exception e){
        e.printStackTrace();
        return;
    }
    //      XMLOutputter outputter = new XMLOutputter("", true);
    //      try{
    //        outputter.output(doc, System.out);
    //      }catch(IOException e){e.printStackTrace();};
    Element root = doc.getRootElement();
    importDocumentRoot(root);
  }
  
  //Added by Marcel A. Ball - NRC - 2003
  /**
   *    <code>parseRuleMLDocument(Document doc)</code> allows the user to parse
   *    a RuleML file contained in a JDOM Document object, this is useful if 
   *    transformations must be done on the document before parsing.
   *
   *    @param doc - The <code>org.jdom.Document</code> containing the RuleML
   *    document, should have a rulebase element as it's root element.
   */
  public void parseRuleMLDocument(Document doc) throws JDOMException{
    Element root = doc.getRootElement();
    importDocumentRoot(root);
  }
  //End Add
  
  //Added by Marcel A. Ball - NRC - 2003
  /**
   *    <code>parseRuleMLClause(Element el)</code> is a method to parse a RuleML
   *    clause from a JDOM Element Object (The Element should start at the fact
   *    or imp level.
   *
   *    @param el - The <code>org.jdom.Element</code> containing the clause to be
   *    parsed.
   *    @return A <code>DefiniteClause</code> object representing the clause.
   */
  public DefiniteClause parseRuleMLClause(Element el)throws JDOMException, ParseException{
    if(el.getName().equals(IMP)){
        currentClause = new Vector();
        vTable = new Hashtable();
        varCount = 0;
        atomCount = 0;
        Element headElement = el.getChild(_HEAD);
        importHead(headElement);
        Element bodyElement = el.getChild(_BODY);
        importBody(bodyElement);
        DefiniteClause dc = new DefiniteClause(currentClause, vTable, symbolTable);
        vTable = null;
        currentClause = null;
        return dc;
    }
    else if(el.getName().equals(FACT)){
        currentClause = new Vector();
        vTable = new Hashtable();
        varCount = 0;
        atomCount = 0;
        Element headElement = el.getChild(_HEAD);
        importHead(headElement);
        DefiniteClause dc = new DefiniteClause(currentClause, vTable, symbolTable);
        vTable = null;
        currentClause = null;  
        return dc;
    }
    else
    {
        throw new ParseException("Passed element is not a imp or fact.");
    }
  }
  //End Add
  /**
   * Prints out an <code> Element </code> and all its children.
   * @param elem the <code> Element </code> that is being printed.
   */
  private void dump(Element elem){
    System.out.println("dumping " + elem);
    System.out.print("children are :");
    List list = elem.getChildren();
    Iterator li = list.iterator();
    while(li.hasNext()){
      Element o = (Element) li.next();
      System.out.println("Child " + o);
    }
  }
 /**
  * The starting point of the RuleML parsing. It simply calls the {@link #importRulebase}.
  * @param elem an <code>Element</code> value indicating the root of the document that is being parsed.
  */
  private void importDocumentRoot(Element elem){
//      List list = elem.getChildren(RULEBASE);
//      for(Iterator it = list.iterator(); it.hasNext();){
//        Element next = (Element) it.next();
//        importRulebase(next);
//      }
    importRulebase(elem);
  }
 /**
  * Parses all the elements under the <code> Element </code> elem and changes and stores them in the internal form of DefiniteClauses.
  */
  private void importRulebase(Element elem){
    List list = elem.getChildren();
    for(Iterator it = list.iterator(); it.hasNext();){
      Element next = (Element) it.next();
      if(next instanceof Element){
	if(compare(next.getName(),IMP)){
	  importImp(next);
	}
	else if (compare(next.getName(),FACT)){
	  importFact(next);
	}
      }
      else{
	//ignore elements that are not IMP or FACT
      }
    }
  }
  /**
   * Parses the {@link #IMP} element.
   * @param elem an IMP <code>Element</code> value that is being parsed.  
   */
  private void importImp(Element elem){
    //      System.out.println("Dumping imp" );
    //      dump(elem);
    currentClause = new Vector();
    vTable = new Hashtable();
    varCount = 0;
    atomCount = 0;
    Element headElement = elem.getChild(_HEAD);
    importHead(headElement);
    Element bodyElement = elem.getChild(_BODY);
    importBody(bodyElement);
    inputClauses.add(new DefiniteClause(currentClause, vTable, symbolTable));
    vTable = null;
    currentClause = null;
  }
/**
   * Parses the {@link #_BODY} element.
   * @param elem an _BODY <code>Element</code> value that is being parsed.  
   */
  private void importBody(Element elem){
    //      System.out.println("Dumping body");
    //      dump(elem);
    Element atomElement = elem.getChild(ATOM);
    if(atomElement != null){
      importAtom(atomElement);
    }
    Element conjElement = elem.getChild(AND);
    if(conjElement != null){
      List list = conjElement.getChildren(ATOM);
      for(Iterator it = list.iterator(); it.hasNext();){
	Element next = (Element) it.next();
	//  	System.out.println("Dumping what should be an atom?");
	//  	dump(next);
	importAtom(next);
      }
    }
  }
/**
   * Parses the {@link #FACT} element.
   * @param elem an FACT <code>Element</code> value that is being parsed.  
   */
  private void importFact(Element elem){
    currentClause = new Vector();
    vTable = new Hashtable();
    varCount = 0;
    atomCount = 0;
    Element headElement = elem.getChild(_HEAD);
    importHead(headElement);
    inputClauses.add(new DefiniteClause(currentClause, vTable, symbolTable));
    vTable = null;
    currentClause = null;
  }
  
  /**
   * Parses the {@link #_HEAD} element.
   * @param elem an _HEAD <code>Element</code> value that is being parsed.  
   */

  private void importHead(Element elem){
    Element atomElement = elem.getChild(ATOM);
    importAtom(atomElement);
  }
/**
   * Parses the {@link #ATOM} element.
   * @param elem an ATOM <code>Element</code> value that is being parsed.  
   */
  private void importAtom(Element elem){
    int[] entry = {0, 0};
    int start = currentClause.size();
    currentClause.addElement(entry);
    Element oprElement = elem.getChild(_OPR);
    String symbol = importOpr(oprElement);
    int arity = importArgs(elem);
    int length = currentClause.size() - start;
    entry[0] = symbolTable.internSymbol(symbol, arity);
    entry[1] = length;
  }
  /**
   * Parses the {@link #_OPR} element.
   * @param elem an _OPR <code>Element</code> value that is being parsed.  
   */
  private String importOpr(Element elem){
    return importRel(elem);
  }
/**
   * Parses the {@link #REL} element.
   * @param elem an REL <code>Element</code> value that is being parsed.  
   */
  private String importRel(Element elem){
    Element relElement = elem.getChild(REL);
    return relElement.getText();
  }
/**
   * Parses the arguments of a {@link #ATOM} element, it will call {@link #importInd} and {@link #importVar} accordingly.
   * @param elem an ATOM <code>Element</code> value that is being parsed.  
   */
  private int importArgs(Element elem){
    List children = elem.getChildren();
    int nArgs = children.size() - 1;
    for(Iterator it = children.iterator(); it.hasNext();){
      Element next = (Element) it.next();
      if(next instanceof Element){
	if(compare(next.getName(), IND)){
	  importInd(next);
	}
	else if (compare(next.getName(), VAR)){
	  importVar(next);
	}
	else {
	  //do something drastic
	}
      }
    }
    return nArgs;
  }
/**
   * Parses the {@link #IND} element.
   * @param elem an IND <code>Element</code> value that is being parsed.  
   */
  private void importInd(Element elem){
    String symbol = elem.getText();
    // check for a empty ind, if so set symbol to nullgensymNNNN - Added by 
    // Marcel A. Ball - NRC - 2003
    if(symbol == null || symbol.equals("")) 
        symbol= "nullgensym" + nextGenSym();
    // End add
    
    int[] entry = {0, 1};
    entry[0] = symbolTable.internSymbol(symbol,0);
    currentClause.addElement(entry);
  }
/**
   * Parses the {@link #VAR} element.
   * @param elem an VAR <code>Element</code> value that is being parsed.  
   */
  private void importVar(Element elem){
    String varName = elem.getText();
    int i = internVar(varName);
    int[] entry = {-i, 1};
    currentClause.addElement(entry);
  }

  /**
   * An <code> Hashtable </code> variable containing the variable name of distinct variables of the clause currently being parsed.
   */
  private Hashtable vTable;
  /**
   * An <code> int </code> variable containing the number of distinct variables we have seen so far as the current clause is being parsed.
   */
  private int varCount;
  /**
   * An <code> int </code> variable containing the number of atoms in the clause that is currently being parsed.
   */
  private int atomCount;
  //Added by Marcel A. Ball - NRC - 2003
  /**
   * Symbol generator unique ID number
   */
  private static int genSym = 1234; 
  //End Add
  /**
   * An <code> Vector </code> variable containing the flatterm table of the clause that is currently being parsed.
   */
  private Vector currentClause;

  /** 
      This is similar to interSymbol from @see SymbolTable, but
      the hash table vhTable of variables is created anew for
      each clause, and there is no need to have a table of print
      names for variables since we do not intend to print them
      with their original names.  Thus internVar is a quick way
      to count the number of distinct vars we have seen so far in
      the clause, and to identify the ones in common. The count
      starts at 1.  New variables are referenced (named)
      according to the count that was current when the varible
      was first encountered.  Variables are put into the clause
      as negative numbers, which we get by making the additive
      complement of the reference.  Thus the first variable found
      is called -1, the second -2, etc.

      @param symbol the newly read token identifying a variable,
      which is decided by its upper case first letter
      @return an integer identifying this variable's position in
      the list of variables seen so far in this clause.  This
      list is chronologically ordered from 1.  */
  private int internVar(String symbol){
    //Added by Marcel A. Ball - NRC - 2003
    //check for a empty var, if so set var name to AnonGenSymNNNN 
    if(symbol.equals(""))
        symbol = "AnonGenSym" + nextGenSym();
    //End Add
    Object varPos = vTable.get(symbol);
    if(varPos==null){ //This is a new variable.
      varCount++;
      vTable.put(symbol, new Integer(varCount));
      return varCount;
    }
    else
      return ((Integer)varPos).intValue();
  }

  //Added my Marcel A. Ball - NRC - 2003
  /**
   *    <code>nextGenSym()</code> is a method to get the next integer value from 
   *        a static int, use for generating unique symbols for anonymous variables
   *        and null constants.
   *
   *    @return A <code>int</code> value containg the next integer.
   */
  public static int nextGenSym(){
    int r = genSym;
    genSym++;
    return r;
  }
  //End Add

  public  static void main(String args[]){
    try{ 
      SymbolTable st = new SymbolTable();
      RuleMLFileParser fp = new RuleMLFileParser(st);
      fp.parseRuleMLFile("default.xml");
    
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
      
    } catch (JDOMException e){
      e.printStackTrace();
    }

  }

}





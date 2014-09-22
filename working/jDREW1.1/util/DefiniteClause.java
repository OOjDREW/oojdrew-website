
package jDREW.util;

 
import java.util.*;
import org.jdom.*;


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

   <code>DefiniteClause</code> objects consist of an array of arrays of
   size 2, where each inner array consists of, in the first
   position, either an index into a symbol table or variable
   table and, in the second position, a length.  The symbol table
   is a static array of strings stored in the TPTPFileParser
   class. The variable table is not stored with the clause, but
   rather with a Unifier object that is created at various times,
   such as during resolution and factoring steps. But the indices
   into that table are stored in the clause as negative integers.
   (Note that the negative numbers are indexed from -1 but the
   variable table in Unifier is index from 0.)
   
   DefiniteClauses are stored sequences as of terms which represent the
   literals in the clause.  DefiniteClauses, and indeed all terms, are
   stored as "flat" with lengths, which means that the term
   structure, telling which terms are arguments to which, is
   encoded with the lengths: those second items in the arrays of
   size 2.  For instance if we are representing p(a, q(b, X)) the
   symbols array is {{^p,5},{^a,1},{^q,3},{^b,1},{-1,1}}.  Here
   ^p is used to represent that index into the symbol table where
   "p" is stored and the -1 indicates the first variable in the
   variable table.
   

    */

public class DefiniteClause implements Comparable{

  /**
   * A Constant variable used to generate RuleML format of the definite clause. It also appears in {@link RuleMLFileParser}.
   */
  public static final String IND = "ind";
  /**
    * A Constant variable used to generate RuleML format of the definite clause. It also appears in {@link RuleMLFileParser}.
   */
  public static final String VAR = "var";
  /**
     * A Constant variable used to generate RuleML format of the definite clause. It also appears in {@link RuleMLFileParser}.
   */
  public static final String _OPR = "_opr";
  /**
   * A Constant variable used to generate RuleML format of the definite clause. It also appears in {@link RuleMLFileParser}.
   */
  public static final String REL = "rel";
  /**
   * A Constant variable used to generate RuleML format of the definite clause. It also appears in {@link RuleMLFileParser}.
   */
  public static final String IMP = "imp";
  /**
   * A Constant variable used to generate RuleML format of the definite clause. It also appears in {@link RuleMLFileParser}.
   */
  public static final String FACT = "fact";
  /**
   * A Constant variable used to generate RuleML format of the definite clause. It also appears in {@link RuleMLFileParser}.
   */
  public static final String _HEAD = "_head";
  /**
   * A Constant variable used to generate RuleML format of the definite clause. It also appears in {@link RuleMLFileParser}.
   */
  public static final String _BODY = "_body";
  /**
   * A Constant variable used to generate RuleML format of the definite clause. It also appears in {@link RuleMLFileParser}.
   */
  public static final String ATOM = "atom";
  /**
   * A Constant variable used to generate RuleML format of the definite clause. It also appears in {@link RuleMLFileParser}.
   */
  public static final String AND = "and";
  

  
  /**
   <code>clauseCount</code> counts the number of clauses created so far
   */
  public static int clauseCount=0;


  /**
    <code>clauseNumber</code> is the number of the clause as it
   is created, or if  renumbered is true, it is the number of
   the clause in the proof
   */
    public int clauseNumber; //takes the current clauseCount

  /**
   <code>varCount</code> counts the number of variables in this
   clause so the unifier will know how many replacements the
   substitution will need.  */
  public int varCount;


  /**
   <code>atomCount</code> holds the number of literals in the clause
   */
  public int atomCount;
  
  /**
   <code>countAtoms</code> refresh the {@link #atomCount} and return it.
   @return the refreshed {@link #atomCount}
   */
  private int countAtoms(){
    int atomCount = 0;
    int pos = 0;
    while(pos < symbols.length){
      pos += symbols[pos][1];
      atomCount++;
    }
    return atomCount;
  }

  /**
     The variable <code>symbolTable</code> refers to the
     SymbolTable where the printNames for the symbols in this
     DefiniteClause are found.  This is a public field because we
     intend to use it to test the reference for equality with
     other symbolTable references and to know how to print this
     clause.  */
  public SymbolTable symbolTable;

  /**
    <code>symbols</code> refers to the symbols, variables and
    the lengths of the subterms in this clause, as described above.
   */
  public int[][] symbols;
  
  //Added by Marcel A. Ball - NRC - 2003
  /**
   *    If the boolean value RuleMLOut is set to false DefiniteClauses will
   *    produce Prolog output when toString() is called, if set to true, toString()
   *    will product output in RuleML format.
   */
  public static boolean RuleMLOut = false;
  //End Add
  
  /**
     Creates a new <code>DefiniteClause</code> instance for an initial clause

     @param syms - a <code>Vector</code> value, containing the symbols
     @param vars - a <code>Hashtable</code> value, containing the variable names
     @param symbolTable - a <code>SymbolTable</code> value, so we
     can trace the pedigree of this clause and also to know where
     the print names for it are.  */
  DefiniteClause(Vector syms, Hashtable vars, SymbolTable symbolTable){
    loadVariableNames(vars);
    this.symbolTable = symbolTable;
    this.varCount = vars.size();
    loadSymbols(syms);
    this.atomCount = countAtoms();
    clauseNumber = clauseCount;
    clauseCount++;
  }
/**
     Creates a new <code>DefiniteClause</code> instance for an initial clause

     @param syms - a <code>Vector</code> value, containing the symbols (the flatterm table) of the DefiniteClause
     @param variableNames - a <code>String</code> array, containing the variable names
     @param symbolTable - a <code>SymbolTable</code> value, so we
     can trace the pedigree of this clause and also to know where
     the print names for it are.  */
  public DefiniteClause(Vector syms, String[] variableNames,  SymbolTable symbolTable){
    this.variableNames = variableNames;
    this.varCount = variableNames.length;
    hasVariableNames = true;
    this.symbolTable = symbolTable;
    loadSymbols(syms);
    this.atomCount = countAtoms();
    clauseNumber = clauseCount;
    clauseCount++;
  }
/**
     Creates a new <code>DefiniteClause</code> instance for an initial clause

     @param syms - a <code>Vector</code> value, containing the symbols (the flatterm table) of the DefiniteClause
     @param vars - a <code>Vector</code> value, containing the variable names
     @param symbolTable - a <code>SymbolTable</code> value, so we
     can trace the pedigree of this clause and also to know where
     the print names for it are.  */
  DefiniteClause(Vector syms, Vector vars, SymbolTable symbolTable){
    loadVariableNames(vars);
    this.varCount = varCount;
    this.atomCount = countAtoms();
    this.symbolTable = symbolTable;
    loadSymbols(syms);
    clauseNumber = clauseCount;
    clauseCount++;
  }

	/**
	 * A <code>String</code> array, containing the variable names of the definite clause
      */
  public String[] variableNames;
  /**
	 * A <code>Boolean</code> variable, set to be true if the caluse contains variables and their names, otherwise set to be false.
      */
  public boolean hasVariableNames;

/**
 * load the variable names stored in a Vector to the {@link #variableNames}.
 * @param vars - a <code>Vector</code> value, containing the variable names.
 */ 
  private void loadVariableNames(Vector vars){
    varCount = vars.size();
    hasVariableNames = true;
    variableNames = new String[varCount];
    Iterator it = vars.iterator();
    int vPos = 0;
    while(it.hasNext()){
      String varName = (String) it.next();
      variableNames[vPos] = varName;
      vPos++;
    }
  }
/**
 * load the variable names stored in a Hashtable to the {@link #variableNames}.
 * @param vars - a <code>Hashtable</code> value, containing the variable names.
 */ 
  private void loadVariableNames(Hashtable vars){
    varCount = vars.size();
    hasVariableNames = true;
    variableNames = new String[varCount];
    Iterator it = vars.entrySet().iterator();
    while(it.hasNext()){
      Map.Entry me = (Map.Entry) it.next();
      int pos = ((Integer) me.getValue()).intValue();
      String name = (String) me.getKey();
      variableNames[pos-1] = name;//sub 1 because vars are indexed from 1
    }
  }

  /**
     Creates a new <code>DefiniteClause</code> instance for an initial clause

     @param syms - a <code>Vector</code> value, containing the symbols (the flatterm table) of the DefiniteClause
     @param varCount - an <code>int</code> value, number of variables of the definite clause
     @param symbolTable - a <code>SymbolTable</code> value, so we
     can trace the pedigree of this clause and also to know where
     the print names for it are.  */
  DefiniteClause(Vector syms, int varCount, SymbolTable symbolTable){
    this.varCount = varCount;
    this.symbolTable = symbolTable;
    hasVariableNames = false;
    loadSymbols(syms);
    this.atomCount = countAtoms();
    clauseNumber = clauseCount;
    clauseCount++;
  }

  /**
     <code>loadSymbols</code> loads the symbols array for this
     clause from the Vector v containing an array values of
     length 2.  In either case the symbol consists of a either an
     index into the symbol table or a variable (which is a
     negative numbered index)

     @param v - a <code>Vector</code> value containing the flatterm table of the DefiniteClause*/
  private void loadSymbols(Vector v){
    symbols = new int[v.size()][2];
    Iterator it = v.iterator();
    int i = 0;
    while(it.hasNext()){
      Object next = it.next();
      int[] se = (int[]) next;
      symbols[i] = se;
      i++;
    }
  }

  //Modified by Marcel A. Ball - NRC - 2003
  /**
   *    <code>toString()</code> is a method to get a string representatin of
   *        this DefiniteClause. If the static boolean variable <code>RuleMLOut</code>
   *        is true it is returned formatted as a RuleML XML, otherwise it is 
   *        formatted as prolog.
   *
   *    @retrun A <code>String</code> value containing the string representation
   *        of the DefiniteClause.
   */
  public String toString(){
      if(RuleMLOut)
          return toRuleMLString();
      else
          return toPrologString();
      
  }
  //End Modify

  //Added by Marcel A. Ball - NRC - 2003
  /**
   *    <code>toRuleMLString()</code> is used by <code>toString()</code> to 
   *        get the RuleML XML representation of this DefiniteClause. It can be
   *        called by the user to get a DefiniteClause printed as RuleML while
   *        leaving the default output format as prolog.
   *
   *    @return A <code>String</code> value containing the RuleML representation
   *        of the DefiniteClause.
   */   
  public String toRuleMLString()
  {
    Element el = toRuleMLElement();
    java.io.StringWriter sw = new java.io.StringWriter();
    org.jdom.output.XMLOutputter xml = new org.jdom.output.XMLOutputter();
    xml.setIndent(true);
    xml.setNewlines(true);
    xml.setIndentSize(2);
    try{
        xml.output(el,sw);
    }catch(java.io.IOException e){}
    return sw.getBuffer().toString();
  }
  //End Add
  
  //Added by Marcel A. Ball - NRC - 2003
  /**
   *    <code>toRuleMLElement()</code> is a method used by <code>toRuleMLString</code>
   *        in producing the RuleML string representation of a DefiniteClause. The
   *        user can call this method to get the DefiniteClause as an jdom Element
   *        Object - the top element is either fact or imp.
   *
   *    @return An <code>Element</code> value containing the DefiniteClause as a
   *        jdom XML representation.
   */
  public Element toRuleMLElement(){
    StringBuffer b = new StringBuffer();
    Element el = null;
   
    int endOfHead = symbols[0][1];
    if(endOfHead < symbols.length)
        el = new Element(IMP);
    else
        el = new Element(FACT);

    Element head = new Element(_HEAD);
    el.addContent(head);
    elementAppendRML(head, 0, endOfHead, false, false);
    
    if(endOfHead < symbols.length){
      boolean needAnd = needAnd(endOfHead, symbols.length);
      Element body = new Element(_BODY);
      el.addContent(body);
      elementAppendRML(body, endOfHead, symbols.length, needAnd, false);

    }
    return el;
  }
  //End add
  
  //Added by Marcel A. Ball - NRC - 2003
  /**
   *    <code>toPrologString()</code> is a method used by <code>toString()</code>
   *        to produce a prolog representation of a DefiniteClause. The user can
   *        call this method if they wish to get a prolog representation while
   *        having the default output be in RuleML format.
   *
   *    @return A <code>String</code> value containg the prolog clause.
   */
  public String toPrologString(){
    StringBuffer b = new StringBuffer();
    int endOfHead = symbols[0][1];
    bufferAppend(b, 0, endOfHead);
    if(endOfHead < symbols.length){
      b.append(":-");
      bufferAppend(b, endOfHead, symbols.length);
    }
    b.append(".");
    
    return b.toString();
  }
  //End Add

  /**
     <code>println</code> prints the clause in a standard format
     followed by a new line character

  */
  void println(){
    System.out.println(toString());
  }

  /**
     <code>print</code> prints the clause in a standard format
  */
  void print(){
    System.out.println(toString());
  }

  /**
     <code>bufferAppend</code> is the auxilliary function for printing a clause

     @param b - a <code>StringBuffer</value>
     @param from - an <code>int</code> value telling at which
     * symbol to start printing
     @param to - an <code>int</code> value telling which symbol
     * is ONE PAST the point where we stop printing
  */
  void bufferAppend(StringBuffer b, int from, int to){
    while(from<to){
      int symb = symbols[from][0];
      if(symb<0){
	if(hasVariableNames){
	  b.append(variableNames[-symb-1]);
	  b.append(""+(-symb-1));
	}
	else {
	  b.append("V");
	  b.append(-symb-1);
	}
      }
      else if(symb >= symbolTable.size()){
	b.append("$"+symb);
      }
      else {
	String s = symbolTable.
	  symbolTableString(symb);

	  if (Character.isUpperCase(s.charAt(0)) ||
	      ParserBasic.containsDelimiters(s)){
	    //if starts with upper case or contains delimiters
	    //print with quotes
	    b.append('\'');
	    b.append(s);
	    b.append('\'');
	  }
	  else{
	    b.append(s);
	  }

	int len = symbols[from][1];
	if(len>1){
	  b.append("(");
	  bufferAppend(b, from+1, from + len);
	  b.append(")");
	  from+=len-1;
	}
      }
      from++;
      if(from<to) b.append(",");
    }
  }

  /**
   *    <code>needAnd(int from, int to)</code> is a method used by 
   *        <code>toRuleMLElement()</code> to see if there is more than one atom
   *        within a _body element that needs to be enclosed by an "and" element.
   *
   *    @param from - an <code>int</code> value telling at which symbol to start 
   *        printing
   *    @param to - an <code>int</code> value telling which symbol is ONE PAST 
   *        the point where we stop printing
   *    @retrun A <code>boolean</code> value, true if an and element is needed
   */
  
  private boolean needAnd(int from, int to){
    int count = 0;  
    while(from < to){
        int symb = symbols[from][0];
        if(symb >= 0 && symb < symbolTable.size())
        {
            int len = symbols[from][1];
            if(len > 1)
                count++;
            from+= len-1;
        }
        from++;
    }
    if(count >= 2)
        return true;
    else
        return false;
  }
  
  /**
   *    <code>bufferAppendRML</code> is the auxilliary function for creating a RuleML
   *        element representing this DefiniteClause.
   *
   *    @param el - an <code>Element</code> value that we are currently working within.
   *    @param from - an <code>int</code> value telling at which symbol to start 
   *        printing
   *    @param to - an <code>int</code> value telling which symbol is ONE PAST 
   *        the point where we stop printing
   *    @param needAnd - a <code>boolean</code>, set to true if this clause needs
   *        an and element.
   *    @param inAtom - a <code>boolean</code> value, set to true if we are 
   *        already inside an atom element, otherwise set to false.
   */
  private void elementAppendRML(Element el, int from, int to, boolean needAnd, boolean inAtom){
    Element and = null;
    Element currAtom = null;
    if(!inAtom){
        if(needAnd)
        {
            and = new Element(AND);
            el.addContent(and);
            currAtom = new Element(ATOM);
            and.addContent(currAtom);
        }
        else
        {
            currAtom = new Element(ATOM);
            el.addContent(currAtom);
        }
   }
    else
        currAtom = el;
    while(from<to){
      int symb = symbols[from][0];
      if(symb<0){
	Element var = new Element(VAR);
        currAtom.addContent(var);
        if(hasVariableNames){
            String vName = variableNames[-symb-1];
            if(!vName.startsWith("AnonGenSym"))
              var.addContent(vName + "" + (-symb-1));
	}
	else 
          var.addContent("V" + (-symb-1)); 
      }
//      else if(symb >= symbolTable.size()){
//	b.append("$"+symb);
//      }
      else {
	String s = symbolTable.
	  symbolTableString(symb);
          

	int len = symbols[from][1];
	if(len>1){
	  //b.append("");
          Element opr = new Element(_OPR);
          currAtom.addContent(opr);
          Element rel = new Element(REL);
          opr.addContent(rel);
          rel.addContent(s);
	  elementAppendRML(currAtom, from+1, from + len, needAnd, true);
	  from+=len-1;
          if(!inAtom){
              if(from + 1 < to)
              {
                currAtom = new Element(ATOM);
                and.addContent(currAtom);
            }
          }
	}
        else{
            Element ind = new Element(IND);
            currAtom.addContent(ind);
            if(!s.startsWith("nullgensym"))
                ind.addContent(s);
        }
      }
      from++;
    }
    
  }
  
  String literalIndexToString(int from, int to){
    StringBuffer sb = new StringBuffer();
    bufferAppend(sb, from, to);
    return sb.toString();
  }
  /**
   * This method compare two definite clauses. If the two definite clauses 
   * differ only by variables or not at all, they are treated as equal.
   * Shorter clauses are smaller in the order than longer ones. Here, we define the length of
   * a clause to be the total number of the function symbols, predicates, ground values and variables.
   * For example, the length of a(b(c(X,g))) is 5.
     If they are the same length, then
     we use symbolTable order on literals, as read from
     left to right, where variables are ordered lower than
     functors, and two variables cannot be compared (i.e. are "equal")
     SymbolTable order is the order in which the literals are entered
     into the symbolTable.
   */
  public int compareTo(Object o){
    DefiniteClause otherClause = (DefiniteClause) o;
    //we assume that it makes sense only to compare two clauses
    //Shorter clauses are smaller in the order than longer ones.
    //If they are the same length, then
    //we use symbolTable order on literals, as read from
    //left to right, where variables are ordered lower than
    //functors, and two variables cannot be compared (i.e. are "equal")
    //SymbolTable order is the order in which the literals are entered
    //into the symbolTable.
    if(otherClause.symbols.length>symbols.length)
      return -1;
    else if(symbols.length>otherClause.symbols.length)
      return +1;
    else{
      for(int i = 0; i < symbols.length; i++){
	int left = 0;
	if(symbols[i][0]>0) left = symbols[i][0];
	int right = 0;
	if(otherClause.symbols[i][0]>0) right = otherClause.symbols[i][0];
	if(left<right)
	  return -1;
	if(right<left)
	  return 1;
      }
      //if they differ only by variables or not at all, they are incomparable
      return 0;
    }
  }

// Additional methods added to interact with RACSA/RALOCA
//These methods were added by Iuli Popescu and Sean Gallacher
  /**
   * This method determines if the current DefiniteClause is a
   * rule or not.  
   * @return boolean value that is true if it is a rule and false if it
   * is a fact.
   */
  public boolean isRule() { 
    // return false if the number of symbols in the clause is
    // equal to the number of symbols in the head of the clause
    return !(symbols.length == symbols[0][1]);
  }

  /**
   * This method obtains the first predicate symbol from the
   * clause. ie: p(x):-y(w). returns p
   * @return String the predicate symbol
   */
  public String getPredicate() {
    return symbolTable.symbolTableString(symbols[0][0]);
  }
  
  /**
   * This method returns all terms of a rule clause
   * @return String[] the terms from the rule clause
   */
  public String[] getTerms() {
    StringBuffer b = new StringBuffer();
    Vector termsVector = new Vector();
    String[] termsArray = null;

    int from = symbols[0][1];
    int to = symbols.length;
    
    while(from<to){
      int symb = symbols[from][0];
      
      if(symb<0){
	if(hasVariableNames){
	  b.append(variableNames[-symb-1]);
	  b.append(""+(-symb-1));
	}
	else {
	  b.append("V");
	  b.append(-symb-1);
	}
      }
      else if(symb >= symbolTable.size()){
	b.append("$"+symb);
      }
      else {
	String s = symbolTable.
	  symbolTableString(symb);
	
	if (Character.isUpperCase(s.charAt(0)) ||
	    ParserBasic.containsDelimiters(s)){
	  //if starts with upper case or contains delimiters
	  //print with quotes
	  b.append('\'');
	  b.append(s);
	  b.append('\'');
	}
	else{
	  b.append(s);
	}
	
	int len = symbols[from][1];
	if(len>1){
	  b.append("(");
	  bufferAppend(b, from+1, from + len);
	  b.append(")");
	  from+=len-1;
	}
      }
      from++;
      termsVector.addElement(b.toString());
      if(from<to){
	//add to return array
	b = new StringBuffer();
      }
    }
    
    termsArray = new String[termsVector.size()];
    int i=0;
    Enumeration e = termsVector.elements();
    while (e.hasMoreElements()){
      termsArray[i] = (String)e.nextElement();
      i++;
    }
    return termsArray;
  }

  /**
   * This method gets the facts within the body of 
   * this rule clause, excluding constants.
   * @return DefiniteClause[] the array of DefiniteClause
   * facts from the rule body.
   */
  public DefiniteClause[] getRuleBody(){
    StringBuffer b = new StringBuffer();
    Vector termsVector = new Vector();
    boolean constant;
    DefiniteClause[] clauseArray = null;

    int from = symbols[0][1];
    int to = symbols.length;
    
    while(from<to){
      constant = false;
      int symb = symbols[from][0];
      
      //constant
      if (symbols[from][1] == 1){
	constant = true;
      }
      if(symb<0){
	if(hasVariableNames){
	  b.append(variableNames[-symb-1]);
	  b.append(""+(-symb-1));
	}
	else {
	  b.append("V");
	  b.append(-symb-1);
	}
      }
      else if(symb >= symbolTable.size()){
	b.append("$"+symb);	
      }
      else {
	String s = symbolTable.symbolTableString(symb);
	
	if (Character.isUpperCase(s.charAt(0)) ||
	    ParserBasic.containsDelimiters(s)){
	  //if starts with upper case or contains delimiters
	  //print with quotes
	  b.append('\'');
	  b.append(s);
	  b.append('\'');
	}
	else{
	  b.append(s);
	}
	
	int len = symbols[from][1];
	if(len>1){
	  b.append("(");
	  bufferAppend(b, from+1, from + len);
	  b.append(")");
	  from+=len-1;
	}
      }
      from++;
      if(!constant){
	termsVector.addElement(b.toString());
      }
      if(from<to){
	//add to return array
	b = new StringBuffer();
      }
    }
    
    try{
      DCFileParser dcfp = new DCFileParser(symbolTable);
      String term; 
      clauseArray = new DefiniteClause[termsVector.size()];
      int i=0;
      Enumeration e = termsVector.elements();
      while (e.hasMoreElements()){
	term = ((String)e.nextElement())+"."; 
	clauseArray[i] = dcfp.parseDefiniteClause(term); 
	i++;
      }
      
      return clauseArray;
      
    }catch (Exception e){
      e.printStackTrace(); 
      return null; 
    }
    
  }
  
  /**
   * This method gets the head of this DefiniteClause.
   * ie: p(x):-y(z). returns p(x). as a DefiniteClause.
   * @return DefiniteClause the head of the clause
   */
  public DefiniteClause getHead()
  {
    StringBuffer b = new StringBuffer();
    int endOfHead = symbols[0][1];
    bufferAppend(b, 0, endOfHead);
    b.append(".");	  
    String head = b.toString();
    DefiniteClause headDC = null;
    try{
	DCFileParser dcfp = new DCFileParser(symbolTable);
      headDC = dcfp.parseDefiniteClause(head);
    }	catch(Exception e){
	e.printStackTrace();
    }
    return headDC;	
  }

  /**
   * This method returns the arguments of this DefiniteClause.
   * ie: p(x,z). returns x and z.
   * @return String[] the fact arguments.
   */
  public String[] getFactArguments() {
    StringBuffer b = new StringBuffer();
    Vector termsVector = new Vector();
    String termsArray[] = null;
    
    int numSymbols = symbols[0][1];
    
    for(int i=1; i<numSymbols; i++){
      int symb = symbols[i][0];
      if(symb < 0){
	if(hasVariableNames){
	  b.append(variableNames[-symb-1]);
	  b.append(""+(-symb-1));
	  }
	else {
	  b.append("V");
	  b.append(-symb-1);
	}
	termsVector.addElement(b.toString());
	b = new StringBuffer();
      }
	else{
	  termsVector.addElement(symbolTable.symbolTableString(symbols[i][0]));
	}
    }   
    termsArray = new String[termsVector.size()];
    
    int i=0;
    Enumeration e = termsVector.elements();
    while (e.hasMoreElements()){
      termsArray[i] = (String)e.nextElement();
      i++;
    }
  
    return termsArray;
  }

}


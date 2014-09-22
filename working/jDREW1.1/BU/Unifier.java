
package jDREW.BU;

import java.util.*;
import jDREW.util.*;

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

   A <code>Unifier</code> is an object that performs two separate
   functions: it tells whether two clauses literals that occur in
   Definite clauses can unify, and it builds the resulting
   clauses after the required substitution, required for the
   unification, have been applied.

   In this file the clauses are always a fact and a rule.

   */
public class Unifier{
  /**
   * A constant value which is 0, specifying where the "fact" clause information is stored in the {@link #clauses}, {@link #symbols} and {@link #vars}.
   */
  static final int FACT = 0;
  /**
   * A constant value which is 1, specifying where the "rule" clause information is stored in the {@link #clauses}, {@link #symbols} and {@link #vars}.
   */
  static final int RULE = 1;
  /**
   * A constant value which is 0, specifying where the "left" clause information is stored in the {@link #clauses}, {@link #symbols} and {@link #vars}.
   */
  static final int LEFT = 0;
  /**
   * A constant value which is 1, specifying where the "right" clause information is stored in the {@link #clauses}, {@link #symbols} and {@link #vars}.
   */
  static final int RIGHT = 1;
  /**
   * A constant value which is 2, only used in the {@link #GROUNDING_MODE}, specifying where the grounded value of each variable is stored.
   */
  static final int SPARE = 2;
  /**
   * A <code> DefiniteClause </code> array stores the two DefiniteClauses (a rule and a fact) being unified.
   */
  DefiniteClause clauses[];
  
  /**
   * A constant value indicating that a variable is still a free variable, has not been substituted yet.
   */
  static final int UNDEFINED = 0;
  /**
   * A constant value indicating one of the three kinds of proccessing modes supported by the <code>Unifier</code> class.
   */
  static final int UNIFICATION_MODE = 0;
  /**
   * A constant value indicating one of the three kinds of proccessing modes supported by the <code>Unifier</code> class.
   */
  static final int GROUNDING_MODE = 3;
  /**
   * A constant value indicating one of the three kinds of proccessing modes supported by the <code>Unifier</code> class.
   */
  static final int ONEWAY_MODE = 4;
  /**
   * A three dimensional array storing the flatterm of the two {@link jDREW.util.DefiniteClause DefiniteClauses} on which the unifying operaion is being carried out.
   * One of them is stored in symboles[{@link #FACT}] and the other is stored in symboles[{@link #RULE}]
   */
  int[][][] symbols = null;
  /**
     The variable <code>symbolTable</code> stores the symbol table shared by the rule clause and the fact clause.  
   	*/
  SymbolTable symbolTable;
  /**
   * A three dimensional array storing the substitutions of the variables of the two being unified {@link jDREW.util.DefiniteClause DefiniteClauses}.
   */
  int[][][] vars = null;
  /**
   * A <code> String </code> array variable stores the variable names of the rule clause and the fact clause.
   */
  String[] variableNames;
  /**
   * The total number of variables of the rule clause and the fact clause.
   */
  int variablesSize;
  /**
   * A temporary value used in {@link #apply} and {@link #resolvent}.
   */
  int variablesCount;
  /**
   * A <code>boolean</code> variable indicating whether the rule clause and the fact clause are successfully unified, initially set to be false.  
   */
  boolean unified = false;
  /**
   * A <code>int</code> variable indicating the mode of the unifier.  
   */
  int mode;
  /**
   * See {@link #Unifier(DefiniteClause, int, int, Unifier, int)}
   */
  int rLeft;
  /**
   * See {@link #Unifier(DefiniteClause, int, int, Unifier, int)}.
   */
  int rRight;


  /**
      Generates the unified set and set the {@link #unified} to true
      if the second literal (the first premise) of the <code> rule </code>
      DefiniteClause can unify with the <code>fact</code> clause.
      If so then the variables in the array vars are set.  You 
      may then call apply to create a new clause with the variables'
      values placed in the list of symbols.

      Since variables are represented in a term as indexes (-1,
      -2, ...)  we must identify each variable with the term it
      comes from, using LEFT (0) and RIGHT (1) to indicate the
      term, or side.  Recall that terms themselves are arrays of
      arrays of size 2 and these arrays of size 2 contain either
      the index into the symbol table in the first position and
      the length of this subterm starting in the second position,
      or the negative number index of a variable in the first
      position and 1 in the second position.  Also recall that
      the arrays are "flat" in that consecutive symbols in the
      term are stored in consecutive positions in the outer
      array, and because of this we must know the lengths of the
      subterms to be able to reconstruct the term.

      We tend to use variable names m and n to refer to
      index/side arrays of size 2, to use dm and dn to refer to
      dereferenced versions of these.  Thus if m refers to a
      variable (negative first value) and if the variable has
      received a replacement value so far in the algorithm, dm
      will refer to the value.  If m is replaced by another
      variable then dm will refer to the result of recursively
      dereferencing that variable.  If m has not received a
      replacement then dm and m will be the same.

      We tend to use lm to refer to the actual values in the
      term that m refers to; thus it is the result of looking up m.
      @param rule -a rule clause the second literal of which will unify the <code> fact </code> clause.
      @param fact -a fact clause that will unify the first literal of the <code> rule <code> clause.

*/

  Unifier(DefiniteClause rule, DefiniteClause fact){
    // assumes MODE is UNIFICATION_MODE
    mode = UNIFICATION_MODE;
    if(fact.symbolTable != rule.symbolTable)
      throw new RuntimeException("Symbol tables of resolvents do not match.");
    this.symbolTable = rule.symbolTable;
    clauses = new DefiniteClause[2];
    clauses[FACT] = fact;
    clauses[RULE] = rule;
    symbols = new int[2][][];
    symbols[FACT] = fact.symbols;
    symbols[RULE] = rule.symbols;
    vars = new int[2][][];
    vars[FACT] = new int[fact.varCount][2];
    vars[RULE] = new int[rule.varCount][2];
    //the number of distinct variables resulting
    //will not exceed the number of distinct variables given
    variablesSize = fact.varCount + rule.varCount;
    variableNames = new String[variablesSize];
    int mEnd = symbols[FACT].length;
    unified = unify(0, FACT, mEnd, symbols[RULE][0][1], RULE);
  }




  /**
   * This function only handles {@link #GROUNDING_MODE} in which every variable
   * of the <code>leftC</code> clause is replaced by a fictitious constant (ground value).
   * The ficititious constants will not be identical to any existing symbol. 
   * This function is used (together with {@link #Unifier(DefiniteClause, int, int, Unifier, int)}
   * to tell if one clause, say A, subsumes another clause, say B. In order to do this, we first
   * ground all the variables in clause B to some value that is not identical to any existing symbol. If clause A
   * can unify the grounded clause B, we say that A subsumes B. 
   * @param leftC - the definite clause that is being grounded.
   * @param mode - only {@link #GROUNDING_MODE} value is accepted.
   */
  Unifier(DefiniteClause leftC, int mode){
    //
    //Place a sequence of constant terms on the right that are atoms
    // beyond the limit of the symbols parsed, so that they will
    // not be identical to any symbol seen so far.
    // Refer the variables in leftC to these constants.
    this.mode = mode;
    this.symbolTable = leftC.symbolTable;
    if(mode == GROUNDING_MODE){
      symbols = new int[3][][];
      symbols[LEFT] = leftC.symbols;
      symbols[SPARE] = new int[leftC.varCount+1][2];
      int stLimit = symbolTable.size();
      vars = new int[1][][];
      vars[LEFT] = new int[leftC.varCount][2];
      for(int i = 0; i < leftC.varCount; i++){
	//	vars[LEFT][i] = {i, SPARE};
	//	clauses[RIGHT].symbols[i+1] = {stLimit, 1}
	vars[LEFT][i][0]=i+1; vars[LEFT][i][1]=SPARE;
	symbols[SPARE][i+1][0] = stLimit;
	symbols[SPARE][i+1][1] = 1;
	stLimit++;
      }
      unified = true;
    }
  }

  /**
     Creates a new <code>Unifier</code> that reuses the constants and
     the bindings in the right clause.  This is only used in ONEWAY_MODE
     and when the Unifier <code> u </code> was a GROUNDING_MODE, although this second
     condition is not checked.
     * This function is used (together with {@link #Unifier(DefiniteClause, int)}
   * to tell if one clause, say A, subsumes another clause, say B. In order to do this, we first
   * ground all the variables in clause B to some value that is not identical to any existing symbol. If clause A
   * can unify the grounded clause B, we say that A subsumes B. 

     @param leftC - the subsuming clause (left clause). 
     @param rLeft - an <code>int</code> value that indicates the literal of the subsuming clause on which the unification operation will be carried out.
     @param rRight - an <code>int</code> value that indicates the literal of the subsumed clause on which the unification operation will be carried out.
     @param u - an <code>Unifier</code> value that stores the information of the subsumed clause (the right clause).
     @param mode - only {@link #ONEWAY_MODE} is accepted.
  */
  Unifier(DefiniteClause leftC, int rLeft, int rRight, Unifier u, int mode){
    this.mode = mode;
    if(leftC.symbolTable != u.symbolTable)
      throw new RuntimeException
	("Symbol tables of resolvents do not match.");
    this.symbolTable = leftC.symbolTable;
    if (mode == ONEWAY_MODE){
      symbols = new int[3][][];
      symbols[LEFT] = leftC.symbols;
      symbols[RIGHT]= u.symbols[LEFT];
      symbols[SPARE] = u.symbols[SPARE];
      vars = new int[2][][];
      vars[LEFT] = new int[leftC.varCount][2];
      vars[RIGHT] = u.vars[LEFT];
      //skip to the literal indicated by rLeft
      int iLeft = 0;
      for(int i = 0; i<rLeft; i++)
	iLeft+=symbols[LEFT][iLeft][1];
      //skip to the literal indicated by rRight
      int iRight = 0;
      for(int i = 0; i<rRight; i++)
	iRight+=symbols[RIGHT][iRight][1];
//        int[] m = {iLeft, LEFT};
//        int[] n = {iRight, RIGHT};
      int mEnd = iLeft + symbols[LEFT][iLeft][1];
      //mEnd is the index of the beginning plus the length
//        for(int i = 0; i < leftC.varCount; i++){
//  	vars[LEFT][i][0]=UNDEFINED;
//        }//unnecessary since UNDEFINED = 0 and allocated vars are zeroed
      unified = unify(iLeft, LEFT, mEnd, iRight, RIGHT);
      //    dump();
    }
  }
 /**
  * The function <code>unify</code> performs unification on two literals. If two literals can be unified,
  * the unified set will be stored in the {@link #vars} variable and true will be returned.
  * @param m0 -together with <code>m1</code>, points to the current symbol of the first literal being proecessed.
  * @param m1 -together with <code>m0</code>, points to the current symbol of the first literal being proecessed.
  * @param mEnd -together with <code>m1</code>, points to the end of the first literal being unified.   
  * @param n0 --together with <code>n1</code>, points to the current symbol of the second literal being proecessed.
  * @param n1 --together with <code>n0</code>, points to the current symbol of the second literal being proecessed.
  * @return -true if the two literal unifies with each other.
  */
  boolean unify(int m0, int m1, int mEnd, int n0, int n1){
    /* when optimizing, convert this to non-recursive */

    if( m0 == mEnd )
      return true;
    else{
      deref(m0, m1);
      int dM0 = derefed0;
      int dM1 = derefed1;
      deref(n0, n1);
      int dN0 = derefed0;
      int dN1 = derefed1;
      if(dM0<0){
	if(dN0<0){
	  //m and n both reference unbound variables
	  if(dM0==dN0 && dM1==dN1){
	    //m and n are the same variable
	    //do nothing
	  }
	  else {
	    //m and n are different variables
	    //bind m to n
	    assignToVar(dN0, dN1, dM0, dM1);
	  }
	}
	else {
	  //m is a variable but n is not
	  if(occursIn(dN0, dN1, lookup(dN0, dN1)[1]+dN0, dM0, dM1))
	    return false;
	  else
	    assignToVar(dM0, dM1, dN0, dN1);
	}
      }
      else{
	if(dN0<0)
	  //m is not a variable but n is
	  if(occursIn(dM0, dM1, lookup(dM0, dM1)[1]+dM0, dN0, dN1))
	    return false;
	  else
	    assignToVar(dN0, dN1, dM0, dM1);
	else {
	  //neither m nor n is a variable
	  int[] ldM = lookup(dM0, dM1);
	  int[] ldN = lookup(dN0, dN1);
	  if(ldM[0] == ldN[0]){
	    //same function symbols, so check inner
	    //int[] newDM = {dM[0]+1, dM[1]};
	    //int[] newDN = {dN[0]+1, dN[1]};
	    if(!unify(dM0+1, dM1, ldM[1]+dM0, dN0+1, dN1))
	      return false;
	  }
	  else {
	    //different function symbols, so give up
	    return false;
	  }
	}
      }
      //continue for the next pair of terms
      //int[] newM = {m[0] + lookup(m)[1], m[1]};
      //int[] newN = {n[0] + lookup(n)[1], n[1]};
      return unify(m0 + lookup(m0, m1)[1], m1, mEnd, n0+lookup(n0, n1)[1], n1);
    }
  }
  /**
   * Checks if the unification involves unifying a variable against a term containing that variable. 
   * Take ex(X,f(X)) and ex(Y,Y) as example, the unification of these two literals involves trying to unify X with f(X).  
   * @param m0 -together with <code>m1</code>, points to the current symbol of the first literal being proecessed.
  * @param m1 -together with <code>m0</code>, points to the current symbol of the first literal being proecessed.
  * @param mEnd -together with <code>m1</code>, points to the end of the first literal being processed.   
  * @param n0 --together with <code>n1</code>, points to the current symbol of the second literal being proecessed.
  * @param n1 --together with <code>n0</code>, points to the current symbol of the second literal being proecessed.
  * @return -true if the unification of the two literals involves unifying a variable against a term containing that variable..
   * 
   */
  boolean occursIn(int m0, int m1, int mEnd, int n0, int n1){
    /* when optimizing, convert this to non-recursive */
    if(m0==mEnd)
      return false;
    else {
      deref(m0, m1);
      int dm0 = derefed0;
      int dm1 = derefed1;
      if(dm0==n0&&dm1==n1){
	  //System.out.println("OCCUR CHECK");
	  return true;
      }
      else {
	if(dm0>=0){
	  //dm is not a var, so look deeper
	  int[] ldm = lookup(dm0, dm1);
 	  int newM0 = dm0+1;
 	  int newM1 = dm1;
	  if (occursIn(newM0, newM1, ldm[1]+dm0, n0, n1)){
	    return true;
	  }
	}
	int[] lm = lookup(m0, m1);
	int newM0 = m0+lm[1];
	int newM1 = m1;
	return occursIn(newM0, newM1, mEnd, n0, n1);
      }
    }
  }


  /**
   * Assigns the variable specified by <code>var0,var1</code> its value (substitution) specified by <code>val0,val1</code>.
   * @param var0 together with <code> var1</code>, specifying the location of the variable.
   * @param var1 together with <code> var0</code>, specifying the location of the variable.
   * @param val0 together with <code> val1</code>, specifying the location of the value (substitution).
   * @param val1 together with <code> val0</code>, specifying the location of the value (substitution).
   */
  void assignToVar(int var0, int var1, int val0, int val1){
    int[] val = new int[2];
    val[0] = val0;
    val[1] = val1;
    vars[var1][-1-var0]=val;
  }
  /**
   * Together with {@link #derefed1}, points to the result of dereferencing the symbol.
   */
  private int derefed0;
  /**
   * Together with {@link #derefed0}, points to the result of dereferencing the symbol.
   */
  private int derefed1;
  /**
   *  If the symbole specified by <code> m0, m1 </code> is a variable and it has a substitution as a result of the unification,
   * <code> deref </code> will find out the location of the substitution and assigns them to {@link #derefed0} and {@link #derefed1}.
   * Otherwise, the location of the symbole itself will be assigned to {@link #derefed0} and {@link #derefed1}.   
   * 
   */
  void deref(int m0, int m1){
    /* when optimizing, convert this to non-recursive */
    if(m0>=0){
      //m is not a variable
      int[] lm = symbols[m1][m0];
      if (lm[0] >= 0){
	//m does not reference a variable
	derefed0 = m0;
	derefed1 = m1;
	return;
      }
      else{
	//m does reference a variable
	//int[] newLM = {lm[0], m[1]};
	//remember which side m was on
	deref(lm[0], m1);
      }
    }
    else {
      //m is a variable
      int[] lm = vars[m1][-1-m0];
      if(lm[0] == UNDEFINED){
	derefed0 = m0; derefed1 = m1;
	return;
      }
      else {
	//m has a value
	deref(lm[0], lm[1]);
	int dlm0 = derefed0;
	int dlm1 = derefed1;
	//perform path compression, so m directly refers to its value
	assignToVar(m0, m1, dlm0, dlm1);
	return;
      }
    }
  }
/**
 * Looks up and returns in an <code>int</code> array of size 2 the symbol that is specified by <code>m0,m1</code>.
 * If the symbol is a variable and it has a substitution as a result of the unification, the substitution will be returned instead.
 * @param m0 -together with <code> m1</code>, pinpoints the location of the symbol.
 * @param m1 -together with <code> m0</code>, pinpoints the location of the symbol.
 * @return - an <code>int</code> array of size 2.
 */
  int[] lookup(int m0, int m1){
    if(m0<0)
      return vars[m1][-1-m0];
    else
      return symbols[m1][m0];
  }

/**
 * Apply the substitution to the rule clause and return it.
 * @throws an UnifyException is thrown if the rule clause and the fact clause has not unified or can not unify.
 * 
 */


  public DefiniteClause resolvent() throws Unifier.UnifyException {
    if (!unified)
      throw new Unifier.UnifyException("Attempt to use a substitution without a unification");
    Vector sv = new  Vector(); // Vector of symbols in result
    int[][] vv = new int[variablesSize][]; // variables in result
    variablesCount = 0;
    int start = 0;
    int end = symbols[RULE].length;
    termToSkip[0] = symbols[RULE][0][1];
    termToSkip[1] = RULE;
    int len = apply(start, RULE, end, 0, sv, vv);
    DefiniteClause newRule = new DefiniteClause(sv, variableNames,  symbolTable);
    return newRule;
  }
  /**
   * Used by {@link #apply} indicating the subterm that is skipped.
   */
  private int[] termToSkip = new int[2];

  /**
     Applying a given substitution. Function <code>apply</code> builds
     sv and vv to contain, respectively, the symbol
     (and variable) references, and the variables' replacements.  As
     it constructs these vectors, it must update the lengths
     associated with each subterm.  A recursive call to apply is done
     to create the inner term, and that call returns the number of
     symbols, or the length, of the inner term.  This value is then
     assigned into the reference that has already been put into the sv
     vector.
     When applying substitutions it is sometimes important to leave
     out certain subterms, for example during factoring and resolution.

     @param m - an <code>int[]</code> value
     @param end - an <code>int</code> value
     @param length - an <code>int</code> value
     @param sv - a <code>Vector</code> value
     @param vv - a <code>Vector</code> value
     @return an <code>int</code> value
  */
  int apply(int m0, int m1, int end, int length, Vector sv, int[][] vv){
    /* when optimizing, convert this to non-recursive */
    if(m0 == end)
      return length;
    else {
      deref(m0, m1);
      int dm0 = derefed0;
      int dm1 = derefed1;
      int[] ldm = lookup(dm0,dm1);
      if(dm0<0){
	//dereferenced to a var
	//decide if this is the first time we've seen this var
	//is linear search the best idea? probably so
	int i = 0; boolean found = false;
	while(i<variablesCount && !found){
	  int[] oldv = vv[i];
	  if(oldv[0]==dm0 && oldv[1]==dm1)
	    found=true;
	  else
	    i++;
	}
	int vnum;
	if(!found){
	  //if this is the first time, add it to the vv (vector of vars)
	  vnum = variablesCount;
	  int[] dm = {dm0, dm1};
	  vv[variablesCount] = dm;
	  variablesCount++;
	  if(dm1 == RULE){
	    variableNames[i] = clauses[RULE].variableNames[-dm0-1];
	  }
	  else {//dm1 = FACT
	    variableNames[i] = clauses[FACT].variableNames[-dm0-1];
	  }
	}
	else
	  vnum = i;
	//	System.out.println("db "+(-1-vnum)+" "+1);
	int[] newv = {-vnum-1, 1};
	sv.addElement(newv);
	length++;
      }
      else{
	//dereferenced to a symbol
	//if it is not a term to be skipped
	//add it to the sv (symbol vector) and
	//recurse for the inner stuff
	if(m0 != termToSkip[0] || m1 != termToSkip[1]){
	  int[] newldm = {ldm[0], ldm[1]};
	  //make a copy of the array so we can change the length
	  sv.addElement(newldm);
	  //	System.out.println("db "+newldm[0]+" "+newldm[1]);

	  int nextdm0 = dm0+1;
	  int nextdm1 = dm1;
	  int sublength = apply(nextdm0, nextdm1, dm0+ldm[1], 0, sv, vv);
	  newldm[1] = sublength+1;
	  length += sublength+1;
	}
      }
      int[] lm = lookup(m0, m1);
      m0+=lm[1];
      return apply(m0, m1, end, length, sv, vv);
    }
  }




//    public void dump(){
//      for(int j = 0; j<symbols.length; j++){
//        System.out.println("Symbols" + j);
//        for(int i = 0; i < symbols[j].length;i++){
//  	System.out.print(i + "  ");
//  	if(symbols[j][i][0]>=0){
//  	  if(symbols[j][i][0]>SymbolTable.current.size())
//  	    System.out.println("$" + symbols[j][i][0]);
//  	  else{
//  	    System.out.print(SymbolTable.current.
//  			     printName(symbols[j][i][0])+" ");
//  	    System.out.print("("+symbols[j][i][0]+")");
//  	  }
//  	}
//  	else
//  	  System.out.print(symbols[j][i][0]+" ");
//  	System.out.println(symbols[j][i][1]);
//        }
//        System.out.println("Vars" + j );
//        for(int i = 0; i < vars[j].length;i++){
//  	System.out.print(-i-1 + "  ");
//  	System.out.print(vars[j][i][0]+" ");
//  	System.out.println(vars[j][i][1]);
//        }
//      }
//    }


  /**
     <code>UnifyException</code> exception thrown if a problem with
     * Unify, in particular if someone tries to apply a
     * substitution that does not exist.

     @author Bruce Spencer <bspencer@unb.ca>
     @version

     Created: Tue Sep 19 13:03:14 2000
     @see Exception
  */
  class UnifyException extends RuntimeException{
    public UnifyException(String msg){
      super(msg);
    }
    public UnifyException(){
      super();
    }
  }
}

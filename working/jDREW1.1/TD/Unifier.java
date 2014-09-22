/**
  Copyright 2002 Bruce Spencer
*/

package jDREW.TD;

import java.util.*;

/**
   A <code>Unifier</code> is an object that performs two separate
   functions: it tells whether two clauses literals that occur in
   Definite clauses can unify, and it builds the resulting
   clauses after the required substitution, required for the
   unification, have been applied.

   In this file the clauses are taken from a DCTree, so the constructor is given a goal, which is to be solved in the tree, and a subgoallist, which has both the head of a definite clause

   @author Bruce Spencer <bspencer@unb.ca>
   @version

   Created: Fri Nov 23 07:18:16 2001 */
public class Unifier{

  static final int GOAL = 0;
  static final int SUBGOALLIST = 1;

  static final int UNDEFINED = 0;

  static final int DCTREE_MODE = 0;


  int[][][] symbols = null;
  int[][][] vars = null;
  String[] variableNames;
  int variablesSize;
  int variablesCount;
  boolean unified = false;
  int mode;
  int rLeft;
  int rRight;
  DCTree.Goal goal;
  DCTree.GoalList subGoalList;


  /**
      Returns with unified set to true if the Clause left at
      literal in position iLeft can unify with Clause right in
      position iRight.  If so then the variables in the array
      vars are set.  You may then call apply to create a new
      clause with the variables' values placed in the list of
      symbols.

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

*/

  Unifier(DCTree.Goal goal, DCTree.GoalList subGoalList, int mode){
    this.mode = mode;
    if(mode == DCTREE_MODE){
      this.goal = goal;
      this.subGoalList = subGoalList;
      symbols = new int[2][][];
      symbols[GOAL] = goal.goalList.symbols;
      symbols[SUBGOALLIST] = subGoalList.symbols;
      vars = new int[2][][];
      vars[GOAL] = new int[goal.goalList.varCount][2];
      vars[SUBGOALLIST] = new int[subGoalList.varCount][2];
      //the number of distinct variables resulting
      //will not exceed the number of distinct variables given
      variablesSize = goal.goalList.varCount + subGoalList.varCount;
      variableNames = new String[variablesSize];
      int mEnd = goal.symbolIndex + symbols[GOAL][goal.symbolIndex][1];
      unified = unify(goal.symbolIndex, GOAL, mEnd, 0, SUBGOALLIST);
    }
  }

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



  void assignToVar(int var0, int var1, int val0, int val1){
    int[] val = new int[2];
    val[0] = val0;
    val[1] = val1;
    vars[var1][-1-var0]=val;
  }

  private int derefed0;
  private int derefed1;

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

  int[] lookup(int m0, int m1){
    if(m0<0)
      return vars[m1][-1-m0];
    else
      return symbols[m1][m0];
  }




  public void applyToGoal() throws Unifier.UnifyException {
    if (!unified)
      throw new Unifier.UnifyException("Attempt to use a substitution without a unification");
    Vector sv = new  Vector(); // Vector of symbols in result
    int[][] vv = new int[variablesSize][]; // variables in result
    variablesCount = 0;
    if (mode == DCTREE_MODE){
      int start = 0;
      int end = symbols[GOAL].length;
      int len = apply(start, GOAL, end, 0, sv, vv);
      goal.goalList.loadSymbols(sv);
      goal.goalList.hasVariableNames = true;
      //        goal.goalList.variableNames = null;
      goal.goalList.variableNames = variableNames;
      goal.goalList.varCount = variablesSize;
      goal.setSymbolIndex();
    }
  }

  public void applyToGoalList() throws Unifier.UnifyException {
    if (!unified)
      throw new Unifier.UnifyException("Attempt to use a substitution without a unification");
    Vector sv = new  Vector(); // Vector of symbols in result
    int[][] vv = new int[variablesSize][]; // variables in result
    variablesCount = 0;
    if (mode == DCTREE_MODE){
      int start = 0;
      int end = symbols[SUBGOALLIST].length;
      int len = apply(start, SUBGOALLIST, end, 0, sv, vv);
      subGoalList.loadSymbols(sv);
      subGoalList.hasVariableNames = true;
      //        subGoalList.variableNames = null;
      subGoalList.variableNames = variableNames;
      subGoalList.varCount = variablesSize;
    }
  }


  /**
     Apply a given substitution.

     apply builds sv and vv to contain, respectively, the symbol
     (and variable) references, and the variables' replacements.  As
     it constructs these vectors, it must update the lengths
     associated with each subterm.  A recursive call to apply is done
     to create the inner term, and that call returns the number of
     symbols, or the length, of the inner term.  This value is then
     assigned into the reference that has already been put into the sv
     vector.

     When applying substitutions it is sometimes important to leave
     out certain subterms, for example during factoring and resolution.

  */

  /**
     <code>apply</code> method here.

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
	  if(dm1 == GOAL){
	    variableNames[i] = goal.goalList.variableNames[-dm0-1];
	  }
	  else {//dm1 = SUBGOAL
	    variableNames[i] = subGoalList.variableNames[-dm0-1];
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

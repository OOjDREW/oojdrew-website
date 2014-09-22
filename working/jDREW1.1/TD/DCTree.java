

package jDREW.TD;

import java.util.*;
import java.io.IOException;
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

  Definite Clause Trees are rooted clause trees
  built entirely from definite clauses.  The root
  clause node is labeled with a negative clause and
  all other clause nodes are labeled with definite clauses.

  It is more common to describe such clause trees as goal trees
  where each goal is a positive literal, and the root of the
  clause tree is some placeholder.  Thus we have several
  Java classes described below.

  <p> In the following GoalList and SubGoalList are synonymous,
  but SubGoalList is used to emphasize that we are discussing
  the list of children of a Goal, as opposed to the list of
  siblings of which the Goal is a member.


  <ul>

  <li>Goal - contains an atom (atomic formula) to be solved. Each
  Goal object belongs to exactly one GoalList.  Also each Goal
  can have at most one SubGoalList attached to it at a given
  time.  The SubGoalList is attached by calling
  attachSubGoalList, and this GoalList forms the children of the
  Goal in the GoalTree.  The possible GoalLists relevant for this
  Goal are created by first constructing the SubGoalListIterator
  and then calling next() on this Iterator to generate
  successively the SubGoalLists.  By relevant, we mean that the
  Goal is unifiable with the head atom of a Definite Clause in
  the DCTree's input clauses.

  <p> A Goal may be partially or fully solved.  A Goal with an
  attached SubGoalList is fully solved if that SubGoalList is empty,
  or if all of the Goals in that SubGoalList are solved.  In
  this case we also say that the SubGoalList is solved.  Solved
  is synonymous with fully solved.  A Goal or its attached
  SubGoalList is partially solved if not fully solved and not
  failed (see below).

  <p> A Goal may also become failed, which means that there are
  no SubGoalLists available for it, or all SubGoalLists available
  for it are failed GoalLists.  A failed GoalList contains at
  least one failed Goal. A Goal informs the GoalList of which it
  is a member that is a failed Goal by calling failed(this) on
  the GoalList. Recall that a Goal knows this only after all of
  its SubGoalLists have been found to be failed GoalLists, and
  since this is under programmer control, it is up to the
  programmer to make the call to failed.  Depending on the client
  program, this method may or may not be used as it is sets
  variables that are provided only for the user's convenience of
  record keeping, and do not initiate any other activity in the
  DCTree.

  <li>GoalList - contains a list of Goals and is attached to a
  specific Goal which is called its parent.  If and when a Goal
  in the list becomes solved, or partially solved, the effect is
  to bind variables in the atomic formula.  These bindings are
  immediately applied to the sibling Goals in this SubGoalList.
  They may also be propogated to the parent Goal, by calling
  subGoalListSolved() on the parent when the parent is attached
  to this SubGoalList.  By default, this call must be done by the
  programmmer, but the default behaviour can be set to
  PROPAGATE_FULLY_SOLVED, which changes it so that subGoalSolved
  is called on the parent when the SubGoalList is fully solved.
  The default behaviour can also be set to PROPAGATE_EAGERLY
  which will propagate eagerly: subGoalSolved is called
  automatically whenever a GoalList becomes more fully solved by
  the solution of one more of its member Goals.

  <p> Because a Goal may be attached to a failed SubGoalList, but
  other SubGoalLists are still available for it, it is important
  to be able undo the effects of this failed SubGoalList on the
  Goal, and consequently effects that have may been propagated
  throughout the DCTree. Thus each Goal and each GoalList has a
  backup facility that makes it possible to retrieve a previous
  version, replacing the old values the variable bindings.  In
  the case of the GoalList, the backup facility also retrieves a
  records of what member Goals have been solved. The backup
  facility is used through the createBackup() and restoreBackup()
  routines.  Thus backups are done manually, not automatically,
  and are based on a stack; N calls to createBackup() followed by
  M calls to restoreBackup(), when N<=M, will restore to the
  point when N-M call to createBackup() was made. If M>N, an
  exception is thrown.

  (We may consider automatic backups through the tree but the
  problem is that backups need to be synchronized so that the
  tree is brought back to a consistent overall state.  This is
  also the reason why propagation from GoalLists to Goals is not
  automatic by default.  One can keep the effects of changes to
  the Clause tree local in a GoalList until that GoalList is
  fully solved, and only then propagate it.  This removes the
  need to undo the results of propagating partially solved
  GoalLists through the tree.)






  </ul>

  
 */

public class DCTree{

  public static final boolean INTERNAL_TRACE = false;
  public static final int REPORT_EVERY_N_INFERENCES = 5000;

  public DiscTree discTree;

  private Stack choicePoints;
  private int nExtensions;
  private int nInferences;
  private GoalList top;

  public DCTree(DiscTree discTree)
    throws ParseException {
    this.discTree = discTree;
    choicePoints = new Stack();
    nExtensions = 0;
    nInferences = 0;
  }

  public String toString(){
    StringBuffer b = new StringBuffer();
    b.append("Size " + nExtensions + "\n");
    toString(1, top, b);
    return b.toString();
  }

  private void toString(int indent, GoalList gl, StringBuffer b){
    for(int i = 0; i < indent; i++)
      b.append(" ");
    b.append(gl.toString());
    b.append("\n");
    for(int i = 1; i < gl.atomCount; i++){
      if (gl.memberGoals[i].state == Goal.HAS_SUBGOALLIST_STATE)
	toString(indent+2, gl.memberGoals[i].subGoalList, b);
    }
  }

  public class DepthFirstSolutionIterator implements Iterator {

    DepthFirstSolutionIterator(DefiniteClause queryClause){
      foundNext = false;
      failed = false;
      enteringFirstTime = true;
      top = new GoalList(queryClause);
      top.init();
    }

    DepthFirstSolutionIterator(GoalList goalList){
      foundNext = false;
      failed = false;
      enteringFirstTime = true;
      top = goalList;
      top.init();
    }

    boolean foundNext;
    boolean failed;
    boolean enteringFirstTime;

    public boolean hasNext(){
      if(foundNext)
	return true;
      else if(failed){
	foundNext = false;
	return false;
      }
      else{
	if(!enteringFirstTime)
	  if(!chronologicalBacktrack())
	    failed = true;
	boolean succeeded = false;
	while(!failed && !succeeded){
	  Goal g = firstOpenGoal();
	  if(DCTree.INTERNAL_TRACE && g != null){
	    System.out.println("New Goal is " +
			       g + " in state " + g.stateToString());
	  }
	  if(g==null)
	    succeeded = true;
	  else if(g.hasMoreChoices()){
	    choicePoints.push(g);
	    g.nextChoice(Goal.PROPAGATE_WHEN_SOLVED);
	  }
	  else {
	    g.refreshChoices();
	    if(!chronologicalBacktrack()){
	      failed = true;
	    }
	  }
	}
	foundNext = succeeded;
	return foundNext;
      }
    }

    public Object next() {
      if(!hasNext())
	throw new NoSuchElementException();
      foundNext = false;
      enteringFirstTime = false;
      return top;
    }

    public void remove() throws UnsupportedOperationException{
      throw new UnsupportedOperationException
	("DepthFirstSolutionIterator does not allow remove");
    }

    private boolean chronologicalBacktrack(){
      //go back to most recent choicepoint that has something to try
      //unbind any variables that were done since that choice

      boolean foundSomethingToRetry = false;
      while(!choicePoints.empty() && !foundSomethingToRetry){
	Goal g = (Goal) choicePoints.pop();
	if(DCTree.INTERNAL_TRACE){
	  System.out.println("BTing - Removing " + g.subGoalList + " from " + g);
	}
	g.undoChoice();
	if(g.hasMoreChoices()){
	  foundSomethingToRetry = true;
	  if(DCTree.INTERNAL_TRACE){
	    System.out.println("BTing - found something to retry: " + g);
	  }
	}
	else {
	  g.refreshChoices();
	}
      }
      return foundSomethingToRetry;
    }

  }

  public class IterativeDepthFirstSolutionIterator implements Iterator {

    IterativeDepthFirstSolutionIterator(GoalList goalList,
					int max, int by){
      foundNext = false;
      failed = false;
      enteringFirstTime = true;
      this.max = max;
      this.by = by;
      bumpedIntoLimit = false;
      top = goalList;
      top.init();
    }

    IterativeDepthFirstSolutionIterator(DefiniteClause queryClause,
					int max, int by){

      foundNext = false;
      failed = false;
      enteringFirstTime = true;
      this.max = max;
      this.by = by;
      bumpedIntoLimit = false;
      top = new GoalList(queryClause);
      top.init();
    }

    private boolean foundNext;
    private boolean failed;
    private boolean enteringFirstTime;
    private int max;
    private int by;
    private boolean bumpedIntoLimit;

    public boolean hasNext(){
      if(foundNext)
	return true;
      else if(failed && !bumpedIntoLimit){
	foundNext = false;
	return false;
      }
      else{
	if(!enteringFirstTime)
	  if(!iterativeDepthChronologicalBacktrack())
	    failed = true;
	boolean succeeded = false;
	while(!(failed && !bumpedIntoLimit) && !succeeded){
	  //failure in this search method means failure without
	  //having hit the depth limit, hence "!failed" is
	  //replaced by "!(failed && !bumpedIntoLimit)"
	  Goal g = firstOpenGoal();
	  if(DCTree.INTERNAL_TRACE && g != null){
	    System.out.println("New Goal is " +
			       g + " in state " + g.stateToString());
	  }
	  if(g==null)
	    if (max-by < nExtensions && nExtensions <= max)
	      succeeded = true;
	    else{
	      if(!iterativeDepthChronologicalBacktrack())
		failed = true;
	    }
	  else if(g.hasMoreChoices()){
	    int nextChoiceSize = g.nextChoiceSize();
	    choicePoints.push(g);
	    g.nextChoice(Goal.PROPAGATE_WHEN_SOLVED);
	    if(INTERNAL_TRACE){
	      System.out.println("NExtensions = " + nExtensions +
				 "\nnextChoiceSize = " + nextChoiceSize +
				 "\nmax = " + max);

	    }
	    if(nExtensions+nextChoiceSize> max){
	      //uses the number of new subgoals as an admissible heuristic
	      //in an a* search
	      bumpedIntoLimit = true;
	      if(!iterativeDepthChronologicalBacktrack())
		failed = true;
	    }

	  }
	  else {
	    g.refreshChoices();
	    if(!iterativeDepthChronologicalBacktrack())
	      failed = true;
	  }
	}
	foundNext = succeeded;
	return foundNext;
      }
    }

    public Object next() {
      if(!hasNext())
	throw new NoSuchElementException();
      foundNext = false;
      enteringFirstTime = false;
      return top;
    }

    public void remove() throws UnsupportedOperationException{
      throw new UnsupportedOperationException
	("DepthFirstSolutionIterator does not allow remove");
    }

    private boolean iterativeDepthChronologicalBacktrack(){
      //go back to most recent choicepoint that has something to try
      //unbind any variables that were done since that choice

      boolean foundSomethingToRetry = false;
      while(!choicePoints.empty() && !foundSomethingToRetry){
	Goal g = (Goal) choicePoints.pop();
	if(DCTree.INTERNAL_TRACE){
	  System.out.println("BTing - Removing " + g.subGoalList + " from " + g);
	}
	g.undoChoice();
	if(g.hasMoreChoices()){
	  foundSomethingToRetry = true;
	  if(DCTree.INTERNAL_TRACE){
	    System.out.println("BTing - found something to retry: " + g);
	  }
	}
	else {
	  g.refreshChoices();
	}
      }
      if(!foundSomethingToRetry){
	if(bumpedIntoLimit){
	  max+=by;
	  if(DCTree.INTERNAL_TRACE){
	    System.out.println("Increasing the limit from " +
			       (max-by) + " to " + max);
	  }
	  bumpedIntoLimit = false;
	  foundSomethingToRetry = true;
	}
      }
      return foundSomethingToRetry;
    }

  }

  public Iterator depthFirstSolutionIterator(DefiniteClause queryClause){
    return new DepthFirstSolutionIterator(queryClause);
  }
  public Iterator depthFirstSolutionIterator(GoalList goalList){
    return new DepthFirstSolutionIterator(goalList);
  }

  public Iterator iterativeDepthFirstSolutionIterator
    (DefiniteClause queryClause, int max, int by){
    return new IterativeDepthFirstSolutionIterator(queryClause, max, by);
  }
  public Iterator iterativeDepthFirstSolutionIterator
    (DefiniteClause queryClause){
    return new IterativeDepthFirstSolutionIterator(queryClause, 1, 1);
  }
  public Iterator iterativeDepthFirstSolutionIterator
    (GoalList goalList, int max, int by){
    return new IterativeDepthFirstSolutionIterator(goalList, max, by);
  }
  public Iterator iterativeDepthFirstSolutionIterator
    (GoalList goalList){
    return new IterativeDepthFirstSolutionIterator(goalList, 1, 1);
  }


  public Goal firstOpenGoal(){
    //This is a shortcut to finding the first open goal
    // we will develop a better strategy involving an openGoals iterator
    return top.firstOpenGoal();
  }





  class Goal {
    public static final int NO_AUTO_PROPAGATE = 0;
    public static final int PROPAGATE_WHEN_SOLVED = 1;
    public static final int PROPAGATE_EAGERLY = 2;

    public static final int INITIAL_STATE = 0;
    public static final int HAS_SUBGOALLIST_STATE = 1;
    public static final int HAS_NO_SUBGOALLIST_STATE = 2;
    public static final int HAS_BUILTIN_SOLUTION_STATE = 3;
    public static final int HAS_NO_BUILTIN_SOLUTION_STATE = 4;
    public static final int HAS_NAF_SOLUTION_STATE = 5;
    public static final int HAS_NO_NAF_SOLUTION_STATE = 6;
    public static final int HAS_UNCONSUMED_NAF_SOLUTION_STATE = 7;

    public static final int PROVE_BY_SUBGOALLIST = 0;
    public static final int PROVE_BY_BUILTIN = 1;
    public static final int PROVE_BY_NAF = 2;

    int state;
    int proveByType;

    String stateToString(){
      switch (state) {
      case INITIAL_STATE : {return "INITIAL_STATE";}
      case HAS_SUBGOALLIST_STATE : {return "HAS_SUBGOALLIST_STATE";}
      case HAS_NO_SUBGOALLIST_STATE  : {return "HAS_NO_SUBGOALLIST_STATE";}
      case HAS_BUILTIN_SOLUTION_STATE  : {return "HAS_BUILTIN_SOLUTION_STATE";}
      case HAS_NO_BUILTIN_SOLUTION_STATE  : {return "HAS_NO_BUILTIN_SOLUTION_STATE";}
      case HAS_NAF_SOLUTION_STATE  : {return "HAS_NAF_SOLUTION_STATE";}
      case HAS_NO_NAF_SOLUTION_STATE  : {return "HAS_NO_NAF_SOLUTION_STATE";}
      case HAS_UNCONSUMED_NAF_SOLUTION_STATE  : {return "HAS_UNCONSUMED_NAF_SOLUTION_STATE";}
      }
      return "No string for this state " + state;
    }

    Goal(GoalList goalList, int literalIndex){
      this.goalList = goalList;
      this.literalIndex = literalIndex;
      solved = false;
      subGoalList = null;
      state = INITIAL_STATE;
    }

    boolean nafGoal(){
      int nafPosition = discTree.symbolTablePosition("~",1);
      return goalList.symbols[symbolIndex][0] == nafPosition;
    }

    SubGoalListIterator sglit;
    GoalList goalList;
    int literalIndex;
    int symbolIndex;
    int propagateMode;
    boolean solved;
    GoalList subGoalList;

    boolean hasMoreChoices(){
      if(proveByType == PROVE_BY_SUBGOALLIST){
	return hasMoreSubGoalLists();
      }
        else if (proveByType == PROVE_BY_NAF){
  	if(state == HAS_NO_NAF_SOLUTION_STATE ||
	   state == HAS_NAF_SOLUTION_STATE)
  	  return false;
  	else if(state == INITIAL_STATE){
  	  String negatedGoalString = this.toString();
  	  String goalString = negatedGoalString.
  	    substring(2,negatedGoalString.length()-1);
  	  if(DCTree.INTERNAL_TRACE)
  	    System.out.println("Unnegated goal " + goalString);
  	  DCTree nafTree = null;
	  DefiniteClause nafQueryClause = null;
  	  try{
  	    String newTreeString = goalString + ".";
	    DCFileParser dcfp = new DCFileParser(discTree.symbolTable);
	    nafQueryClause = dcfp.parseQueryClause(newTreeString);

  	    if(DCTree.INTERNAL_TRACE)
  	      System.out.println("New DCTree for " + newTreeString);
  	    nafTree = new DCTree(discTree);
  	  } catch (ParseException e){
  	    e.printStackTrace();
	    System.exit(-1);
  	  }
  	  Iterator nafSolver =
  	    nafTree.depthFirstSolutionIterator(nafQueryClause);

  	  if(nafSolver.hasNext())
  	    state = HAS_NO_NAF_SOLUTION_STATE;
  	  else
  	    state = HAS_UNCONSUMED_NAF_SOLUTION_STATE;
  	}
  	return state == HAS_UNCONSUMED_NAF_SOLUTION_STATE;
        }
      // else if (provedByType == PROVED_BY_BUILTIN){
      else
	return false;//Can't happen unless proveByType is missing
    }

    void nextChoice(int propagateMode){
      if (DCTree.INTERNAL_TRACE)
	System.out.println("Trying next choice for " + this);
      if(!hasMoreChoices())
	throw new DCException("Attempted next choice for " +
			      this + ", but no more choices exist");
      else if(proveByType == PROVE_BY_SUBGOALLIST){
	attachNextSubGoalList(propagateMode);
      }
      else if (proveByType == PROVE_BY_NAF){
	solved = true;
	state = HAS_NAF_SOLUTION_STATE;
	goalList.notifySolved(this);
      }
      // else if (provedByType == PROVED_BY_BUILTIN){
      nExtensions++;
      nInferences++;
      if(DCTree.REPORT_EVERY_N_INFERENCES > 0){
	if(nInferences % DCTree.REPORT_EVERY_N_INFERENCES == 0)
	System.out.println("Performed " + nInferences + " inferences");
      }
    }

    int nextChoiceSize(){
      if(!hasMoreChoices())
	throw new DCException("Attempted next choice for " +
			      this + ", but no more choices exist");
      else if(proveByType == PROVE_BY_SUBGOALLIST)
	return sglit.nextGoalListSize()-1;
      else
	return 1;
    }

    void undoChoice(){
      if (DCTree.INTERNAL_TRACE)
	System.out.println("Undoing choice for " + this);
      if(proveByType == PROVE_BY_SUBGOALLIST)
	removeSubGoalList();
      else if (proveByType == PROVE_BY_NAF){
	removeNAFSolve();
	state = HAS_NO_NAF_SOLUTION_STATE;
      }
      //else if (proveByType == PROVED_BY_BUILTIN)
      nExtensions--;
    }


    void refreshChoices(){
      if (DCTree.INTERNAL_TRACE)
	System.out.println("refreshing choices for " + this);
      if(proveByType == PROVE_BY_SUBGOALLIST)
	state = INITIAL_STATE;
      else if (proveByType == PROVE_BY_NAF)
	state = INITIAL_STATE;
      //else if (proveByType == PROVED_BY_BUILTIN)
    }

    void removeNAFSolve(){
      if(solved){
	//should be solved.  If not we should not be here
	goalList.notifyUnsolved(this);
	solved = false;
      }
    }

    boolean hasMoreSubGoalLists(){
      if(state == INITIAL_STATE){
	state = HAS_NO_SUBGOALLIST_STATE;
	sglit = new SubGoalListIterator(this,
					SubGoalListIterator.APPLY_TO_GOALLIST);
	if(DCTree.INTERNAL_TRACE){
	  SubGoalListIterator sglit_1 = new SubGoalListIterator(this,
					  SubGoalListIterator.APPLY_TO_GOALLIST);
	  System.out.println("Choices for Goal " + this + " are " );
	  int count = 0;
	  while(sglit_1.hasNext()){
	    count++;
	    System.out.println(count + " " + sglit_1.next());
	  }
	  System.out.println("Total of " + count + " choices" );
	}
      }
      return sglit.hasNext();
    }

    void attachNextSubGoalList(int propagateMode){
      //have already checked that a new subgoalist exists
      //first undo any bindings that might already have been propagated
      if(state == HAS_SUBGOALLIST_STATE)
	removeSubGoalList();
      GoalList subGoalList = (GoalList) sglit.next();
      if (DCTree.INTERNAL_TRACE)
	System.out.println("Adding " + subGoalList + " to goal " + this);
      this.subGoalList = subGoalList;
      this.propagateMode = propagateMode;
      subGoalList.setParent(this);
      state = HAS_SUBGOALLIST_STATE;
      if(subGoalList.solved()){
	solved = true;
	subGoalList.propagateBindingsToParent();
	goalList.notifySolved(this);
      }
      if(propagateMode == PROPAGATE_EAGERLY){
	subGoalList.propagateBindingsToParent();
      }
    }

    void removeSubGoalList(){
      if (DCTree.INTERNAL_TRACE)
	System.out.println("Removing " + this.subGoalList + " from goal " + this);
      if(solved){
	goalList.notifyUnsolved(this);
	solved = false;
      }
      state = HAS_NO_SUBGOALLIST_STATE;
      subGoalList = null;
    }

    public void setSymbolIndex(){
      goalList.setSymbolIndex();
    }

    public String toString(){
      StringBuffer b = new StringBuffer();
      setSymbolIndex();
      goalList.bufferAppend(b, symbolIndex,
			    symbolIndex+goalList.symbols[symbolIndex][1]);
      return b.toString();
    }

  }//Goal

  public class GoalList{
    GoalList(DefiniteClause dc){
      symbols = dc.symbols;
      varCount = dc.varCount;
      if(dc.hasVariableNames){
	hasVariableNames = true;
	variableNames = dc.variableNames;
      }
      else
	hasVariableNames = false;
    }
    GoalList(int[][] symbols, int varCount){
      this.symbols = symbols;
      this.varCount = varCount;
      hasVariableNames = false;
    }
    GoalList(Vector sv, int varCount){
      loadSymbols(sv);
      this.varCount = varCount;
      hasVariableNames = false;
    }
    GoalList(int[][] symbols){
      this.symbols = symbols;
      countVars();
      hasVariableNames = false;
    }
    GoalList(Vector sv){
      loadSymbols(sv);
      countVars();
      hasVariableNames = false;
    }
    void init(){
      countAtoms();
      memberGoals = new Goal[atomCount];
      memberGoals[0]=null;
      for(int i = 1; i < atomCount; i++){
	memberGoals[i] = new Goal(this, i);
      }
      setSymbolIndex();
      for(int i = 1; i < atomCount; i++){
	if(memberGoals[i].nafGoal())
	  memberGoals[i].proveByType = Goal.PROVE_BY_NAF;
	else
	  memberGoals[i].proveByType = Goal.PROVE_BY_SUBGOALLIST;
	//else -- add in builtins
      }
    }

    public void loadSymbols(Vector v){
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
    void countAtoms(){
      int i = 0;
      while(i<symbols.length){
	atomCount++;
	i+=symbols[i][1];
      }
    }

    void countVars(){
      varCount = 0;
      for(int i = 0; i < symbols.length; i++){
	if(symbols[i][0]<0)
	  varCount++;
      }
    }
    void setSymbolIndex(){
      int symbolIndex = 0;
      for(int i = 1; i < atomCount; i++){
	symbolIndex += symbols[symbolIndex][1];
	memberGoals[i].symbolIndex = symbolIndex;
      }
    }
    int varCount;
    String[] variableNames;
    boolean hasVariableNames;
    int atomCount=0;
    int[][] symbols;
    Goal parent;
    Goal[] memberGoals;
    boolean hasParent = false;
    boolean solved = false;
    void setParent(Goal goal){
      parent = goal;
      hasParent = true;
    }
    void notifySolved(Goal g){
      if(solved()){
	if(hasParent){
	  parent.solved=true;
	  if(parent.propagateMode==Goal.PROPAGATE_WHEN_SOLVED)
	    propagateBindingsToParent();
	  parent.goalList.notifySolved(parent);
	}
      }
    }

    boolean solved(){
      int i = 1;
      while(i<atomCount){
	if(!memberGoals[i].solved)
	  return false;
	else
	  i++;
      }
      return true;
    }

    void notifyUnsolved(Goal g){
      if (DCTree.INTERNAL_TRACE) System.out.println("Unsolving goal " + g );
      if(g.propagateMode == Goal.PROPAGATE_WHEN_SOLVED){
	if(g.proveByType == Goal.PROVE_BY_SUBGOALLIST){
	  g.goalList.restoreBackup();
	  if (DCTree.INTERNAL_TRACE) System.out.println("Restoring unsolved goal to " + g );
	}
      }
      if(solved()){
	if(hasParent)
	  parent.goalList.notifyUnsolved(parent);
      }
      g.solved=false;
    }

    public void propagateBindingsToParent(){
      if(!hasParent){
	System.out.println(this + "has no parent");
	//Should we throw an exception here?
      }
      else{
	if (DCTree.INTERNAL_TRACE) System.out.println("moving bindings of " + this + " to " + parent );
	Unifier u = new Unifier(parent, this, Unifier.DCTREE_MODE);
	if(u.unified){
	  parent.goalList.createBackup();
	  u.applyToGoal();
	  this.setSymbolIndex();
	  if (DCTree.INTERNAL_TRACE) System.out.println("Now they are moved to " + parent );
	}
	else
	  throw new DCException("Could not propagate bindings");
      }
    }

    public String toString(){
      StringBuffer b = new StringBuffer();
      int endOfHead = symbols[0][1];
      bufferAppend(b, 0, endOfHead);
      b.append(":-");
      bufferAppend(b, endOfHead, symbols.length);
      b.append(".");

      return b.toString();
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
	  else{
	    b.append('V');
	    b.append(-symb-1);
	  }
	}
	else if(symb >= discTree.symbolTable.size()){
	  //symbol not in table, so not "interned" in the LISP vernacular
	  b.append("$"+symb);
	}
	else {
	  String s = discTree.symbolTable.symbolTableString(symb);
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
	  if(s.equals("++/1")||s.equals("--/1")){
	    bufferAppend(b, from+1, from + len);
	    from+=len-1;
	  }
	  else if(len>1){
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

    //    public GoalIterator openGoals(){
    //    }

    public Goal firstOpenGoal(){
      for(int i = 1; i < atomCount; i++){
	Goal g = memberGoals[i];
	if(g.proveByType == Goal.PROVE_BY_SUBGOALLIST){
	  if(g.state != Goal.HAS_SUBGOALLIST_STATE &&
	     !g.solved){
	    return g;
	  }
	  else if(!g.solved)
	    return g.subGoalList.firstOpenGoal();
	}
	else if(g.proveByType == Goal.PROVE_BY_NAF)
 	  if(g.state == Goal.INITIAL_STATE)
	    return g;
	//else if (g.proveByType == Goal.PROVE_BY_BUILTIN)
	//if(g.state == INITIAL) return g;
      }
      return null;//means no open goals
    }


    public Goal head(){
      return new Goal(this, 0);
    }
    private Stack s = new Stack();



    public void createBackup(){
      s.push(new StackFrame(symbols, varCount, hasVariableNames, variableNames));
    }

    public void restoreBackup(){
      if(s.empty()){
	System.out.println("Popped empty stack");
      }
      else {
	StackFrame sf = (StackFrame) s.pop();
	symbols = sf.symbols;
	varCount = sf.varCount;
	hasVariableNames = sf.hasVariableNames;
	variableNames = sf.variableNames;
	setSymbolIndex();
      }
    }

    public void purgeBackup(){
      if(s.empty())
	throw new DCException("Popped empty stack");
      StackFrame sf = (StackFrame) s.pop();
    }


    class StackFrame {
      int[][] symbols;
      int varCount;
      boolean hasVariableNames;
      String[] variableNames;
      StackFrame(int[][] symbols, int varCount, boolean hasVariableNames, String[] variableNames){
	this.symbols = new int[symbols.length][];
	//for(int i = 0; i < symbols.length; i++){
	//  this.symbols[i] = symbols[i];
	//}
	System.arraycopy(symbols, 0, this.symbols, 0, symbols.length);
	this.varCount = varCount;
	this.hasVariableNames = hasVariableNames;
	if(hasVariableNames){
	  this.variableNames = new String[varCount];
	  System.arraycopy(variableNames, 0, this.variableNames, 0,
			   varCount);
	}
      }
    }
  }//DCTree.GoalList

  class SubGoalListIterator implements Iterator{
    public static final int APPLY_TO_GOALLIST = 0;
    public static final int APPLY_TO_GOAL_AND_GOALLIST = 1;
    public static final int APPLY_TO_GOAL = 2;
    SubGoalListIterator(Goal goal, int mode){
      this.goal = goal;
      this.uit = discTree.unifiableIterator(goal.goalList.symbols, goal.literalIndex);
      this.mode = mode;
      if (mode == APPLY_TO_GOALLIST){
      }
      else if (mode == APPLY_TO_GOAL_AND_GOALLIST){
      }
      else if (mode == APPLY_TO_GOAL){
      }
      else{
	throw new DCException("SubGoalListIterator mode not valid");
      }
    }
    private int mode;
    public DiscTree.UnifiableIterator uit;
    private GoalList nextGoalList;
    private Goal goal;
    private boolean foundNext = false;
    public boolean hasNext(){
      if(foundNext)
	return true;
      else if(!uit.hasNext())
	return false;
      else{
	nextGoalList = new GoalList( (DefiniteClause) uit.next());
	nextGoalList.init();
	Unifier u = new Unifier(goal, nextGoalList, Unifier.DCTREE_MODE);
	if(u.unified){
	  if(mode == APPLY_TO_GOALLIST
	     || mode == APPLY_TO_GOAL_AND_GOALLIST){
	    u.applyToGoalList();
	    nextGoalList.setSymbolIndex();
	  }
	  if(mode == APPLY_TO_GOAL_AND_GOALLIST
	     || mode == APPLY_TO_GOAL){
	    goal.goalList.createBackup();
	    u.applyToGoal();
	    goal.setSymbolIndex();
	  }
	  foundNext = true;
	  return true;
	}
	else
	  return hasNext();
      }
    }

    public Object next(){
      if(!hasNext())
	throw new NoSuchElementException();
      foundNext = false;
      //System.out.println("SubGoalListIterator returns next as " + nextGoalList);
      return nextGoalList;
    }

    public int nextGoalListSize(){
      if(!hasNext())
	throw new NoSuchElementException();
      return nextGoalList.atomCount;
    }


    public void remove() throws UnsupportedOperationException{
      throw new UnsupportedOperationException
	("SubGoalListIterator does not allow remove");
    }
  }
}//DCTree

class DCException extends RuntimeException{
  DCException(String s){
    super(s);
  }
}

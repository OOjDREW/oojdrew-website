/**
  Copyright 2002 Bruce Spencer
*/

package jDREW.TD;

import java.util.*;
import java.io.*;
import org.jdom.JDOMException;

import jDREW.TEST.*;
import jDREW.util.*;


public class PrologTopLevel{

  

  public static final boolean SHOW_TREE = true;

  public static void main(String args[]) throws IOException, ParseException, JDOMException {
  
    SymbolTable st = new SymbolTable();
    DiscTree dt = new DiscTree(st);
    DCTree myDCTree = new DCTree(dt);
    DCFileParser dcfp = new DCFileParser(st);
    dcfp.parseDCFile(EnvTool.localPath+"TD\\connected.dc");
    Iterator it = dcfp.iterator();
    while(it.hasNext()){
      dt.insert((DefiniteClause)it.next());
    }


    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    String queryString = null;
    while(true){
      try{
	System.out.println("Enter a query");
	queryString = br.readLine();
	DefiniteClause queryClause =
	  dcfp.parseQueryClause(queryString );

	Iterator dfSolver =
	  myDCTree.iterativeDepthFirstSolutionIterator(queryClause);

	boolean userWantsMore = true;
	while(userWantsMore && dfSolver.hasNext()){
	  DCTree.GoalList solution = (DCTree.GoalList) dfSolver.next();
	  if(SHOW_TREE)
	    System.out.println("Tree : " + myDCTree);

	  System.out.println(solution);
	  System.out.print("more? (y/n) [y] ");
	  String line = br.readLine().trim().toLowerCase();
	  userWantsMore = !(line.equals("n"));
	}
	if(userWantsMore && !dfSolver.hasNext())
	  System.out.println("No (more) answers");
      }
      catch(ParseException pe){
	System.out.println("Had trouble parsing query " +
			   queryString);
      }
      catch(EOFException eof){
	System.out.println("Encountered EOF on input stream");
	System.exit(-1);
      }
      catch(IOException ioe){
	System.out.println("Had trouble reading query ");
      }
    }
  }
}//PrologTopLevel



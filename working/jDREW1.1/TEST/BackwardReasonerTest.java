package jDREW.TEST;

import java.util.Iterator;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.TestCase;
import jDREW.util.*;
import jDREW.TD.*;

/**
 * @author jiak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class BackwardReasonerTest extends TestCase {

	/**
	 * Constructor for BackwardReasoner.
	 * @param arg0
	 */
	public BackwardReasonerTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(BackwardReasonerTest.class);
	}
	
	public void testRunBackwardReasoner()  {
		final String[] matchings={"$top:-a(w).",
								  "$top:-a(z)."};
		Vector resv=new Vector();
		try{
		
			SymbolTable st = new SymbolTable();
		    DiscTree dt = new DiscTree(st);
		    DCTree myDCTree = new DCTree(dt);
		    DCFileParser dcfp = new DCFileParser(st);
		    dcfp.parseDCFile(EnvTool.localPath+"TEST\\BackwardReasoner.dc");
		    Iterator it = dcfp.iterator();
		    while(it.hasNext()){
		      dt.insert((DefiniteClause)it.next());
		    }
		    
		    DefiniteClause queryClause =
		    dcfp.parseQueryClause("a(X).");
	
			Iterator dfSolver =
			  myDCTree.iterativeDepthFirstSolutionIterator(queryClause);
			
			while(dfSolver.hasNext()){
			  DCTree.GoalList solution = (DCTree.GoalList) dfSolver.next();
			  resv.add(solution.toString());
			}	    
			
			Assert.assertTrue(EnvTool.checkEqualOnStrings(resv,matchings));
		    
		}catch(Exception e){
			fail("unexpected exception catched");
		}
		
	}

}

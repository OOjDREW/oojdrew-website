package jDREW.TEST;

import java.util.Iterator;
import java.util.Vector;

import jDREW.util.DCFileParser;
import jDREW.util.DefiniteClause;
import jDREW.util.DiscTree;
import jDREW.util.SymbolTable;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author jiak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DiscTreeTest extends TestCase {

	/**
	 * Constructor for DiscTreeTest.
	 * @param arg0
	 */
	public DiscTreeTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(DiscTreeTest.class);
	}
	
	public void testUnifiableIterator()  {
		final String DCStream1="%sytax test begins\n % fact test\n a(b). a(a(b)). a(X,b). p(f(h(X)),h(Y),f(X),Y). p(f(g1),h,h(g2),g1). % rule test \n a(X,b):-a(b). a(a(b)):-a(b). p(f(h(X)),h(Y),f(X),Y) :- p(f(g1),h,h(g2),g1). % sytax test ends\n";
		final String goal="a(X).";
		final String[] unifiableMatchings={"a(b).",
										   "a(a(b)).",
										   "a(a(b)):-a(b)."};
		Vector resv=new Vector();
		try{
			
			SymbolTable st = new SymbolTable();
		    DiscTree dt = new DiscTree(st);
		    DCFileParser dcfp = new DCFileParser(st);
		    dcfp.parseDCStream(DCStream1);
		    DefiniteClause queryClause =  dcfp.parseDefiniteClause(goal);
		    
		    Iterator it = dcfp.iterator();
		    while(it.hasNext()){
		    	//DefiniteClause d=(DefiniteClause)it.next();
		    	//System.out.println(d.toString());
		      	dt.insert((DefiniteClause)it.next());
		    }
		    
		    DiscTree.UnifiableIterator ui=dt.unifiableIterator(queryClause,0);

	        while(ui.hasNext())
	        {
	          //String s=ui.next().toString();
	          //System.out.println(s);
	          resv.add(ui.next().toString());
	        }

	        Assert.assertTrue(EnvTool.checkEqualOnStrings(resv,unifiableMatchings));

		    
		}catch(Exception e){
			fail("unexpected exception catched");
		}
		
	}

}

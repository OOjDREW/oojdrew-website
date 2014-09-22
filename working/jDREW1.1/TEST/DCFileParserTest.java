package jDREW.TEST;

import junit.framework.Assert;
import junit.framework.TestCase;

import jDREW.util.DCFileParser;
import jDREW.util.DefiniteClause;
import jDREW.util.SymbolTable;
import java.util.*;
/**
 * @author jiak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DCFileParserTest extends TestCase {

	/**
	 * Constructor for DCFileParserTest.
	 * @param arg0
	 */
	Vector st1=new Vector(10);
	Vector cl=new Vector(10);
		
	public DCFileParserTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(DCFileParserTest.class);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
	    st1.add("null/0");
	    st1.add("b/0");
	    st1.add("a/1");
	    st1.add("h/1");
	    st1.add("f/1");
	    st1.add("p/4");
	    st1.add("g1/0");
	    st1.add("h/0");
	    st1.add("g2/0");
	    
	    
	    cl.add("a(b).");
	    cl.add("a(a(b)).");
	    cl.add("p(f(h(X0)),h(Y1),f(X0),Y1).");
	    cl.add("p(f(g1),h,h(g2),g1).");
	    cl.add("a(a(b)):-a(b).");
	    cl.add("p(f(h(X0)),h(Y1),f(X0),Y1):-p(f(g1),h,h(g2),g1).");
			    	    
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDCFileParser() {
		SymbolTable st = new SymbolTable();
	    DCFileParser fp = new DCFileParser(st);
	    fp.parseDCFile(EnvTool.localPath+"TEST//test.dc");
	    
	    Vector stv=new Vector();
	    Assert.assertEquals(st1.size(),st.size());
	    for(int i = 0; i < st.size(); i++){
      		stv.add(st.printName(i));
        }
	    
	    Assert.assertTrue(EnvTool.checkEqualOnStrings(stv,st1));
	    
	    DefiniteClause clause=null;
	    Assert.assertEquals(cl.size(),fp.inputClausesSize());
	    Vector dcv=new Vector();
	    for(int i = 0; i < fp.inputClausesSize(); i++){
 			clause = (DefiniteClause) fp.getInputClause(i);
 			dcv.add(clause.toString());

	    }
		Assert.assertTrue(EnvTool.checkEqualOnStrings(dcv,cl));
     
	}
	public void testDCStreamParser() {
		//String ruleStream="%sytax test begins\n% fact test\na(b).\na(a(b)).\np(f(h(X)),h(Y),f(X),Y).\np(f(g1),h,h(g2),g1).\n% rule test\na(a(b)):-a(b).\np(f(h(X)),h(Y),f(X),Y) :- p(f(g1),h,h(g2),g1).\n% sytax test ends\n";
		String ruleStream=EnvTool.DCStream;					
		SymbolTable st = new SymbolTable();
	    DCFileParser fp = new DCFileParser(st);
	    fp.parseDCStream(ruleStream);
	    Vector stv=new Vector();
	    Assert.assertEquals(st1.size(),st.size());
	    for(int i = 0; i < st.size(); i++){
      		stv.add(st.printName(i));
        }
	    
	    Assert.assertTrue(EnvTool.checkEqualOnStrings(stv,st1));
	   
	    DefiniteClause clause=null;
	    Iterator cit = fp.iterator();
	    Vector dcv=new Vector();
	    for(int i = 0; i < fp.inputClausesSize(); i++){
 			clause = (DefiniteClause) fp.getInputClause(i);
 			dcv.add(clause.toString());

	    }
		Assert.assertTrue(EnvTool.checkEqualOnStrings(dcv,cl));
	 
	}
}
	


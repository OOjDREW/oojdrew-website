package jDREW.TEST;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author jiak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AllTests {
	
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(AllTests.class);
	}
   	public static Test suite() {
		TestSuite suite = new TestSuite("Test for jDREW");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(DCFileParserTest.class));
		suite.addTest(new TestSuite(TokenStreamTest.class));
		suite.addTest(new TestSuite(ForwardReasonerTest.class));
		suite.addTest(new TestSuite(BackwardReasonerTest.class));
		suite.addTest(new TestSuite(DiscTreeTest.class));
		//$JUnit-END$
		return suite;
	}
}

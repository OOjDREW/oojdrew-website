package jDREW.TEST;

import jDREW.util.ParseException;
import jDREW.util.ParserBasic;
import jDREW.util.TokenStream;
import jDREW.util.TokenStream.Token;
import java.io.*;
import java.util.Vector;

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
public class TokenStreamTest extends TestCase {

	/**
	 * Constructor for TokenStreamTest.
	 * @param arg0
	 */
	
	TokenStream t = null;
	
	public TokenStreamTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TokenStreamTest.class);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSTRING_MODE(){
		
		
		try{
			
	 		 TokenStream t =new TokenStream("top :- regular('Ho nda'). %fref", 
			   ParserBasic.DELIMITERS, TokenStream.STRING_MODE);	
			 TokenStream.Token to=null;	
			 Vector tName=new Vector();
			 int[] tType =new int[8];
			 tName.add("top");
			 tName.add(":");
			 tName.add("-");
			 tName.add("regular");
			 tName.add("(");
			 tName.add("Ho nda");
			 tName.add(")");
			 tName.add(".");
			 
			 tType[0]=0;
			 tType[1]=2;
			 tType[2]=2;
			 tType[3]=0;
			 tType[4]=2;
			 tType[5]=1;
			 tType[6]=2;
			 tType[7]=2;
			 
			 for(int i=0;i<8;i++)
			 {
		      	Assert.assertTrue(t.hasMoreTokens());
		      	to=t.nextToken();
		      	Assert.assertEquals(to.getTokenName(),(String)tName.get(i));
		      	Assert.assertEquals(to.getTokenType(),tType[i]);
			 }
	      
	      	Assert.assertTrue(!t.hasMoreTokens());
	      
		  } catch (IOException e){
		   	  fail("IOException captured in testSTRING_MODE!");
		      e.printStackTrace();
		  } catch (ParseException e){
		   	  fail("ParseException captured! testSTRING_MODE");
		      e.printStackTrace();
    	  }
	   }
    	
    	public void testFILE_MODE() {
	
		try{
			
			TokenStream t =new TokenStream(EnvTool.localPath+"TEST//test.dc", 
			   ParserBasic.DELIMITERS, TokenStream.FILE_MODE);	
			TokenStream.Token to=null;	
			Vector tName=new Vector();
			int[] tType =new int[13];
			
			 tName.add("a");
			 tName.add("(");
			 tName.add("b");
			 tName.add(")");
			 tName.add(".");
			 tName.add("a");
			 tName.add("(");
			 tName.add("a");
			 tName.add("(");
			 tName.add("b");
			 tName.add(")");
			 tName.add(")");
			 tName.add(".");
			 
			 tType[0]=0;
			 tType[1]=2;
			 tType[2]=0;
			 tType[3]=2;
			 tType[4]=2;
			 tType[5]=0;
			 tType[6]=2;
			 tType[7]=0;
 			 tType[8]=2;
			 tType[9]=0;
			 tType[10]=2;
 			 tType[11]=2;
			 tType[12]=2;



			 
			 for(int i=0;i<13;i++)
			 {
		      	Assert.assertTrue(t.hasMoreTokens());
		      	to=t.nextToken();
		      	Assert.assertEquals(to.getTokenName(),(String)tName.get(i));
		      	Assert.assertEquals(to.getTokenType(),tType[i]);
			 }
	     	      
		   } catch (IOException e){
		   	  fail("IOException captured in testFILE_MODE!");
		      e.printStackTrace();
		   } catch (ParseException e){
		   	  fail("ParseException captured in testFILE_MODE!");
		      e.printStackTrace();
    	}
	}

}

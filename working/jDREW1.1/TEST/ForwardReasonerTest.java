package jDREW.TEST;

import java.util.Iterator;
import java.util.Vector;

import jDREW.BU.ForwardReasoner;
import jDREW.util.DCFileParser;
import jDREW.util.DefiniteClause;
import jDREW.util.DiscTree;
import jDREW.util.Heap;
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
public class ForwardReasonerTest extends TestCase {

	/**
	 * Constructor for ForwardReasonerTest.
	 * @param arg0
	 */
	public ForwardReasonerTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(ForwardReasonerTest.class);
	}

	public void testRunForwardReasoner() {
		
		String[] facts={
		"eats(fred,pears).",
		"eats(fred,t_bone_steak).",
		"eats(fred,apples).",
		"like(fred,pears).",
		"like(fred,t_bone_steak).",
		"like(fred,apples).",
		"hold_party(helen).",
		"birthday(fred).",
		"birthday(tom).",
		"birthday(helen).",
		"happy(helen).",
		"happy(mary).",
		"happy(jane).",
		"fun(honda_81).",
		"red(apple_1).",
		"red(block_1).",
		"red(car_27).",
		"car(desoto_48).",
		"car(edsel_57).",
		"blue(flower_3).",
		"blue(glass_9).",
		"blue(honda_81).",
		"bike(honda_81).",
		"bike(iris_8).",
		"bike(my_bike).",
		"on_route(rome).",
		"on_route(home).",
		"on_route(halifax).",
		"on_route(gatwick).",
		"move(home,taxi,halifax).",
		"move(gatwick,plane,rome).",
		"move(halifax,train,gatwick)."
		};

	
		ForwardReasoner fr=new ForwardReasoner();
		
		DCFileParser dcfp = new DCFileParser(fr.symbolTable);
	    dcfp.parseDCFile(EnvTool.localPath+"TEST//ForwardReasoner.dc");
	    fr.loadDefiniteClauses(dcfp.iterator());
		
	    fr.runForwardReasoner();
	
	    Iterator lit = fr.oldFactDiscTree.allLeavesIterator();
	    Vector litv=new Vector();
	    int j = 0;
	    while(lit.hasNext()){
	    	litv.add(((DefiniteClause) lit.next()).toString());
	    }
	    Assert.assertTrue(EnvTool.checkEqualOnStrings(litv,facts));

	}

}

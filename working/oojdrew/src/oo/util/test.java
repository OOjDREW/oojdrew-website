import jdrew.oo.util.*;
import jdrew.oo.td.*;
import jdrew.oo.bu.*;
import java.util.*;
import java.io.*;
import jdrew.oo.util.parsing.*;

import ptolemy.graph.*;


public class test{
	
	//Use the ruleml parser to turn some ruleml Code into terms
	//to see how it works
	
	public static void main(String args[]){
	
	Vector edges = new Vector();
	Vector nodes = new Vector();
	boolean negative = true;
	boolean positive = false;
	
	//maybe need to keep a vector of the edges as I create them
	//so That I can refer to them later

	DirectedGraph dg = new DirectedGraph();
	
	//for{	
	Node a = new Node(1);
	Node b = new Node(2);
	Node c = new Node(3);
	Node d = new Node(4);
	Node e = new Node(5);
	Node f = new Node(6);
	Node g = new Node(7);
	
	dg.addNode(a);
	dg.addNode(b);
	dg.addNode(c);
	dg.addNode(d);
	dg.addNode(e);
	dg.addNode(f);
	dg.addNode(g);
	
	Edge e1 = new Edge(a,b,positive);
	Edge e2 = new Edge(b,c,positive);
	Edge e3 = new Edge(c,b,negative);
	Edge e4 = new Edge(c,a,positive);
	Edge e5 = new Edge(a,d,positive);
	Edge e6 = new Edge(d,e,positive);
	Edge e7 = new Edge(e,a,positive);
	Edge e8 = new Edge(e,f,negative);
	
		
	dg.addEdge(e1);
	dg.addEdge(e2);
	dg.addEdge(e3);
	dg.addEdge(e4);
	dg.addEdge(e5);
	dg.addEdge(e6);
	dg.addEdge(e7);
	dg.addEdge(e8);
	
	edges.addElement(e1);
	edges.addElement(e2);
	edges.addElement(e3);
	edges.addElement(e4);
	edges.addElement(e5);
	edges.addElement(e6);
	edges.addElement(e7);
	edges.addElement(e8);
	//}for
	
		
	Collection col = dg.cycleNodeCollection();
	nodes.addAll(col);
	
	Iterator edgeIterator = edges.iterator();
	boolean hasNegEdgeInCycle = false;

	while(edgeIterator.hasNext()){
		//you can return the source / sink nodes of the edges to work
		//with terms instead of working with numbers and strings
		//Can call unify here on the terms to see if the nodes matches with
		//the edges, which actually makes more sense I think??  So if I derive
		//that that edge is in a cylce then I check for negativity(easily done)
		//Then I just return true if a negative edge in a cycle is found
		//otherwise I return false		
		Edge ed;
		ed = (Edge)edgeIterator.next();
		String s1 = ed.toString();
		String s2 = s1.substring(1,s1.length()-1);
	
		StringTokenizer st = new StringTokenizer(s2,",");
		
		int first = Integer.parseInt(st.nextToken());
		int second = Integer.parseInt(st.nextToken().trim());
		
		//check now nodes are in the vector
		
		int numberOfNodes = nodes.size();
		System.out.println("Number of nodes in cycles: " + numberOfNodes);
		for(int i = 0; i < numberOfNodes; i++){
			
			Node n1 = (Node)nodes.elementAt(i);
			int part1 = Integer.parseInt(n1.toString());
			
			for(int j = 0; j < numberOfNodes; j++){
				
				Node n2 = (Node)nodes.elementAt(j);
				int part2 = Integer.parseInt(n2.toString());
				//if the source and sink are in the nodes in cycles
				//that edge is in a cycle
				//need to make sure that I do not mix up
				//the sink and node for this to work right
				if(first == part1 && second == part2){
					System.out.println("edges Found in cycle: " + ed.toString());
					
					//if(ed.getNeg()){
				//		System.out.println("edge is negative: " + ed.getNeg());
				//		hasNegEdgeInCycle = true;
				//	}//if
					
				}//if					
			}//for	
		}//for	
	}//while

	System.out.println("Does rules graph contain a negative edge?: " + hasNegEdgeInCycle);
	
	
		
/*		SymbolTable.internSymbol("Cool");
		SymbolTable.internSymbol("Ben");
		SymbolTable.internSymbol("Jacket");
		
				
		Term[] terms = new Term[3];
		
		Term t1;
		t1 = new Term(6,1,0);
	
		System.out.println(t1.toString());
		
		Term t2 = new Term(7,1,0);
		System.out.println(t2.toString());
		
		Term t3 = new Term(8,1,0);
		System.out.println(t3.toString());

		terms[0] = t1;
		terms[1] = t2;
		terms[2] = t3;
		
		Term t4 = new Term(2,1,0,terms);
		System.out.println(t4.toString());
		
			System.out.println("");
		



String rule = 
 "<Assert>"+
 "<And mapClosure= \"universal\">" +
 
 "<Atom>" +
 "<Rel>goes</Rel>" +
 "<Ind>ben</Ind>" +
 "<Ind>school</Ind>" +
 "</Atom>"+
 
 "<Atom>"+
 "<Rel>plays</Rel>"+
 "<Slot><Ind>name</Ind><Ind>ben</Ind></Slot>"+
 //"<Data>baseball</Data>"+
 "</Atom>" +
 
 "</And>" +
 "</Assert>";
ForwardReasoner fr = new ForwardReasoner();	


RuleMLParser parser = new RuleMLParser();
try{
parser.parseRuleMLString(1,rule);		
	}
catch(Exception e){
	
}

Iterator it = parser.iterator();
while(it.hasNext()){
DefiniteClause dc = (DefiniteClause)it.next();
String s = dc.toRuleMLString();
System.out.println(s);
}

//fr.loadClauses(parser.iterator());		

//fr.printClauses();
//System.out.println("");
//fr.runForwardReasoner();
//fr.printClauses();

		
			
*/	
		
	}
	
	
}
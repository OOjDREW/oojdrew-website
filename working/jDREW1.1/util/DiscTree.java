
package jDREW.util;



import java.util.*;
import java.io.*;
import org.jdom.JDOMException;

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
   
   Discrimination trees, and the access iterator associated with
   them, provide a means to quickly refer to a list of items,
   each of which is associated with a particular first order term
   structure, called it accessor term.  Given a term structure
   used as the query part of the access, the "target term" if you
   like, there are three iterators that could be used to access
   the items: LessGeneralIterator, MoreGeneralIterator and
   UnifiableIterator.  These return iterators that give the items
   associated with accessor terms that are, respectively, less
   general, more general and unifiable with the target term.  The
   indexing data structure used is the discrimination tree.  It
   is constructed (as if) by listing all of the accessor terms
   and combining like prefixes, to construct a tree.  In this
   implementation we refer to DiscTrees, really just a specific
   type of discrimination tree, built for our purposes.

   A DiscTree is a rooted directed tree of DiscNodes where each
   DiscNode refers to its k children DiscNodes as child 0, ...,
   child k-1.  The number k may vary from DiscNode to DiscNode.
   A DiscNode which is a leaf node, here called a DiscLeafNode,
   refers to a list of items, which are the things we are trying
   to find.  Insertion of a pair consisting of an item and a
   sequence of numbers (i_1, ..., i_n) where 0<=i<k for each i,
   consists of, for each j=1,...,n, following the i_j'th child
   DiscNode, or if no such child exists, creating it, and then
   placing the item at the resulting leaf DiscNode.  No sequence
   in such an insertion pair is a proper prefix of any other such
   sequence.  Thus arities of function symbols are strictly
   observed.  Removal of an item/sequence pair consists of
   finding the leaf as in the insertion step, and removing the
   item from that list in the leaf, if it exists. No removals
   from internal nodes is done.

   If an insertion is attempted to a child j and the array of
   children for this particular DiscNode has k<j children, then
   the array of children is increased to the current size of the
   symbolTable, the old array is copied into the new array, the
   old array is deleted and the references are diverted to the
   new array.


  

 */
public class DiscTree {
  
  /**
     The variable <code>root</code> stores the root node of the DiscTree.
  */
  private DiscInteriorNode root;

	/**
     Creates a new <code>DiscTree</code> instance.  
   	*/
  public DiscTree(){
    symbolTable = new SymbolTable();
    root = null;
  }
	/**
     Creates a new <code>DiscTree</code> instance using the inputed {@link SymbolTable}. 
   	*/
  public DiscTree(SymbolTable symbolTable){
    this.symbolTable = symbolTable;
    root = null;
  }
  /**
     The variable <code>symbolTable</code> Stores the symbol table that is used for the definiteClauses stored in the DiscTree.  
   	*/
  public SymbolTable symbolTable;
	/**
     Inserts a {@link DefiniteClause} into the discTree, using the literal specified by the <code>position</code> as index. 
     @param clause - a <code>DefiniteClause</code> object, specifying the DefiniteClause being inserted. 
     @param position - an <code>int</code> value apecifying the position of the literal. For example, a(x):-b(x). The position of a(x) is 0, and the position of b(x) is 1.
   	*/
  public void insert(DefiniteClause clause, int position){
    if(root == null){
      root = new DiscInteriorNode();
    }
    root.insert(clause, position);
  }
	/**
     Inserts a {@link DefiniteClause} into the discTree, using the first literal as index. 
     @param clause - a <code>DefiniteClause</code> object, specifying the DefiniteClause being inserted. 
   	*/
  public void insert(DefiniteClause clause){
    insert(clause,0);
  }
  /**
     Retrieves from the DiscTree all the DefiniteClauses the index literal of which exactly matches (ignoring distinctions implied by variables)
       with the literal in position <code>position</code> in DefinteClause cl. The index literal of a DefiniteClause is the literal used as the index when the DefiniteClause was inserted into the DiscTree.
     @param cl - a <code>DefiniteClause</code> object, specifying the DefiniteClause being used for the purpose of retrieval. 
     @param position - a <code>int</code> value, specifying the position of the literal in <code>cl<code> that will be used for matching. Take a(x):-b(x) as an example, the positon of a(x) is 0, and the position of b(x) is 1.
     @return a <code>Vector</code> value that contains all the returned DefiniteClause objects.
     @throws NoSuchDiscNodeException if no such definite clause was inserted to the DiscTree.
     
   	*/
  public Vector retrieve(DefiniteClause cl, int position) throws NoSuchDiscNodeException{
    if(root == null)
      throw new NoSuchDiscNodeException();
    return root.retrieve(cl, position);
  }
  /**
     Same as {@link #retrieve} except that this function use a two dimensional int array {@link DefiniteClause#symbols symbols} to represent the {@link DefiniteClause}. The <code>symbols</code> is in fact the flatterm table of the definite clause.
   	*/
  public Vector retrieve(int[][] symbols, int position) throws NoSuchDiscNodeException{
    if(root == null)
        throw new NoSuchDiscNodeException();
    return root.retrieve(symbols,position);
  }
  /**
   * Return a UnifiableIterator object that can be used to enumerate all the DefiniteClauses the index literal of which can be 
   * unified with the literal of <code>cl</code> that is specified by <code>position</code>. The index literal of a DefiniteClause is the literal used as the index when the DefiniteClause was inserted into the DiscTree.
   */
  public UnifiableIterator unifiableIterator(DefiniteClause cl, int position){
    return new UnifiableIterator(cl, position);
  }
/**
     Same as {@link #unifiableIterator} except that this function use a two dimension int array {@link DefiniteClause#symbols symbols}to represent the {@link DefiniteClause}. The <code>symbols</code> is in fact the flatterm table of the definite clause.
   	*/
  public UnifiableIterator unifiableIterator(int[][] symbols, int position){
    return new UnifiableIterator(symbols, position);
  }
  /**
   * Overides the toString function of <code>Object</code> that gives a string representation of the DiscTree.
   * @return a string representation of the object
   */
  public String toString(){
    if(root == null)
      return null;
    return "root"+root.toString(0);
  }

  
  /**
     Generates an <code>iterator</code> instance that iterates all the {@link DefiniteClause DefiniteClauses} stored in this DiscTree. 
     @return an <code>Iterator</code> value
  */
  public Iterator allLeavesIterator(){
    return new AllLeavesIterator();
  }
  
 /**
  * Returns the postion in the {@link #symbolTable} of the symbol that is specified by the <code>symbol</code> and  <code>arity</code>.
  * @param symbol the name of the symbol.
  * @param arity the arity of the symbol.
  * @return the postion of the symbol in the {@link #symbolTable}.
  */
  public int symbolTablePosition(String symbol, int arity){
    return symbolTable.internSymbol(symbol, arity);
  }
 /**
  * Returns a String that is composed of white spaces.
  * @param indent -the number of white spaces.
  * @return a String that is composed of white spaces the number of which is specified by <code>indent</code>
  */
  private static String indentString(int indent){
    StringBuffer s = new StringBuffer("\n");
    for(int i = 0; i < indent; i++){
      s.append("  ");
    }
    return new String(s);
  }

  /**
     The interface <code>DiscNode</code> describes the functions we
     might want to do with DiscNodes.  
  */
  interface DiscNode {
  	/**
	 * Inserts a DefiniteClause into the {@link DiscTree sub-DiscTree} rooted at this DiscNode using the literal in the position <code> position </code> in the DefiniteClause as the index. 
	 * @param clause - the DefiniteClause being inserted.
	 * @param position - the position of the index literal
	 */
    void insert(DefiniteClause clause, int position);
    
    /**
	 * Inserts a DefiniteClause into the {@link DiscTree sub-DiscTree} rooted at this DiscNode using the first literal as the index. 
	 * @param clause - the DefiniteClause being inserted.
	 */
    void insert(DefiniteClause clause);
    
    /**
       Retrieves the DefiniteClauses under this DiscNode
       that are exact matches (ignoring distinctions implied by variables)
       with the literal in position <code>position</code> in DefiniteClause cl.

       @param cl - the <code>DefiniteClause</code> being inserted.
       @param position - an <code>int</code> value apecifying the position of the literal. For example, a(x):-b(x). The position of a(x) is 0, and the position of b(x) is 1.
       @return a <code>Vector</code> value
       @exception NoSuchDiscNodeException if no such literal clause was inserted
    */
    Vector retrieve(DefiniteClause cl, int position) throws NoSuchDiscNodeException;
    /**
     Same as {@link #retrieve(DefiniteClause,int)} except that this function use a two dimensional <code>int</code> array {@link DefiniteClause#symbols symbols} to represent the {@link DefiniteClause}. The <code>symbols</code> is in fact the flatterm table of the definite clause.
   	*/
    Vector retrieve(int[][] symbols, int position) throws NoSuchDiscNodeException;
    /**
     * Retrives all the {@link DefiniteClause DefiniteClauses} under this DiscNode.
     * @return A <code>Vector</code> variable that contains the {@link DefiniteClause DefiniteClauses} under this DiscNode.
     */
    Vector retrieve() throws NoSuchDiscNodeException;
    /**
	   *    Gets a string representatin of the {@link DiscTree} under this DiscNode.
	   *    @param indent An <code> int </code> value specifying how much indent will be applyed to the string representation.  
	   *    @retrun A <code>String</code> value containing the string representation
	   *        of the DiscTree.
	   */
    public String toString(int indent);
  }
	/**
	 * <code>DiscLeafNode</code> represents the leaf node of the {@link DiscTree}. As all the other nodes of discTree, it implements the {@link DiscTree.DiscNode} interface.
	 */
  class DiscLeafNode implements DiscNode{
    /**
     * A <code>Vector</code> variable that contains the {@link DefiniteClause DefiniteClauses} that are supposed to be under this leaf node.
     */
    Vector items;
    /**
     Creates a new <code>DiscLeafNode</code> instance.  
   	*/
    DiscLeafNode(){
      items = new Vector();
    }
    /**
     * Inserts a DefiniteClause under this leaf node.
     * @param clause - the DefinteClause being inserted.
     * @param position - is not used in this function, it is here just to comply with the {@link DiscTree.DiscNode} interface.
     */
    public void insert(DefiniteClause clause, int position){
      items.add(clause);
    }
    /**
     * Insersts a DefiniteClause under this leaf node.
     * @param clause - the DefinteClause being inserted.
     */
    public void insert(DefiniteClause clause){
        items.add(clause);
    }
    /**
     * Retrives all the {@link DefiniteClause DefiniteClauses} under this leaf node.
     * @param cl - is not used in this function, it is here just to comply with the {@link DiscTree.DiscNode} interface.
     * @param position - is not used in this function, it is here just to comply with the {@link DiscTree.DiscNode} interface.
     * @return A <code>Vector</code> variable that contains the {@link DefiniteClause DefiniteClauses} under this leaf node.
     */
    public Vector retrieve(DefiniteClause cl, int position) 
      throws NoSuchDiscNodeException{
      return items;
    }
    /**
     * Retrives all the {@link DefiniteClause DefiniteClauses} under this leaf node.
     * @param symbols - is not used in this function, it is here just to comply with the {@link DiscTree.DiscNode} interface.
     * @param position - is not used in this function, it is here just to comply with the {@link DiscTree.DiscNode} interface.
     * @return A <code>Vector</code> variable that contains the {@link DefiniteClause DefiniteClauses} under this leaf node.
     */
    public Vector retrieve(int[][] symbols, int position)
      throws NoSuchDiscNodeException{
      return items;
    }
    /**
     * Retrives all the {@link DefiniteClause DefiniteClauses} under this leaf node.
     * @return A <code>Vector</code> variable that contains the {@link DefiniteClause DefiniteClauses} under this leaf node.
     */
    public Vector retrieve()
      throws NoSuchDiscNodeException{
      return items;
    }
      /**
	   *    Gets a string representatin of
	   *        all the {@link DefiniteClause DefiniteClauses} under this leaf node.
	   *    @retrun A <code>String</code> value containing the string representation
	   *        of the DefiniteClauses.
	   */
    public String toString () {
      return toString(0);
    }
    /** Same as {@link #toString} function except that this function specifies how much indent will be applyed to the string representation.
     * @param An <code> int </code> value specifying how much indent will be applyed to the string representation.  
     * @retrun A <code>String</code> value containing the string representation
	       of the DefiniteClauses.
     */
    public String toString (int indent) {
      StringBuffer s = new StringBuffer();
      s.append(DiscTree.indentString(indent));
      s.append("<");
      if(items.size()>0){
	for(int i = 0; i<items.size()-1; i++){
	  s.append(items.get(i));
	  s.append(",");
	}
	s.append(items.get(items.size()-1));
      }
      s.append(">");
      return s.toString();
    }
  }

  //Modified by Marcel A. Ball - NRC - 2003
  //Changed to use a Hashtable instead of a large sparsely populated array
  /**
	 * <code>DiscInteriorNode</code> represents the interior node of the {@link DiscTree}. As all the other nodes of discTree, it implements the {@link DiscTree.DiscNode} interface.
	 */
  public class DiscInteriorNode implements DiscNode{
	/**
	 * A <code> Hashtable</code> variable containing the children of this interior node. The children could be {@link DiscTree.DiscInteriorNode} or {@link DiscTree.DiscLeafNode}.
	 */
    Hashtable childs;
	/**
	 * Creates a DiscInteriorNode instance.
	 */
    DiscInteriorNode(){
      childs = new Hashtable();
    }
	/**
	 * Inserts a DefiniteClause into the {@link DiscTree sub-DiscTree} rooted at this node using the first literal as the index. 
	 * @param clause - the DefiniteClause being inserted.
	 */
    public void insert(DefiniteClause clause){
        insert(clause,0);
    }
    /**
	 * Inserts a DefiniteClause into the {@link DiscTree sub-DiscTree} rooted at this node using the literal in the position <code> position </code> in the DefiniteClause as the index. 
	 * @param clause - the DefiniteClause being inserted.
	 * @param position - the position of the index literal
	 */
    public void insert(DefiniteClause clause, int position){
      //position tells which literal in the DefiniteClause we 
      //want to use as index.  Position 0 refers to the first
      //literal, position 1 to the second and so on.
      int symbolIndex = 0;
      //symbolIndex refers to the index in the array symbols 
      //where the literal of interest begins
      for(int i = 0; i < position; i++)
	symbolIndex += clause.symbols[symbolIndex][1];
      insert(clause.symbols, symbolIndex, 
	     symbolIndex+clause.symbols[symbolIndex][1], clause);
    }
	/**
     *Same as {@link #insert(DefiniteClause,int)} except that this function use <code>"from", "to"</code> to specify where the literal begains and ends in the {@link DefiniteClause#symbols flatterm table} instead of using <code> position </code>.
     *@param symbols - the flatterm table of a definite clause.
     *@param from - the begin position of the index literal in the flatterm table.
     *@param to - the end position of the index literal in the flatterm table.
     * @param - the DefiniteClause being inserted.
   	*/
    public void insert(int[][] symbols, int from, int to, DefiniteClause clause){
      int at = symbols[from][0];
      if(at<0){
	//this is a variable.  Since these are imperfect discrimination trees
	// we map all variables to 0
	at = 0;
      }
      Integer iat = new Integer(at);
      if(!childs.containsKey(iat)){
	if(from+1 == to)
	  childs.put(iat, new DiscLeafNode());
	else
	  childs.put(iat, new DiscInteriorNode());
      }
      
      if(from+1 == to)
	((DiscLeafNode) childs.get(iat)).insert(clause, 0);
      else
	((DiscInteriorNode) childs.get(iat)).insert(symbols, from+1, to, clause);
    }

    /**
       Retrieves the definite clauses
       that are exact matches (ignoring distinctions implied by variables)
       with the literal in position <code>position</code> in DefiniteClause cl.

       @param cl - a <code>DefiniteClause</code> value
       @param position - an <code>int</code> value apecifying the position of the literal. For example, a(x):-b(x). The positon of a(x) is 0, and the position of b(x) is 1.
       @return a <code>Vector</code> value
       @exception NoSuchDiscNodeException if no such literal clause was inserted
    */
    
    public Vector retrieve(DefiniteClause cl, int position) throws NoSuchDiscNodeException{
        return retrieve(cl.symbols,position);
    }
    /**
     Same as {@link #retrieve(DefiniteClause,int)} except that this function use a two dimensional int array {@link DefiniteClause#symbols symbols} to represent the {@link DefiniteClause}. The <code>symbols</code> is in fact the flatterm table of the definite clause.
   	*/
    public Vector retrieve(int[][] symbols, int position) throws NoSuchDiscNodeException{
      //assumes that the clause cl has at least one element
      int symbolIndex = 0;
      //symbolIndex refers to the index in the array symbols 
      //where the literal of interest begins
      for(int i = 0; i < position; i++)
	symbolIndex += symbols[symbolIndex][1];
      return retrieve(symbols, symbolIndex, 
	     symbolIndex+symbols[symbolIndex][1]);
    }
	/**
	 * This is a function defined in the {@link DiscTree.DiscNode} interface yet not really supported by DiscInterNode class. This function simply throws an {@link UnsupportedOperationException}.
	 */
    public Vector retrieve() throws NoSuchDiscNodeException{
        throw new UnsupportedOperationException();
    }
    /**
     Same as {@link #retrieve(int[][],int)} except that this function use <code>"from", "to"</code> to specify where the literal begains and ends in the {@link DefiniteClause#symbols flatterm table} instead of using <code> position </code>.
   	*/
    public Vector retrieve(int[][] symbols, int from, int to) 
      throws NoSuchDiscNodeException{
      int at = symbols[from][0];
      if(at<0){
	//this is a variable.  Since these are imperfect discrimination trees
	// we map all variables to 0
	at = 0;
      }
      Integer iat = new Integer(at);
      if(childs.containsKey(iat)){
        if(from+1 == to)
            return ((DiscLeafNode) childs.get(iat)).retrieve(); 
        else
            return ((DiscInteriorNode) childs.get(iat)).retrieve(symbols, from+1, to);
      }
      else
          throw new NoSuchDiscNodeException();
    }
    //End Modify
    
    /**
	   *    Gets a string representatin of the DiscTree under this interior node.
	   *    @retrun A <code>String</code> value containing the string representation
	   *        of the DiscTree.
	   */
    public String toString(){
      return toString(0);
    }
	/** same as {@link #toString} function except that this function specifies the overall indent of the String prepresentation.
     * @param indent - the number of white spaces.
     * @retrun A <code>String</code> value containing the string representation
	       of the DiscTree.
     */
    public String toString(int indent){
      StringBuffer b = new StringBuffer();
      b.append(DiscTree.indentString(indent));
      
      Enumeration e = childs.keys();
      while(e.hasMoreElements()){
        Integer iat = (Integer)e.nextElement();
        b.append(symbolTable.printName(iat.intValue()));
        b.append("{");
        b.append(((DiscNode)childs.get(iat)).toString(indent+1));
        b.append(DiscTree.indentString(indent));
        b.append("}");
      }
      
      return b.toString();
    }
  }

  //Modified by Marcel A. Ball - NRC - 2003
  // Changed to make use of the new Hashtable implementation of the DiscInteriorNodes
  /**
   * <code>AllLeavesIterator</code> is used to iterates all the {@link DefiniteClause DefiniteClauses} stored in a DiscTree. 
   */
  class AllLeavesIterator implements Iterator{
    /**
     * Refers to the current interior node, initially set to be the root of the {@link DiscTree}.
     */
    DiscInteriorNode n;
    /**
     * Refers to the current child of n
     */
    Integer at; 
    /**
     * An enumeration of the keys of the hashtable that contains the children of n.
     */
    Enumeration e;
    /**
     * An <code>stack</code> variable stores all the {@link DiscTree.StackFrameAL "backtracking points"} generated so far during the traversing. 
     */
    Stack allLeavesStack;
    /**
     * An <code> Iterator </code> object that iterates all the {@link DefiniteClause DefiniteClauses} stored under a {@link DiscTree.DiscLeafNode}.
     */
    Iterator leafIterator;
    /**
     * A <code>boolean</code> value indicates whether the result found by the previous call of {@link #hasNext} has been consumed by the function {@link #next} or not.
     */
    boolean foundNext;
    /**
     * Creates an AllLeavesIterator instance
     */
    AllLeavesIterator(){
      n = root;
      
      if(n != null)
          e = n.childs.keys();
      at = null;
      allLeavesStack = new Stack();
      leafIterator = null;
      foundNext = false;
    }
    /**
	 * Finds out the next DefiniteClause without consuming it.
	 * @return -true if there exists unconsumed matching DefiniteClause.
	 */
    public boolean hasNext(){
        if(foundNext){
            return true;
        }
        else if(n == null){
            return false;
        }
        else if(leafIterator!=null){
            if(leafIterator.hasNext())
            {
                foundNext = true;
                return true;
            }
            else if(e.hasMoreElements()){
                leafIterator = null;
                return hasNext();
            }
            else if(allLeavesStack.empty()){
                return false;
            }
            else{
                StackFrameAL fr = (StackFrameAL)allLeavesStack.pop();
                n = fr.n;
                e = fr.e;
                at = null;
                return hasNext();
            }
        }
        else if(!e.hasMoreElements()){
            if(allLeavesStack.empty())
                return false;
            else
            {
                StackFrameAL fr = (StackFrameAL)allLeavesStack.pop();
                n = fr.n;
                e = fr.e;
                at = null;
                return hasNext();
            }
        }
        else {
            at = (Integer)e.nextElement();
            if(n.childs.get(at) instanceof DiscLeafNode){
                leafIterator = ((DiscLeafNode) n.childs.get(at)).items.iterator();
                return hasNext();
            }
            else{
                StackFrameAL fr = new StackFrameAL();
                fr.n = n;
                fr.e = e;
                allLeavesStack.push(fr);
                n = (DiscInteriorNode) n.childs.get(at);
                at = null;
                e = n.childs.keys();
                return hasNext();
            }
        }
    }
	/**
     * Consume the next DefiniteClause found by the {@link #hasNext} function. 
     * @return the next matching DefiniteClause.
     * @throws -a NoSuchElementException is throwed if there is no unconsumed unifiable formular.
     */
    public Object next() throws NoSuchElementException {//Not Changed
      if(!hasNext())
	throw new NoSuchElementException();
      foundNext = false;
      return leafIterator.next();
    }
	/**
	 * Not supported in this class. An <code>UnsupportedOperationException</code> is simplely thrown.
	 */
    public void remove() throws UnsupportedOperationException{//Not Changed
      throw new UnsupportedOperationException
	("AllLeavesIterator does not allow remove");
    }
  }
 /**
  * An inner class that is used to store information for the purpose of backtracking. It is used by the {@link DiscTree.AllLeavesIterator}.
  */
  class StackFrameAL{
  	DiscInteriorNode n;
    Integer at;
    Enumeration e;
  }
  //End Modify
  
  //Modified by Marcel A. Ball - NRC - 2003
  // Changed to use the Hashtable implementation of DiscInteriorNode
  /**
   * 
   * 
   */
  public class UnifiableIterator implements Iterator{

    /**
     * Creates an UnifiableIterator instance.
     * @param cl - a <code>DefiniteClause</code> variable specifying the DefiniteClaused being used for the purpose of the unification.
     * @param position - a <code>int</code> value specifying that the literal in position <code> position </code> in the DefiniteClause <code> cl </code> will be used for the unification.
     */
    UnifiableIterator (DefiniteClause cl, int position){ //Not Changed
        this(cl.symbols, position);
    }
    /**
     Same as <code> UnifiableIterator(DefiniteClause,int) </code> except that this function use a two dimensional int array {@link DefiniteClause#symbols symbols} to represent the {@link DefiniteClause}. The <code>symbols</code> is in fact the flatterm table of the definite clause.
   	*/
    UnifiableIterator(int[][] symbols, int position){ //Not Changed
      n = root;      
      this.symbols = symbols;
      int symbolIndex = 0;
      //symbolIndex refers to the index in the array symbols 
      //where the literal of interest begins
      for(int i = 0; i < position; i++)
	symbolIndex += symbols[symbolIndex][1];
      
      from = symbolIndex;
      to = from+symbols[from][1];
      s = from;
      if(n==null)
	s = to;//this is a simple method (but a kludge) to deal with empty trees
    }
	/**
	 * A <code> DiscTree.DiscNode </code> variable specifying the current node of the {@link DiscTree} on which the <code> UnifiableIterator </code> will carry out the unification operation. The initial value of <code> n </code> is set to the root of the DiscTree.
	 */
    private DiscTree.DiscNode n;
    /**
     * The flatterm table of the {@link DefiniteClause} that is specifyed by the constructor of the <code> DiscTree.UnifiableIterator </code> class.
     */
    private int[][] symbols;
    /**
     * The begin position of the literal in the flatterm table that is specifyed by the constructor of the <code> DiscTree.UnifiableIterator </code> class.
     */
    private int from;
    /**
     * The end position of the literal in the flatterm table that is specifyed by the constructor of the <code> DiscTree.UnifiableIterator </code> class.
     */
    private int to;
    /**
     * An <code>stack</code> variable stores all the "backtracking points" generated so far during the unification. The information of backtracing point is stored in two different data structures according to differnet situations. The two data structures are {@link DiscTree.StackFrameMG} and {@link DiscTree.StackFrameLG}.
     */
    private Stack back = new Stack();
    /**
     * The position of the current symbol that will be processed. The initial value is equal to {@link #from}.
     */
    private int s;
    /**
     * An <code> boolean </code> variable indicating whether the current value of {@link #n} and {@link #s} is the result of restoring from a {@link DiscTree.StackFrameMG}.
     */
    private boolean recentRestore = false;
    /**
     * A <code>boolean</code> value indicates whether the result found by the previous call of {@link #hasNext} has been consumed by the function {@link #next} or not.
     */ 
    private boolean foundNext = false;
    /**
     * An <code> Iterator </code> object that iterates all the {@link DefiniteClause DefiniteClauses} stored under a {@link DiscTree.DiscLeafNode}.
     */
    private Iterator leafIterator = null;
    /**
     * An temporary<code> EndTermIterator </code> variable that is used by the {@link DiscTree.UnifiableIterator}.  
     */
    private EndTermIterator e = null;
	/**
	 * Finds out the next matching DefiniteClause without consuming it.
	 * @return -true if there exists unconsumed matching DefiniteClause.
	 */
    public boolean hasNext(){
        if(foundNext)
            return true;
        else if (leafIterator != null && leafIterator.hasNext()){
            foundNext = true;
            return true;
        }
        else {
            foundNext = false;
            leafIterator = null;
            if( s==to){
                if (back.empty()) 
                    return false;
                else {
                    if(back.peek() instanceof StackFrameLG){
                        StackFrameLG restore = (StackFrameLG) back.pop();
                        s = restore.s;
                        e = restore.e;
                        n = (DiscTree.DiscNode) e.next();
                        if(e.hasNext()) 
                            back.push(new StackFrameLG(s, e));
                        e = null;
                    }
                    else {
                        StackFrameMG restore = (StackFrameMG) back.pop();
                        recentRestore = true;
                        s = restore.s;
                        n = restore.n;
                    }
                }
            }
        
            DiscTree.DiscNode matchingChild = null;
            while(s<to){
                int sChildIndex= symbols[s][0];
                if(sChildIndex<0) 
                    sChildIndex = 0;

                if(!recentRestore && ((DiscTree.DiscInteriorNode)n).childs.containsKey(new Integer(0)) && sChildIndex != 0){
                    StackFrameMG pair = new StackFrameMG(s, n);
                    back.push(pair);
                    n = (DiscNode)((DiscTree.DiscInteriorNode)n).childs.get(new Integer(0));
                    s += symbols[s][1];
                }
                else{ 
                    recentRestore = false;
                    //recall that sChildIndex= symbols[s][0];
                    if(((DiscTree.DiscInteriorNode)n).childs.containsKey(new Integer(sChildIndex)))
                        matchingChild = (DiscNode)((DiscTree.DiscInteriorNode)n).childs.get(new Integer(sChildIndex));
                    else
                        matchingChild = null;


                    if(matchingChild != null && sChildIndex != 0){
                        recentRestore = false;
                        n = matchingChild;
                        s++;
                    }
                    else if(sChildIndex == 0 && e == null){
                        //EndTermIterator eNew = new EndTermIterator(1,n);
                        //while(eNew.hasNext()){
                        //  System.out.println("New end term iterator " + eNew.next());
                        //}
                        e = new EndTermIterator(1,n);
                        if (e.hasNext()){
                            n = (DiscTree.DiscNode) e.next();
                            s++;
                            if(e.hasNext())
                                back.push(new StackFrameLG(s, e));
                        }
                        e = null;
                    }
                    else if(!back.empty()){
                        if((back.peek()) instanceof StackFrameLG){
                            StackFrameLG restore = (StackFrameLG) back.pop();
                            s = restore.s;
                            e = restore.e;
                            n = (DiscTree.DiscNode) e.next();
                            if(e.hasNext()) 
                                back.push(new StackFrameLG(s, e));
                            e = null;
                        }

                        else {
                            StackFrameMG restore = (StackFrameMG) back.pop();
                            recentRestore = true;
                            s = restore.s;
                            n = restore.n;
                        }
                    }
                    else{
                        foundNext = false;
                        return false;
                    }
                }
            }
            try{
                leafIterator = n.retrieve().iterator();
                foundNext = true;
                return true;
            }
            catch (DiscTree.NoSuchDiscNodeException e){
                System.err.println("This should not happen.");
                e.printStackTrace();
                return false;
            }
        }
    }
    /**
     * Consume the next matching DefiniteClause found by the {@link #hasNext} function. 
     * @return the next matching DefiniteClause.
     * @throws -a NoSuchElementException is throwed if there is no unconsumed unifiable formular.
     */
    public Object next() throws NoSuchElementException {// Not Changed
      if(!hasNext())
	throw new NoSuchElementException();
      foundNext = false;
      return leafIterator.next();
    }
	/**
	 * Not supported in this class. An <code>UnsupportedOperationException</code> is simplely thrown.
	 */
    public void remove() throws UnsupportedOperationException{ // Not Changed
      throw new UnsupportedOperationException
	("MoreGeneralIterator does not allow remove");
    }
  }
//End Modify
  /**
  * An inner class that is used to store information for the purpose of backtracking. It is used by the {@link DiscTree.UnifiableIterator}.
  * <code> StackFrameMG </code> is used in the situation that the UnifiableIterator unified  a variable symbol of the literal that is specifyed by {@link DiscTree.UnifiableIterator#s s} with all the children of the DiscTree node that is specifyed by {@link DiscTree.UnifiableIterator#n n}.
  */
  class StackFrameMG{
    StackFrameMG(int s, DiscTree.DiscNode n){
      this.s = s;
      this.n = n;
    }
    int s;
    DiscTree.DiscNode n;
  }
  /**
  * An inner class that is used to store information for the purpose of backtracking. It is used by the {@link DiscTree.UnifiableIterator}.
  * <code> StackFrameMG </code> is used in the situation that the UnifiableIterator unified  a ground symbol of the literal that is specifyed by {@link DiscTree.UnifiableIterator#s s} with a "variable" child of the DiscTree node that is specifyed by {@link DiscTree.UnifiableIterator#n n}.
  */
  class StackFrameLG{
    StackFrameLG(int s, EndTermIterator e){
      this.s = s;
      this.e = e;
    }
    int s;
    EndTermIterator e;
  }
 

  /**
   * An inner class that is used in the situation that the UnifiableIterator unified  a variable symbol of the literal that is specifyed by {@link DiscTree.UnifiableIterator#s s} with all the children of the DiscTree node that is specifyed by {@link DiscTree.UnifiableIterator#n n}.
   */
  class EndTermIterator implements Iterator{
  	
	/**
     * A <code>boolean</code> value indicates whether the result found by the previous call of {@link #hasNext} has been consumed by the function {@link #next} or not.
     */
    private boolean foundNext = false;
    /**
     * An <code>stack</code> variable stores all the {@link DiscTree.StackFrameET "backtracking points"}.
     */
    private Stack back = new Stack();
    /**
     * An <code>DiscNode</code> variable contains the next unconsumed result returned by the previous call of {@link #hasNext}.
     */
    private DiscNode nextEnd;
    
    /**
     * Creates an <code>EndTermIterator</code> instance. 
     * @param k - should be set to 1 so that the <code>EndTermIterator</code> can correctly track the length of the term that is supposed to be unified with the variable symbol that is specifyed by {@link DiscTree.UnifiableIterator#s s}.
     * @param n - the current DiscTree node where <code> EndTermIterator </code> begins the iterating operation.
     */
    EndTermIterator(int k, DiscNode n){ 
      back = new Stack();
      DiscInteriorNode n2 = (DiscInteriorNode)n;
      back.push(new StackFrameET(k, n2, n2.childs.keys()));
    }

  //Modified by Marcel A. Ball - NRC - 2003
  //Changed to support the Hashtable implementation of DiscInteriorNode
    /**
	 * Finds out the next DiscTree node that is immediately after the item unified with the variable symbol that is specifyed by {@link DiscTree.UnifiableIterator#s s}.
	 * @return -true if there exists unconsumed matching DefiniteClause.
	 */
    public boolean hasNext(){
      if(foundNext)
	return true;
      else if (back.empty())
	return false;
      else{
	StackFrameET top = (StackFrameET) back.peek();
        
        if(!top.e.hasMoreElements()){
	  back.pop();
	  return hasNext();
	}
	else {
	  int k = 0;
          top.i = (Integer)top.e.nextElement();
	  if(top.i.intValue() == 0)
	    k = top.k-1;
	  else
	    k = symbolTable.arityTableInt(top.i.intValue()) + top.k - 1;
	  if(k==0){
	    foundNext = true;
	    nextEnd = (DiscNode) top.n.childs.get(top.i);
	    return true;
	  }
	  else {
              DiscInteriorNode din = (DiscInteriorNode)top.n.childs.get(top.i);
	    back.push(new StackFrameET(k, din, din.childs.keys()));
	    return hasNext();
	  }
	}
      }
    }
    //End Modify
    /**
     * Consume the {@link DiscTree.DiscNode} found by the {@link #hasNext} function. 
     * @return the next {@link DiscTree.DiscNode}.
     * @throws -a NoSuchElementException is throwed if there is no unconsumed DiscNode.
     */
    public Object next() throws NoSuchElementException{
      if(!hasNext())
	throw new NoSuchElementException();
      else {
	foundNext = false; //'consume' the next found endterm node
	return nextEnd;
      }
    }
	/**
	 * Not supported in this class. An <code>UnsupportedOperationException</code> is simplely thrown.
	 */
    public void remove() throws UnsupportedOperationException{
      throw new UnsupportedOperationException();
    }
  }
/**
  * An inner class that is used to store information for the purpose of backtracking. It is used by the {@link DiscTree.EndTermIterator}.
  */
  class StackFrameET{
  	/**
  	 * An <code>int</code> value keeping track of the remaining length of the item that is supposed to be unified with the variable symbol that is specifyed by {@link DiscTree.UnifiableIterator#s s}.
  	 */
    int k;
    /**
     * An <code>int</code> value that is changed by {@link DiscTree.UnifiableIterator} to indicate the position in the {@link SymbolTable} of the symbol that is currently being processed. 
     */
    Integer i;
    /**
     * A DiscTree node. 
     */
    DiscInteriorNode n;
    /**
     * An <code> Enumeration </code> object to enumerate all the children of {@link #n}.
     */
    Enumeration e;
    StackFrameET(int k, DiscInteriorNode n, Enumeration e){
      this.k = k;
      this.n = n;
      this.e = e;
    }
  }

  class NoSuchDiscNodeException extends Exception{}

} // DiscTree




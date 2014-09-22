
package jDREW.util;


import java.util.*;
import java.io.*;
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

 * <code>Heap<code> class implements a binary heap in which all the item are stored according to their priorities.
 * A binary heap has two properties: 1. The value of any noode is >= values of its two childeren. 2. No holes in the tree.
 * Here <code>Heap<code> is used to store facts according to their length, that is the shortest fact has the highest priority.
 */
public class Heap{

  /**
   * An <code>Vector</code> variable holds all the items in this <code> Heap </code>.
   */	
  private Vector vec;
  
  /**
   <code>waitForNextElement</code> tells whether or not this 
   is a heap that waits for a new element when it has run out,
   hoping that it is supplied by another thread.

   */
  private boolean waitForNextElement;

   /**
     Creates a new <code>Heap</code> instance. The value of {@link #waitForNextElement} is set to be false. 
     @param initialSize - a <code>int</code> value, specifying the initial size of the heap.
   	*/
  public Heap(int initialSize){
    waitForNextElement = false;
    vec = new Vector(initialSize);
  }
  
  /**
     Creates a new <code>Heap</code> instance. The value of {@link #waitForNextElement} is set to be false. 
    */
  public Heap(){
    waitForNextElement = false;
    vec = new Vector();
  }
  
  /**
     Creates a new <code>Heap</code> instance. 
     @param waitForNewElement - a <code>boolean</code> value, specifying the value of {@link #waitForNextElement}.
   	*/
  public Heap(boolean waitForNewElement){
    this.waitForNextElement = waitForNewElement;
    vec = new Vector();
  }

	/**
     Return true if the <code>Heap</code> is empty. 
     Always return false if the value of {@link #waitForNextElement} is set to be true.
     @return a boolean value showing if the <code>Heap</code> is empty.
   	*/
  public boolean isEmpty(){
    if(waitForNextElement)
      return false;
    return vec.isEmpty();
  }
  
  /**
   * Adds all the items of the input array to the heap while maintaining the properties of the binary heap.
   * @param anArray the input array contains the items being added.
   */
  public synchronized void addAll(Comparable[] anArray){
    for(int i = 0; i < anArray.length; i++){
      vec.add(anArray[i]);
    }
    heapify();
  }
  /**
   * Adds all the items of the input Vector to the <code>heap</code> while maintaining the properties of the binary heap.
   * @param comps the input <code> Vector </code> contains the items being added.
   */
  public synchronized void addAll(Vector comps){
    Iterator it = comps.iterator();
    while(it.hasNext()){
      vec.add(it.next());
    }
    heapify();
  }
  /**
   * Reorganize the Heap (if necessary) so that all the item are stored according to their priorities (the two properities of the binary heap are maintained).
   */
  private void heapify(){
    int halfSize = (vec.size()+1)/2-1;
    for(int i = halfSize; i >= 0; i--){
      percDown(i);
    }
  }
  /**
   * <code>percUp</code> function moves the <italic>i<italic>th item (which usually violates the >= property of the binary heap that states the value of any node is >= values of its two children)
   * upword so that the the <italic>i<italic>th item is stored according to its priority. This function is usually called when a new item is inserted into the <code>Heap</code>.
   */
  private void percUp(int i){
    //assumes 0<=i<vec.size
    if(i > 0){
      Comparable ati = (Comparable) vec.elementAt(i);
      int parent = (i+1)/2-1;
      boolean searching = true;
      while(searching){
	Comparable atp = (Comparable) vec.elementAt(parent);
	searching = ati.compareTo(atp)<0;
	if(searching){
	  vec.setElementAt(atp,i);
	  searching = parent>0;
	  i = parent;
	  parent = (parent+1)/2-1;
	}
      }
      vec.setElementAt(ati,i);
    }
  }
  /**
   * Returns and removes the shortest fact from the <code>heap</code> while maintaining the properties of the binary heap.
   */
  public synchronized Comparable removeSmallest(){
    //assumes has been heapified
    Comparable smallest = (Comparable) vec.elementAt(0);
    if(vec.size()==1){
      vec.removeElementAt(0);
    }
    else {
      Comparable lastmost = (Comparable) vec.elementAt(vec.size() - 1);
      vec.removeElementAt(vec.size()-1);
      vec.setElementAt(lastmost, 0);
      percDown(0);
    }
    return smallest;
  }
  
  /**
   * <code>percDown</code> function moves the <italic>i<italic>th item (which usually violates the >= property of the binary heap that states the value of any node is >= values of its two children)
   * downword so that the the <italic>i<italic>th item is stored according to its priority. This function is usually called when an item is removed from the <code>Heap</code>.
   */
  
  private void percDown(int i){
    int leftChild = 2*i+1;
    if(leftChild < vec.size()){
      Comparable ati = (Comparable) vec.elementAt(i);
      int rightChild = leftChild+1;
      if(rightChild == vec.size()){
	Comparable atLeft = (Comparable) vec.elementAt(leftChild);
	if(ati.compareTo(atLeft)>0){
	  vec.setElementAt(ati, leftChild);
	  vec.setElementAt(atLeft, i);
	}
      }
      else {
	Comparable atLeft = (Comparable) vec.elementAt(leftChild);
	Comparable atRight = (Comparable) vec.elementAt(rightChild);
	if(ati.compareTo(atLeft)>0){
	  if(ati.compareTo(atRight)>0){
	    if(atLeft.compareTo(atRight)>0){
	      vec.setElementAt(atRight, i);
	      vec.setElementAt(ati, rightChild);
	      percDown(rightChild);
	    }
	    else {
	      vec.setElementAt(atLeft, i);
	      vec.setElementAt(ati, leftChild);
	      percDown(leftChild);
	    }
	  }
	  else {
	    vec.setElementAt(atLeft, i);
	    vec.setElementAt(ati, leftChild);
	    percDown(leftChild);
	  }
	}
	else if(ati.compareTo(atRight)>0){
	  vec.setElementAt(atRight, i);
	  vec.setElementAt(ati, rightChild);
	  percDown(rightChild);
	}
      }
    }
  }
  /**
   * Inserts an item into the <code>Heap</code> while maintaining the properties of the binary heap. 
   */
  public synchronized void add(Comparable c){
    vec.add(c);
    percUp(vec.size()-1);
  }
  /**
   * Checks if the <code> Heap </code> functions correctly. This is a destuctive check which means after the checking, all the items are 
   * removed from the Heap and the Heap is made empty.
   */
  public boolean check(){
    //destuctively checks for sanity 
    //(you might be sane but unfortunately we have to kill you to find out)
    Comparable prev;
    if(!isEmpty()){
      prev = removeSmallest();
      while(!isEmpty()){
	Comparable curr = (Comparable) removeSmallest();
	if(curr.compareTo(prev)<0){
	  return false;
	}
	System.out.println("extracted " + curr);
	prev = curr;
      }
    }
    return true;
  }
 /**
  * A <code>StringBuffer<code> variable used to hold the String representation of the <code>Heap</code>.
  */
  private StringBuffer sb;
  /**
   * Overides the toString function of <code>Object</code> that gives a string representation of the Heap.
   * @return a string representation of the object
   */
  public synchronized String toString(){
    sb = new StringBuffer();
    toString(0, 0);
    return new String(sb);
  }
 /**
   * A recursively called function that gives a String representation of the binary heap that is rooted at <code>node</code>.
   * @param node an <code>int</code> value specifying the position of the node in the {@link #vec}.
   * @param indent an <code>int</code> value specifying the indent of the String representation.
   */
  private void toString(int node, int indent){
    if(node < vec.size()){
      int leftChild = node*2+1;
      toString(leftChild, indent+2);
      indent(indent);
      sb.append(vec.get(node).toString());
      int rightChild = leftChild+1;
      toString(rightChild, indent+2);
    }
  }
  /**
   * Generates the indent.
   * @param n an <code>int</code> value specifying how much the indent is.
   */
  private void indent(int n){
    sb.append("\n");
    for(int i = 0; i < n; i++){
      sb.append("  ");
    }
  }
}

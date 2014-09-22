
package jDREW.util;


import java.util.*;

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

  A SymbolTable is intended to keep track of the symbols and
  arities encountered, from parsing or other means, and to make
  theiir printNames available.  Symbol strings are stored in a
  Vector of Strings and arities are stored in a Vector of
  Integers.  When a symbol is to be entered to the SymbolTable, a
  check is made to determine if this symbol has been seen
  before. by looking it up in a HashTable of printNames. The
  HashTable associates the printName with an Integer representing
  the position in the SymbolTable where the symbol resides.  If
  it has not been seen before, then a new Symbol is position is
  added to the SymbolTable and the new Symbol is entered.
  
  

 */

public class SymbolTable  {

  /**
     Creates a new <code>SymbolTable</code> instance.
  */
  public SymbolTable() {
    hTable = new Hashtable();
    symbols = new Vector();
    arities = new Vector();
    symbols.add(0, null);//skip one item in the symbol table so that
    //no symbol exists in position 0
    arities.add(0, new Integer(0));
  }
  
  /**
     The variable <code>symbols</code> is a Vector of Strings,
     indexed from 1 -- we do not want any indexes to position 0.
     There is a null String added in position 0 in
     <code>symbols</code>, and subsequent symbols are put in at
     the next available position, as returned by
     <code>size</code>.  */
  private Vector symbols;

  /**
     The variable <code>arities</code> is a Vector of Integers,
     where the intValue of each entry is the arity of the symbol
     in the corresponding position in <code>symbols</code>.
     
  */
  private Vector arities;


  /** The HashTable keeps track of whether we have seen a given
      token representing a predicate symbol or function symbol
      before.  If not, it is entered into the symbol table at a
      new position.  This new token and its new position are
      placed in the hash table, using the new token as the key.
      If we have seen it before, we retrieve from the hash table
      the position of that token in the symbol table.

      Recall that in DCFileParser, for each symbol to be placed
      in a clause, an int[2] is created consisting of the
      symbol's position in the symbol table, so we can retrieve
      its print name when necessary, and the length of that
      symbol's structure in the clause.  */
  private Hashtable hTable;

  
  /** Internalize the symbol that has just been entered.  It may
      already have been entered.  If so, we return its position
      in the symbol table.  Otherwise we create a new symbol
      table entry for it and remember in the hash table where
      that entry is.
      @param symbol the newly read symbol that may or may not have
      been seen already.
      @return an integer identifying this symbols's position in
      the list of symbols seen so far.  This list is ordered from
      0.  */
  int internSymbol(String symbol, int arity){
    int sTPos;
    String symbolArity = printName(symbol, arity);
    Object hTablePos = hTable.get(symbolArity);
    if(hTablePos==null){
      sTPos = size();
      hTable.put(symbolArity, new Integer(sTPos));
      symbols.addElement(symbol);
      arities.addElement(new Integer(arity));
    }
    else
      sTPos = ((Integer)hTablePos).intValue();
    return sTPos;
  }
  /**
     <code>symbolTableString</code> returns the String at
     position i in the symbolTable.  It also does the cast to
     String.

     @param i - an <code>int</code> value
     @return a <code>String</code> value */
  public String symbolTableString(int i){
    return (String) symbols.get(i);
  }

  /**
     <code>arityTableInt</code> returns the arity of the symbol
     at position i in the symbolTable.  It casts the Object
     to an Integer and extracts the int value.

     @param i - an <code>int</code> value
     @return an <code>int</code> value */
  public int arityTableInt(int i){
    return ((Integer) arities.get(i)).intValue();
  }
  /**
     <code>size</code> returns the current size of the
     symbolTable Vector, which is taken as the next available
     position for inserting a new String into the symbolTable.

     @return an <code>int</code> value */
  public int size(){
    return symbols.size();
  }

  /**
     <code>printName</code> converts the symbol at index i to a
     name suitable for displaying.

     @param i - an <code>int</code> value
     @return a <code>String</code> value */
  public String printName(int i){
    return printName(symbolTableString(i), arityTableInt(i));
  }

  /**
     <code>printName</code> converts a symbols name and arity to
     a string suitable for printing.

     @param s - a <code>String</code> value
     @param i - an <code>int</code> value
     @return a <code>String</code> value */
  public String printName(String s, int i){
    return s + "/" + i;
  }

  public void dump(){
    Iterator sIt = symbols.iterator();
    Iterator aIt = arities.iterator();
    int count=0;
    while(sIt.hasNext()){
      String s = (String) sIt.next();
      Integer i = (Integer) aIt.next();
      System.out.println("Symbol " + count + " " + s + "/" + i);
      count++;
    }
    
  }


} // SymbolTable


// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.util;

import java.util.*;
import java.util.regex.*;
import jdrew.oo.*;
import nu.xom.*;

/**
 * An object that represents a logic term (Ind, Var, Cterm, Plex, Atom). For a
 * simple term (ind or variable) the subTerms array is set to null,
 * for non-simple terms (atoms, complex terms, plexs) the subTerms array
 * contains the parameters for that term.
 *
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: A deductive reasoning engine for Object-Oriented Knowledge
 * in OO RuleML</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Marcel Ball
 * @version 0.89
 */
public class Term implements Comparable {
      
        /**
         * This is a regular expression to test against to see if a symbol needs
         * to be quoted when producing POSL format output.
         */
         
        public static final String regex = "^(-)?[a-zA-Z0-9\\$][a-zA-Z0-9_\\$\\.]*$";

    /**
     *
     */
    public int symbol;
    /**
     *
     */
    public int wsymbol;
    /**
     *
     */
    public int role;
    /**
     *
     */
    public int type;
    /**
     *
     */
    public int href = -1;

    /**
     *
     */
    public Term[] subTerms = null;

    /**
     *
     */
    public Term oid = null;

    /**
     *
     */
    public boolean atom = false;
    /**
     *
     */
    boolean activeCall = false;

    /**
     *
     */
    public int rest = -1; 
    /**
     *
     */
    public int prest = -1;

    /**
     * Used by the unification process to check what clause a term belongs to
     * when doing unification. This is not used elsewhere.
     */
    int side;
    
    /**
     *This is used to distinguish between Data and Ind
     */
	private boolean isData = false;
	
	private boolean dataSlot = false;

    /**
     * Creates a new simple term (variable or ind).
     *
     * @param symbol int  The symbol code to be used for this term, for
     * a variable this is a negative number - and the variable name is stored
     * in the variableNames array of the DefiniteClause object, for a ind this
     * is a positive number and the symbol text is stored in the SymbolTable.
     *
     * @param role int  The role name code to be used for this term, for
     * a positional argument this is equal to the INOROLE member of the
     * SymbolTable. For slotted arguments the name of the role is stored in the
     * SymbolTable.
     *
     * @param type int  The type code to be used for this term, the types
     * for the engine are defined by an RDFS file, and are parsed by the
     * RDFSParser object into the Types system.
     */
     
     //The type comes from the RDFSParser object
          
    public Term(int symbol, int role, int type) {
        this.symbol = symbol;
        this.role = role;
        this.type = type;
    }

    /**
     * Create a new simple term with a URI reference or label (must be ind)
     *
     * @param symbol int The symbol code to be used for this term, for
     * a variable this is a negative number - and the variable name is stored
     * in the variableNames array of the DefiniteClause object, for a ind this
     * is a positive number and the symbol text is stored in the SymbolTable.
     *
     * @param role int The role name code to be used for this term, for a
     * positional argument this is equal to the INOROLE member of the
     * SymbolTable. For a slotted parameter the role name is stored in the
     * SymbolTable.
     *
     * @param type int The type code to be used for this term, the types
     * for the engine are defined by an RDFS file, and are parsed by the
     * RDFSParser object into the Types system.
     *
     * @param href int The symbol code for the URI for this term; The actual
     * URI is stored in the SymbolTable.
     */
     
     //This allows a uri
     
    public Term(int symbol, int role, int type, int href) {
        this.symbol = symbol;
        this.role = role;
        this.type = type;
        this.href = href;
    }

    /**
     * Creates a new complex term (atom, cterm, plex).
     *
     * @param symbol int value The symbol code to be used for the relation or
     * constructor of this term, this is always a positive number and the symbol
     * text is stored in the SymbolTable.
     *
     * @param role int The role name code to be used for this term, for a
     * positional argument this is equal to the INOROLE member of the
     * SymbolTable. For a slotted parameter the role name is stored in the
     * SymbolTable.
     *
     * @param type int The type code to be used for this term, the types
     * for the engine are defined by an RDFS file, and are parsed by the
     * RDFSParser object into the Types system.
     *
     * @param subTerms Term[] An array containing the terms
     * that will be the parameters of this atom, cterm or plex that is being
     * created.
     */
    
    //The symbol is the code to be used for the relation or the constructor
    //of the term
    //The role for a positional argument is equal to INORLE of the symbol table
    //For sloted parameters the role name is stored in the symbol table
    
    public Term(int symbol, int role, int type, Term[] subTerms) {
        this(symbol, role, type);
        Vector st = this.sort(subTerms);
        this.subTerms = new Term[st.size()];
        for (int i = 0; i < st.size(); i++) {
            this.subTerms[i] = (Term) st.get(i);
            if (this.subTerms[i].getRole() == SymbolTable.IREST) {
                rest = i;
            }
            if (this.subTerms[i].getRole() == SymbolTable.IPREST) {
                prest = i;
            }
        }
    }

    /**
     * Creates a new complex term (atom, cterm, plex).
     *
     * @param symbol int value The symbol code to be used for the relation or
     * constructor of this term, this is always a positive number and the symbol
     * text is stored in the SymbolTable.
     *
     * @param role int value The role name code to be used for this term, for a
     * positional argument this is equal to the INOROLE member of the
     * SymbolTable. For a slotted parameter the role name is stored in the
     * SymbolTable.
     *
     * @param type int value The type code to be used for this term, the types
     * for the engine are defined by an RDFS file, and are parsed by the
     * RDFSParser object into the Types system.
     *
     * @param subTerms Vector value An array containing the terms
     * that will be the parameters of this atom, cterm or plex that is being
     * created.
     */
    public Term(int symbol, int role, int type, Vector subTerms) {
        this(symbol, role, type);
        Vector st = this.sort(subTerms);
        this.subTerms = new Term[st.size()];
        for (int i = 0; i < st.size(); i++) {
            this.subTerms[i] = (Term) st.get(i);
            if (this.subTerms[i].getRole() == SymbolTable.IREST) {
                rest = i;
            }
            if (this.subTerms[i].getRole() == SymbolTable.IPREST) {
                prest = i;
            }
        }
    }

    /**
     * A method to get the symbol for a role within a complex term. If the r
     * value is "" this will return the first positional argument. Also if there
     * is more than one value of a role name this will return the first one.
     *
     * @param r String A string containing the name of the role to
     * retrieve the symbol for.
     *
     * @return String A string containing the symbol associated with the
     * passed role. If this is a variable it will be returned as ?Varx, were x
     * is the variable id, for for complex term this will contain the
     * constructor symbol. Values are not enclosed within " even if this would
     * be required for POSL Syntax.
     *
     */
    public String getSymbolForRole(String r) {
        int role = SymbolTable.internRole(r);
        for (int i = 0; i < this.subTerms.length; i++) {
            if (subTerms[i].getRole() == role) {
                return subTerms[i].getSymbolString();
            }
        }
        return null;
    }

    /**
     * Gets the symbol code for this term. If it is a complex term this gives
     * the symbol code for the predicate/constructor. If this value is a
     * negative number then this term is a variable.
     *
     * @return int value - the symbol code for this term.
     */
    public int getSymbol() {
        return symbol;
    }

    /**
     * Sets the symbol code for this term.
     *
     * @param symbol int value - the value to set symbol to.
     */
    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the symbol for this term. If it is a complex term this gives the
     * symbol for the predicate/constructor. If it is a variable then the
     * symbol is prefixed with a ?.
     *
     * @return java.lang.String value - the symbol for this term.
     */
    public String getSymbolString() {
        if (symbol > 0) {
            return SymbolTable.symbol(symbol);
        } else {
            return "?Var" + ( -(symbol + 1));
        }
    }

    /**
     * Gets the role name code for this term. If this is a rest paramater it
     * will be equal to the IREST member of the associated SymbolTable. If this
     * is a positional argument it will be equal to the INOROLE member of the
     * associated SymbolTable.
     *
     * @return int value - the role code for this term.
     */
    public int getRole() {
        return role;
    }

    /**
     * Sets the role code for this term.
     *
     * @param role int value - the value to set role to.
     */
    public void setRole(int role) {
        this.role = role;
    }

    /**
     * Gets the role name for this term. If this is a positional argument this
     * will be an empty string, for a rest paramater it will be '$REST',
     * otherwise it is the name of the role.
     *
     * @return java.lang.String value - the role name for this term.
     */
    public String getRoleString() {
        return SymbolTable.role(role);
    }

    /**
     * Gets the type code for this term. For an untyped term this will be equal
     * to the IOBJECT member of the TypeHierarchy associated with the clause.
     *
     * @return int value - the type code for this term.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type code for this term.
     *
     * @param type int value - the value to set type to.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets the value of the side member variable - this is used by the
     * unification process to keep track of terms.
     *
     * @return int value - the current value of the side member.
     */
    public int getSide() {
        return side;
    }

    /**
     * Sets the value of the side member variable.
     *
     * @param side int value - the value to set side to.
     */
    public void setSide(int side) {
        this.side = side;
    }

    /**
     * Gets the index of the slotted rest paramater.
     *
     * @return int value - the index of the slotted rest parameter within this
     * term, or -1 if this is a simple term or a complex term with no slotted
     * rest paramater.
     */
    public int getRest() {
        return rest;
    }

    /**
     * Gets the index of the positional rest parameter.
     *
     * @return int value - the index of the position rest parameter within this
     * term, or -1 if this is a simple term or a complex term with no positional
     * rest parameter.
     */
    public int getPosRest() {
        return prest;
    }

    /**
     * Gets an array of the arguments for this term.
     *
     * @return jdrew.oo.util.Term[] value - a Term[] array of the paramaters for
     * this term. Returns null if this is a simple term (variable or ind).
     */
    public Term[] getSubTerms() {
        return subTerms;
    }

    /**
     * Tests if a term is a complex term.
     *
     * @return boolean value - true if this term is a complex term (atom, cterm,
     * tup) false otherwise.
     */
    public boolean isCTerm() {
        return (subTerms != null);
    }

    /**
     * Test if this term is an atom.
     *
     * @return boolean value - true if this term is an atom, false otherwise.
     * This is used only by the output routines, for other things cterms and
     * atoms can be treated the same.
     */
    public boolean isAtom() {
        return (subTerms != null && atom);
    }

    /**
     * Sets the boolean atom flag to the passed value, if the atom member
     * variable is set to true and it is not a complex term (subTerms array is
     * set to null) isAtom() will still return false.
     *
     * @param atom boolean value - the value that the atom member should be set
     * to.
     */
    public void setAtom(boolean atom) {
        this.atom = atom;
    }
    
    /**
     * Sets the isData flag to the passed value, if isData
     * variable is set to true then it is a data element if not
     * it is a Ind
     * @param data boolean value - the value that the isData member should be
     * set to.
     */
    
    public void setData(boolean data){
    	isData = data;
    }
    
    /**
     * Gets the isData vaule
     * @return boolean - the value that the isData member
     */
    
    public boolean getData(){
  		return isData;  	
    }

	public void setDataSlot(boolean data){
		dataSlot = data;
	}
	
	public boolean getDataSlot(){
		return dataSlot;
	}
	
    /**
     * Returns a string representation of this term. The format of this string
     * is determined by the value of the PRPRINT static variable in the
     * jdrew.oo.Config class. If this is true, this will produce a string in
     * POSL syntax, otherwise it will produce a string in OO RuleML XML
     * syntax.
     *
     * This method is used when you do not have access to the variable names;
     * variables are output as ?Varx - where x is the variable id.
     *
     * @return java.lang.String value - the String representation of this term.
     */
    public String toString() {
        if (Config.PRPRINT) {
            return this.toPOSLString(true);
        } else {
            return this.toRuleMLString();
        }
    }

    /**
     * Returns a string representation of this term. The format of this string
     * is determined by the value of the PRPRINT static variable in the
     * jdrew.oo.Config class. If this is true, this will produce a string in
     * POSL syntax, otherwise it will produce a string in OO RuleML XML
     * syntax.
     *
     * @return java.lang.String value - the String representation of this term.
     */
    public String toString(String[] varNames) {
        if (Config.PRPRINT) {
            return this.toPOSLString(varNames, true);
        } else {
            return this.toRuleMLString(varNames);
        }
    }

    /**
     * Returns a string representation of this term in POSL syntax. This method
     * is used in cases where you do not have access to the variable names
     * associated with the clause - variablenames will be output as ?Varx where
     * x is the variable id.
     *
     * @return java.lang.String value - the POSL syntax representation of this
     * term.
     */

    public String toPOSLString(boolean head) {
        String s = "";

        if (this.role != SymbolTable.INOROLE &&
            this.role != SymbolTable.IREST &&
            this.role != SymbolTable.IPREST) {
            String rs = SymbolTable.role(this.role);
            if (!rs.matches(regex)) {
                rs = "\"" + rs + "\"";
            }
            s += rs + "->";
        }

        if (this.symbol < 0) {
            s += "?Var" + ( -(symbol + 1));
        } else {
            if (this.symbol != SymbolTable.INAF &&
                this.symbol != SymbolTable.IPLEX &&
                this.symbol != SymbolTable.IASSERT) {
                String sym = SymbolTable.symbol(this.symbol);

                if(sym.startsWith("$gensym") && !jdrew.oo.Config.PRINTGENSYMS){
                    int idx = sym.indexOf("$", 7);
                    if(idx > -1){
                        String skoname = sym.substring(idx + 1);
                        if(!sym.matches(regex)){
                            skoname = "\"" + skoname + "\"";
                        }
                        sym = "_" + skoname;
                    }
                    else
                        sym = "_";
                }
                else if (!sym.matches(regex)) {
                    sym = "\"" + sym + "\"";
                }
                s += sym;
            }
        }

        if (this.isAtom() && this.symbol == SymbolTable.INAF) {
            s += "naf(";
            for (int i = 0; i < this.subTerms.length; i++) {
                s += this.subTerms[i].toPOSLString(head);
                if ((i + 1) < this.subTerms.length) {
                    s += ",";
                }
            }
            s += ")";
        } else if (this.isAtom() && this.symbol == SymbolTable.IASSERT) {
            s += "assert( ";
            s += this.subTerms[0].toPOSLString(true);
            if (this.subTerms.length > 1) {
                s += " :- ";
                for (int i = 1; i < this.subTerms.length; i++) {
                    s += this.subTerms[i].toPOSLString(false);
                    if (i + 1 < this.subTerms.length) {
                        s += ", ";
                    }
                }
            }
            s += ". )";
        } else if (this.isCTerm()) {
            String sb;
            String eb;
            if (this.isAtom()) {
                sb = "(";
                eb = ")";
            } else {
                sb = "[";
                eb = "]";
            }

            s += sb;
            for (int i = 0; i < this.subTerms.length; i++) {
                if (this.subTerms[i].role == SymbolTable.IREST) {
                    s += " !"; // handle printout if term is a slotted rest term
                } else if (this.subTerms[i].role == SymbolTable.IPREST) {
                    s += " |"; // handle printout if term is a positional rest term
                }

                if(this.subTerms[i].role == SymbolTable.IOID && !jdrew.oo.Config.PRINTGENOIDS && head){
                    if(this.subTerms[i].getSymbolString().startsWith("$gensym"))
                        continue;
                }
                s += this.subTerms[i].toPOSLString(head);
                if (this.subTerms[i].role == SymbolTable.IOID) {
                    s += "^ ";
                }

                if ((i + 1) < this.subTerms.length) {


                    if (this.subTerms[i].role != SymbolTable.IOID &&
                        this.subTerms[i + 1].role == SymbolTable.INOROLE) {
                        s += ", ";
                    } else if (this.subTerms[i + 1].role == SymbolTable.IREST) {
                    } else if (this.subTerms[i + 1].role == SymbolTable.IPREST) {
                    } else if (this.subTerms[i].role != SymbolTable.IOID) {
                        s += "; ";
                    }
                }
            }
            s += eb;
        }

        if (this.type != Types.IOBJECT) {
            String ts = Types.typeName(this.type);
            if (!ts.matches(regex)) {
                ts = "\"" + ts + "\"";
            }
            s += " : " + ts;
        }
        return s;
    }

    /**
     * Returns a string representation of this term in POSL syntax.
     *
     * @param varNames String[] A string array containing the variable names
     * to use when outputting the string. These are stored in the
     * DefiniteClause that the term is part of.
     *
     * @return String The POSL syntax representation of the term.
     */
    public String toPOSLString(String[] varNames) {
        return toPOSLString(varNames, false, true);
    }

    public String toPOSLString(String[] varNames, boolean head){
        return toPOSLString(varNames, false, head);
    }

    /**
     * Returns a string representation of this term in POSL syntax, with the
     * option of omitting the role name. This is used by the mechanism for
     * saving variable bindings.
     *
     * @param varNames String[] A string array containing the variable names
     * to use when outputting the string. These are stored in the
     * DefiniteClause that the term is part of.
     *
     * @param skiprole boolean If true then the role name is omitted from the
     * string output - if false then the role name is included.
     *
     * @return String The POSL syntax representation of the term.
     */
    public String toPOSLString(String[] varNames, boolean skiprole, boolean head) {
        String s = "";

        if (!skiprole) {
            if (this.role != SymbolTable.INOROLE &&
                this.role != SymbolTable.IREST &&
                this.role != SymbolTable.IPREST &&
                this.role != SymbolTable.IOID) {
                String rs = SymbolTable.role(this.role);
                if (!rs.matches(regex)) {
                    rs = "\"" + rs + "\"";
                }
                s += rs + "->";
            }
        }

        if (this.symbol < 0) {
            if (varNames[ -(symbol + 1)].startsWith("$ANON") && !jdrew.oo.Config.PRINTANONVARNAMES) {
                s += "?";
            } else {
                s += "?" + varNames[ -(symbol + 1)];
                if (jdrew.oo.Config.PRINTVARID) {
                    s += "_" + ( -(symbol + 1));
                }
            }
        } else {
            if (this.symbol != SymbolTable.INAF &&
                this.symbol != SymbolTable.IPLEX &&
                this.symbol != SymbolTable.IASSERT) {
                String sym = SymbolTable.symbol(this.symbol);

                if(sym.startsWith("$gensym") && !jdrew.oo.Config.PRINTGENSYMS){
                    int idx = sym.indexOf("$", 7);
                    if(idx > -1){
                        String skoname = sym.substring(idx + 1);
                        if(!sym.matches(regex)){
                            skoname = "\"" + skoname + "\"";
                        }
                        sym = "_" + skoname;
                    }
                    else
                        sym = "_";
                }
                else if (!sym.matches(regex)) {
                    sym = "\"" + sym + "\"";
                }

                s += sym;
            }
        }

        if (this.isAtom() && this.symbol == SymbolTable.INAF) {
            s += "naf( ";
            for (int i = 0; i < this.subTerms.length; i++) {
                s += this.subTerms[i].toPOSLString(varNames, head);
                if ((i + 1) < this.subTerms.length) {
                    s += ",";
                }
            }
            s += " )";
        } else if (this.isAtom() && this.symbol == SymbolTable.IASSERT) {
            s += "assert( ";
            s += this.subTerms[0].toPOSLString(varNames, true);
            if (this.subTerms.length > 1) {
                s += " :- ";
                for (int i = 1; i < this.subTerms.length; i++) {
                    s += this.subTerms[i].toPOSLString(varNames, false);
                    if (i + 1 < this.subTerms.length) {
                        s += ", ";
                    }
                }
            }
            s += ". )";
        } else if (this.isCTerm()) {
            String sb;
            String eb;
            if (this.isAtom()) {
                sb = "(";
                eb = ")";
            } else {
                sb = "[";
                eb = "]";
            }

            s += sb;
            for (int i = 0; i < this.subTerms.length; i++) {
                if (this.subTerms[i].role == SymbolTable.IREST) {
                    s += "!"; // handle printout if first term is a slotted rest term
                } else if (this.subTerms[i].role == SymbolTable.IPREST) {
                    s += "|"; // handle printout if first term is a positional rest term
                }

                if(this.subTerms[i].role == SymbolTable.IOID && !jdrew.oo.Config.PRINTGENOIDS){
                    if(this.subTerms[i].symbol > 0 && this.subTerms[i].getSymbolString().startsWith("$gensym") && head)
                        continue;
                    if(this.subTerms[i].symbol < 0 && varNames[-(this.subTerms[i].symbol + 1)].startsWith("$ANON") && !head)
                        continue;
                }

                s += this.subTerms[i].toPOSLString(varNames, head);
                if (this.subTerms[i].role == SymbolTable.IOID) {
                    s += "^ ";
                }

                if ((i + 1) < this.subTerms.length) {
                    if (this.subTerms[i].role != SymbolTable.IOID &&
                        this.subTerms[i + 1].role == SymbolTable.INOROLE) {
                        s += ", ";
                    } else if (this.subTerms[i + 1].role == SymbolTable.IREST) {
                    } else if (this.subTerms[i + 1].role == SymbolTable.IPREST) {
                    } else if (this.subTerms[i].role != SymbolTable.IOID) {
                        s += "; ";
                    }
                }
            }
            s += eb;
        }

        if (this.type != Types.IOBJECT) {
            String ts = Types.typeName(this.type);
            if (!ts.matches(regex)) {
                ts = "\"" + ts + "\"";
            }
            s += " : " + ts;
        }
        return s;
    }


    /**
     * Produces an OO RuleML XML syntax representation of this term, stored in a
     * string.
     *
     * This version is for the case where you do not have access to the
     * variable names for the term.
     *
     * @return java.lang.String value - The OO RuleML syntax representation of
     * this, stored as a "pretty printed" string.
     */
     
     
    public String toRuleMLString() {
        Element rml = this.toRuleML(true);
        java.io.StringWriter sw = new java.io.StringWriter();
        nu.xom.Serializer sl = new nu.xom.Serializer(sw);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
            sl.write(rml);
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        }
        return sw.getBuffer().toString();
    }

    /**
     * Produces an OO RuleML XML syntax representation of this term, stored in a
     * string.
     *
     * @param varNames String[] A string array containing the variable names to
     * use when outputting - these are stored in the DefiniteClause object
     * associated with the term.
     *
     * @return String A string containing the OO RuleML XML representation of
     * this term.
     */
    public String toRuleMLString(String[] varNames) {
        Element rml = this.toRuleML(varNames, true);
        java.io.StringWriter sw = new java.io.StringWriter();
        nu.xom.Serializer sl = new nu.xom.Serializer(sw);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
            sl.write(rml);
        } catch (java.io.IOException e) {
            System.err.println(e.getMessage());
        }
        return sw.getBuffer().toString();
    }

    /**
     * Produces an OO RuleML XML syntax representation of this term, stored in a
     * nu.xom.Element object.
     *
     * This version is for when you do not have access to the variable names.
     *
     * @return Element The OO RuleML syntax representation of
     * this, as a Element value.
     */

    public Element toRuleML(boolean head) {
    	
    	//Print the Clauses in RULEML91 Format
    	
   		if(jdrew.oo.gui.BottomUpGUI.currentParser == RuleMLParser.RULEML91 ||
   		    jdrew.oo.gui.TopDownGUI.currentParser == RuleMLParser.RULEML91 ||
   		    jdrew.oo.gui.Translator.currentParser == RuleMLParser.RULEML91){
   		   			
   		Element el = null;
       	boolean dst = false;

        if (this.isCTerm()) {
            if (this.isAtom() && this.symbol == SymbolTable.INAF) {
                el = new Element("Naf");
            } else if (this.isAtom() && this.symbol == SymbolTable.IASSERT) {
                el = new Element("Assert");
                if (this.subTerms.length == 1) {
                    el.insertChild(subTerms[0].toRuleML(true), 0);
                } else {
                    Element el2 = new Element("Implies");
                    if (this.subTerms.length > 2) {
                        Element el3 = new Element("And");
                        for (int i = 1; i < this.subTerms.length; i++) {
                            el3.appendChild(this.subTerms[i].toRuleML(false));
                        }
                        el2.appendChild(el3);
                    } else {
                        el2.appendChild(this.subTerms[1].toRuleML(false));
                    }
                    el2.appendChild(this.subTerms[0].toRuleML(true));
                    el.appendChild(el2);

                }
                dst = true;
            } else if (this.isAtom()) {
                el = new Element("Atom");
                Element rel = new Element("Rel");
                el.insertChild(rel, el.getChildCount());
                rel.insertChild(SymbolTable.symbol(this.symbol), 0);
            } else if (this.symbol == SymbolTable.IPLEX) {
                el = new Element("Plex");
            } else {
                el = new Element("Expr");
                Element ctor = new Element("Fun");
                el.insertChild(ctor, el.getChildCount());
                ctor.insertChild(SymbolTable.symbol(this.symbol), 0);
            }

            for (int i = 0; i < this.subTerms.length && !dst; i++) {
                el.insertChild(this.subTerms[i].toRuleML(head), el.getChildCount());
            }
        } else {
            if(this.role == SymbolTable.IOID && !jdrew.oo.Config.PRINTGENOIDS && head){
                if(this.symbol > 0 && this.getSymbolString().startsWith("$gensym"))
                    return null;
            }


            if (this.symbol < 0) {
                el = new Element("Var");
                el.insertChild("Var" + ( -(symbol + 1)), 0);
            } else {
                String sym = SymbolTable.symbol(this.symbol);
                if(sym.startsWith("$gensym") && !jdrew.oo.Config.PRINTGENSYMS){
                    el = new Element("Skolem");
                    int idx = sym.indexOf("$", 7);
                    if(idx > -1){
                        String skoname = sym.substring(idx + 1);
                        el.appendChild(skoname);
                    }
                }else{
                	
                	//if isData is set then add it as Data if not then IND
                	//Need to find where this method is called So I can set
                	//the isData before it gets to this point with a method
                	//That I need to create at some point
                	
                	if(isData){
                   	 	el = new Element("Data");
                    	el.insertChild(sym, 0); 	
                	}
                	
                	if(!isData){
                    	el = new Element("Ind");
                    	el.insertChild(sym, 0);                		
                	}
                	

                }
            }
        }

        if (this.type != Types.IOBJECT) {
            Attribute a = new Attribute("type", Types.typeName(this.type));
            el.addAttribute(a);
        }

        if (this.role == SymbolTable.IREST) {
            Element e2 = new Element("resl");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.IPREST) {
            Element e2 = new Element("repo");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.IOID) {
            Element e2 = new Element("oid");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.INOROLE) {
            return el;
        } else {
            Element e2 = new Element("slot");
			Element e3 = null;
			//if isData is true then change it here
			
			if(!dataSlot){
           		e3 = new Element("Ind");
           		e3.appendChild(SymbolTable.role(this.role));
            }
            
            if(dataSlot){
            	e3 = new Element("Data");
            	e3.appendChild(SymbolTable.role(this.role));	
            }
            
            e2.appendChild(e3);
            e2.appendChild(el);
            return e2;
        }
   	}//RuleML91
    	
        //Print the Clauses in RULEML89 Format
    	
   		if(jdrew.oo.gui.BottomUpGUI.currentParser == RuleMLParser.RULEML88 ||
   		   jdrew.oo.gui.TopDownGUI.currentParser == RuleMLParser.RULEML88  ||
   		   jdrew.oo.gui.Translator.currentParser == RuleMLParser.RULEML88){
    	
    	
    	        Element el = null;
        boolean dst = false;

        if (this.isCTerm()) {
            if (this.isAtom() && this.symbol == SymbolTable.INAF) {
                el = new Element("Naf");
            } else if (this.isAtom() && this.symbol == SymbolTable.IASSERT) {
                el = new Element("Assert");
                if (this.subTerms.length == 1) {
                    el.insertChild(subTerms[0].toRuleML(true), 0);
                } else {
                    Element el2 = new Element("Implies");
                    if (this.subTerms.length > 2) {
                        Element el3 = new Element("And");
                        for (int i = 1; i < this.subTerms.length; i++) {
                            el3.appendChild(this.subTerms[i].toRuleML(false));
                        }
                        el2.appendChild(el3);
                    } else {
                        el2.appendChild(this.subTerms[1].toRuleML(false));
                    }
                    el2.appendChild(this.subTerms[0].toRuleML(true));
                    el.appendChild(el2);

                }
                dst = true;
            } else if (this.isAtom()) {
                el = new Element("Atom");
                Element rel = new Element("Rel");
                el.insertChild(rel, el.getChildCount());
                rel.insertChild(SymbolTable.symbol(this.symbol), 0);
            } else if (this.symbol == SymbolTable.IPLEX) {
                el = new Element("Plex");
            } else {
                el = new Element("Cterm");
                Element ctor = new Element("Ctor");
                el.insertChild(ctor, el.getChildCount());
                ctor.insertChild(SymbolTable.symbol(this.symbol), 0);
            }

            for (int i = 0; i < this.subTerms.length && !dst; i++) {
                el.insertChild(this.subTerms[i].toRuleML(head), el.getChildCount());
            }
        } else {
            if(this.role == SymbolTable.IOID && !jdrew.oo.Config.PRINTGENOIDS && head){
                if(this.symbol > 0 && this.getSymbolString().startsWith("$gensym"))
                    return null;
            }


            if (this.symbol < 0) {
                el = new Element("Var");
                el.insertChild("Var" + ( -(symbol + 1)), 0);
            } else {
                String sym = SymbolTable.symbol(this.symbol);
                if(sym.startsWith("$gensym") && !jdrew.oo.Config.PRINTGENSYMS){
                    el = new Element("Skolem");
                    int idx = sym.indexOf("$", 7);
                    if(idx > -1){
                        String skoname = sym.substring(idx + 1);
                        el.appendChild(skoname);
                    }
                }else{
                	
                	//if isData is set then add it as Data if not then IND
                	//Need to find where this method is called So I can set
                	//the isData before it gets to this point with a method
                	//That I need to create at some point
                	
                	if(isData){
                   	 	el = new Element("Data");
                    	el.insertChild(sym, 0); 	
                	}
                	
                	if(!isData){
                    	el = new Element("Ind");
                    	el.insertChild(sym, 0);                		
                	}
                	

                }
            }
        }

        if (this.type != Types.IOBJECT) {
            Attribute a = new Attribute("type", Types.typeName(this.type));
            el.addAttribute(a);
        }

        if (this.role == SymbolTable.IREST) {
            Element e2 = new Element("resl");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.IPREST) {
            Element e2 = new Element("repo");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.IOID) {
            Element e2 = new Element("oid");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.INOROLE) {
            return el;
        } else {
            Element e2 = new Element("slot");
			Element e3 = null;
			//if isData is true then change it here
			
			if(!dataSlot){
           		e3 = new Element("Ind");
           		e3.appendChild(SymbolTable.role(this.role));
            }
            
            if(dataSlot){
            	e3 = new Element("Data");
            	e3.appendChild(SymbolTable.role(this.role));	
            }
            
            e2.appendChild(e3);
            e2.appendChild(el);
            return e2;
        }
    	
   	}
    	
      	Element ret = null;
		return ret;
    }

    /**
     * Produces an OO RuleML XML syntax representation of this term, stored in a
     * nu.xom.Element object.
     *
     * @param varNames String[] The variable names associated with the term;
     * these are stored in the DefiniteClause object associated with the term.
     *
     * @return Element The OO RuleML syntax representation of this, as an
     * Element value.
     */
     
    public Element toRuleML(String[] varNames, boolean head) {
    	
    	//Printing the Clauses in RuleML 0.91 Format
    	
        if(jdrew.oo.gui.BottomUpGUI.currentParser == RuleMLParser.RULEML91  ||
   		     jdrew.oo.gui.TopDownGUI.currentParser == RuleMLParser.RULEML91 ||
   		     jdrew.oo.gui.Translator.currentParser == RuleMLParser.RULEML91){

        Element el = null;
        boolean dst = false;
        if (this.isCTerm()) {
            if (this.isAtom() && this.symbol == SymbolTable.INAF) {
                el = new Element("Naf");
            } else if (this.isAtom() && this.symbol == SymbolTable.IASSERT) {
                el = new Element("Assert");
                if (this.subTerms.length == 1) {
                    el.insertChild(subTerms[0].toRuleML(varNames, true), 0);
                } else {
                    Element el2 = new Element("Implies");
                    if (this.subTerms.length > 2) {
                        Element el3 = new Element("And");
                        for (int i = 1; i < this.subTerms.length; i++) {
                            el3.appendChild(this.subTerms[i].toRuleML(varNames, false));
                        }
                        el2.appendChild(el3);
                    } else {
                        el2.appendChild(this.subTerms[1].toRuleML(varNames, false));
                    }
                    el2.appendChild(this.subTerms[0].toRuleML(varNames, true));
                    el.appendChild(el2);

                }
                dst = true;
            }

            else if (this.isAtom()) {
                el = new Element("Atom");
                Element rel = new Element("Rel");
                el.insertChild(rel, el.getChildCount());
                rel.insertChild(SymbolTable.symbol(this.symbol), 0);
            } else if (this.symbol == SymbolTable.IPLEX) {
                el = new Element("Plex");
            } else {
                el = new Element("Expr");
                Element ctor = new Element("Fun");
                el.insertChild(ctor, el.getChildCount());
                ctor.insertChild(SymbolTable.symbol(this.symbol), 0);
            }

            for (int i = 0; i < this.subTerms.length && !dst; i++) {
                Element tmp = this.subTerms[i].toRuleML(varNames, head);
                if(tmp != null)
                    el.insertChild(tmp, el.getChildCount());
            }
        } else {
            if(this.role == SymbolTable.IOID && !jdrew.oo.Config.PRINTGENOIDS){
                if(this.symbol > 0 && this.getSymbolString().startsWith("$gensym") && head)
                    return null;
                else if(this.symbol < 0 && varNames[-(this.symbol + 1)].startsWith("$ANON") && !head)
                    return null;
            }
            if (this.symbol < 0) {
                el = new Element("Var");
                if (!varNames[ -(symbol + 1)].startsWith("$ANON") || jdrew.oo.Config.PRINTANONVARNAMES) {
                    String vname = varNames[ -(symbol + 1)];
                    if(jdrew.oo.Config.PRINTVARID)
                        vname += -(symbol + 1);
                    el.insertChild(vname, 0);
                }
            } else {
                String sym = SymbolTable.symbol(this.symbol);
                if(sym.startsWith("$gensym") && !jdrew.oo.Config.PRINTGENSYMS){
                    el = new Element("Skolem");
                    int idx = sym.indexOf("$", 7);
                    if(idx > -1){
                        String skoname = sym.substring(idx + 1);
                        el.appendChild(skoname);
                    }
                }else{
                	
                	
                	System.out.println(this.toPOSLString(true));
                	//modify here to make change for Data
                	//second postion in the slot
                	if(!isData){
                    	el = new Element("Ind");
                    	el.insertChild(sym, 0);
                	}
                	if(isData){
                    	el = new Element("Data");
                    	el.insertChild(sym, 0);                		
                	}
                }
            }
        }

        if (this.type != Types.IOBJECT) {
            Attribute a = new Attribute("type", Types.typeName(this.type));
            el.addAttribute(a);
        }

        if (this.role == SymbolTable.IREST) {
            Element e2 = new Element("resl");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.IPREST) {
            Element e2 = new Element("repo");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.IOID) {
            Element e2 = new Element("oid");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.INOROLE) {
            return el;
        } else {
        	System.out.println("slot");
        	
        	System.out.println(this.toPOSLString(true));
        	System.out.println(this.getData());
        	
            Element e2 = new Element("slot");
			//modify here
            Element e3 = null;
            //first postion in the slot
            //need a different boolean to tell if data is first
            //postion in slot or not.
            
            if(!dataSlot){
            	e3 = new Element("Ind");
            	e3.appendChild(SymbolTable.role(this.role));
            }
            if(dataSlot){
            	e3 = new Element("Data");
            	e3.appendChild(SymbolTable.role(this.role));
            }
            
            
            e2.appendChild(e3);
            e2.appendChild(el);
            return e2;
        }
		
   	}//RuleML 0.91
    	
    	//RuleML 0.88 Format
    	
        if(jdrew.oo.gui.BottomUpGUI.currentParser == RuleMLParser.RULEML88 ||
   		     jdrew.oo.gui.TopDownGUI.currentParser == RuleMLParser.RULEML88||
   		     jdrew.oo.gui.Translator.currentParser == RuleMLParser.RULEML88){    	
      	
      	Element el = null;
        boolean dst = false;
        if (this.isCTerm()) {
            if (this.isAtom() && this.symbol == SymbolTable.INAF) {
                el = new Element("Naf");
            } else if (this.isAtom() && this.symbol == SymbolTable.IASSERT) {
                el = new Element("Assert");
                if (this.subTerms.length == 1) {
                    el.insertChild(subTerms[0].toRuleML(varNames, true), 0);
                } else {
                    Element el2 = new Element("Implies");
                    if (this.subTerms.length > 2) {
                        Element el3 = new Element("And");
                        for (int i = 1; i < this.subTerms.length; i++) {
                            el3.appendChild(this.subTerms[i].toRuleML(varNames, false));
                        }
                        el2.appendChild(el3);
                    } else {
                        el2.appendChild(this.subTerms[1].toRuleML(varNames, false));
                    }
                    el2.appendChild(this.subTerms[0].toRuleML(varNames, true));
                    el.appendChild(el2);

                }
                dst = true;
            }

            else if (this.isAtom()) {
                el = new Element("Atom");
                Element rel = new Element("Rel");
                el.insertChild(rel, el.getChildCount());
                rel.insertChild(SymbolTable.symbol(this.symbol), 0);
            } else if (this.symbol == SymbolTable.IPLEX) {
                el = new Element("Plex");
            } else {
                el = new Element("Cterm");
                Element ctor = new Element("Ctor");
                el.insertChild(ctor, el.getChildCount());
                ctor.insertChild(SymbolTable.symbol(this.symbol), 0);
            }

            for (int i = 0; i < this.subTerms.length && !dst; i++) {
                Element tmp = this.subTerms[i].toRuleML(varNames, head);
                if(tmp != null)
                    el.insertChild(tmp, el.getChildCount());
            }
        } else {
            if(this.role == SymbolTable.IOID && !jdrew.oo.Config.PRINTGENOIDS){
                if(this.symbol > 0 && this.getSymbolString().startsWith("$gensym") && head)
                    return null;
                else if(this.symbol < 0 && varNames[-(this.symbol + 1)].startsWith("$ANON") && !head)
                    return null;
            }
            if (this.symbol < 0) {
                el = new Element("Var");
                if (!varNames[ -(symbol + 1)].startsWith("$ANON") || jdrew.oo.Config.PRINTANONVARNAMES) {
                    String vname = varNames[ -(symbol + 1)];
                    if(jdrew.oo.Config.PRINTVARID)
                        vname += -(symbol + 1);
                    el.insertChild(vname, 0);
                }
            } else {
                String sym = SymbolTable.symbol(this.symbol);
                if(sym.startsWith("$gensym") && !jdrew.oo.Config.PRINTGENSYMS){
                    el = new Element("Skolem");
                    int idx = sym.indexOf("$", 7);
                    if(idx > -1){
                        String skoname = sym.substring(idx + 1);
                        el.appendChild(skoname);
                    }
                }else{
                	
                	
                	System.out.println(this.toPOSLString(true));
                	//modify here to make change for Data
                	//second postion in the slot
                	if(!isData){
                    	el = new Element("Ind");
                    	el.insertChild(sym, 0);
                	}
                	if(isData){
                    	el = new Element("Data");
                    	el.insertChild(sym, 0);                		
                	}
                }
            }
        }

        if (this.type != Types.IOBJECT) {
            Attribute a = new Attribute("type", Types.typeName(this.type));
            el.addAttribute(a);
        }

        if (this.role == SymbolTable.IREST) {
            Element e2 = new Element("resl");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.IPREST) {
            Element e2 = new Element("repo");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.IOID) {
            Element e2 = new Element("oid");
            e2.insertChild(el, 0);
            return e2;
        } else if (this.role == SymbolTable.INOROLE) {
            return el;
        } else {
        	System.out.println("slot");
        	
        	System.out.println(this.toPOSLString(true));
        	System.out.println(this.getData());
        	
            Element e2 = new Element("slot");
			//modify here
            Element e3 = null;
            //first postion in the slot
            //need a different boolean to tell if data is first
            //postion in slot or not.
            
            if(!dataSlot){
            	e3 = new Element("Ind");
            	e3.appendChild(SymbolTable.role(this.role));
            }
            if(dataSlot){
            	e3 = new Element("Data");
            	e3.appendChild(SymbolTable.role(this.role));
            }
            
            
            e2.appendChild(e3);
            e2.appendChild(el);
            return e2;
        }
      	
      	
    }//RuleML 0.88
      	
      	
      	Element ret = null;
		return ret;
    }

    /**
     * Make an identical deep (recursive) copy of this term. This is used when
     * unifying to avoid making changes to the original data strucutres that
     * represent clauses.
     *
     * @return jdrew.oo.util.Term value - the copy of this term.
     */
    public Term deepCopy() {
        if (this.subTerms != null) {
            Term[] sterms = new Term[this.subTerms.length];
            for (int i = 0; i < this.subTerms.length; i++) {
                sterms[i] = this.subTerms[i].deepCopy();
            }
            Term t = new Term(this.symbol, this.role, this.type, sterms);
            t.atom = this.atom;
            t.side = this.side;
            
            if(isData){
            	t.setData(true);
            }
            
            return t;
        } else {
            Term t = new Term(this.symbol, this.role, this.type);
            t.side = this.side;
            
            if(isData){
            	t.setData(true);
            }
            
            return t;
        }
    }

    /**
     * Produces a deep (recursive) copy of this term, with the side member set
     * to the specified value. This is used when Unifying to avoid making
     * changes to the original data structures that represent clauses.
     *
     * @param pside int The side value that should be stored in the
     * copy.
     *
     * @return Term The copy of this term.
     */
    public Term deepCopy(int pside) {
        if (this.subTerms != null) {
            Term[] sterms = new Term[this.subTerms.length];
            for (int i = 0; i < this.subTerms.length; i++) {
                sterms[i] = this.subTerms[i].deepCopy(pside);
            }
            Term t = new Term(this.symbol, this.role, this.type, sterms);
            t.atom = this.atom;
            t.side = pside;
            //if isData
            if(isData){
            	t.setData(true);
            }  
            return t;
        } else {
            Term t = new Term(this.symbol, this.role, this.type);
            t.side = pside;
            //if isData
            if(isData){
            	t.setData(true);
            }
      
            return t;
        }
    }

    /**
     * Sorts a Vector of term objects by the role code.
     *
     * @param toSort Vector A Vector containing only Term objects - to be
     * sorted by the role name code.
     *
     * @return Vector A Vector containing the sorted terms.
     */
    public static Vector sort(Vector toSort) {
        Vector sorted = new Vector();

        for (int i = 0; i < toSort.size(); i++) {
            Term c = (Term) toSort.get(i);

            int j = 0;
            while (j < sorted.size()) {
                Term chk = (Term) sorted.get(j);
                if (c.getRole() >= chk.getRole()) {
                    j++;
                } else {
                    break;
                }
            }
            sorted.add(j, c);
        }
        return sorted;
    }

    /**
     * Sorts an array of jdrew.oo.util.Term values by the role code.
     *
     * @param toSort Term[] An array of Term objects to be sorted by the role
     * name code.
     *
     * @return Vector A Vector containing the sorted terms.
     */
    public static Vector sort(Term[] toSort) {
        Vector ts = new Vector();
        for (int i = 0; i < toSort.length; i++) {
            ts.add(toSort[i]);
        }
        return sort(ts);
    }

    /**
     * Compares one term to another object. If this object is a Term object,
     * it will compare first by the symbol code, then the role name code, the
     * number of paramaters, and finally by the actual paramaters themselves. If
     * the other object is not a Term, then this always returns 1.
     *
     * @param oth java.lang.Object value - the other object to compare this term
     * to.
     *
     * @return int value - the result of the comparison 1 if the other term is
     * "less than", 0 if they are equal, or -1 if the other term is
     * "greater than".
     */
    public int compareTo(Object oth) {
        if (this.getClass() != oth.getClass()) {
            return 1;
        }

        Term o = (Term) oth;
        if (this.symbol < o.symbol) {
          //  System.out.println("exiting here a");
            return 1;
        } else if (this.symbol > o.symbol) {
          //  System.out.println("exiting here b");
            return -1;
        } else {
            if (this.role < o.role) {
                return 1;
            } else if (this.role > o.role) {
                return -1;
            } else {
                if (this.subTerms == null) {
                    return 0;
                }
                if (this.subTerms.length < o.subTerms.length) {
                    return 1;
                } else if (this.subTerms.length > o.subTerms.length) {
                    return -1;
                } else {
                    for (int i = 0; i < this.subTerms.length; i++) {
                       // System.out.println("exiting here c");
                        int cmp = this.subTerms[i].compareTo(o.subTerms[i]);
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                    return 0;
                }
            }
        }
    }
}

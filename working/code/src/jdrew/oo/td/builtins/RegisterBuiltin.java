// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.td.builtins;

import jdrew.oo.td.*;
import jdrew.oo.util.*;
import java.util.*;

/**
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author Marcel A. Ball
 * @version 0.89
 */
public class RegisterBuiltin extends TDBuiltin {
    private BackwardReasoner br;
    private int sym;
    public RegisterBuiltin(BackwardReasoner br, String symbol){
        this.br = br;
        sym = SymbolTable.internSymbol(symbol);
    }

    public int getSymbol(){
        return sym;
    }

    public DefiniteClause buildResult(BackwardReasoner.GoalList gl, int term) {
        Term t = gl.getAtom(term);

        if(t.getSymbol() != sym)
            return null;

        if(t.subTerms.length != 2)
            return null;

        Term p1 = t.subTerms[1].deepCopy();

        if(p1.getSymbol() < 0 || p1.isCTerm())
            return null;

        String cname = p1.getSymbolString();

        try{
            Class c = Class.forName(cname);
            Object b = c.newInstance();
            if(b instanceof jdrew.oo.builtins.Builtin){ // handle generic Builtin subclass
                jdrew.oo.builtins.Builtin bb = (jdrew.oo.builtins.Builtin) b;
                Hashtable bins = br.getBuiltins();
                Integer sym = new Integer(bb.getSymbol());
                if(bins.containsKey(sym)){
                    Object o = bins.get(sym);
                    if(!bb.getClass().equals(o.getClass()))
                        return null; //something else is already registered - fail
                }
                else
                    br.registerBuiltin(bb); //register built-in
            }
            else if(b instanceof jdrew.oo.td.builtins.TDBuiltin){ //handle TDBuiltin subclass
                jdrew.oo.td.builtins.TDBuiltin bb = (jdrew.oo.td.builtins.TDBuiltin) b;
                Hashtable bins = br.getBuiltins();
                Integer sym = new Integer(bb.getSymbol());
                if(bins.containsKey(sym)){
                    Object o = bins.get(sym);
                    if(!bb.getClass().equals(o.getClass()))
                        return null; //something else is already registered - fail
                }
                else
                    br.registerBuiltin(bb); //register built-in
            }
            else
                return null;
        }catch(Exception e){
            return null;
        }

        Term roid = new Term(SymbolTable.internSymbol("$jdrew-rb-" + cname),
                             SymbolTable.IOID, Types.ITHING);
        Vector v = new Vector();
        v.add(roid);
        v.add(p1);

        Term atom = new Term(sym, SymbolTable.INOROLE, Types.ITHING, v);
        Vector v2 = new Vector();
        v2.add(atom);

        return new DefiniteClause(v2, new Vector());
    }

}

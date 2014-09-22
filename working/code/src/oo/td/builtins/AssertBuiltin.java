// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.td.builtins;

import java.util.*;

import jdrew.oo.td.*;
import jdrew.oo.util.*;

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
public class AssertBuiltin extends TDBuiltin {
    /**
     *
     */
    private BackwardReasoner br;
    /**
     *
     */
    private int symbol = SymbolTable.internSymbol("assert");

    /**
     *
     * @param br BackwardReasoner
     */
    public AssertBuiltin(BackwardReasoner br) {
        super();
        this.br = br;
    }

    /**
     *
     * @return int
     */
    public int getSymbol() {
        return symbol;
    }

    /**
     *
     * @param gl GoalList
     * @param term int
     * @return DefiniteClause
     */
    public DefiniteClause buildResult(BackwardReasoner.GoalList gl, int term) {
        Term t = gl.getAtom(term);
        Vector natoms = new Vector();
        for (int i = 0; i < t.subTerms.length; i++) {
            Term t2 = t.subTerms[i].deepCopy();
            t2.setAtom(true);
            natoms.add(t2);
        }
        String[] variableNames = gl.getVariableNames();
        Vector vnames = new Vector();
        for (int i = 0; i < variableNames.length; i++) {
            vnames.add(variableNames[i]);
        }
        DefiniteClause newdc = new DefiniteClause(natoms, vnames);
        Vector v = new Vector();
        v.add(newdc);
        br.loadClauses(v.iterator());

        Term t2 = t.deepCopy();
        v = new Vector();
        v.add(t2);
        return new DefiniteClause(v, vnames);
    }

}

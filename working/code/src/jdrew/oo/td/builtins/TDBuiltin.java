// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.td.builtins;

import jdrew.oo.td.*;
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
import jdrew.oo.util.*;

public class TDBuiltin {
    /**
     *
     */
    jdrew.oo.builtins.Builtin builtin;
    /**
     *
     */
    public TDBuiltin() {
        builtin = null;
    }

    /**
     *
     * @param b Builtin
     */
    public TDBuiltin(jdrew.oo.builtins.Builtin b) {
        builtin = b;
    }

    /**
     *
     * @return int
     */
    public int getSymbol() {
        return builtin.getSymbol();
    }

    /**
     *
     * @param gl GoalList
     * @param term int
     * @return DefiniteClause
     */
    public DefiniteClause buildResult(BackwardReasoner.GoalList gl, int term) {
        System.out.println("In TDBuiltin...");
        return builtin.buildResult(gl.getAtom(term));
    }

}

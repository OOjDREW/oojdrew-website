// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License. 
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

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
import jdrew.oo.td.*;
import java.util.*;
import java.io.*;
import jdrew.oo.util.parsing.*;

import org.apache.log4j.*;

public class Tester {
    public static void main(String args[]) throws Exception{
        jdrew.oo.Config.PRINTANONVARNAMES = true;
        jdrew.oo.Config.PRINTGENOIDS = true;
        jdrew.oo.Config.PRINTGENSYMS = true;
        jdrew.oo.Config.PRINTVARID = true;
        jdrew.oo.Config.PRPRINT = true;

        StringReader sr = new StringReader("a($gensym100000^ $gensym100001, $gensym100002).");
        POSLLexer lex = new POSLLexer(sr);
        jdrew.oo.util.parsing.POSLParser pp = new jdrew.oo.util.parsing.POSLParser(lex);

        Vector v = new Vector();
        pp.rulebase(v);
        Iterator it = v.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }


          System.out.println(java.util.regex.Pattern.matches("^(-)?[a-zA-Z0-9_][a-zA-Z0-9_\\.]*$", ""));

    }


}

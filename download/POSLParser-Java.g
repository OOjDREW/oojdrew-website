header {
    package jdrew.oo.util.parsing;

    import jdrew.oo.util.*;
    import java.util.*;
}

options {
    language  = "Java";
}

class POSLParser extends Parser;
options {
   k = 3;
}

{
    // instance variables
    private Hashtable skolemMap = new Hashtable();
    private static int aid = 1000;
    private Vector variableNames;
    private Hashtable varClasses;


    // Add other methods
    
    private int internVar(String varname){
        int idx;
        idx = variableNames.indexOf(varname);
        if(idx == -1){
            idx = variableNames.size();
            variableNames.add(varname);
        }
        
        return -(idx+1);
    }
    
    private Hashtable buildTypetable() throws ParseException {
        Hashtable ht = new Hashtable();
        
        Enumeration e = varClasses.keys();
        
        while(e.hasMoreElements()) {
            Object key = e.nextElement();
            Vector value = (Vector) varClasses.get(key);
            int [] types = new int[value.size()];
            for(int i = 0; i < types.length; i++){
                types[i] = ((Integer) value.get(i)).intValue();
            }
            
            int type = Types.greatestLowerBound(types);
            ht.put(key, new Integer(type));
        }
        
        return ht;
    }  
    
    private void fixVarTypes(Term ct, Hashtable types) {
        for (int i = 0; i < ct.subTerms.length; i++) {
            if(ct.subTerms[i].isCTerm()) {
                fixVarTypes(ct.subTerms[i], types);
            } else if (ct.subTerms[i].getSymbol() < 0){
                Integer sym = new Integer(ct.subTerms[i].getSymbol());
                Integer type = (Integer)types.get(sym);
                ct.subTerms[i].type = type.intValue();
            }
        }
    }
    
}

rulebase [ Vector clauses ] { DefiniteClause c; }:
    (c = clause[true] { clauses.add(c); })* EOF;

clause [ boolean newVars ] returns [DefiniteClause dc = null] 
    { 
        if (newVars){
            variableNames = new Vector();
            varClasses = new Hashtable();
        }
        Vector atms = new Vector();
        Term head;
    }:
    head = atom[true] { atms.add(head); } 
    (IMP atoms[atms])? 
    PERIOD 
    { 
        Hashtable types = buildTypetable();
        for(int i = 0; i < atms.size(); i++)
            fixVarTypes((Term)atms.get(i), types);
        dc = new DefiniteClause(atms, variableNames); 
    };
    
atoms [ Vector atms ] { Term a; }:
    a = atom[false] { atms.add(a); } (COMMA a = atom[false] { atms.add(a); })*;

nafatom returns [ Term naf = null ]
    {
        Vector params = new Vector();
    }:
    LPAREN
    atoms[params]
    RPAREN
    {
        naf = new Term(SymbolTable.INAF, SymbolTable.INOROLE, 
                       Types.IOBJECT, params);
        naf.atom = true;
    };
    
assertatom returns [ Term ass = null ] { DefiniteClause c; }:
    LPAREN
    c = clause[false]
    RPAREN
    {
        Vector params = new Vector();
        for(int i = 0; i < c.atoms.length; i++)
            params.add(c.atoms[i]);
        ass = new Term(SymbolTable.IASSERT, SymbolTable.INOROLE,
                          Types.IOBJECT, params);
        ass.atom = true;
    };
    

atom [boolean head] returns [Term atm = null] 
    { 
        Vector params = new Vector();
        int r;
        Term o;
    }:
    r = rel 
    {
        if ( r == SymbolTable.IASSERT )
            return assertatom();
        else if ( r == SymbolTable.INAF )
            return nafatom();
    }
    
    LPAREN 
        ( 
            (oid)=> o = oid { params.add(o); } | 
            {
                if (head) {
                    String symname = "$gensym" + SymbolTable.genid++;
                    int symid = SymbolTable.internSymbol(symname);
                    Term t2 = new Term(symid, SymbolTable.IOID, Types.IOBJECT);
                    params.add(t2);
                } else{
                    String varname = "$ANON" + aid++;
                    int symid = internVar(varname);
                    Integer sym = new Integer(symid);
                    
                    Term t2 = new Term(symid, SymbolTable.IOID, Types.IOBJECT);
                    params.add(t2);
                    
                    Vector v = new Vector();
                    v.add(new Integer(Types.IOBJECT));
                    varClasses.put(sym, v);
                }
            }
        ) 
        (ps[params])? 
    RPAREN 
    { 
        atm = new Term(r, SymbolTable.INOROLE, Types.IOBJECT, params);
        atm.atom = true;
    };

ps [ Vector terms ]:
      pos[terms] (prest[terms])? (SEMI slots[terms])? (srest[terms])?
    | slots[terms] 
        ( 
            ((prest[terms] (SEMI slots[terms])?)? (srest[terms])?) 
            | (SEMI pos[terms] (prest[terms])? (SEMI slots[terms])? (srest[terms])?) 
        )
    | prest[terms] (SEMI slots[terms])? (srest[terms])?
    | srest[terms]
    ;

oid returns [Term t = null]:
    t = term HAT 
    { 
        t.role = SymbolTable.IOID;
    };

prest [ Vector terms ] { Term v, p; }:
    PIPE 
    ( v = var 
        { 
            v.role = SymbolTable.IPREST;
            terms.add(v);
        } 
    | p = posplex 
       { 
            p.role = SymbolTable.IPREST;
            terms.add(p);
        }
    );
    
srest [ Vector terms ] { Term v, p; }:
    BANG 
    (v = var 
        { 
            v.role = SymbolTable.IREST;
            terms.add(v);
        } 
    | p = slotplex 
        {
            p.role = SymbolTable.IREST;
            terms.add(p); 
        }
    );

posplex returns [ Term pp = null ] { Vector params = new Vector(); }:
    LBRACK
    (pos[ params ])?
    (prest[ params ])?
    RBRACK
    { 
        pp = new Term(SymbolTable.IPLEX, SymbolTable.INOROLE, 
                    Types.IOBJECT, params); 
    };
    
slotplex returns [ Term sp = null ] { Vector params = new Vector(); }:
    LBRACK
    (slots[ params ])?
    (srest[ params ])?
    RBRACK
    { 
        sp = new Term(SymbolTable.IPLEX, SymbolTable.INOROLE, 
                       Types.IOBJECT, params); 
    };

pos [ Vector terms ] { Term t; }:
    t = term { terms.add(t); } (COMMA t = term { terms.add(t); })*
    ; 

slots [ Vector terms ] { Term s; }:
    s = slot { terms.add(s); } (SEMI s = slot { terms.add(s); })*
    ;

slot returns [ Term s = null ] { int r; }:
    r = role ARROW s = term 
    { 
        s.role = r;
    }
    ;

term returns [ Term t = null ]:
      t = ind 
    | t = var 
    | t = cterm
    | t = skolem
    | t = plex
    ;

cterm returns [ Term ct = null ] 
    { 
        Vector params = new Vector();
        int c, t;
    }:
    c = ctor LBRACK (ps[params])? RBRACK 
    { 
        ct = new Term(c, SymbolTable.INOROLE, Types.IOBJECT, params); 
    }
    (COLON t = type { ct.type = t; })?;

plex returns [ Term p = null ] 
    { 
        Vector params = new Vector();
    }:
    LBRACK (ps[params])? RBRACK 
    { 
        p = new Term(SymbolTable.IPLEX, SymbolTable.INOROLE, 
                     Types.IOBJECT, params); 
    };

ctor returns [ int c = -1] { String sym; }:
    sym = symbol
    {
        c = SymbolTable.internSymbol(sym);
    };
    
rel returns [ int r = -1 ] { String sym; }:
    sym = symbol
    {
        r = SymbolTable.internSymbol(sym);
    };

role returns [ int r = -1 ] { String sym; }:
    sym = symbol
    {
        r = SymbolTable.internRole(sym);
    };

type returns [ int t = -1] { String sym; }:
    sym = symbol
    {
        t = Types.typeID(sym);
    };


ind returns [ Term i = null ] { String sym; int symid, t;}:
    sym = symbol
    {
        symid = SymbolTable.internSymbol(sym);
        i = new Term(symid, SymbolTable.INOROLE, Types.IOBJECT);
    }
    (COLON t = type { i.type = t; })?;
    
skolem returns [ Term sko = null ]
    { 
        String skoname = "";
        String sym = "";
        int t;
    }:
    USCORE (skoname = symbol)?
    { 
        if (skoname.equals("")){
            sym = "$gensym" + SymbolTable.genid++;
        } else {
            if(this.skolemMap.containsKey(skoname)){
                sym = (String)skolemMap.get(skoname);
            } else {
                sym = "$gensym" + (SymbolTable.genid++) + "$" + skoname;
                skolemMap.put(skoname, sym);
            }
        }
        
        sko = new Term(SymbolTable.internSymbol(sym), 
                       SymbolTable.INOROLE, Types.IOBJECT);
    }
    (COLON t = type { sko.type = t; })?;
    
var returns [ Term v = null ] { String vname = ""; int t; }:
    QMARK (vname = symbol)?
    { 
        String sym;
        if(vname.equals("")){
            sym = "$ANON" + aid++;
        }
        else{
            sym = vname;
        }
        v = new Term(internVar(sym), SymbolTable.INOROLE, Types.IOBJECT);
    }
    (COLON t = type { v.type = t; })?
    {
        Integer symI = new Integer(v.symbol);
        Integer typeI = new Integer(v.type);
        
        Vector v2;
        
        if(varClasses.containsKey(symI)) {
            v2 = (Vector)varClasses.get(symI);
        } else {
            v2 = new Vector();
            varClasses.put(symI, v2);
        }
        
        v2.add(typeI);
    }
    ;
    

symbol returns [ String sym = null]:
      s:SYMBOL { sym = s.getText(); }
    | qs:QSYMBOL { sym = qs.getText(); sym = sym.substring(1, sym.length() - 1); }
    ;

uri returns [ String sym = null]:
    u:URI { sym = u.getText(); };

class POSLLexer extends Lexer;
options {
   k=2;
}

PIPE:	'|';
BANG:	'!';
HAT:	'^';
COLON:	':';
SEMI:	';';
LBRACK: '[';
RBRACK: ']';
LPAREN: '(';
RPAREN: ')';
QMARK: '?';
COMMA: ',';
PERIOD: '.';
LBRACE: '{';
RBRACE: '}';
USCORE: '_';

IMP: ":-";
ARROW: "->";

URI:	'<' ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|':'|'/'|'.'|'?'|'&'|'%'|'#'|'-')+ '>';

SYMBOL: ('-')? ('a'..'z'|'A'..'Z'|'0'..'9'|'$') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'.'|'$')*; 
QSYMBOL: '\"' (~('\"'))+ '\"';

COMMENT:
    '%' (~('\n'))* { _ttype = Token.SKIP; };

MLCOMMENT:
    "/%" (~('%'|'\n') | '\n' { newline(); } )* "%/" { _ttype = Token.SKIP; };

WS:
	(' '
	| '\t'
	| '\r' '\n' { newline(); }
	| '\n'      { newline(); }
	)
	{ _ttype = Token.SKIP; }
	;


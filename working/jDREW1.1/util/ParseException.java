
package jDREW.util;



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

   A <code>ParseException</code> may be thrown during parsing the file.

  
   @see Exception
*/
public class ParseException extends Exception{
  public ParseException(String msg){
    super(msg);
  }
  public ParseException(){
    super();
  }
}

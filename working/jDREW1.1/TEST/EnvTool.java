package jDREW.TEST;

import java.util.Vector;

/**
 * @author jiak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EnvTool {
	public static final String localPath="c:\\jkpProgram\\imbdev\\jDREW\\";
	
	
	public static final String DCStream="%sytax test begins\n% fact test\na(b).\na(a(b)).\np(f(h(X)),h(Y),f(X),Y).\np(f(g1),h,h(g2),g1).\n% rule test\na(a(b)):-a(b).\np(f(h(X)),h(Y),f(X),Y) :- p(f(g1),h,h(g2),g1).\n% sytax test ends\n";
	
	public static boolean checkEqualOnStrings(Vector checkee, Vector checker)
	{
		if(checkee==null && checker==null)
			return true;
		if((checkee==null && checker!=null) || (checkee!=null && checker==null))
			return false;
		if(checkee.size()!=checker.size())
			return false;
		for(int i=0; i<checkee.size(); i++)
		{
			String s=(String)checkee.get(i);
			boolean find=false;
			for(int j=0; j<checker.size();j++)
			{
				if(s.compareTo((String)checker.get(j))==0)
				{
					checker.remove(j);
					find=true;
					break;
				}
				
			}
			if(find==false)
				return false;
		}
		if(checker.size()!=0)
			return false;
		else
			return true;
	}
		
	public static boolean checkEqualOnStrings(String[] checkee, String[] checker)
	{
		final int length=checker.length;
		boolean checkFlag[]=new boolean[length];
		
		if(checkee==null && checker==null)
			return true;
		if((checkee==null && checker!=null) || (checkee!=null && checker==null) )
			return false;
		if(checkee.length!=checker.length)
			return false;
		
		for(int i=0; i<length; i++)
			checkFlag[i]=false;
		
		for(int i=0; i<length; i++)
		{
			String s=checkee[i];
			boolean find=false;
			for(int j=0; j<length;j++)
			{
				if(s.compareTo(checker[j])==0 && checkFlag[j]==false)
				{
					checkFlag[j]=true;
					find=true;
					break;
				}
				
			}
			if(find==false)
				return false;
		}
		for(int i=0; i<length; i++)
			if(checkFlag[i]==false)
				return false;
		return true;
	}
	
	public static boolean checkEqualOnStrings(Vector checkee, String[] checker)
	{
		final int length=checker.length;
		boolean checkFlag[]=new boolean[length];
		
		if(checkee==null && checker==null)
			return true;
		if((checkee==null && checker!=null) || (checkee!=null && checker==null) )
			return false;
		if(checkee.size()!=checker.length)
			return false;
		
		for(int i=0; i<length; i++)
			checkFlag[i]=false;
		
		for(int i=0; i<length; i++)
		{
			String s=(String)checkee.get(i);
			boolean find=false;
			for(int j=0; j<length;j++)
			{
				if(s.compareTo(checker[j])==0 && checkFlag[j]==false)
				{
					checkFlag[j]=true;
					find=true;
					break;
				}
				
			}
			if(find==false)
				return false;
		}
		for(int i=0; i<length; i++)
			if(checkFlag[i]==false)
				return false;
		return true;
	}
}

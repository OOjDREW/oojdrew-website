/**
 * Keeps track of game history information and allow the user to log into the game
 * 
 * Created on June 9, 2005
 * 
 * @author Ben Craig 3155984 a45z1@unb.ca
 */

import java.util.*;
import java.io.*;

public class GameHistory{
	
	private String file;

	/**
	 * The game history constructor.  The text file is in the form of
	 * userName,password,wins,lose
	 * @param String fileIn - the file where the game history is stored 
	 */

	public GameHistory(String fileIn){
		file = fileIn;
	}

	/**
	 * This method will create a player by parsing a String that is recieved from
	 * a file, the string is assumed to have the following format
	 * userName,password,wins,lose
	 * @param String in - the String with the player history
	 */

	public Player parsePlayer(String in){
	
	String name;
	String password;
	int wins;
	int lose;
	
	StringTokenizer tokens = new StringTokenizer(in,",");
	
	name = tokens.nextToken();
	password = tokens.nextToken();
	wins = Integer.parseInt(tokens.nextToken());
	lose = Integer.parseInt(tokens.nextToken());

	Player p = new Player(wins,lose,name,password);
	
	return p;	
}
	
	/**
	 * This method will read all the players in from a file and store
	 * them into an array
	 * @return Player[] an array of players that are read in from the file 
	 */

	public Player[] readHistory(){
	
	int counter = 0;
	Vector historyVector = new Vector();
	Player[] history = null;
	Player p = null;
	
	try{
			
	FileReader inFile = new FileReader(file);
	BufferedReader in = new BufferedReader(inFile);
	String read ="";
	
       while((read = in.readLine()) != null)
		{
			p = this.parsePlayer(read);
			historyVector.addElement(p);
			counter++;	
		}
		in.close();
		history = new Player[counter];
		
		for(int i = 0; i < counter; i++){
			history[i] = (Player)historyVector.elementAt(i);
  		}
  	}
  	
   catch (IOException e)
	{
	System.err.println("error with file");
	}        	
	
	return history;	
}
	
	/**
	 * This method will write one player out to a file at a time
	 * the player will be converted to a string and then written out to a file
	 * in the form of username,password,wins,loses 
	 * the file is always being appended to
	 * @param Player p the player being written out to a file
	 * @param boolean newFile true if the file is to be appended, false if a new
	 *	file is to be written.
	 */	

	public void writeHistory(Player p, boolean newFile){
	
	FileOutputStream out;
    PrintStream print;
    
    try
       {
        out = new FileOutputStream(file,newFile);
        print = new PrintStream(out);
		print.println(p.toString());
		print.close();
      }                            
   
    catch (IOException e)
	{
	System.err.println("error with file");
	}
}
	/**
	 * This method will delete a player from the array, it will be needed
	 * when a returning player comes and his stats need to be updated.
	 * @param name the account to be deleted
	 */	

	public void deletePlayer(String name){
		
		int position = 0;
		boolean delete = false;;
		
		Player[] accounts = this.readHistory();
		//checking to see what postion is being deleted
		for(int i = 0; i < accounts.length; i++){
			if((accounts[i].getName()).equals(name)){
				position = i;
				delete = true;
			}		
		}
	
		if(delete == false)
			return;
		
		//deleteing if the player is at the first of the array
		if(position == 0){
			//if only one player left in file
			if(accounts.length ==1){
			try{
			FileOutputStream out = new FileOutputStream(file,false);
			return;
			}
		    catch (IOException e)
			{
			System.err.println("error with file");
			}       	
	     }
			this.writeHistory(accounts[1],false);	
			for(int i = 2; i < accounts.length;i++){
				this.writeHistory(accounts[i],true);
			}	
			return;
		}
		//deleteing if the player is not at the first of the array
		this.writeHistory(accounts[0],false);
		for(int i = 1; i < accounts.length;i++){
			if(i != position){
				this.writeHistory(accounts[i],true);
			}					
		}
		
	}

	/**
	 * This method will return the top 3 players in an array
	 * method isnt fully complete
	 * @return Player[] an array of the top 3 players
	 */	

	public String getTopPlayers(){
	String top3 = "";
	Player[] top = new Player[3];
	Player[] all = this.readHistory();
	Player temp = null;
	
	int n = all.length;
	
	for(int i = 0; i < n-1; i++){
		for(int j = 0; j < n - i -1; j++){
			if(all[j+1].ratio() > all[j].ratio()){
			temp = all[j];
			all[j] = all[j+1];
			all[j+1] = temp;	
			}
		}
	}
	if(all.length == 2){
		top[0] = all[0];
		top[1] = all[1];
	return top[0].toStringA() + "," + top[1].toStringA();
	}
	top[0] = all[0];
	top[1] = all[1];
	top[2] = all[2];
	return top[0].toStringA() + "," + top[1].toStringA() + "," + top[2].toStringA();
	}
	
	/**
	 * This method will check and see if the player has loged in correctly
	 * @param String in the string that the user types to the server to login
	 * @return int  -2 duplicate user name
	 *				-1 unkown user
	 *				0 for an error with input  
	 *				1 for vaild returning user
	 *				2 for valid new user
	 */	
	
	public int checkLogin(String in){
	
	StringTokenizer tokens = new StringTokenizer(in,",");
	String code,userName;
	
	// check to see if it is invalid input
	if(tokens.countTokens() != 2)
		return 0;
		
	code = tokens.nextToken();
	userName = tokens.nextToken();
	Player[] accounts = this.readHistory();
		
	// checking to see if a new user
	if(code.equals("N")){
		for(int i = 0; i < accounts.length; i++){
			if((accounts[i].getName()).equals(userName)){
				//duplicate user name
				return -2;
			}
						
		}		//valid new user
				return 2;		
	}
	//checking to see if a returning user
	if(code.equals("R")){
		for(int i = 0; i < accounts.length; i++){
			if((accounts[i].getName()).equals(userName)){
				//valid returning user
				return 1;
			}
		}		//unknown user
				return -1;		
	}			
	//returning if code was invalid
	return 0;
	}

	
}
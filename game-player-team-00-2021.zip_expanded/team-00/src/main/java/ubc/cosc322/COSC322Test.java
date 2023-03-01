
package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
	
    private String userName = null;
    private String passwd = null;
    
    int[][] board;
    private int whiteQueen = 1;
    private int blackQueen = 2;
    private int arrow = 3; //could be any number other than 1, 2
    private int myQueen = -1;
    private int opponentQueen = -1;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {				 
    	COSC322Test player = new COSC322Test(args[0], args[1]);
    	//HumanPlayer player = new HumanPlayer();
    	
    	if(player.getGameGUI() == null) {
    		player.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player.Go();
                }
            });
    	}
    }
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	this.board = new int[10][10];
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new BaseGameGUI(this);
    }
 


    @Override
    public void onLogin() {
        userName = gameClient.getUserName(); 
        if(gamegui != null) { 
        	gamegui.setRoomInformation(gameClient.getRoomList()); 
        } 
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
	
    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 
    	switch(messageType) {
    	case GameMessage.GAME_STATE_BOARD:
    		ArrayList<Integer> stateArr = (ArrayList<Integer>)(msgDetails.get(AmazonsGameMessage.GAME_STATE));
    		this.getGameGUI().setGameState(stateArr);
    		break; /*  THIS GAME STATE BOARD IS A MESSAGE CONTAINING THE CURRENT STATE*/
    	case GameMessage.GAME_ACTION_MOVE:
    		///CHECK IF MOVE IS LEGAL HERE!!!!!!!!!!
    		//if it is legal, update our local board and the gui, then let the AI run
    		//if it is ilegal, report the illegal move!
    		this.getGameGUI().updateGameState(msgDetails);
    	case GameMessage.GAME_ACTION_START:
    		String playingWhiteQueens = (String)msgDetails.get(AmazonsGameMessage.PLAYER_WHITE);
    		String playingBlackQueens = (String)msgDetails.get(AmazonsGameMessage.PLAYER_BLACK);
    		assert(!playingWhiteQueens.equals(playingBlackQueens));
    	default:
    		assert(false);
    		break;
    	}
    	return true;   	
    }
    //WRITE A METHOD THAT SETS MYQUEEN TO 1 IF THIS.USERNAME = PLAYINGWHITEQUEENS, and THE OPPOSITE IF THIS.USERNAME = PLAYINGBLACKQUEENS
    
    ///WRITE A METHOD INITIALIZE GAME BOARD THAT TAKES THE ARRAYLIST FROM THE SERVER (121 indexes) AND CONVERTS IT TO THE BOARD[][] ARRAY
    
    //WRITE A METHOD THAT DISPLAYS THE CURRENT STATE OF THE BOARD AS TEXT, FOR TESTING PURPOSES, ALSO WRITE THESE MOVES TO A TEXT
    //FILE SO THAT YOU HAVE PROOF THAT THE OPPONENT MADE AN ILLEGAL MOVE AFTER THE GAME
    
    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return  this.gamegui;
	}

	@Override
	public void connect() {
    	gameClient = new GameClient(userName, passwd, this);			
	}

 
}//end of class


package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
    	//COSC322Test player = new COSC322Test(args[0], args[1]);
    	HumanPlayer player = new HumanPlayer();
    	
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
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		// TODO Auto-generated method stub
		return  this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	gameClient = new GameClient(userName, passwd, this);			
	}
	
	// returns a double representing the advantage of one player
	// positive values are white advantage
	// negative values are black advantage
	// a type can be passed in to determine which heuristics are factored in
	public double getHeuristicEval(int board[][], int type) {
		int minDistanceEval = getMinDistanceEval(board);
		
		double result = (double)minDistanceEval;
		return result;
	}
	
	// returns an integer representing the number of squares controlled by each player
	// return value is in the form (squares controlled by white) - (squares controlled by black)
	public int getMinDistanceEval(int board[][]) {
		int whiteMinDistance[][] = new int[10][10];
		int blackMinDistance[][] = new int[10][10];
		
		// must initialize above arrays to infinity

		
		Queue<Tuple<int>> whiteQueue = new Queue<Tuple<int>>();
		Queue<Tuple<int>> blackQueue = new Queue<Tuple<int>>();
		
		// get initial positions of all queens and add them  to respective queues
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(board[i][j] == whiteQueen) {
					whiteQueue.add(new Tuple<int>(i, j));
					whiteMinDistance[i][j] = 0;
				}
				else if(board[i][j] == blackQueen) {
					blackQueue.add(new Tuple<int>(i, j));
					blackMinDistance[i][j] = 0;
				}
			}
		}
		
		// for each item in the queue we want to loop in all directions
		while(whiteQueue) {
			Tuple<int> whitePosition = whiteQueue.pop();
			int posX = whitePosition[0];
			int posY = whitePosition[1];
			int minDistance = whiteMinDistance[posX][posY] + 1;
			
			boolean leftValid = true; 
			boolean upLeftValid = true;
			boolean upValid = true;
			boolean upRightValid = true;
			boolean rightValid = true;
			boolean downRightValid = true;
			boolean downValid = true;
			
			for(int i = 1; i < 10; i++) {
				// left
				if(leftValid && isSpotValid(board, posX-i, posY)) {
					if(whiteMinDistance[posX-i][posY] < minDistance) {
						whiteMinDistance[posX-i][posY] = minDistance;
						whiteQueue.add(new Tuple<int>(posX-i, posY));
					}
				} else leftValid = false;
				
				// up left
				if(upLeftValid && isSpotValid(board, posX-i, posY-i)) {
					if(whiteMinDistance[posX-i][posY-i] < minDistance) {
						whiteMinDistance[posX-i][posY-i] = minDistance;
						whiteQueue.add(new Tuple<int>(posX-i, posY-i));
					}
				} else upLeftValid = false;
				
				// up
				if(upValid && isSpotValid(board, posX, posY-i)) {
					if(whiteMinDistance[posX][posY-i] < minDistance) {
						whiteMinDistance[posX][posY-i] = minDistance;
						whiteQueue.add(new Tuple<int>(posX, posY-i));
					}
				} else upValid = false;
				
				// up right
				if(upRightValid && isSpotValid(board, posX+i, posY-i)) {
					if(whiteMinDistance[posX+i][posY-i] < minDistance) {
						whiteMinDistance[posX+i][posY-i] = minDistance;
						whiteQueue.add(new Tuple<int>(posX+i, posY-i));
					}
				} else upRightValid = false;
				
				// right
				if(rightValid && isSpotValid(board, posX+i, posY)) {
					if(whiteMinDistance[posX+i][posY] < minDistance) {
						whiteMinDistance[posX+i][posY] = minDistance;
						whiteQueue.add(new Tuple<int>(posX+i, posY));
					}
				} else rightValid = false;
				
				// down right
				if(downRightValid && isSpotValid(board, posX+i, posY+i)) {
					if(whiteMinDistance[posX+i][posY+i] < minDistance) {
						whiteMinDistance[posX+i][posY+i] = minDistance;
						whiteQueue.add(new Tuple<int>(posX+i, posY+i));
					}
				} else downRightValid = false;
				
				// down
				if(downValid && isSpotValid(board, posX, posY+1)) {
					if(whiteMinDistance[posX][posY+1] < minDistance) {
						whiteMinDistance[posX][posY+1] = minDistance;
						whiteQueue.add(new Tuple<int>(posX, posY+1));
					}
				} else downValid = false;
				
				// down left
				if(downLeftValid && isSpotValid(board, posX-i, posY+i)) {
					if(whiteMinDistance[posX-i][posY+i] < minDistance) {
						whiteMinDistance[posX-i][posY+i] = minDistance;
						whiteQueue.add(new Tuple<int>(posX-i, posY+i));
					}
				} else downLeftValid = false;
			}
		}
		
		while(blackQueue) {
			Tuple<int> blackPosition = blackQueue.pop();
			int posX = blackPosition[0];
			int posY = blackPosition[1];
			int minDistance = blackMinDistance[posX][posY] + 1;
			
			boolean leftValid = true; 
			boolean upLeftValid = true;
			boolean upValid = true;
			boolean upRightValid = true;
			boolean rightValid = true;
			boolean downRightValid = true;
			boolean downValid = true;
			
			for(int i = 1; i < 10; i++) {
				// left
				if(leftValid && isSpotValid(board, posX-i, posY)) {
					if(blackMinDistance[posX-i][posY] < minDistance) {
						blackMinDistance[posX-i][posY] = minDistance;
						blackQueue.add(new Tuple<int>(posX-i, posY));
					}
				} else leftValid = false;
				
				// up left
				if(upLeftValid && isSpotValid(board, posX-i, posY-i)) {
					if(blackMinDistance[posX-i][posY-i] < minDistance) {
						blackMinDistance[posX-i][posY-i] = minDistance;
						blackQueue.add(new Tuple<int>(posX-i, posY-i));
					}
				} else upLeftValid = false;
				
				// up
				if(upValid && isSpotValid(board, posX, posY-i)) {
					if(blackMinDistance[posX][posY-i] < minDistance) {
						blackMinDistance[posX][posY-i] = minDistance;
						blackQueue.add(new Tuple<int>(posX, posY-i));
					}
				} else upValid = false;
				
				// up right
				if(upRightValid && isSpotValid(board, posX+i, posY-i)) {
					if(blackMinDistance[posX+i][posY-i] < minDistance) {
						blackMinDistance[posX+i][posY-i] = minDistance;
						blackQueue.add(new Tuple<int>(posX+i, posY-i));
					}
				} else upRightValid = false;
				
				// right
				if(rightValid && isSpotValid(board, posX+i, posY)) {
					if(blackMinDistance[posX+i][posY] < minDistance) {
						blackMinDistance[posX+i][posY] = minDistance;
						blackQueue.add(new Tuple<int>(posX+i, posY));
					}
				} else rightValid = false;
				
				// down right
				if(downRightValid && isSpotValid(board, posX+i, posY+i)) {
					if(blackMinDistance[posX+i][posY+i] < minDistance) {
						blackMinDistance[posX+i][posY+i] = minDistance;
						blackQueue.add(new Tuple<int>(posX+i, posY+i));
					}
				} else downRightValid = false;
				
				// down
				if(downValid && isSpotValid(board, posX, posY+1)) {
					if(blackMinDistance[posX][posY+1] < minDistance) {
						blackMinDistance[posX][posY+1] = minDistance;
						blackQueue.add(new Tuple<int>(posX, posY+1));
					}
				} else downValid = false;
				
				// down left
				if(downLeftValid && isSpotValid(board, posX-i, posY+i)) {
					if(blackMinDistance[posX-i][posY+i] < minDistance) {
						blackMinDistance[posX-i][posY+i] = minDistance;
						blackQueue.add(new Tuple<int>(posX-i, posY+i));
					}
				} else downLeftValid = false;
			}
		}
		
		
		// add up a score from computed minDistance values
		int score = 0;
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(whiteMinDistance[i][j] < blackMinDistance[i][j]) {
					score++;
				}
				else if(whiteMinDistance[i][j] > blackMinDistance[i][j]) {
					score--;
				}
			}
		}
		return score;
	}
	
	public boolean isSpotValid(int board[][], int x, int y) {
		return x >= 0 && x <= 9 && y >= 0 && y <= 9 && board[x][y] == 0;
	}

 
}//end of class

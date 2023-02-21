
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
 * 
 * @author Yong Gao (yong.gao@ubc.ca) Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer {

	private GameClient gameClient = null;
	private BaseGameGUI gamegui = null;

	private String userName = null;
	private String passwd = null;

	int[][] board;
	private int whiteQueen = 1;
	private int blackQueen = 2;
	private int arrow = 3; // could be any number other than 1, 2
	private int myQueen = -1;
	private int opponentQueen = -1;

	/**
	 * The main method
	 * 
	 * @param args for name and passwd (current, any string would work)
	 */
	public static void main(String[] args) {
		COSC322Test player = new COSC322Test("user", "pass");
		
		//HumanPlayer player = new HumanPlayer();
	
		if (player.getGameGUI() == null) {
			player.Go();
		} else {
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
	 * 
	 * @param userName
	 * @param passwd
	 */
	public COSC322Test(String userName, String passwd) {
		this.userName = userName;
		this.passwd = passwd;
		//this.board = new int[10][10];
	
		// comment
		// To make a GUI-based player, create an instance of BaseGameGUI
		// and implement the method getGameGUI() accordingly
		this.gamegui = new BaseGameGUI(this);
	}

	@Override
	public void onLogin() {
		userName = gameClient.getUserName();
		if (gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
		// This method will be called by the GameClient when it receives a game-related
		// message
		// from the server.

		// For a detailed description of the message types and format,
		// see the method GamePlayer.handleGameMessage() in the game-client-api
		// document.
		switch (messageType) {
		case GameMessage.GAME_STATE_BOARD:
			ArrayList<Integer> stateArr = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.GAME_STATE));
			this.getGameGUI().setGameState(stateArr);
			
			this.board = new int[10][10];
			
			//hard coded but ideally set using stateArr
			this.board[0][3] = this.whiteQueen;
			this.board[0][6] = this.whiteQueen;
			
			this.board[3][0] = this.whiteQueen;
			this.board[3][9] = this.whiteQueen;
			
			this.board[6][0] = this.blackQueen;
			this.board[6][9] = this.blackQueen;
			
			this.board[9][3] = this.blackQueen;
			this.board[9][6] = this.blackQueen;
			
			break; /* THIS GAME STATE BOARD IS A MESSAGE CONTAINING THE CURRENT STATE */
		case GameMessage.GAME_ACTION_MOVE:
			/// CHECK IF MOVE IS LEGAL HERE!!!!!!!!!!						
			ArrayList<Integer> queenPosCurr = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR));
			ArrayList<Integer> queenPosNext = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT));
			ArrayList<Integer> arrowPos = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.ARROW_POS));
			
			System.out.println("queenPosCurr " + queenPosCurr);
			System.out.println("queenPosNext " + queenPosNext);
			System.out.println("arrowPos " + arrowPos);
			
			int queenPosCurrRow = queenPosCurr.get(0);
			int queenPosCurrColumn = queenPosCurr.get(1);
			
			int queenPosNextRow = queenPosNext.get(0);
			int queenPosNextColumn = queenPosNext.get(1);
			
			int arrowPosRow = arrowPos.get(0);
			int arrowPosColumn = arrowPos.get(1);
			
			int queenPosCurrValue = this.board[queenPosCurrRow-1][queenPosCurrColumn-1];
			int queenPosNextValue = this.board[queenPosNextRow-1][queenPosNextColumn-1];
			
			//int arrowPosValue = this.board[arrowPosRow-1][arrowPosColumn-1];
					
			// if it is legal, update our local board and the gui, then let the AI run
			// if it is ilegal, report the illegal move!
			
			//check that the queen move is legal
			boolean legal = CheckMoveHelper(queenPosCurrRow, queenPosCurrColumn, queenPosNextRow, queenPosNextColumn);
			
			System.out.println("Queen Move legal " + legal);
			
			//move the queen
			this.board[queenPosCurrRow - 1][queenPosCurrColumn - 1] = 0;
			this.board[queenPosNextRow - 1][queenPosNextColumn -1] = this.opponentQueen;
			
			//check the arrow placement is legal
			boolean arrowLegal = CheckMoveHelper(queenPosNextRow, queenPosNextColumn, arrowPosRow, arrowPosColumn);

			System.out.println("Arrow move legal " + arrowLegal);
			
			legal = legal&&arrowLegal;
			
			if(legal) {
				this.board[arrowPosRow -1][arrowPosColumn-1] = this.arrow;
				this.getGameGUI().updateGameState(msgDetails);
			}else {
				
				System.out.println("Illegal move!");
				
				//revert the queen position if the move is not legal.
				this.board[queenPosCurrRow-1][queenPosCurrColumn-1] = queenPosCurrValue;
				this.board[queenPosNextRow-1][queenPosNextColumn-1] = queenPosNextValue;
				assert(false);
			}
			
			break;
		case GameMessage.GAME_ACTION_START:
			String playingWhiteQueens = (String) msgDetails.get(AmazonsGameMessage.PLAYER_WHITE);
			String playingBlackQueens = (String) msgDetails.get(AmazonsGameMessage.PLAYER_BLACK);
			assert (!playingWhiteQueens.equals(playingBlackQueens));
		default:
			assert (false);
			break;
		}
		return true;
	}
			
	public boolean CheckMoveHelper(int startRow, int startColumn, int targetRow, int targetColumn) {
		
		//must move
		if(startRow == targetRow && startColumn == targetColumn)
			return false;
		
		int rowDirection = targetRow - startRow > 0 ? 1: -1;
		int columnDirection = targetColumn - startColumn > 0 ? 1: -1;
		
		if(startRow == targetRow) {
			//move horizontally
			System.out.println("Horizontal direction: " + columnDirection);
			return CheckMove(startRow, startColumn, targetRow, targetColumn, 0, columnDirection);
		}else if(startColumn == targetColumn) {
			//move vertical
			System.out.println("Vertical direction: " + rowDirection);
			return CheckMove(startRow, startColumn, targetRow, targetColumn, rowDirection, 0);
		}else {
			//check diagonal
			System.out.println("Diagonal row direction: " + rowDirection + " column direction: " + columnDirection);
			return CheckMove(startRow, startColumn, targetRow, targetColumn, rowDirection, columnDirection);
		}
	}
	
	
	public boolean CheckMove(int startRow, int startColumn, int targetRow, int targetColumn, int rowDirection, int columnDirection) {
		
		//start at 1 position away because the player has to move.
		int posRow = startRow + rowDirection;
		int posColumn = startColumn + columnDirection;
		
		while(OnGameBoard(posRow, posColumn)) {
			
			int val = this.board[posRow-1][posColumn-1];
			
			if(val != 0) {
				System.out.println("Move blocked.");
				return false;
			}
			else if(posRow == targetRow && posColumn == targetColumn) {
				return true;
			}
				
			posRow += rowDirection;	
			posColumn += columnDirection;
		}
		
		System.out.println("Target placement is not on a valid path from the current position");
		
		return false;
	}
	
		
	public boolean OnGameBoard (int row, int column) {
		return ValidRowOrColumn(row) && ValidRowOrColumn(column);
	}
	
	public boolean ValidRowOrColumn(int rowOrColumnPos) { 
		return rowOrColumnPos > 0 && rowOrColumnPos <=10;
	}

	// WRITE A METHOD THAT SETS MYQUEEN TO 1 IF THIS.USERNAME = PLAYINGWHITEQUEENS,
	// and THE OPPOSITE IF THIS.USERNAME = PLAYINGBLACKQUEENS
	public void SetMyQueen() {
    	if(this.userName().equals("PLAYINGWHITEQUEENS")) {
    		 this.myQueen = 1;
    		 this.opponentQueen = 2;
    	}else if (this.userName().equals("PLAYINGBLACKQUEENS") ) {
    		  this.myQueen = 2;
    		  this.opponentQueen = 1;
    	}
    }

	/// WRITE A METHOD INITIALIZE GAME BOARD THAT TAKES THE ARRAYLIST FROM THE
	/// SERVER (121 indexes) AND CONVERTS IT TO THE BOARD[][] ARRAY

	// WRITE A METHOD THAT DISPLAYS THE CURRENT STATE OF THE BOARD AS TEXT, FOR
	// TESTING PURPOSES, ALSO WRITE THESE MOVES TO A TEXT
	// FILE SO THAT YOU HAVE PROOF THAT THE OPPONENT MADE AN ILLEGAL MOVE AFTER THE
	// GAME

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
		return this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		gameClient = new GameClient(userName, passwd, this);
	}

}// end of class

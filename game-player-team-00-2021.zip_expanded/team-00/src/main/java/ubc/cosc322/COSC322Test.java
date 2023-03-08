
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
	
	private boolean movedFirst = true;

	/**
	 * The main method
	 * 
	 * @param args for name and passwd (current, any string would work)
	 */
	public static void main(String[] args) {
		COSC322Test player = new COSC322Test("user2", "pass");
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
		
		InitalizeBoard();

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
			
			System.out.println("Board Start");
			
			break; /* THIS GAME STATE BOARD IS A MESSAGE CONTAINING THE CURRENT STATE */
		case GameMessage.GAME_ACTION_MOVE:
			
			System.out.println("Move");
			
			ArrayList<Integer> queenPosCurr = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR));
			ArrayList<Integer> queenPosNext = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT));
			ArrayList<Integer> arrowPos = (ArrayList<Integer>) (msgDetails.get(AmazonsGameMessage.ARROW_POS));
						
			this.SetBoardState(queenPosCurr, queenPosNext, arrowPos, this.opponentQueen);
			this.getGameGUI().updateGameState(msgDetails);
			
			MakeMove();

		case GameMessage.GAME_ACTION_START:
			
			String playingWhiteQueens = (String) msgDetails.get(AmazonsGameMessage.PLAYER_WHITE);
			String playingBlackQueens = (String) msgDetails.get(AmazonsGameMessage.PLAYER_BLACK);
			assert (!playingWhiteQueens.equals(playingBlackQueens));	
			SetMyQueen(playingWhiteQueens, playingBlackQueens);
			
			if(this.myQueen == 1 && movedFirst) {
				movedFirst = false;
				MakeMove();
			}
			
		default:
			assert (false);
			break;
		}
		return true;
	}
	// WRITE A METHOD THAT SETS MYQUEEN TO 1 IF THIS.USERNAME = PLAYINGWHITEQUEENS,
	// and THE OPPOSITE IF THIS.USERNAME = PLAYINGBLACKQUEENS
	
	public void SetMyQueen(String playingWhiteQueens, String playingBlackQueens) {
    	if(this.userName().equals(playingWhiteQueens)) {
    		 this.myQueen = 1;
    		 this.opponentQueen = 2;
    	}else if (this.userName().equals(playingBlackQueens) ) {
    		  this.myQueen = 2;
    		  this.opponentQueen = 1;
    	}
    	
    	System.out.println("SetMyQueen " + this.myQueen);
	}

	/// WRITE A METHOD INITIALIZE GAME BOARD THAT TAKES THE ARRAYLIST FROM THE
	/// SERVER (121 indexes) AND CONVERTS IT TO THE BOARD[][] ARRAY

	// WRITE A METHOD THAT DISPLAYS THE CURRENT STATE OF THE BOARD AS TEXT, FOR
	// TESTING PURPOSES, ALSO WRITE THESE MOVES TO A TEXT
	// FILE SO THAT YOU HAVE PROOF THAT THE OPPONENT MADE AN ILLEGAL MOVE AFTER THE
	// GAME
	
	public void MakeMove() {
		int[][][] state = new int[2][10][10];

		state[0] = this.board;
		state[1] = AmazonsUtility.getMobilityMap(this.board);
		
		MonteCarlo ai = new MonteCarlo(new TreeNode(state, this.myQueen), 100, 3);
		AmazonsAction a = ai.MCTS();
		
		if(a!=null) {
		
		ArrayList<Integer> aiQueenPosCurr = new ArrayList<Integer>();
		aiQueenPosCurr.add(a.queenSrcY + 1);
		aiQueenPosCurr.add(a.queenSrcX + 1);
		
		ArrayList<Integer> aiQueenPosNext = new ArrayList<Integer>();
		aiQueenPosNext.add(a.queenDestY + 1);
		aiQueenPosNext.add(a.queenDestX + 1);
		
		ArrayList<Integer> aiArrowPos = new ArrayList<Integer>();
		aiArrowPos.add(a.arrowDestY + 1);
		aiArrowPos.add(a.arrowDestX + 1);
		
		this.SetBoardState(aiQueenPosCurr, aiQueenPosNext, aiArrowPos, this.myQueen);
		
		this.getGameGUI().updateGameState(aiQueenPosCurr, aiQueenPosNext, aiArrowPos);
		this.getGameClient().sendMoveMessage(aiQueenPosCurr, aiQueenPosNext, aiArrowPos);
		}
	}
	
	public void SetBoardState(ArrayList<Integer> queenPosCurrent, ArrayList<Integer> queenPosNew, ArrayList<Integer> arrowPos, int queen) {
		// update the board state.
		this.board[queenPosCurrent.get(0) - 1][queenPosCurrent.get(1) - 1] = 0;
		this.board[queenPosNew.get(0) - 1][queenPosNew.get(1) - 1] = queen;
		this.board[arrowPos.get(0) - 1][arrowPos.get(1) - 1] = this.arrow;
	}
	
	public void InitalizeBoard() {
		
		movedFirst = true;
		
		this.board = new int[10][10];

		// hard coded but ideally set using stateArr
		this.board[0][3] = this.whiteQueen;
		this.board[0][6] = this.whiteQueen;

		this.board[3][0] = this.whiteQueen;
		this.board[3][9] = this.whiteQueen;

		this.board[6][0] = this.blackQueen;
		this.board[6][9] = this.blackQueen;

		this.board[9][3] = this.blackQueen;
		this.board[9][6] = this.blackQueen;
	}

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
		return this.gamegui;
	}

	@Override
	public void connect() {
		gameClient = new GameClient(userName, passwd, this);
	}

}// end of class

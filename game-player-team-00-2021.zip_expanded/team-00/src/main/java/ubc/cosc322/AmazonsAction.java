package ubc.cosc322;

public class AmazonsAction {
	private int queenSrcX;
	private int queenSrcY;
	private int queenDestX;
	private int queenDestY;
	private int arrowDestX;
	private int arrowDestY;
			
	public AmazonsAction(int queenSrcX, int queenSrcY, int queenDestX, int queenDestY, int arrowDestX, int arrowDestY, int[][] board) {
		this.queenSrcX = queenSrcX;
		this.queenSrcY = queenSrcY;
		this.queenDestX = queenDestX;
		this.queenDestY = queenDestY;
		this.arrowDestX = arrowDestX;
		this.arrowDestY = arrowDestY;
	}
	
	// takes in an action and board
	// returns a new board state
	public static int[][] applyAction(AmazonsAction action, int[][] board) {
		int[][] updatedBoard = new int[10][10];
		
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				updatedBoard[i][j] = board[i][j];
			}
		}
		
		updatedBoard[action.queenDestY][action.queenDestX] = board[action.queenSrcY][action.queenSrcX];
		updatedBoard[action.queenSrcY][action.queenSrcX] = 0;
		updatedBoard[action.arrowDestY][action.arrowDestX] = 3;
		
		return updatedBoard;
	}
	
	// takes in a queen move and board state (no arrow move needed)
	// returns a new board state
	public static int[][] applyQueenMove(int queenSrcX, int queenSrcY, int queenDestX, int queenDestY, int[][] board) {
		int[][] updatedBoard = new int[10][10];
		
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				updatedBoard[i][j] = board[i][j];
			}
		}
		
		updatedBoard[queenDestY][queenDestX] = board[queenSrcY][queenSrcX];
		updatedBoard[queenSrcY][queenSrcX] = 0;
		
		return updatedBoard;
	}
	
	public void printMove() {
		System.out.println(this.queenSrcX + ", " + this.queenSrcY + ", " + this.queenDestX + ", " + this.queenDestY + ", " + this.arrowDestX + ", " + this.arrowDestY);
	}
}

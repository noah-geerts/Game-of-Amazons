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
	// returns a new board state and new mobility map
	public static int[][][] applyAction(AmazonsAction action, int[][] board, int[][] mobilityMap) {
		int[][] updatedBoard = new int[10][10];
		int[][] updatedMobilityMap = new int[10][10];
		
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				updatedBoard[i][j] = board[i][j];
				updatedMobilityMap[i][j] = mobilityMap[i][j];
			}
		}
		
		updatedBoard[action.queenDestY][action.queenDestX] = board[action.queenSrcY][action.queenSrcX];
		updatedBoard[action.queenSrcY][action.queenSrcX] = 0;
		updatedBoard[action.arrowDestY][action.arrowDestX] = 3;
		
		// update mobility where queen left
		if(isSpotValid(action.queenSrcY-1, action.queenSrcX)) {
			updatedMobilityMap[action.queenSrcY-1][action.queenSrcX]++;
		}
		if(isSpotValid(action.queenSrcY-1, action.queenSrcX-1)) {
			updatedMobilityMap[action.queenSrcY-1][action.queenSrcX-1]++;
		}
		if(isSpotValid(action.queenSrcY-1, action.queenSrcX+1)) {
			updatedMobilityMap[action.queenSrcY-1][action.queenSrcX+1]++;
		}
		if(isSpotValid(action.queenSrcY, action.queenSrcX-1)) {
			updatedMobilityMap[action.queenSrcY][action.queenSrcX-1]++;
		}
		if(isSpotValid(action.queenSrcY, action.queenSrcX+1)) {
			updatedMobilityMap[action.queenSrcY][action.queenSrcX+1]++;
		}
		if(isSpotValid(action.queenSrcY+1, action.queenSrcX)) {
			updatedMobilityMap[action.queenSrcY+1][action.queenSrcX]++;
		}
		if(isSpotValid(action.queenSrcY+1, action.queenSrcX-1)) {
			updatedMobilityMap[action.queenSrcY+1][action.queenSrcX-1]++;
		}
		if(isSpotValid(action.queenSrcY+1, action.queenSrcX+1)) {
			updatedMobilityMap[action.queenSrcY+1][action.queenSrcX+1]++;
		}
		
		// update mobility where queen went
		if(isSpotValid(action.queenDestY-1, action.queenDestX)) {
			updatedMobilityMap[action.queenDestY-1][action.queenDestX]--;
		}
		if(isSpotValid(action.queenDestY-1, action.queenDestX-1)) {
			updatedMobilityMap[action.queenDestY-1][action.queenDestX-1]--;
		}
		if(isSpotValid(action.queenDestY-1, action.queenDestX+1)) {
			updatedMobilityMap[action.queenDestY-1][action.queenDestX+1]--;
		}
		if(isSpotValid(action.queenDestY, action.queenDestX-1)) {
			updatedMobilityMap[action.queenDestY][action.queenDestX-1]--;
		}
		if(isSpotValid(action.queenDestY, action.queenDestX+1)) {
			updatedMobilityMap[action.queenDestY][action.queenDestX+1]--;
		}
		if(isSpotValid(action.queenDestY+1, action.queenDestX)) {
			updatedMobilityMap[action.queenDestY+1][action.queenDestX]--;
		}
		if(isSpotValid(action.queenDestY+1, action.queenDestX-1)) {
			updatedMobilityMap[action.queenDestY+1][action.queenDestX-1]--;
		}
		if(isSpotValid(action.queenDestY+1, action.queenDestX+1)) {
			updatedMobilityMap[action.queenDestY+1][action.queenDestX+1]--;
		}
		
		// update mobility where arrow went
		if(isSpotValid(action.arrowDestY-1, action.arrowDestX)) {
			updatedMobilityMap[action.arrowDestY-1][action.arrowDestX]--;
		}
		if(isSpotValid(action.arrowDestY-1, action.arrowDestX-1)) {
			updatedMobilityMap[action.arrowDestY-1][action.arrowDestX-1]--;
		}
		if(isSpotValid(action.arrowDestY-1, action.arrowDestX+1)) {
			updatedMobilityMap[action.arrowDestY-1][action.arrowDestX+1]--;
		}
		if(isSpotValid(action.arrowDestY, action.arrowDestX-1)) {
			updatedMobilityMap[action.arrowDestY][action.arrowDestX-1]--;
		}
		if(isSpotValid(action.arrowDestY, action.arrowDestX+1)) {
			updatedMobilityMap[action.arrowDestY][action.arrowDestX+1]--;
		}
		if(isSpotValid(action.arrowDestY+1, action.arrowDestX)) {
			updatedMobilityMap[action.arrowDestY+1][action.arrowDestX]--;
		}
		if(isSpotValid(action.arrowDestY+1, action.arrowDestX-1)) {
			updatedMobilityMap[action.arrowDestY+1][action.arrowDestX-1]--;
		}
		if(isSpotValid(action.arrowDestY+1, action.arrowDestX+1)) {
			updatedMobilityMap[action.arrowDestY+1][action.arrowDestX+1]--;
		}
		
		return new int[][][]{updatedBoard, updatedMobilityMap};
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
	
	private static boolean isSpotValid(int y, int x) {
		return x >= 0 && x <= 9 && y >= 0;
	}
	
	public void printMove() {
		System.out.println(this.queenSrcX + ", " + this.queenSrcY + ", " + this.queenDestX + ", " + this.queenDestY + ", " + this.arrowDestX + ", " + this.arrowDestY);
	}
}

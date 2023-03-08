package ubc.cosc322;

import java.util.Scanner;

public class TestMain {
	
	static int currentPlayer = 1;
	static int whiteQueen = 1;
	static int blackQueen = 2;
	static int arrow = 3;
	static int[][][] board;
	
	public static void main(String[] args) {
		initializeBoard();
		Scanner in = new Scanner(System.in);
		printBoard();
		System.out.println();
		
		while(true) {
			int terminality = isTerminal(board, currentPlayer);
			if(terminality != 10) {
				if(terminality == 1) {
					System.out.println("player " + currentPlayer + " won");
					break;
				} else if (terminality == 0) {
					System.out.println("draw");
					break;
				} else {
					System.out.println("player " + swapColor(currentPlayer) + " won");
					break;
				}
			}
			
		if(currentPlayer == 1) {
			System.out.println("Player 1's turn:");
			MonteCarlo ai = new MonteCarlo(new TreeNode(board, 2), 100, 3);
			AmazonsAction a = ai.MCTS();
			board = AmazonsAction.applyAction(a, board);
			printBoard();
		} else {
			System.out.println("Player 2's turn:");
			MonteCarlo ai = new MonteCarlo(new TreeNode(board, 2), 100, 3);
			AmazonsAction a = ai.MCTS();
			board = AmazonsAction.applyAction(a, board);
			printBoard();
			/*human player stuff  System.out.println("Player 2's turn. Enter a startX, startY, endX, endY, arrowX, arrowY:");
			int startX = in.nextInt(); int startY = in.nextInt(); int endX = in.nextInt(); int endY = in.nextInt(); int arrowX = in.nextInt(); int arrowY = in.nextInt();
			AmazonsAction a = new AmazonsAction(startX, startY, endX, endY, arrowX, arrowY);
			board = AmazonsAction.applyAction(a, board);
			printBoard();*/
		}
		System.out.println();
		currentPlayer = swapColor(currentPlayer);
		
		}
		

	}
	
	static void initializeBoard() {
    	board = new int[2][10][10];
		board[0] = new int[10][10];
        
        //hard coded but ideally set using stateArr
        board[0][0][3] = whiteQueen;
        board[0][0][6] = whiteQueen;
        
        board[0][3][0] = whiteQueen;
        board[0][3][9] = whiteQueen;
        
        board[0][6][0] = blackQueen;
        board[0][6][9] = blackQueen;
        
        board[0][9][3] = blackQueen;
        board[0][9][6] = blackQueen;
    }
    //WRITE A METHOD THAT DISPLAYS THE CURRENT STATE OF THE BOARD AS TEXT, FOR TESTING PURPOSES, ALSO WRITE THESE MOVES TO A TEXT
    //FILE SO THAT YOU HAVE PROOF THAT THE OPPONENT MADE AN ILLEGAL MOVE AFTER THE GAME
    
   static void printBoard() {
	   System.out.print("    ");
	   for(int i = 0; i < 10; i++) {
		   System.out.print(i + " ");
	   }
	   System.out.print("\n    ");
	   for(int i = 0; i < 10; i++) {
		   System.out.print("--");
	   }
	   System.out.println();
	   for(int i = 0; i < 10; i++) {
		   	System.out.print(i + " | ");
    		for(int j = 0; j < 10; j++) {
    			System.out.print(board[0][i][j] + " ");
    		}
    		System.out.println();
    	}
    }
  
   static int swapColor(int i) {
		if(i==1) {return 2;}
		return 1;
	}
   
   public static int isTerminal(int[][][] state, int color) {
		if(AmazonsActionFactory.getActions(state, color).isEmpty() && AmazonsActionFactory.getActions(state, swapColor(color)).isEmpty()) {
			return 0;	//return 0 if terminal and a tie (both players can't move)
		} else if (!AmazonsActionFactory.getActions(state, color).isEmpty() && AmazonsActionFactory.getActions(state, swapColor(color)).isEmpty()) {
			return 1;	//return 1 if terminal and we won (only opponent can't move)
		} else if (AmazonsActionFactory.getActions(state, color).isEmpty() && !AmazonsActionFactory.getActions(state, swapColor(color)).isEmpty()) {
			return -1;	//return 0 if terminal and we lost (only we can't move)
		} else {
			return 10;	//return 10 if not a terminal state (this is arbitrary);
		}
	}

}

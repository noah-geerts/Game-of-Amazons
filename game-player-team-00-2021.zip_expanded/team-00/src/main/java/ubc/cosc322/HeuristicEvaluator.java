package ubc.cosc322;

import java.util.LinkedList;
import java.util.Queue;

public class HeuristicEvaluator {
	public enum HeuristicType {
		MIN_DISTANCE,
		MIN_DISTANCE_PLUS_MOBILITY
	}
	
	private static int whiteQueen = 1;
    private static int blackQueen = 2;
    
//    public static void main(String[] args) {
//    	int[][] board = new int[][] {
//    		{0,0,0,0,0,0,0,0,0,0},
//    		{0,0,0,0,0,0,0,0,0,0},
//    		{0,2,0,0,0,0,0,0,0,0},
//    		{0,0,0,0,0,0,3,2,0,0},
//    		{0,0,0,0,0,0,3,0,0,0},
//    		{0,0,0,0,0,0,0,3,0,0},
//    		{0,0,0,0,0,0,0,0,0,0},
//    		{0,2,0,0,0,0,1,0,0,0},
//    		{0,0,0,0,0,0,0,0,0,0},
//    		{0,0,0,0,0,0,0,0,0,0}};
//    		
//		//final long startTime = System.nanoTime();
//		System.out.println("Heuristic value is: " + getHeuristicEval(board, HeuristicType.MIN_DISTANCE));
//		//final long endTime = System.nanoTime();
//		//System.out.println("Total execution time: " + (endTime - startTime)/1000000.0);
//    }
	
	// returns a double representing the advantage of one player
	// positive values are white advantage
	// negative values are black advantage
	// a type can be passed in to determine which heuristics are factored in
	public static double getHeuristicEval(int board[][], HeuristicType type) {
		int minDistanceEval = getMinDistanceEval(board);
		double mobilityEval = 0.0;
		
		double result;
		if(type == HeuristicType.MIN_DISTANCE_PLUS_MOBILITY) {
			mobilityEval = getMobilityEval(board);
			result = (mobilityEval + minDistanceEval) / 2.0;
		} else {
			result = (double)minDistanceEval;
		}

		return result;
	}

	
	// returns an integer representing the number of squares controlled by each player
	// return value is in the form (squares controlled by white) - (squares controlled by black)
	private static int getMinDistanceEval(int board[][]) {
		int whiteMinDistance[][] = new int[10][10];
		int blackMinDistance[][] = new int[10][10];
		
		// must initialize above arrays to infinity
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				whiteMinDistance[i][j] = Integer.MAX_VALUE;
				blackMinDistance[i][j] = Integer.MAX_VALUE;
			}
		}
		
		Queue<int[]> whiteQueue = new LinkedList<>();
		Queue<int[]> blackQueue = new LinkedList<>();
		
		// get initial positions of all queens and add them  to respective queues
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(board[i][j] == whiteQueen) {
					whiteQueue.add(new int[]{i, j});
					whiteMinDistance[i][j] = 0;
				}
				else if(board[i][j] == blackQueen) {
					blackQueue.add(new int[]{i, j});
					blackMinDistance[i][j] = 0;
				}
			}
		}
		// for each item in the queue we want to loop in all directions
		while(!whiteQueue.isEmpty()) {
			int[] whitePosition = whiteQueue.poll();
			int posY = whitePosition[0];
			int posX = whitePosition[1];
			int minDistance = whiteMinDistance[posY][posX] + 1;
			
			boolean leftValid = true; 
			boolean upLeftValid = true;
			boolean upValid = true;
			boolean upRightValid = true;
			boolean rightValid = true;
			boolean downRightValid = true;
			boolean downValid = true;
			boolean downLeftValid = true;
			for(int i = 1; i < 10; i++) {
				// up
				if(upValid && isSpotValid(board, posY-i, posX)) {
					if(whiteMinDistance[posY-i][posX] > minDistance) {
						whiteMinDistance[posY-i][posX] = minDistance;
						whiteQueue.add(new int[]{posY-i, posX});
					}
				} else upValid = false;

				
				// up left
				if(upLeftValid && isSpotValid(board, posY-i, posX-i)) {
					if(whiteMinDistance[posY-i][posX-i] > minDistance) {
						whiteMinDistance[posY-i][posX-i] = minDistance;
						whiteQueue.add(new int[]{posY-i, posX-i});
					}
				} else upLeftValid = false;
				
				// left
				if(leftValid && isSpotValid(board, posY, posX-i)) {
					if(whiteMinDistance[posY][posX-i] > minDistance) {
						whiteMinDistance[posY][posX-i] = minDistance;
						whiteQueue.add(new int[]{posY, posX-i});
					}
				} else leftValid = false;
				
				// down left
				if(downLeftValid && isSpotValid(board, posY+i, posX-i)) {
					if(whiteMinDistance[posY+i][posX-i] > minDistance) {
						whiteMinDistance[posY+i][posX-i] = minDistance;
						whiteQueue.add(new int[]{posY+i, posX-i});
					}
				} else downLeftValid = false;
				
				// down
				if(downValid && isSpotValid(board, posY+i, posX)) {
					if(whiteMinDistance[posY+i][posX] > minDistance) {
						whiteMinDistance[posY+i][posX] = minDistance;
						whiteQueue.add(new int[]{posY+i, posX});
					}
				} else downValid = false;
				
				// down right
				if(downRightValid && isSpotValid(board, posY+i, posX+i)) {
					if(whiteMinDistance[posY+i][posX+i] > minDistance) {
						whiteMinDistance[posY+i][posX+i] = minDistance;
						whiteQueue.add(new int[]{posY+i, posX+i});
					}
				} else downRightValid = false;
				
				// right
				if(rightValid && isSpotValid(board, posY, posX+i)) {
					if(whiteMinDistance[posY][posX+i] > minDistance) {
						whiteMinDistance[posY][posX+i] = minDistance;
						whiteQueue.add(new int[]{posY, posX+i});
					}
				} else rightValid = false;
				
				// up right
				if(upRightValid && isSpotValid(board, posY-i, posX+i)) {
					if(whiteMinDistance[posY-i][posX+i] > minDistance) {
						whiteMinDistance[posY-i][posX+i] = minDistance;
						whiteQueue.add(new int[]{posY-i, posX+i});
					}
				} else upRightValid = false;
			}
		}
		
		while(!blackQueue.isEmpty()) {
			int[] whitePosition = blackQueue.poll();
			int posY = whitePosition[0];
			int posX = whitePosition[1];
			int minDistance = blackMinDistance[posY][posX] + 1;
			
			boolean leftValid = true; 
			boolean upLeftValid = true;
			boolean upValid = true;
			boolean upRightValid = true;
			boolean rightValid = true;
			boolean downRightValid = true;
			boolean downValid = true;
			boolean downLeftValid = true;
			for(int i = 1; i < 10; i++) {
				// up
				if(upValid && isSpotValid(board, posY-i, posX)) {
					if(blackMinDistance[posY-i][posX] > minDistance) {
						blackMinDistance[posY-i][posX] = minDistance;
						blackQueue.add(new int[]{posY-i, posX});
					}
				} else upValid = false;

				
				// up left
				if(upLeftValid && isSpotValid(board, posY-i, posX-i)) {
					if(blackMinDistance[posY-i][posX-i] > minDistance) {
						blackMinDistance[posY-i][posX-i] = minDistance;
						blackQueue.add(new int[]{posY-i, posX-i});
					}
				} else upLeftValid = false;
				
				// left
				if(leftValid && isSpotValid(board, posY, posX-i)) {
					if(blackMinDistance[posY][posX-i] > minDistance) {
						blackMinDistance[posY][posX-i] = minDistance;
						blackQueue.add(new int[]{posY, posX-i});
					}
				} else leftValid = false;
				
				// down left
				if(downLeftValid && isSpotValid(board, posY+i, posX-i)) {
					if(blackMinDistance[posY+i][posX-i] > minDistance) {
						blackMinDistance[posY+i][posX-i] = minDistance;
						blackQueue.add(new int[]{posY+i, posX-i});
					}
				} else downLeftValid = false;
				
				// down
				if(downValid && isSpotValid(board, posY+i, posX)) {
					if(blackMinDistance[posY+i][posX] > minDistance) {
						blackMinDistance[posY+i][posX] = minDistance;
						blackQueue.add(new int[]{posY+i, posX});
					}
				} else downValid = false;
				
				// down right
				if(downRightValid && isSpotValid(board, posY+i, posX+i)) {
					if(blackMinDistance[posY+i][posX+i] > minDistance) {
						blackMinDistance[posY+i][posX+i] = minDistance;
						blackQueue.add(new int[]{posY+i, posX+i});
					}
				} else downRightValid = false;
				
				// right
				if(rightValid && isSpotValid(board, posY, posX+i)) {
					if(blackMinDistance[posY][posX+i] > minDistance) {
						blackMinDistance[posY][posX+i] = minDistance;
						blackQueue.add(new int[]{posY, posX+i});
					}
				} else rightValid = false;
				
				// up right
				if(upRightValid && isSpotValid(board, posY-i, posX+i)) {
					if(blackMinDistance[posY-i][posX+i] > minDistance) {
						blackMinDistance[posY-i][posX+i] = minDistance;
						blackQueue.add(new int[]{posY-i, posX+i});
					}
				} else upRightValid = false;
			}
		}
		
		/*
		int boardControl[][] = new int[10][10];
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				boardControl[i][j] = 0;
			}
		}
		*/
		
		// add up a score from computed minDistance values
		int score = 0;
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(whiteMinDistance[i][j] < blackMinDistance[i][j]) {
					score++;
					//boardControl[i][j] = 1;
				}
				else if(whiteMinDistance[i][j] > blackMinDistance[i][j]) {
					score--;
					//boardControl[i][j] = 2;
				}
			}
		}
		//printBoard(boardControl);
		return score;
	}
	
	private static boolean isSpotValid(int board[][], int y, int x) {
		return x >= 0 && x <= 9 && y >= 0 && y <= 9 && board[y][x] == 0;
	}
	
	private static void printBoard(int[][] board) {
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(board[i][j] == Integer.MAX_VALUE) {
					System.out.print(9);
				} else {
					System.out.print(board[i][j]);
				}
				System.out.print(' ');
			}
			System.out.println();
		}
		System.out.println();
	}
	
	private static double getMobilityEval(int[][] board) {
		return 0.0;
	}
	
}

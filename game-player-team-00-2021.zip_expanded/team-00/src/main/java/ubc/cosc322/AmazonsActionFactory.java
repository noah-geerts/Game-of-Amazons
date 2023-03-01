package ubc.cosc322;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AmazonsActionFactory {
	
//	public static void main(String args[]) {
//		int[][] defaultBoard = new int[][] {
//			{0,0,0,2,0,0,2,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{2,0,0,0,0,0,0,0,0,2},
//			{0,0,0,0,0,0,0,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{1,0,0,0,0,0,0,0,0,1},
//			{0,0,0,0,0,0,0,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{0,0,0,1,0,0,1,0,0,0},
//		};
//		
//		int[][] board = new int[][] {
//			{0,0,0,2,0,0,2,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{0,0,0,0,0,0,0,0,0,0},
//			{2,0,0,0,0,0,0,0,0,2},
//			{0,0,3,0,0,0,0,0,0,0},
//			{3,3,0,0,3,0,0,0,3,3},
//			{1,3,0,0,0,0,0,0,3,1},
//			{3,3,0,0,0,0,0,0,3,3},
//			{0,0,3,3,3,3,3,3,0,0},
//			{0,0,3,1,3,3,1,3,0,0},
//		};
//		
//		int[][] mobilityMap = AmazonsUtility.getMobilityMap(defaultBoard);
//		AmazonsUtility.printBoard(mobilityMap);
//		
//		ArrayList<AmazonsAction> actions = getActions(defaultBoard, 1);
//		int[][][] newState = AmazonsAction.applyAction(actions.get(0), defaultBoard, mobilityMap);
//		AmazonsUtility.printBoard(newState[1]);
//		
//		AmazonsUtility.printBoard(defaultBoard);
//		AmazonsUtility.printBoard(newState[0]);
//
//	}
	
	// takes in a board state and a color (1 is white, 2 is black)
	// returns an ArrayList of all actions
	public static ArrayList<AmazonsAction> getActions(int[][] board, int color) {
		ArrayList<AmazonsAction> actions = new ArrayList<>();
		
		Queue<int[]> queenPositions = new LinkedList<>();
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(board[i][j] == color) {
					queenPositions.add(new int[]{i, j});
				}
			}
		}
		
		while(!queenPositions.isEmpty()) {
			int[] queenPosition = queenPositions.poll();
			int queenSrcY = queenPosition[0];
			int queenSrcX = queenPosition[1];
			
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
				if(upValid && AmazonsUtility.isSpotValid(board, queenSrcY-i, queenSrcX)) {
					//System.out.println(queenSrcY-i + ", " + queenSrcX);
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX, queenSrcY-i, board));
				} else upValid = false;

				// up left
				if(upLeftValid && AmazonsUtility.isSpotValid(board, queenSrcY-i, queenSrcX-i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX-i, queenSrcY-i, board));
				} else upLeftValid = false;
				
				// left
				if(leftValid && AmazonsUtility.isSpotValid(board, queenSrcY, queenSrcX-i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX-i, queenSrcY, board));
				} else leftValid = false;
				
				// down left
				if(downLeftValid && AmazonsUtility.isSpotValid(board, queenSrcY+i, queenSrcX-i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX-i, queenSrcY+i, board));
				} else downLeftValid = false;
				
				// down
				if(downValid && AmazonsUtility.isSpotValid(board, queenSrcY+i, queenSrcX)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX, queenSrcY+i, board));
				} else downValid = false;
				
				// down right
				if(downRightValid && AmazonsUtility.isSpotValid(board, queenSrcY+i, queenSrcX+i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX+i, queenSrcY+i, board));
				} else downRightValid = false;
				
				// right
				if(rightValid && AmazonsUtility.isSpotValid(board, queenSrcY, queenSrcX+i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX+i, queenSrcY, board));
				} else rightValid = false;
				
				// up right
				if(upRightValid && AmazonsUtility.isSpotValid(board, queenSrcY-i, queenSrcX+i)) {
					actions.addAll(getArrowMoves(queenSrcX, queenSrcY, queenSrcX+i, queenSrcY-i, board));
				} else upRightValid = false;
			}
			
		}
		
		return actions;
	}
	
	private static ArrayList<AmazonsAction> getArrowMoves(int queenSrcX, int queenSrcY, int queenDestX, int queenDestY, int[][] board) {
		ArrayList<AmazonsAction> actions = new ArrayList<>();
		
		int[][] newBoard = AmazonsAction.applyQueenMove(queenSrcX, queenSrcY, queenDestX, queenDestY, board);
		
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
			if(upValid && AmazonsUtility.isSpotValid(newBoard, queenDestY-i, queenDestX)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX, queenDestY-i, board));
			} else upValid = false;

			// up left
			if(upLeftValid && AmazonsUtility.isSpotValid(newBoard, queenDestY-i, queenDestX-i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX-i, queenDestY-i, board));
			} else upLeftValid = false;
			
			// left
			if(leftValid && AmazonsUtility.isSpotValid(newBoard, queenDestY, queenDestX-i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX-i, queenDestY, board));
			} else leftValid = false;
			
			// down left
			if(downLeftValid && AmazonsUtility.isSpotValid(newBoard, queenDestY+i, queenDestX-i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX-i, queenDestY+i, board));
			} else downLeftValid = false;
			
			// down
			if(downValid && AmazonsUtility.isSpotValid(newBoard, queenDestY+i, queenDestX)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX, queenDestY+i, board));
			} else downValid = false;
			
			// down right
			if(downRightValid && AmazonsUtility.isSpotValid(newBoard, queenDestY+i, queenDestX+i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX+i, queenDestY+i, board));
			} else downRightValid = false;
			
			// right
			if(rightValid && AmazonsUtility.isSpotValid(newBoard, queenDestY, queenDestX+i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX+i, queenDestY, board));
			} else rightValid = false;
			
			// up right
			if(upRightValid && AmazonsUtility.isSpotValid(newBoard, queenDestY-i, queenDestX+i)) {
				actions.add(new AmazonsAction(queenSrcX, queenSrcY, queenDestX, queenDestY, queenDestX+i, queenDestY-i, board));
			} else upRightValid = false;
		}
		
		return actions;
	}
}

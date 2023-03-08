package ubc.cosc322;

import java.util.ArrayList;

public class MonteCarlo {	
	long allowedTimeMs;
	TreeNode root;
	int explorationCoefficient;
	
	public static void main(String[] args) {
		int[][] defaultBoard = new int[][] {
			{0,0,0,2,0,0,2,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{2,0,0,0,0,0,0,0,0,2},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{1,0,0,0,0,0,0,0,0,1},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,1,0,0,1,0,0,0},
		};
		int[][] mobilityMap = AmazonsUtility.getMobilityMap(defaultBoard);
		int[][][] state = new int[][][] {defaultBoard, mobilityMap};
		int currentPlayer = 1;
		
		TreeNode currentState = new TreeNode(state, currentPlayer);
		
		while(!currentState.isTerminal()) {
			currentState.printBoard();
			MonteCarlo mc = new MonteCarlo(currentState, 30000, 1);
			AmazonsAction action = mc.MCTS();
			state = AmazonsAction.applyAction(action, state);
			if(currentPlayer == 1) currentPlayer = 2;
			else currentPlayer = 1;
			currentState = new TreeNode(state, currentPlayer);
		}
		AmazonsUtility.printBoard(state[0]);
		System.out.println("Player " + currentPlayer + " loses!");
	}
	
	MonteCarlo(TreeNode root, long allowedTimeMs, int explorationCoefficient){
		this.root = root;
		this.allowedTimeMs = allowedTimeMs;
		this.explorationCoefficient = explorationCoefficient;
	}
	
	// performs an MCTS from the current root and returns the best action
	public AmazonsAction MCTS() {
		long currentTime = System.currentTimeMillis();
		int i = 0;
		for(long startTime = System.currentTimeMillis(); currentTime - startTime < allowedTimeMs; currentTime = System.currentTimeMillis()) {
			TreeNode leaf = traverse(root);
			if(!leaf.expanded && !leaf.isTerminal()) { // if the leaf was already expanded, it means we've traversed the whole game tree, and should not run more
				leaf = leaf.expandAt(0);
				double result = heuristicRollout(leaf);
				//int result = rollout(leaf);
				backpropogate(leaf, result);
			} else {
				System.out.println("Forced win found.");
				System.out.println("Prediction: " + leaf.color + " loses.");
				break;
			}
			i++;
		}
		System.out.println(i);

		// returns an action based on child with highest winrate
		AmazonsAction bestAction = null;
		double bestWinrate = -10000;
		for (TreeNode t : root.children) {
			double winrate = 0;
			double Q = t.Q;
			double N = t.N;
			if (N != 0) {
				winrate = Q / N;
			}
			if (winrate > bestWinrate) {
				bestWinrate = winrate;
				bestAction = t.action;
			}
		}
		return bestAction;
	}

	public TreeNode traverse(TreeNode node) {
		// if the node is not a leaf node, traverse to its best child
		if (!node.hasUnexpandedChildren() && node.hasExpandedChildren()) {
			// get the child with the highest UCB score
			double maxUCB = -1; // all UCB values will be >=0
			TreeNode bestChild = null;
			for (TreeNode n : node.children) {
				double currentUCB = n.getUCB(explorationCoefficient);
				if (currentUCB > maxUCB) {
					maxUCB = currentUCB;
					bestChild = n;
				}
			}
			// traverse recursively
			return traverse(bestChild);
		}
		return node;
	}
	
	//playerColor is the color of the player we desire to win, so the root node's color will be passed in
	public int rollout(TreeNode start) {
		TreeNode currentNode = new TreeNode(start); //copy start node
		while(true) {	
			if(currentNode.isTerminal()) { //if a node is terminal, the current color loses
				if(currentNode.getColor() == start.getColor()) return 0;
				else return 1;
			}
			
			//choose a random child
			int numChildren = currentNode.getNumPossibleActions();
			int random = (int)(Math.random() * numChildren); //generate a random int between 0 and numChildren - 1
			
			//expand that child and continue looping
			currentNode = currentNode.expandAt(random);
		}
	}
	
	public double heuristicRollout(TreeNode node) {
		double heuristicResult = HeuristicEvaluator.getHeuristicEval(node.boardState, node.getColor());
		double result = 0.5;
		if(node.getColor() == 1) {
			if(heuristicResult < -1) {
				result = 1;
			} else if(heuristicResult > 1) {
				result = 0;
			}
		} else {
			if(heuristicResult < -1) {
				result = 0;
			} else if(heuristicResult > 1) {
				result = 1;
			}
		}
		
		return result;
	}
	
	public void backpropogate(TreeNode leaf, double result) {
		if(leaf.equals(root)) {
			return;
		}
		leaf.N++; leaf.Q += result;
		double parentResult = 0.5;
		if(result == 1) parentResult = 0;
		else if(result == 0) parentResult = 1;
		backpropogate(leaf.parent, parentResult);
	}
	
}

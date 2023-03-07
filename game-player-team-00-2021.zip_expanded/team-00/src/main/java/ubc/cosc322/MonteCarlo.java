package ubc.cosc322;

import java.util.ArrayList;

public class MonteCarlo {

	int nSimulations;
	TreeNode root;
	int explorationCoefficient;
	
	MonteCarlo(TreeNode root, int nSimulations, int explorationCoefficient){
		this.root = root;
		this.nSimulations = nSimulations;
		this.explorationCoefficient = explorationCoefficient;
	}
	
	// performs an MCTS from the current root and returns the best action
	public AmazonsAction MCTS() {
		for (int i = 0; i < nSimulations; i++) {
			TreeNode leaf = traverse(root);
			int result = rollout(leaf, root.color);
			backpropogate(leaf, result);
		}

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
		if (!node.children.isEmpty()) {
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
		// if the node is a leaf node, if it is unvisited, return it for rollout
		if (node.N == 0) {
			return node;
		}
		// if it has been visited, expand it and return one of its children for rollout
		node.expand();
		// after expanding, we need to check if no children were created (the node is
		// terminal)
		if (node.children.isEmpty()) {
			return node;
		}
		return node.children.get(0);

	}
	
	//playerColor is the color of the player we desire to win, so the root node's color will be passed in
	public int rollout(TreeNode start, int playerColor) {
		int color = start.color;
		int[][][] state = start.boardState;
		while(true) {
			int terminality = isTerminal(state, playerColor);	//we check win conditions based on the DESIRED PLAYER's COLOR
			//if terminality is not 10, we return it, because we either won or lost
			if(terminality != 10) {return terminality;}
			
			//generate substates and choose one at random
			ArrayList<int[][][]> subStates = generateSubStates(color,state);
			int random = (int)(Math.random() * subStates.size()); //generate a random int between 0 and subStates.size() - 1
			
			//continue looping with a substate chosen and the color swapped
			state = subStates.get(random);
			color = swapColor(color);
		}
	}
	
	public void backpropogate(TreeNode leaf, int result) {
		if(leaf.equals(root)) {
			return;
		}
		leaf.N++; leaf.Q += result;
		backpropogate(leaf.parent, result);
	}
	
	public int isTerminal(int[][][] state, int color) {
		if(AmazonsActionFactory.getActions(state, color).isEmpty() && AmazonsActionFactory.getActions(state, swapColor(color)).isEmpty()) {
			return 0;	//return 0 if terminal and a tie (both players can't move)
		} else if (!AmazonsActionFactory.getActions(state, color).isEmpty() && AmazonsActionFactory.getActions(state, swapColor(color)).isEmpty()) {
			return 1;	//return 1 if terminal and we won (only opponent can't move)
		} else if (AmazonsActionFactory.getActions(state, color).isEmpty() && !AmazonsActionFactory.getActions(state, swapColor(color)).isEmpty()) {
			return 0;	//return 0 if terminal and we lost (only we can't move)
		} else {
			return 10;	//return 10 if not a terminal state (this is arbitrary);
		}
	}
	
	public ArrayList<int[][][]> generateSubStates(int color, int[][][] state){
		ArrayList<AmazonsAction> actions = AmazonsActionFactory.getActions(state, color);
		ArrayList<int[][][]> subStates = new ArrayList<>();
		for(AmazonsAction a: actions) {
			subStates.add(AmazonsAction.applyAction(a, state));
		}
		return subStates;
	}
	
	public int swapColor(int i) {
		if(i==1) {return 2;}
		return 1;
	}
	
}

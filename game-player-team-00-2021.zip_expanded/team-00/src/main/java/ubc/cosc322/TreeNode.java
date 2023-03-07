package ubc.cosc322;

import java.util.ArrayList;

public class TreeNode {

		int color;
		int Q;
		int N;
		int[][][] boardState;
		AmazonsAction action; 	//action taken to reach node (used to return best action after MCTS)
		TreeNode parent;
		ArrayList<TreeNode> children;
		
		//this constructor will be used to create the root node of the Monte-Carlo tree search 
		//2 will be passed in for int N, because we can't have an unvisited root node without 
		//creating an error with the UCB formula (we would divide by 0) and we don't want the second term of UCB to be 0
		TreeNode(int[][][] boardState, int color){
			this.children = new ArrayList<>();
			this.action = null;
			this.boardState = boardState;
			this.parent = null;
			this.color = color;
			this.N = 2;
			this.Q = 0;
		}
		
		//this constructor will be used for creating child nodes
		TreeNode(int[][][] boardState, TreeNode parent, AmazonsAction action){
			this.boardState = boardState;
			this.parent = parent;
			if(parent.color == 2) {
				this.color = 1;
			} else {
				this.color = 2;
			}
			this.N = 0;
			this.Q = 0;
			this.children = new ArrayList<>();
			this.action = action;
		}
		
		public double getUCB(int eC) {
			//if this node hasn't been visited yet, N=0, and the second term of the UCB will be infinity, so we just return Double.MAX_VALUE to avoid errors
			if(N==0) {return 10000.0;}
			//otherwise return the UCB formula
			return Q/N + eC*Math.sqrt(Math.log(parent.N)/N);
		}
		
		//generate a child node given a subState of the current node's state and the action taken to get there
		public void generateChild(int[][][] subState, AmazonsAction a) {
			TreeNode child = new TreeNode(subState, this, a);
			children.add(child);
		}
		
		//expands the current node
		public void expand() {
			ArrayList<AmazonsAction> actions = AmazonsActionFactory.getActions(boardState, color);
			//create a list of all possible subStates from the current node's state
			for(AmazonsAction a: actions) {
				generateChild(AmazonsAction.applyAction(a, boardState), a);
			}
		
		}
		
		//checks if a node is terminal
		public boolean isTerminal() {
			int opponentColor;
			if(this.color == 1) {
				opponentColor = 2;
			} else {opponentColor = 1;}
			//if both players have no legal moves, the node is terminal
			if(AmazonsActionFactory.getActions(boardState, color).isEmpty() || AmazonsActionFactory.getActions(boardState, opponentColor).isEmpty()) {
				return true;
			}
			return false;
		}
}

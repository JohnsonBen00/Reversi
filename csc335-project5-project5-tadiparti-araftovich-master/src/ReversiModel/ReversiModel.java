package ReversiModel;

import ReversiBoard.ReversiBoard;

/**
 * File: ReversiModel.java
 * 
 * This class encapsulates the methods that stores and interacts with the user's moves.
 * 
 * @author Benhur J. Tadiparti tadiparti@email.arizona.edu
 * 
 * @author Andrew Raftovich araftovich@email.arizona.edu
 */
public class ReversiModel extends java.util.Observable{
	
	private int col;
	private int row;
	private int whiteCount;
	private int blackCount;
	private char player;
	private String gameWinner;
	private String gameLoser;
	private boolean isNetworkGame;
	private char[][] tokens; 
	private ReversiBoard myBoard;
	
	/**
	 * Initials the size of the board and the 4 starting tokens of every new game.
	 * @author Benhur J. Tadiparti
	 */
	public ReversiModel(int col, int row) {
		this.col = col;
		this.row = row;
		this.whiteCount = 2;
		this.blackCount = 2;
		this.player = 'B';
		this.gameWinner = "";
		this.gameLoser = "";
		tokens = new char[8][8];
		this.tokens [3][3] = 'W';
		this.tokens [4][4] = 'W';
		this.tokens [3][4] = 'B';
		this.tokens [4][3] = 'B';
		myBoard = new ReversiBoard(this.tokens);
	}
	
	/**
	 * Resets the board to inital state.
	 * @author Andrew Raftovich
	 */
	public void resetBoard() {
		this.tokens = new char[8][8];
		this.whiteCount = 2;
		this.blackCount = 2;
		this.player = 'B';
		this.gameWinner = "";
		this.tokens [3][3] = 'W';
		this.tokens [4][4] = 'W';
		this.tokens [3][4] = 'B';
		this.tokens [4][3] = 'B';
		myBoard = new ReversiBoard(this.tokens);
		setChanged();
		notifyObservers(this);
	}
	
	/**
	 * Sets true if this is a network game, else false.
	 * @param isNetworkGame a boolean that informs the controller if this game is on a network or not
	 */
	public void setIsNetwork(boolean isNetworkGame) {
		this.isNetworkGame = isNetworkGame;
	}
	
	/**
	 * Returns a boolean that lets the controller know if or if not this game is on a network.
	 * @return true is game is not a network, else false
	 */
	public boolean getIsNetwork() {
		return this.isNetworkGame;
	}
	
	/**
	 * Sets the player.
	 * @param player (string) the token color
	 */
	public void setPlayer(char player) {
		if(player == 'B' || player == 'W') {
			this.player = player;
		}
		setChanged();
		notifyObservers(this);
	}
	
	/**
	 * Sets the tokens in the models 2d array onto the board.
	 * @param board - board object
	 */
	public void setBoard(ReversiBoard board) {
		this.myBoard = board;
		this.tokens = board.getBoard();
		this.whiteCount = 0;
		this.blackCount = 0;
		for(int x = 0; x < col; x++) {
			for(int y = 0; y < row; y++) {
				if(this.tokens[x][y] == 'B') {
					this.blackCount++;
				} else if (this.tokens[x][y] == 'W') {
					this.whiteCount++;
				}
			}
		}
		setChanged();
		notifyObservers(this);
	}
	
	/**
	 * Returns the board object.
	 * @return board object
	 */
	public ReversiBoard getBoard() {
		return this.myBoard;
	}
	
	/**
	 * Returns the number of columns in the board.
	 * @return the width of the board
	 * 
	 * @author Benhur J. Tadiparti
	 */
	public int getCol() {
		return this.col;
	}
	
	/**
	 * Returns the number of rows in the board.
	 * @return the height of the board
	 * 
	 * @author Benhur J. Tadiparti
	 */
	public int getRow() {
		return this.row;
	}
	
	/**
	 * Places a token on the board in a new position on board.
	 * @param x Row position of token.
	 * @param y Column position of token.
	 * @param token Color of the new token being placed.
	 * 
	 * @author Andrew Raftovich
	 */
	public void setToken(int x, int y, char token) {
		if(token == 'B' && this.tokens[y][x] == 0) {
			this.tokens[y][x] = token;
			this.blackCount++;
		} else if (token == 'W' && this.tokens[y][x] == 0) {
			this.tokens[y][x] = token;
			this.whiteCount++;
		}
		setChanged();
		notifyObservers(this);
	}
	
	/**
	 * Flips a token at a specific position on board if there existed a token there previously of opposite color.
	 * @param x Row position of token.
	 * @param y Column position of token.
	 * @param token Color of the new token.
	 * 
	 * @author Andrew Raftovich
	 */
	public void flipToken(int x, int y) {
		if(this.tokens[y][x] == 'W') {
			this.tokens[y][x] = 'B';
			this.whiteCount--;
			this.blackCount++;
		} else if (this.tokens[y][x] == 'B') {
			this.tokens[y][x] = 'W';
			this.blackCount--;
			this.whiteCount++;
		}
		setChanged();
		notifyObservers(this);
	}
	
	/**
	 * Returns a single position in the board.
	 * @param x Row position of token.
	 * @param y Column position of token.
	 * @return Value at specific position in the array.
	 * 
	 * @author Andrew Raftovich
	 */
	public char getPosition(int x, int y) {
		return this.tokens[y][x];
	}
	
	/**
	 * Returns the TOKENS List.
	 * @return every (x,y) coordinate of every token on the board
	 * 
	 * @author Benhur J. Tadiparti
	 */
	public char[][] getTokens() {
		return this.tokens;
	}
	
	/**
	 * Returns the score of player 1.
	 * @return the number of white tokens on the board
	 * 
	 * @author Benhur J. Tadiparti
	 */
	public int getWhite() {
		return this.whiteCount;
	}
	
	/**
	 * Returns the score of player 2.
	 * @return the number of black tokens on the board
	 * 
	 * @author Benhur J. Tadiparti
	 */
	public int getBlack() {
		return this.blackCount;
	}
	
	/**
	 * Notifies the view that something has been changed.
	 */
	public void forceUpdate() {
		setChanged();
		notifyObservers(this);
	}
	
	/**
	 * Sets whose move is next.
	 * @return a char that represents whose move is next
	 * 
	 * @author Andrew Raftovich
	 */
	public char nextMove() {
		this.player = (this.player == 'B') ? 'W' : 'B';
		return this.player;
	}
	
	/**
	 * Returns the current player.
	 * @return a char that represents who's turn it is
	 * 
	 * @author Andrew Raftovich
	 */
	public char getPlayer() {
		return this.player;
	}
	
	/**
	 * Sets the winner of the game and notifies the view that something has been changed.
	 * @param winner - player who has the most tokens on the board
	 * 
	 * @author Andrew Raftovich
	 */
	public void setWinner(String winner) {
		this.gameWinner = winner;
		setChanged();
		notifyObservers(this);
	}
	
	/**
	 * Returns the winner of the game.
	 * @return the player with the most tokens on the board
	 * 
	 * @author Andrew Raftovich
	 */
	public String getWinner() {
		return this.gameWinner;
	}
	
	/**
	 * Sets the loser of the game and notifies the view that something has been changed.
	 * @param loser - player who has the least tokens on the board
	 * 
	 * @author Benhur J. Tadiparti
	 */
	public void setLoser(String loser) {
		this.gameLoser = loser;
		setChanged();
		notifyObservers(this);
	}
	
	/**
	 * Returns the loser of the game.
	 * @return the player with the least tokens on the board
	 * 
	 * @author Benhur J. Tadiparti
	 */
	public String getLoser() {
		return this.gameLoser;
	}
	
}
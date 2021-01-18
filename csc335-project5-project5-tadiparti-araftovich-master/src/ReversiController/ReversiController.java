package ReversiController;

import ReversiModel.ReversiModel;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import ReversiBoard.ReversiBoard;

/**
 * File: ReversiController.java
 * 
 * Modifies the user input for the model and also contains logic for checker replacement.
 * 
 * @author Andrew Raftovich araftovich@email.arizona.edu
 * 
 * @author Benhur Tadiparti tadiparti@email.arizona.edu
 */
public class ReversiController {
		
	private ReversiModel myModel;
	private int flip;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private Thread t;
    private ReversiBoard board;	
    private boolean freezeBoard = false;
    private boolean isServer;
    private boolean isHuman;
    private boolean gameOver = false;

	public ReversiController(ReversiModel myModel) {
		this.myModel = myModel;
		this.flip = 0; // Keeps score of how many tokens were turned.
	}
	
	/**
	 * When the user clicks a spot on the board, if it is their turn, update the board with checker placement.
	 * @param row the horizontal position of the checker.
	 * @param col the vertical position of the checker.
	 * 
	 * @author Andrew Raftovich araftovich@email.arizona.edu
	 */
	public void humanTurn(int row, int col) {
		if(isValidPosition(col, row) && validMoveOrFlip(col, row, 'v', this.myModel.getPlayer()) && !freezeBoard && !gameOver) {
			this.myModel.setToken(col, row, this.myModel.getPlayer());
			validMoveOrFlip(col, row, 'f', this.myModel.getPlayer());

			if(this.myModel.getIsNetwork()) {
				sendData();
			} else if(!isGameOver()){
				this.myModel.nextMove();
				computerTurn();
			}
		}	
	}
	
	/**
	 * Sends data to a Server/Client
	 * 
	 * @author Andrew Raftovich
	 * @throws InterruptedException 
	 */
	public void sendData() {
		
		if(!gameOver) {
			ReversiBoard tempBoard = new ReversiBoard(this.myModel.getTokens());
			try {
				outputStream.writeObject(tempBoard);
				outputStream.flush();
			} catch (IOException e) {
				endConnection();
				new Alert(AlertType.INFORMATION, "Connection Closed.").showAndWait();
				return;
			} catch (NullPointerException e) {
				new Alert(AlertType.INFORMATION, "Connection Closed.").showAndWait();
				return;
			}
			freezeBoard = true;
			isGameOver();
		}
	}
		
	/**
	 * Creates a network connection to a Server or Client
	 * @param port Number that the host is using.
	 * @param host Address for the host.
	 * @param isServer If the connection is a server or client connection.
	 * @param isHuman If the connector is either a human or robot.
	 * 
	 * @author Andrew Raftovich
	 */
	public void setNetwork(int port, String host, boolean isServer, boolean isHuman) {
		this.myModel.setIsNetwork(true);
		this.isServer = isServer;
		this.isHuman = isHuman;
		
		try {
			if(isServer) {
				serverSocket = new ServerSocket(4000);
				socket = serverSocket.accept();

				freezeBoard = false;
			} else {
				socket = new Socket("localhost", 4000);
				freezeBoard = true;
			}
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.flush();
			inputStream = new ObjectInputStream(socket.getInputStream());
			
			if(this.isServer && !this.isHuman) {
				computerTurn();
				sendData();
			}
		} catch (UnknownHostException e) {
			new Alert(AlertType.INFORMATION, "Connection Closed.").showAndWait();
		} catch (IOException e) {
			new Alert(AlertType.INFORMATION, "Connection Closed.").showAndWait();
		}
		
		t = new Thread(() -> {
			
			while(socket.isConnected() && !gameOver) {
				
				try{
					board = (ReversiBoard)inputStream.readObject();
				} catch (ClassNotFoundException e) {
					endConnection();
					return;
				} catch (IOException e) {
					endConnection();
					return;
				}
					
				Platform.runLater(() -> {
					this.myModel.setBoard(board);
					this.freezeBoard = false;
					if(!isGameOver()) {
						if(this.isServer) {
							this.myModel.setPlayer('B');
							if(!this.isHuman) {
								computerTurn();
								sendData();
							}
						} else {
							this.myModel.setPlayer('W');
							if(!this.isHuman) {
								computerTurn();
								sendData();
							}
						}
					}
				});

			}
		});
		t.start();
	}
	
	/**
	 * Ends connection with a Server/Client
	 * 
	 * @author Andrew Raftovich
	 */
	public void endConnection() {
		if(socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				new Alert(AlertType.INFORMATION, "Connection Closed.").showAndWait();
			}
		}
		this.freezeBoard = false;
		this.myModel.resetBoard();
		this.myModel.setIsNetwork(false);
		this.gameOver = false;
		this.serverSocket = null;
		this.socket = null;
		this.outputStream = null;
		this.inputStream = null;
		this.t = null;
	}
	
	/**
	 * Checks if the checker placement on the board is near an opposing color/Valid position.
	 * @param row the horizontal position of the checker.
	 * @param col the vertical position of the checker.
	 * @return If the checker placement is valid.
	 * 
	 * @author Andrew Raftovich araftovich@email.arizona.edu
	 */
	private boolean isValidPosition(int row, int col) {
		char[][] board = this.myModel.getTokens();
		char oppositeTeam = (this.myModel.getPlayer() == 'B') ? 'W' : 'B';
		int maxRows = this.myModel.getRow();
		int maxCols = this.myModel.getCol();

		// Check if in bounds and the position is empty
		if(row > maxRows || col > maxCols || row < 0 || col < 0 || board[col][row] != 0) {
			return false;
		}

		//FOR loop for above row if in range of board
		if(col-1 >= 0) {
			for(int tempRow = row-1; tempRow <= row+1; tempRow++) {
				if(tempRow >= 0 && tempRow < maxRows && board[col-1][tempRow] == oppositeTeam ) {
					return true;
				}
			}
		}

		//FOR loop for below row if in range of board
		if(col+1 < maxCols) {
			for(int tempRow = row-1; tempRow <= row+1; tempRow++) {
				if(tempRow >= 0 && tempRow < maxRows && board[col+1][tempRow] == oppositeTeam) {
					return true;
				}
			}
		}

		//Check left
		if(row-1 >= 0 && board[col][row-1] == oppositeTeam) {
			return true;
		}

		//Check right
		if(row+1 < maxRows && board[col][row+1] == oppositeTeam) {
			return true;
		}

		return false;
	}
	
	/**
	 * Checks to see if this is a valid move, meaning that at least one token of the 
	 * opponents team will be swapped, or flips tokens to match token that was placed.
	 * @param row the horizontal position of the checker.
	 * @param col the vertical position of the checker.
	 * @return true if and only if there are tokens that can be turned or have been turned.
	 * @author Andrew Raftovich
	 * @author Benhur Tadiparti
	 */
	private Boolean validMoveOrFlip(int row, int col, char type, char player) {
		char[][] board = this.myModel.getTokens();
		char oppositeTeam = (player == 'B') ? 'W' : 'B';
		int maxRows = this.myModel.getRow();
		int maxCols = this.myModel.getCol();
		
		// Check right for same colored token at 1 > distance
		if(row+1 < maxRows && board[col][row+1] == oppositeTeam) {
			int tempRow = row+1;
			while(tempRow+1 < maxRows && board[col][tempRow+1] == oppositeTeam) {
				tempRow++;
			}
			if(tempRow+1 < maxRows && board[col][tempRow+1] == player) {
				if (type == 'f') {
					System.out.println("Flip all coins between RIGHT");
					for(int rowItem = row+1; rowItem < tempRow+1; rowItem++) {
						this.myModel.flipToken(rowItem, col);
					}
				} else {
					this.flip = tempRow;
					return true;
				}
			}
		}
		
		// Check left 
		if(row-1 >= 0 && board[col][row-1] == oppositeTeam) {
			int tempRow = row-1;
			while(tempRow-1 >= 0 && board[col][tempRow-1] == oppositeTeam) {
				tempRow--;
			}
			if(tempRow-1 >= 0 && board[col][tempRow-1] == player) {
				if (type == 'f') {
					System.out.println("Flip all coins between LEFT");
					for(int rowItem = tempRow; rowItem < row; rowItem++) {
						this.myModel.flipToken(rowItem, col);
					}
				} else {
					this.flip = tempRow;
					return true;
				}
			}
		}
		
		// Check up
		if(col-1 >= 0 && board[col-1][row] == oppositeTeam) {
			int tempCol = col-1;
			while(tempCol-1 >= 0 && board[tempCol-1][row] == oppositeTeam) {
				tempCol--;
			}
			if(tempCol-1 >= 0 && board[tempCol-1][row] == player) {
				if (type == 'f') {
					System.out.println("Flip all coins between UP");
					for(int colItem = col-1; colItem > tempCol-1; colItem--) {
						this.myModel.flipToken(row, colItem);
					}
				} else {
					this.flip = tempCol;
					return true;
				}
			}
		}
		
		// Check down
		if(col+1 < maxCols && board[col+1][row] == oppositeTeam) {
			int tempCol = col+1;
			while(tempCol+1 < maxCols && board[tempCol+1][row] == oppositeTeam) {
				tempCol++;
			}
			if(tempCol+1 < 8 && board[tempCol+1][row] == player) {
				if (type == 'f') {
					System.out.println("Flip all coins between DOWN");
					for(int colItem = col+1; colItem < tempCol+1; colItem++) {
						this.myModel.flipToken(row, colItem);
					}
				} else {
					this.flip = tempCol;
					return true;
				}
			}
		}

		// Check diagonal upper right
		if(row+1 < maxRows && col-1 >= 0 && board[col-1][row+1] == oppositeTeam) {
			int tempRow = row+1;
			int tempCol = col-1;
			while(tempRow+1 < maxRows && tempCol-1 >= 0 && board[tempCol-1][tempRow+1] == oppositeTeam) {
				tempRow++;
				tempCol--;
			}
			if(tempRow+1 < maxRows && tempCol-1 >= 0 && board[tempCol-1][tempRow+1] == player) {
				if (type == 'f') {
					System.out.println("Flip all coins between UP RIGHT");
					int colItem = col-1;
					for(int rowItem = row+1; rowItem < tempRow+1; rowItem++) {
						this.myModel.flipToken(rowItem, colItem--);
					}
				} else {
					this.flip = tempRow;
					return true;
				}	
			}
		}
		
		// Check diagonal upper left
		if(row-1 >= 0 && col-1 >= 0 && board[col-1][row-1] == oppositeTeam) {
			int tempRow = row-1;
			int tempCol = col-1;
			while(tempRow-1 >= 0 && tempCol-1 >= 0 && board[tempCol-1][tempRow-1] == oppositeTeam) {
				tempRow--;
				tempCol--;
			}
			if(tempRow-1 >= 0 && tempCol-1 >= 0 && board[tempCol-1][tempRow-1] == player) {
				if (type == 'f') {
					System.out.println("Flip all coins between UP LEFT");
					int colItem = col-1;
					for(int rowItem = row-1; rowItem > tempRow-1; rowItem--) {
						this.myModel.flipToken(rowItem, colItem--);
					}
				} else {
					this.flip = tempRow;
					return true;
				}
			}
		}
		
		// Check diagonal lower right
		if(row+1 < maxRows && col+1 < maxCols && board[col+1][row+1] == oppositeTeam) {
			int tempRow = row+1;
			int tempCol = col+1;
			while(tempRow+1 < maxRows && tempCol+1 < maxCols && board[tempCol+1][tempRow+1] == oppositeTeam) {
				tempRow++;
				tempCol++;
			}
			if(tempRow+1 < maxRows && tempCol+1 < maxCols && board[tempCol+1][tempRow+1] == player) {
				if (type == 'f') {
					System.out.println("Flip all coins between DOWN RIGHT");
					int colItem = col+1;
					for(int rowItem = row+1; rowItem < tempRow+1; rowItem++) {
						this.myModel.flipToken(rowItem, colItem++);
					}
				} else {
					this.flip = tempRow;
					return true;
				}
			}
		}
		
		// Check diagonal lower left
		if(row-1 >= 0 && col+1 < maxCols && board[col+1][row-1] == oppositeTeam) {
			int tempRow = row-1;
			int tempCol = col+1;
			while(tempRow-1 >= 0 && tempCol+1 < maxCols && board[tempCol+1][tempRow-1] == oppositeTeam) {
				tempRow--;
				tempCol++;
			}
			if(tempRow-1 >= 0 && tempCol+1 < maxCols && board[tempCol+1][tempRow-1] == player) {
				if (type == 'f') {
					System.out.println("Flip all coins between DOWN LEFT");
					int rowItem = row-1;
					for(int colItem = col+1; colItem < tempCol+1; colItem++) {
						this.myModel.flipToken(rowItem--, colItem);
					}
				} else {
					this.flip = tempRow;
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks if the game is over and update model.
	 * 
	 * @author Andrew Raftovich
	 */
	public Boolean isGameOver() {
		int rowSize = this.myModel.getRow();
		int colSize = this.myModel.getCol();
		int whitePieces = this.myModel.getWhite();
		int blackPieces = this.myModel.getBlack();
		char oppositeTeam = (this.myModel.getPlayer() == 'B') ? 'W' : 'B';
		if((rowSize*colSize) == (whitePieces+blackPieces)) {
			if(whitePieces > blackPieces) {
				if (this.myModel.getPlayer() == 'B') {
					this.myModel.setLoser("Black");
				} else {
					this.myModel.setWinner("White");
				}
				endConnection();
			} else if (blackPieces > whitePieces) {
				if (this.myModel.getPlayer() == 'W') {
					this.myModel.setLoser("White");
				} else {
					this.myModel.setWinner("Black");
				}
				endConnection();
			} else {
				this.myModel.setWinner("tie");
				endConnection();
			}
		} else {
			// Check if no more valid positions left on board.
			boolean noMoreMoves = true;
			for(int col = 0; col < colSize; col++) {
				for(int row = 0; row < rowSize; row++) {
					if(this.myModel.getPosition(col, row) == 0 && (validMoveOrFlip(col, row, 'v', this.myModel.getPlayer()) 
							|| validMoveOrFlip(col, row, 'v', oppositeTeam))) {
						noMoreMoves = false;
						break;
					}
				}
			}
			
			if(noMoreMoves) {
				if(whitePieces > blackPieces) {
					if (this.myModel.getPlayer() == 'B') {
						this.myModel.setLoser("Black");
					} else {
						this.myModel.setWinner("White");
					}
					endConnection();
				} else if (blackPieces > whitePieces) {
					if (this.myModel.getPlayer() == 'W') {
						this.myModel.setLoser("White");
					} else {
						this.myModel.setWinner("Black");
					}
					endConnection();
				} else {
					this.myModel.setWinner("(Tie) Both");
					endConnection();
				}
			}
		}
		return false;
	}
	
	/**
	 * This is the AI program, and it chooses the best move to play aganist the user.
	 * @author Benhur Tadiparti
	 */
	public void computerTurn() {
		if(!gameOver) {			
			int bestRow = 0;
			int bestCol = 0;
			int numFlip = 0;
			for(int col = 0; col < this.myModel.getCol(); col++) {
				for(int row = 0; row < this.myModel.getRow(); row++) {
					if(this.myModel.getPosition(col, row) == 0 && isValidPosition(col, row) && 
							validMoveOrFlip(col, row, 'v', this.myModel.getPlayer())) {
						if(numFlip < this.flip) {
							bestRow = row;
							bestCol = col;
							numFlip = this.flip;
						} 
					}
				}
			}
			
			this.flip = 0;
			this.myModel.setToken(bestCol, bestRow, this.myModel.getPlayer());
			validMoveOrFlip(bestCol, bestRow, 'f', this.myModel.getPlayer());

			if(!this.myModel.getIsNetwork()) {
				this.myModel.nextMove();
				isGameOver();
			}
		}
	}
	
	/**
	 * Resets the board and game.
	 * 
	 * @author Andrew Raftovich
	 */
	public void resetBoard() {
		this.myModel.resetBoard();
		this.myModel.setIsNetwork(false);
		this.gameOver = false;
	}
	
	/**
	 * Tells the model that the model if it is playing local AI or a server connection.
	 * @param isNetwork
	 * 
	 * @author Andrew Raftovich
	 */
	public void setIsNetwork(boolean isNetwork) {
		this.myModel.setIsNetwork(isNetwork);
	}	

}
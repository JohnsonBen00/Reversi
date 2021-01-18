package ReversiBoard;

import java.io.Serializable;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * File: ReversiBoard.java
 * 
 * This  class that captures the row, column, and color of a move that the user played.
 * 
 * @author Benhur J. Tadiparti tadiparti@email.arizona.edu
 * 
 * @author Andrew Raftovich araftovich@email.arizona.edu
 */
public class ReversiBoard implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private char[][] tokens;
	
	/**
	 * Initializes valuable data required to 'paint' the board.
	 * @param token - a 2d char list that contains all the tokens that have been played
	 * @param board - GridPane
	 * 
	 * @author Benhur J. Tadiparti
	 */
	public ReversiBoard(char[][] token) {
		this.tokens = token;
	}
	
	/**
	 * Colors each token that is on the board.
	 * 
	 * @author Benhur J. Tadiparti
	 * @author Andrew Raftovich
	 */
	public void updateBoard(GridPane board) {
		for(Node child : board.getChildren()) {
			Circle circle = (Circle) ((Pane) child).getChildren().get(0);
			if (this.tokens[GridPane.getRowIndex(child)][GridPane.getColumnIndex(child)] == 'W') {
				circle.setOpacity(1);
				circle.setFill(Color.WHITE);
			} else if (this.tokens[GridPane.getRowIndex(child)][GridPane.getColumnIndex(child)] == 'B') {
				circle.setOpacity(1);
				circle.setFill(Color.BLACK);
			} else {
				circle.setOpacity(0);
			}
		}
	}
	
	/**
	 * Sends the board for Model usage.
	 * @return Sends board of checkers that are either black or white.
	 * 
	 * @author Andrew Raftovich
	 */
	public char[][] getBoard() {
		return this.tokens;
	}
		
	/**
	 * Shows an alert indicating who won the game.
	 * @param winner String of winning checker
	 * 
	 * @author Andrew Raftovich
	 */
	public void showWinner(String winner) {
		if(winner.toLowerCase().equals("tie")) {
			new Alert(AlertType.INFORMATION, "Tied game.").showAndWait();
		} else {
			new Alert(AlertType.INFORMATION, "You won!").showAndWait();			
		}
	}
	
	/**
	 * Shows an alert indicating who lost the game.
	 * @param loser String of losing checker
	 * 
	 * @author Benhur J. Tadiparti
	 */
	public void showLoser() {
		new Alert(AlertType.INFORMATION, "You lost!").showAndWait();
	}
	
	/**
	 * Updates the text on the GUI indicating the total score.
	 * @param gameText JavaFX Text object containing the game score.
	 * @param whiteTokens Total white tokens on screen.
	 * @param blackTokens Total black tokens on screen.
	 * 
	 * @author Andrew Raftovich
	 */
	public void updateTokenCounter(Text gameText, int whiteTokens, int blackTokens) {
		gameText.setText("White: " + whiteTokens + " - Black: " + blackTokens);
	}
	
}
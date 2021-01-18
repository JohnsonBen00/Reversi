package ReversiView;

import java.util.Observable;
import java.util.Observer;

import ReversiController.ReversiController;
import ReversiModel.ReversiModel;
import ReversiNetwork.ReversiNetwork;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * File: ReversiView.java
 * 
 * Displays the Reversi board with the initial 4 checkers shown.
 * 
 * @author Andrew Raftovich araftovich@email.arizona.edu
 * 
 * @author Benhur Tadiparti tadiparti@email.arizona.edu
 */
public class ReversiView extends Application implements Observer{
	
	private final int COL_SIZE = 8;
	private final int ROW_SIZE = 8;
	
	private MenuBar menu = new MenuBar();
	private Menu fileText = new Menu("File");
	private MenuItem newGame = new MenuItem("New Game");
	private MenuItem networkedGame = new MenuItem("Networked Game");
	private GridPane board = new GridPane();
	private TilePane tile = new TilePane();
	private Text gameText = new Text();
	private GridPane window = new GridPane();
	
	private ReversiNetwork network;
	private ReversiModel myModel = new ReversiModel(COL_SIZE, ROW_SIZE);
	private ReversiController myController = new ReversiController(myModel);	

	@Override
	public void start(Stage primaryStage) throws Exception {
		myModel.addObserver(this);
		this.myModel.setIsNetwork(false);
		
		
		/** Get position of checker position when user clicks on board */
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() { 
	         @Override 
	         public void handle(MouseEvent e) { 
	        	 Node source = (Node)e.getSource();
	        	 int col = GridPane.getColumnIndex(source);
	        	 int row = GridPane.getRowIndex(source);
	        	 myController.humanTurn(row, col);
	         } 
	     }; 
		
		board.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		for(int x = 0; x < COL_SIZE; x++) {
			for(int y = 0; y < ROW_SIZE; y++) {
				Circle tempCircle = new Circle(20.0f);
				tempCircle.setOpacity(0);
								
				StackPane tempPane = new StackPane();
				tempPane.setPrefSize(48, 48);
				tempPane.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
				tempPane.getChildren().add(tempCircle);
				tempPane.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
				board.add(tempPane, x, y, 1, 1);
				GridPane.setMargin(tempPane, new Insets(1.5, 1.5, 1.5, 1.5));
			}
		}
		
		this.myModel.forceUpdate();
		
		tile.getChildren().add(board);
		tile.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
		TilePane.setMargin(board, new Insets(9, 9, 9, 9));
		
		/** Allows the user to play a new game against whomever they would like to play*/
        EventHandler<ActionEvent> menuAction = new EventHandler<ActionEvent>() {
        	@Override
            public void handle(ActionEvent event) {
                MenuItem mItem = (MenuItem) event.getSource();
                String label = mItem.getText();
                if ("New Game".equalsIgnoreCase(label)) {
                	myController.resetBoard();
                } else if ("Networked Game".equalsIgnoreCase(label)) {
                	network = new ReversiNetwork(myController);
                	Stage secondaryStage = new Stage();
                	network.start(secondaryStage);
                } 
            }
        };
        newGame.setOnAction(menuAction);
        networkedGame.setOnAction(menuAction);

		fileText.getItems().add(newGame);
		fileText.getItems().add(networkedGame);
				
		menu.getMenus().add(fileText);
		gameText.setText("White: " + myModel.getWhite() + " - Black: " + myModel.getBlack());
		
		window.add(menu, 0, 0, 1, 1);
		window.add(tile, 0, 1, 1, 1);	
		window.add(gameText, 0, 2, 1, 1);
		GridPane.setMargin(gameText, new Insets(1, 1, 1, 1));
			
		primaryStage.setScene(new Scene(window, 435, 490));
        primaryStage.setTitle("Reversi");
        primaryStage.show();
        
	}
	
	/**
	 * When a user input or move event occurs, the view updates the model and board objects.
	 */
	@Override
	public void update(Observable o, Object arg) {
		ReversiModel tempModel = (ReversiModel)arg;
		tempModel.getBoard().updateBoard(this.board);
		tempModel.getBoard().updateTokenCounter(gameText, this.myModel.getWhite(), this.myModel.getBlack());
				
		if(!tempModel.getWinner().equals("")) {
			tempModel.getBoard().showWinner(tempModel.getWinner());
			tempModel.setWinner("");
		}
		if(!tempModel.getLoser().equals("")) {
			tempModel.getBoard().showLoser();
			tempModel.setLoser("");
		}
	}
	
}
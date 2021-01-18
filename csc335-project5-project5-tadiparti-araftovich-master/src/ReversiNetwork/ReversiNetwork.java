package ReversiNetwork;

import ReversiController.ReversiController;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * File: ReverseNetwork.java
 * 
 * Sets up a network connection with a desired opponent
 * 
 * @author Benhur Tadiparti tadiparti@email.arizona.edu
 * 
 * @author Andrew Raftovich araftovich@email.arizona.edu
 */
public class ReversiNetwork extends Stage {
	
	private Rectangle2D screen;
	private ReversiController myController;
	
	public ReversiNetwork(ReversiController myController) {
		this.myController = myController;
	}
	
	/**
	 * Takes in a new stage to create pop up window and store the user's inputs. 
	 * @param secondaryStage - new stage for the new network setup pop up
	 */
	public void start(Stage secondaryStage) {
		this.screen = Screen.getPrimary().getVisualBounds(); // Screen bounds
		
		secondaryStage.initModality(Modality.APPLICATION_MODAL);
		Pane window = new Pane();
		// Create HBox
		HBox cbox = new HBox();
		
		Label create = new Label("Create:  ");
		create.setFont(new Font(15));
		create.setAlignment(Pos.CENTER);
		
		ToggleGroup createToggle = new ToggleGroup();
		
		RadioButton server = new RadioButton("Server  ");
		server.setSelected(true);
		RadioButton client = new RadioButton("Client  ");
		
		server.setToggleGroup(createToggle);
		client.setToggleGroup(createToggle);
				
		cbox.getChildren().add(create);
		cbox.getChildren().add(server);
		cbox.getChildren().add(client);
		cbox.relocate(15, 20);
		window.getChildren().add(cbox);
		// Play as HBox
		HBox pabox = new HBox();
		
		Label playAs = new Label("Play as:  ");
		playAs.setFont(new Font(15));
		playAs.setAlignment(Pos.CENTER);
		
		ToggleGroup playerToggle = new ToggleGroup(); 
		
		RadioButton human = new RadioButton("Human  ");
		human.setSelected(true);
		RadioButton computer = new RadioButton("Computer  ");
		
		human.setToggleGroup(playerToggle);
		computer.setToggleGroup(playerToggle);
				
		pabox.getChildren().add(playAs);
		pabox.getChildren().add(human);
		pabox.getChildren().add(computer);
		pabox.relocate(15, 60);
		window.getChildren().add(pabox);
		// Server - Port HBox
		HBox SPbox = new HBox();
		
		Label serverPort = new Label("Server ");
		TextField spTX = new TextField("localhost"); // TextField for Server
		spTX.setPrefWidth(this.screen.getWidth() * .085);

		Label portServer = new Label(" Port ");
		TextField psTX = new TextField("4000"); // TextField for Port
		psTX.setPrefWidth(this.screen.getWidth() * .085);

		SPbox.getChildren().add(serverPort);
		SPbox.getChildren().add(spTX);
		SPbox.getChildren().add(portServer);
		SPbox.getChildren().add(psTX);
		SPbox.relocate(15, 100);
		window.getChildren().add(SPbox);
		// Button HBox
		HBox Bbox = new HBox();
		
		Button ok = new Button("OK");
		Label space = new Label(" ");
		Button cancel = new Button("Cancel");
		Bbox.getChildren().add(ok);
        ok.setOnAction((event) -> {
        	
        	String createOrJoin = ((RadioButton)createToggle.getSelectedToggle()).getText().trim();
        	String humanOrComputer = ((RadioButton)playerToggle.getSelectedToggle()).getText().trim();
        	
        	try {
        		int port = Integer.parseInt(psTX.getText());
        		// Starts a network game
        		this.myController.setNetwork(port, spTX.getText(), createOrJoin.equals("Server"), humanOrComputer.equals("Human"));
        		
        		secondaryStage.close();
        		
        	} catch (NumberFormatException e) {
        		e.printStackTrace();
        		new Alert(AlertType.ERROR, "Invalid port number format").showAndWait();
        	}
        });
        
		Bbox.getChildren().add(space);
		
		Bbox.getChildren().add(cancel);
        cancel.setOnAction((event) -> {
        	secondaryStage.close(); // Close Stage
        });
        
		Bbox.relocate(15, 140);
		window.getChildren().add(Bbox);
		
		window.setStyle("-fx-background-color: white");
		secondaryStage.setScene(new Scene(window, 450, 200));
        secondaryStage.setTitle("Network Setup");
        secondaryStage.showAndWait();
	}
	
}
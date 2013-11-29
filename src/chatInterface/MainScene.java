package chatInterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import client.Client;
import common.Common;


public class MainScene extends Scene
{
	
	private static MainScene instance = null;
	private Stage primaryStage;
	private BorderPane root;
	private Client client = new Client("test", "localhost", 5000);
	
	public static boolean SetInstance(Stage primaryStage, BorderPane root)
	{
		if (MainScene.instance == null)
		{
			MainScene.instance = new MainScene(primaryStage, root);
			return true;
		}
		return false;
	}
	
	public static MainScene GetInstance()
	{
		return MainScene.instance;
	}
	
	public MainScene(Stage primaryStage, BorderPane root)
	{
		super(root);
		this.root = root;
		this.primaryStage = primaryStage;
		chatArea();
		buildMenu();
		
	}
	
	public void chatArea()
	{
		client.start();
		VBox contener = new VBox();
		final TextArea textArea = new TextArea();
		textArea.setMaxHeight(450);
		textArea.setMinHeight(450);
		textArea.setMaxWidth(500);
		textArea.setMinWidth(500);
		textArea.setEditable(false);
		
		final TextField textField = new TextField();
		textField.setMaxWidth(437);
		textField.setMinWidth(437);
		
		
		
		Button buttonSend = new Button("Envoyer");
		buttonSend.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0)
			{
				sendMessageToTextArea(textField, textArea);
				
			}
		});
		
		textField.setOnKeyPressed(new EventHandler<KeyEvent>()
		{
			public void handle(KeyEvent event)
			{
			    if (event.getCode() == KeyCode.ENTER) 
			    { 
			    	sendMessageToTextArea(textField, textArea);
			    }
			}
		});		
		HBox  hBox = new HBox();
		hBox.getChildren().addAll(textField, buttonSend);
		
		contener.getChildren().addAll(textArea, hBox);
		
		GridPane gridPane = new GridPane();
		gridPane.add(contener, 0, 0);
		gridPane.setAlignment(Pos.TOP_LEFT);
		
		
		this.root.setCenter(contener);
	}
	
	public void buildMenu()
	{
		Menu menuOptions = new Menu("Options");
		
		MenuItem menuItemClose = new MenuItem("Fermer");
		
		MenuItem menuItemUser = new MenuItem("Nom d'utilisateur");
		
		menuOptions.getItems().addAll(menuItemClose, menuItemUser);
		
		Menu menuAide = new Menu("?");
		
		MenuItem menuItemHelp = new MenuItem("Aide");
		
		MenuItem menuItemAbout = new MenuItem("À propos...");
	
		
		menuAide.getItems().addAll(menuItemHelp, menuItemAbout);
		
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(menuOptions, menuAide);
		
		this.root.setTop(menuBar);
		
	}
	
	
	public void sendMessageToTextArea(TextField textField, TextArea textArea)
	{
		this.client.sendMessage(textField.getText() + "\n", Common.MESSAGE, "Test");
		textArea.appendText(textField.getText());
		textField.clear();
	}

}

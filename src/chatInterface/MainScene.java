package chatInterface;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainScene extends Scene
{
	
	private static MainScene instance = null;
	private Stage primaryStage;
	private BorderPane root;
	
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
		
	}
	
	public void chatArea()
	{
		VBox contener = new VBox();
		TextArea textArea = new TextArea();
		textArea.setMaxHeight(450);
		textArea.setMinHeight(450);
		textArea.setMaxWidth(500);
		textArea.setMinWidth(500);
		textArea.setDisable(true);
		
		TextField textField = new TextField();
		textField.setMaxWidth(425);
		textField.setMinWidth(425);
		
		
		Button buttonSend = new Button("Envoyer");
		HBox  hBox = new HBox();
		hBox.getChildren().addAll(textField, buttonSend);
		
		contener.getChildren().addAll(textArea, hBox);
		
		GridPane gridPane = new GridPane();
		gridPane.add(contener, 0, 0);
		gridPane.setAlignment(Pos.TOP_LEFT);
		
		
		this.root.setCenter(contener);
	}

}

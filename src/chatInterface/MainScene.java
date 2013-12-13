package chatInterface;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import client.Client;

import common.Common;

/**
 * La classe MainScene est une classe utilisée pour créer le menu et mettre en commun les autres classes.
 * Elle contient la zone de clavardage et la zone de connexion.
 * La classe hérite de Scene.
 * @author Mathieu Moreault
 * @author Anthony Lavallée
 * @author Élise Leclerc
 * @author Pierre Marion
 */
public class MainScene extends Scene
{
    private static MainScene instance = null;
    private final Stage primaryStage;
    private final BorderPane root;
    private Client client;
    private final TextArea textArea = new TextArea();
    //private ListView<String> userlist = new ListView<String>();
    private final TextArea userlist = new TextArea();
    
    private static final String PATHNAME_LOGO = "/ressources/rafiki.jpg";
    
    private static final String FONT = "Georgia";
    
    final TextField txtServer = new TextField("localhost");
	final TextField txtUsername = new TextField("");
	final TextField txtPort = new TextField(Integer.toString(Common.DEFAULT_PORT));
    
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
            loginArea();
            buildMenu();
    }
    public void chatArea(String server, String username, String port)
    {
    	if (client == null)
    	{
		client = new Client(username, server, Integer.parseInt(port), this);
        client.start();
    	}
    	else
    	{
    		client.updateNameUser(txtUsername.getText());
    	}
        
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
                        sendMessage(textField);
                        
                }
        });
        
        textField.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
                @Override
				public void handle(KeyEvent event)
                {
                    if (event.getCode() == KeyCode.ENTER) 
                    {
                    	
                            sendMessage(textField);
                    }
                   
                }
        });
        //OnClose Event de la fenêtre
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				client.sendMessage("", Common.LOGOUT, Common.DEFAULT_ROOM);
				System.exit(0);
			}
		});

        userlist.setMinWidth(100);
        userlist.setMaxWidth(100);
        userlist.setMaxHeight(450);
        userlist.setMinHeight(450);
        userlist.setEditable(false);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(textField, buttonSend);
        
        GridPane gridPane = new GridPane();
        //gridPane.add(contener, 0, 0);
        gridPane.setAlignment(Pos.TOP_LEFT);
        
        
        this.root.setLeft(textArea);
        this.root.setRight(userlist);
        this.root.setBottom(hBox);
        

    }
    /**
     * S'occupe de faire la mise en forme des contrôles pour la connexion
     * d'un usager.
     */
    public void loginArea()
    {
    	
    	final Label lblServer = new Label("Server : ");
    	final Label lblUsername = new Label("Username : ");
    	Button btnConnect = new Button("Connecter");
        btnConnect.setOnAction(new EventHandler<ActionEvent>()
        {
                @Override
                public void handle(ActionEvent arg0)
                {
                        chatArea(txtServer.getText(), txtUsername.getText(), txtPort.getText());
                }
        });
    	Button btnQuit = new Button("Quitter");
        btnQuit.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
            	System.exit(0);
            }
        });
        GridPane grid = new GridPane();
		grid.add(lblServer, 0, 0);
		grid.add(txtServer, 1, 0);
		grid.add(txtPort, 2, 0);
		grid.add(lblUsername, 0, 1);
		grid.add(txtUsername, 1, 1);
		grid.add(btnConnect, 0, 2);
		grid.add(btnQuit, 1, 2);
		
		for (Node node : grid.getChildren())
		{
			GridPane.setMargin(node, new Insets(0, 10, 10, 0));
		}
		
		
		
		BorderPane subPane = new BorderPane();
		
		Image imgLogo = new Image(PATHNAME_LOGO);
		ImageView imvLogo = new ImageView(imgLogo);
		
		
		subPane.setTop(imvLogo);
		BorderPane.setMargin(imvLogo, new Insets(25, 100, 25, 100));
		BorderPane.setMargin(grid, new Insets(25, 100, 25, 100));
		subPane.setCenter(grid);
		
        root.setLeft(subPane);
    }
    
    public void buildMenu()
    {
            Menu menuOptions = new Menu("Options");
            
            MenuItem menuItemClose = new MenuItem("Fermer");
            menuItemClose.setOnAction(new EventHandler<ActionEvent>()
            		{
            			@Override
            			public void handle(ActionEvent arg0)
            			{
            				System.exit(0);

            			}
            		});
            
            MenuItem menuItemUser = new MenuItem("Changer d'utilisateur");
            
            menuItemUser.setOnAction(new EventHandler<ActionEvent>()
            		{
            			@Override
            			public void handle(ActionEvent arg0)
            			{
            				if (client != null)
            				{
            				//client.sendMessage("", Common.LOGOUT, Common.DEFAULT_ROOM);
            				
            				MainScene.this.root.getChildren().clear();
            				
            				
            				buildMenu();
            				//loginArea();
            				changeUserName();
            				}
            				
//            				

            			}
            		});
            
            menuOptions.getItems().addAll(menuItemClose, menuItemUser);
            
            Menu menuAide = new Menu("?");
            
            MenuItem menuItemHelp = new MenuItem("Aide");
            menuItemHelp.setOnAction(new EventHandler<ActionEvent>()
            		{
            			@Override
            			public void handle(ActionEvent arg0)
            			{
            				helpWindow();

            			}
            		});
            
            MenuItem menuItemAbout = new MenuItem("À propos...");
            menuItemAbout.setOnAction(new EventHandler<ActionEvent>()
            		{
            			@Override
            			public void handle(ActionEvent arg0)
            			{
            				aboutWindow();

            			}
            		});
    
            
            menuAide.getItems().addAll(menuItemHelp, menuItemAbout);
            
            MenuBar menuBar = new MenuBar();
            menuBar.getMenus().addAll(menuOptions, menuAide);
            
            this.root.setTop(menuBar);
            
    }
    
    
    private void sendMessage(TextField textField)
    {
    	if (textField.getText().equalsIgnoreCase(Common.COMMAND_LOGOUT))
		{
			client.sendMessage("", Common.LOGOUT, Common.DEFAULT_ROOM);
			System.exit(0);
		}
		else
		{
			client.sendMessage(textField.getText() + "\n", Common.MESSAGE, Common.DEFAULT_ROOM);
			textField.clear();
		}
    }
    public void appendToTextArea(String textToAppend)
    {
    	this.textArea.appendText(textToAppend);
    }
    
    public void setUsers(ObservableList<String> list)
    {
    	FXCollections.sort(list);
    	userlist.clear();
    	for (int i = 0; i < list.size(); i++)
    	{
    		userlist.appendText(list.get(i) + "\n");
    	}
    }
    
    private void aboutWindow()
    {
    	Label lblApplicationName = new Label("Rafiki Chat System");
    	lblApplicationName.setFont(new Font(FONT, 20));
    	Label lblVersion = new Label("Version 1.0");
    	lblVersion.setFont(new Font(FONT, 15));
    	Label lblDescription = new Label("Cours: Système d'exploitation");
    	lblDescription.setFont(new Font(FONT, 15));
		Label lblRealisationDate = new Label("Novembre-décembre 2013");
		lblRealisationDate.setFont(new Font(FONT, 15));
		Label lblBy = new Label("Réalisé par: ");
		lblBy.setFont(new Font(FONT, 20));
		Label lblName1 = new Label("Élise Carbonneau-Leclerc");
		lblName1.setFont(new Font(FONT, 15));
		Label lblName2 = new Label("Anthony Lavallée");
		lblName2.setFont(new Font(FONT, 15));
		Label lblName3 = new Label("Pierre Marion");
		lblName3.setFont(new Font(FONT, 15));
		Label lblName4 = new Label("Mathieu Moreault");
		lblName4.setFont(new Font(FONT, 15));

		GridPane grid = new GridPane();

		grid.add(lblApplicationName, 0, 0);
		grid.add(lblVersion, 0, 1);
		grid.add(lblDescription, 0, 2);
		grid.add(lblRealisationDate, 0, 3);
		grid.add(lblBy, 0, 4);
		grid.add(lblName1, 0, 5);
		grid.add(lblName2, 0, 6);
		grid.add(lblName3, 0, 7);
		grid.add(lblName4, 0, 8);
		
		for (Node node : grid.getChildren())
		{
			GridPane.setMargin(node, new Insets(10, 5, 5, 10));
		}

		Stage stage = new Stage();
		stage.setTitle("À propos");
		Scene scene = new Scene(grid);
		stage.setResizable(false);
		stage.setScene(scene);

		stage.showAndWait();
    }
    
    
    private void helpWindow()
    {
    	Label lblApplicationName = new Label("Aide: Rafiki Chat System");
    	lblApplicationName.setFont(new Font(FONT, 20));
    	Label lblHelp1 = new Label("L'application Rafiki est un système de clavardage.");
    	lblHelp1.setFont(new Font(FONT, 15));
    	Label lblHelp2 = new Label("Connectez-vous et commencer à parler!");
    	lblHelp2.setFont(new Font(FONT, 15));
    	Label lblHelp3 = new Label("Vous pouvez changer d'utilisateur en tout temps grâce au menu «Options»");
    	lblHelp3.setFont(new Font(FONT, 15));
    	Label lblHelp4 = new Label("La liste à droite de la zone de clavardage sont les personnes présentes dans la discution.");
    	lblHelp4.setFont(new Font(FONT, 15));
    	

		GridPane grid = new GridPane();

		grid.add(lblApplicationName, 0, 0);
		grid.add(lblHelp1, 0, 1);
		grid.add(lblHelp2, 0, 2);
		grid.add(lblHelp3, 0, 3);
		grid.add(lblHelp4, 0, 4);
		
		
		for (Node node : grid.getChildren())
		{
			GridPane.setMargin(node, new Insets(10, 5, 5, 10));
		}

		Stage stage = new Stage();
		stage.setTitle("Aide");
		Scene scene = new Scene(grid);
		stage.setResizable(false);
		stage.setScene(scene);

		stage.showAndWait();
    }
    
    private void changeUserName()
    {
    	final Label lblUsername = new Label("Username : ");
    	Button btnConnect = new Button("Changer");
        btnConnect.setOnAction(new EventHandler<ActionEvent>()
        {
                @Override
                public void handle(ActionEvent arg0)
                {
                	client.sendMessage(txtUsername.getText(), Common.NICKCHANGE, Common.DEFAULT_ROOM);
                    chatArea(txtServer.getText(), txtUsername.getText(), txtPort.getText());
                }
        });
    	Button btnQuit = new Button("Quitter");
        btnQuit.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent arg0)
            {
            	System.exit(0);
            }
        });
        GridPane grid = new GridPane();
		grid.add(lblUsername, 0, 0);
		grid.add(txtUsername, 1, 0);
		grid.add(btnConnect, 0, 2);
		grid.add(btnQuit, 1, 2);
		
		for (Node node : grid.getChildren())
		{
			GridPane.setMargin(node, new Insets(0, 10, 10, 0));
		}
		
		
		
		BorderPane subPane = new BorderPane();
		
		Image imgLogo = new Image(PATHNAME_LOGO);
		ImageView imvLogo = new ImageView(imgLogo);
		
		
		subPane.setTop(imvLogo);
		BorderPane.setMargin(imvLogo, new Insets(25, 100, 25, 100));
		BorderPane.setMargin(grid, new Insets(25, 100, 25, 100));
		subPane.setCenter(grid);
		
        root.setLeft(subPane);
    }
    
    /*
     * Code qui utilise une ListView. Cause une exception.
    public void setUsers(ObservableList<String> list)
    {
    	FXCollections.sort(list);
    	userlist.setItems(list);
    }
    */

}
package chatInterface;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * La classe AppFrame est une classe utilisée pour construire la fenêtre principale de l'application.
 * La classe hérite de Application.
 * @author Mathieu Moreault
 * @author Anthony Lavallée
 * @author Élise Leclerc
 * @author Pierre Marion
 */
public class AppFrame extends Application
{
    public static final String TITLE = "Chat Rafiki";
    private static final double DEFAULT_LOCATION_X = 100;
    private static final double DEFAULT_LOCATION_Y = 100;
    private static final double HEIGHT_APPLICATION = 525;
    private static final double WIDTH_APPLICATION = 600;
    
    /**
	 * La méthode qui affiche la fenêtre selon la position donnée et le titre donné.
	 * 
	 * @param Stage
	 *            le «Stage» principale de la fenêtre
	 */
    @Override
    public void start(Stage primaryStage)
    {
            primaryStage.setTitle(AppFrame.TITLE);
            primaryStage.setX(AppFrame.DEFAULT_LOCATION_X);
            primaryStage.setX(AppFrame.DEFAULT_LOCATION_Y);
            primaryStage.setHeight(AppFrame.HEIGHT_APPLICATION);
            primaryStage.setWidth(AppFrame.WIDTH_APPLICATION);
            primaryStage.setResizable(false);

            if (MainScene.SetInstance(primaryStage, new BorderPane()))
            {
                    primaryStage.setScene(MainScene.GetInstance());
            }
            primaryStage.show();

    }
}
package chatInterface;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * La classe AppFrame est une classe utilis�e pour construire la fen�tre principale de l'application.
 * La classe h�rite de Application.
 * @author Mathieu Moreault
 * @author Anthony Lavall�e
 * @author �lise Leclerc
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
	 * La m�thode qui affiche la fen�tre selon la position donn�e et le titre donn�.
	 * 
	 * @param Stage
	 *            le �Stage� principale de la fen�tre
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
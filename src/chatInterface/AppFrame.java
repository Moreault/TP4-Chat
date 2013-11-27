package chatInterface;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AppFrame extends Application
{
	private static final String TITLE = "Chat Rafiki";
	private static final double DEFAULT_LOCATION_X = 100;
	private static final double DEFAULT_LOCATION_Y = 100;
	private static final double HEIGHT_APPLICATION = 525;
	private static final double WIDTH_APPLICATION = 500;
	
	
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

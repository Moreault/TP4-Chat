package chatInterface;

import javafx.application.Application;


/**
 * La classe MainApp est une classe utilisée pour lancer l'application à partir de la classe AppFrame.
 * Elle lance l'interface de l'application.
 * @author Mathieu Moreault
 * @author Anthony Lavallée
 * @author Élise Leclerc
 * @author Pierre Marion
 */
public class MainApp 
{
	/**
	 * Le «main» qui lance l'application à partir de la classe AppFrame.
	 * 
	 * @param String
	 *            arguments
	 * @see AppFrame
	 */
	public static void main(String[] args)
	{
		Application.launch(AppFrame.class, args);
	}
}

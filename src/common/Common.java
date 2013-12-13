package common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Cette classe regroupe les fonctions non-sp�cifiques � aucune autre classe qui
 * sont utilis�es par le client ET par le server.
 * @author Mathieu Moreault
 * @author Anthony Lavall�e
 * @author �lise Leclerc
 * @author Pierre Marion
 */

public class Common {
	public static final String ERROR_GENERIC = "Erreur: L'application a rencontr� une erreur";
	public static final int DEFAULT_PORT = 5000;
	public static final String MESSAGE_CONNECTED = "s'est connect�.";
	public static final String TAG_MESSAGE = "message";
	public static final String TAG_TEXT = "text";
	public static final String TAG_TYPE = "type";
	public static final String TAG_ROOM = "room";
	public static final String TAG_USERNAME = "username";
	public static final String TAG_USERLIST = "userlist";
	public static final String DEFAULT_ROOM = "Default";
	public static final int LOGOUT = 0;
	public static final int LOGGEDIN = 1;
	public static final int MESSAGE = 2;
	public static final int ACTION = 3;
	public static final int SERVER_MESSAGE = 4;
	public static final int CONNECTION_REFUSED = 5;
	public static final int NICKCHANGE = 6;
	public static final String COMMAND_ACTION = "/me";
	public static final String COMMAND_LOGOUT = "/logout";
	public static final String DEFAULT_NAME = "Anon";
	private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm");
	
	/**
	 * Cette fonction valide le port avant de l'assigner au server. 
	 * � noter que plusieurs ports sont r�serv�s.
	 * @param _port Le port � valider. Doit �tre de 1 � 65535 inclusivement.
	 * @return True si le port est valide, False sinon.
	 */
	static public boolean estUnPortValide(int _port)
	{
		//Le port 1 est r�serv�
		if (_port > 0 && _port <= 65535)
		{
			return true;
		}
		return false;
	}
	/**
	 * Fonction qui formatte les noms d'utilisateur. Plac� ici pour changer ais�ment
	 * le formattage.
	 * @userName _port Le nom d'utilisateur � formatter.
	 * @return Le nom formatt�.
	 */
	static public String formatName(String userName)
	{
		return "<" + userName + ">";
	}
	public static String timeStamp()
	{
		return "[" + TIMESTAMP_FORMAT.format(new Date()) + "]";
	}
}

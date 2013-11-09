package common;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * La classe "Message."
 * Il s'agit d'un message envoyé par un client, lu et renvoyé par le server. Nous avons
 * apparement besoin de la serialization mais je ne comprends toujours pas exactement
 * pourquoi.
 * @author Mathieu Moreault
 * @author Anthony Lavallée
 * @author Élise Leclerc
 * @author Pierre Marion
 */
public class Message implements Serializable 
{

	protected static final long serialVersionUID = 1112122200L;
	static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm");
	
	public static final int LOGOUT = 0;
	public static final int LOGGEDIN = 1;
	public static final int MESSAGE = 2;
	public static final int ACTION = 3;
	private int typeMessage;
	private String textMessage;
	
	/**
	 * Le constructeur de la classe Message
	 * @param _typeMessage Le type de message: LOGOUT = 0, LOGGEDIN = 1, MESSAGE = 2, ACTION = 3
	 * @param _textMessage La partie texte du message envoyé
	 */
	public Message(int _typeMessage, String _textMessage) {
		this.typeMessage = _typeMessage;
		this.textMessage = _textMessage;
	}	
	/**
	 * La méthode getter du type du message
	 * @return Le type du message: LOGOUT = 0, LOGGEDIN = 1, MESSAGE = 2, ACTION = 3
	 */
	public int getType() 
	{
		return typeMessage;
	}
	/**
	 * La méthode getter du texte du message
	 * @return La partie texte du message
	 */
	public String getMessage() 
	{
		return textMessage;
	}
	public static String timeStamp()
	{
		return "[" + TIMESTAMP_FORMAT.format(new Date()) + "]";
	}
}

package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import common.Common;


/**
 * Le ServerListener est un thread du côté du client qui écoute le server.
 * Cette classe hérite de classe Thread de Java.
 * @author Mathieu Moreault
 * @author Anthony Lavallée
 * @author Élise Leclerc
 * @author Pierre Marion
 */

public class ServerListener extends Thread 
{
	//Constantes
	private static final String ERROR_SERVER_CLOSED_CONNECTION = "Erreur: Le server a fermé la connexion.";
	//Variables
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Client client;
	
	public ServerListener(Client _client, ObjectInputStream _sInput, ObjectOutputStream _sOutput)
	{
		super();
		this.client = _client;
		this.sInput = _sInput;
		this.sOutput = _sOutput;
	}
	
	/**
	 * Cette méthode écoute le server et attends des messages puis les affiche
	 * lorsqu'ils arrivent.
	 */
	public void run()
	{
		//Nous voulons que cette méthode roule sans arrêt, donc while (true)
		while (true)
		{
			try
			{
				//C'est cette ligne qui reçoit le message XML envoyé par le server
				String msg = this.sInput.readObject().toString();
				//Faire un SAXBuilder
				SAXBuilder builder = new SAXBuilder();
				//Nous avons une string (message) que nous devons traiter comme du XML
				Document document = builder.build(new StringReader(msg));
				//Extrait la racine du document xml <message>
				Element root = document.getRootElement();
				//Extraire la partie texte du message
				String user = root.getChildText(Common.TAG_USERNAME);
				String text = root.getChildText(Common.TAG_TEXT);
				int msgType = Integer.parseInt(root.getChildText(Common.TAG_TYPE));
				String msgRoom = root.getChildText(Common.TAG_ROOM);		
				String finalMsg = new String(text);
				if (msgType == Common.MESSAGE)
				{
					finalMsg = Common.formatName(user) + " " + finalMsg;
					if (client.getTimestamps())
					{
						finalMsg = Common.timeStamp() + " " + finalMsg;
					}
					System.out.print(finalMsg + "\n");
				}
				else if (msgType == Common.ACTION)
				{
					finalMsg = "* " + user + " " + finalMsg;
					if (client.getTimestamps())
					{
						finalMsg = Common.timeStamp() + " " + finalMsg;
					}
					System.out.print(finalMsg + "\n");
				}
				else if (msgType == Common.SERVER_MESSAGE)
				{
					if (client.getTimestamps())
					{
						finalMsg = Common.timeStamp() + " " + finalMsg;
					}
					client.clientEcho(finalMsg);
				}
				else if (msgType == Common.LOGGEDIN)
				{
					finalMsg = "-> " + finalMsg;
					if (client.getTimestamps())
					{
						finalMsg = Common.timeStamp() + " " + finalMsg;
					}
					System.out.print(finalMsg  + " " + Common.MESSAGE_CONNECTED + "\n");
				}
				
				//À FAIRE : Lorsque nous aurons l'interface graphique, ces messages
				//s'afficheront dedans, pas dans la console. Duh.
				
				System.out.print("> ");
			}
			catch (IOException e)
			{
				client.clientEcho(ERROR_SERVER_CLOSED_CONNECTION);
				//Faire terminer la boucle
				break;
			} 
			catch (ClassNotFoundException e2) 
			{
				client.clientEcho(Common.ERROR_GENERIC);
			} 
			catch (JDOMException e) 
			{
				e.printStackTrace();
			}
		}
	}

}

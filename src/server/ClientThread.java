package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.Socket;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import common.Common;
/**
 * Nous devrons avoir un ClientThread par client. 
 * Cette classe h�rite de la classe Thread de Java.
 * @author Mathieu Moreault
 * @author Anthony Lavall�e
 * @author �lise Leclerc
 * @author Pierre Marion
 */
public class ClientThread extends Thread
{
	private static final String ERROR_COULD_NOT_SEND_MESSAGE = "Erreur: Le message n'a pas pu �tre envoy� �";
	private static final String ERROR_COULD_NOT_CREATE_THREAD = "Erreur: Le thread du client n'a pas pu �tre cr��.";
	private static final String ERROR_CLOSING_STREAMS = "Erreur: L'application doit fermer les streams d'entr�e/sortie.";
	private static final String INFO_CLIENT_DISCONNECTED = "Un client s'est d�connect�.";
	//L'information relative au client connect�
	private String userName;
	private int clientID;
	//Le socket utilis� pour parler
	private Socket socket;
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	//Le server
	private Server server;
	//Les messages que le client recevra
	private String message;

	/**
	 * Le constructeur de la classe ClientThread. Le nom de l'utilisateur est lu
	 * plus loin dans le constructeur avec le ObjectInputStream.
	 * @param _server Le server courant
	 * @param _socket Le socket qui sera utilis� par le client pour communiquer.
	 * @param _clientID L'ID unique du client qui se connecte
	 */
	public ClientThread(Server _server, Socket _socket, int _clientID) 
	{
		this.socket = _socket;
		this.server = _server;
		//On incr�mente la variable currentID et on l'assigne � ce client
		this.clientID = _clientID;
		//Essaie de cr�er les stream d'Input et d'Output
		try
		{
			this.sOutput = new ObjectOutputStream(socket.getOutputStream());
			this.sInput  = new ObjectInputStream(socket.getInputStream());
			//Le userName est dans le stream d'Input en XML
			String xml = new String((String) sInput.readObject());
			//Faire un SAXBuilder
			SAXBuilder builder = new SAXBuilder();
			//Nous avons une string (message) que nous devons traiter comme du XML
			Document document = builder.build(new StringReader(xml));
			//Extrait la racine du document xml <username>
			this.userName = document.getRootElement().getText();
		}
		catch (IOException e)
		{
			Server.serverEcho(ERROR_COULD_NOT_CREATE_THREAD);
			//Annule la cr�ation du thread
			return;
		}
		catch (ClassNotFoundException e2)
		{
			Server.serverEcho(ERROR_COULD_NOT_CREATE_THREAD);
			return;
		} 
		catch (JDOMException e) 
		{
			e.printStackTrace();
		}
	}
	/**
	 * Cette m�thode remplace celle de la classe Thread de Java. Elle continuera
	 * de s'ex�cuter dans une boucle infinie tant que le client restera connect�.
	 */
	public void run()
	{
		boolean connected = true;
		while (connected)
		{
			try
			{
				this.message = (String) this.sInput.readObject();
				//Faire un SAXBuilder
				SAXBuilder builder = new SAXBuilder();
				//Nous avons une string (message) que nous devons traiter comme du XML
				Document document = builder.build(new StringReader(this.message));
				//Extrait la racine du document xml <message>
				Element root = document.getRootElement();
				//Extraire la partie texte du message
				String text = root.getChildText(Common.TAG_TEXT);
				int msgType = Integer.parseInt(root.getChildText(Common.TAG_TYPE));
				String msgRoom = root.getChildText(Common.TAG_ROOM);
				if (msgType == Common.LOGOUT)
				{
					//FAIRE UN BROADCAST POUR ANNONCER LE LOGOUT D'UN CLIENT � TOUS LES CLIENTS
				}
				else if (msgType == Common.LOGGEDIN)
				{
					//FAIRE UN BROADCAST POUR ANNONCER LE LOGIN D'UN CLIENT � TOUS LES CLIENTS
				}
				else if (msgType == Common.MESSAGE)
				{
					server.broadcast(text, this.userName, msgType, msgRoom);
				}
				else if (msgType == Common.ACTION)
				{
					//FAIRE UN BROADCAST POUR AFFICHER UNE ACTION � TOUS LES CLIENTS
				}
			}
			catch (IOException e)
			{
				Server.serverEcho(INFO_CLIENT_DISCONNECTED);
				break;
			}
			catch (ClassNotFoundException e2)
			{
				break;
			} 
			catch (JDOMException e) 
			{
				e.printStackTrace();
			}
		}
		//La boucle est termin�e, s'enlever soi-m�me de la liste des clients
		this.server.disconnectClient(clientID);
		this.closeStreams();
	}
	/**
	 * Cette m�thode envoit un message au client.
	 * @return True si le message a �t� envoy�, False sinon
	 */
	public boolean sendMessage(String msg)
	{
		//V�rifier si le client est toujours connect� au server
		if (!socket.isConnected())
		{
			this.closeStreams();
			return false;
		}
		//On tente d'�crire le message dans l'Output stream
		try
		{
			sOutput.writeObject(msg);
		}
		catch (IOException e)
		{
			Server.serverEcho(ERROR_COULD_NOT_SEND_MESSAGE + " " + this.userName);
		}
		return true;
	}
	/**
	 * M�thode qui retourne l'ID du client courant
	 * @return L'ID du client courant
	 */
	public int getClientID()
	{
		return this.clientID;
	}
	/**
	 * Cette m�thode tente de fermer les streams d'Input, d'Output et la connexion au socket.
	 */
	public void closeStreams()
	{
		try
		{
			this.sInput.close();
			this.sOutput.close();
			this.socket.close();
		}
		catch (Exception e)
		{
			Server.serverEcho(ERROR_CLOSING_STREAMS);
		}
	}
	public String getUsername()
	{
		return userName;
	}
}

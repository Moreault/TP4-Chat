package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import common.Common;
/**
 * Nous devrons avoir un ClientThread par client. 
 * Cette classe hérite de la classe Thread de Java.
 * @author Mathieu Moreault
 * @author Anthony Lavallée
 * @author Élise Leclerc
 * @author Pierre Marion
 */
public class ClientThread extends Thread
{
	private static final String ERROR_COULD_NOT_SEND_MESSAGE = "Erreur: Le message n'a pas pu être envoyé à";
	private static final String ERROR_COULD_NOT_CREATE_THREAD = "Erreur: Le thread du client n'a pas pu être créé.";
	private static final String ERROR_CLOSING_STREAMS = "Erreur: L'application doit fermer les streams d'entrée/sortie.";
	private static final String INFO_CLIENT_DISCONNECTED = "s'est déconnecté.";
	private static final int MAXIMUM_NAME_LENGTH = 10;
	private static final int MINIMUM_NAME_LENGTH = 3;
	//L'information relative au client connecté
	private String userName;
	private int clientID;
	//Le socket utilisé pour parler
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
	 * @param _socket Le socket qui sera utilisé par le client pour communiquer.
	 * @param _clientID L'ID unique du client qui se connecte
	 */
	public ClientThread(Server _server, Socket _socket, int _clientID) 
	{
		this.socket = _socket;
		this.server = _server;
		//On incrémente la variable currentID et on l'assigne à ce client
		this.clientID = _clientID;
		//Essaie de créer les stream d'Input et d'Output
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
			String name = document.getRootElement().getText();
			if (!this.validateName(name))
			{
				this.userName = Common.DEFAULT_NAME + this.clientID;
			}
			else
			{
				this.userName = name;
			}
		}
		catch (IOException e)
		{
			Server.serverEcho(ERROR_COULD_NOT_CREATE_THREAD);
			//Annule la création du thread
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
	 * Cette méthode remplace celle de la classe Thread de Java. Elle continuera
	 * de s'exécuter dans une boucle infinie tant que le client restera connecté.
	 */
	@Override
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
					server.broadcast("-> " + this.getUsername() + " " + INFO_CLIENT_DISCONNECTED, Common.SERVER_MESSAGE);
					server.broadcastUserlist(this);
				}
				else if (msgType == Common.LOGGEDIN)
				{
					//FAIRE UN BROADCAST POUR ANNONCER LE LOGIN D'UN CLIENT À TOUS LES CLIENTS
				}
				else if (msgType == Common.MESSAGE)
				{
					server.broadcast(text, this.userName, msgType, msgRoom);
				}
				else if (msgType == Common.ACTION)
				{
					//FAIRE UN BROADCAST POUR AFFICHER UNE ACTION À TOUS LES CLIENTS
				}
				else if (msgType == Common.NICKCHANGE)
				{
					server.broadcast("-> " + this.getUsername() + " a changé le nom", Common.SERVER_MESSAGE);
					this.userName = text;
					server.broadcastUserlist(this);
				}
			}
			catch (IOException e)
			{
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
		//La boucle est terminée, s'enlever soi-même de la liste des clients
		this.server.disconnectClient(clientID);
		this.closeStreams();
	}
	/**
	 * Cette méthode envoit un message au client.
	 * @return True si le message a été envoyé, False sinon.
	 */
	public boolean sendMessage(String msg)
	{
		//Vérifier si le client est toujours connecté au server
		if (!socket.isConnected())
		{
			this.closeStreams();
			return false;
		}
		//On tente d'écrire le message dans l'Output stream
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
	 * Méthode qui retourne l'ID du client courant
	 * @return L'ID du client courant
	 */
	public int getClientID()
	{
		return this.clientID;
	}
	/**
	 * Cette méthode tente de fermer les streams d'Input, d'Output et la connexion au socket.
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
	/**
	 * Cette méthode extrait la liste de noms des clients connectés et l'envoie à un client
	 * en format XML.
	 */
	public void sendUserlist(ArrayList<ClientThread> clientList)
	{
		String xml = new String("<" + Common.TAG_USERLIST + ">");
		for (int i = clientList.size() - 1; i >= 0; i--)
		{
			xml = xml + "<" + Common.TAG_USERNAME + ">" + clientList.get(i).getUsername() + "</" + Common.TAG_USERNAME + ">";
		}
		xml = xml + "</" + Common.TAG_USERLIST + ">";
		this.sendMessage(xml);
	}
	/**
	 * Cette méthode valide un nom d'utilisateur passé en paramètres.
	 * @param name Le nom à valider.
	 * @return True si il est valide, false sinon.
	 */
	public boolean validateName(String name)
	{
		//Doit être entre 3 et 10 caractères de long
		if (!(name.length() <= MAXIMUM_NAME_LENGTH && name.length() >= MINIMUM_NAME_LENGTH))
		{
			return false;
		}
		//Doit être composé de lettres et de chiffres seulement
		for (int i = 0; i < name.length(); i++)
		{
			if (!Character.isLetterOrDigit(name.charAt(i)))
			{
				return false;
			}
		}
		//Doit être un nom unique
		for (int i = 0; i < server.getClientList().size(); i++)
		{
			if (server.getClientList().get(i).getUsername().equalsIgnoreCase(name))
			{
				return false;
			}
		}
		return true;
	}
}

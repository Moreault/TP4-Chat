package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Message;
/**
 * Nous devrons avoir un ClientThread par client. 
 * Cette classe h�rite de la classe Thread de Java.
 * @author Mathieu Moreault
 * @author Anthony Lavall�e
 */
public class ClientThread extends Thread
{
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
	private Message message;

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
			//Le userName est dans le stream d'Input
			this.userName = (String) sInput.readObject();
			//� FAIRE : M�THODE POUR AFFICHER QUE LE CLIENT S'EST CONNECT�
		}
		catch (IOException e)
		{
			//� FAIRE : M�ME M�THODE QU'EN HAUT, AFFICHE UNE ERREUR
			//Annule la cr�ation du thread
			return;
		}
		catch (ClassNotFoundException e2)
		{
			//� FAIRE : M�ME M�THODE QU'EN HAUT, AFFICHE UNE ERREUR G�N�RIQUE
			return;
		}
	}
	/**
	 * Cette m�thode continuera de s'ex�cuter tant que le client restera connect�.
	 */
	public void connection()
	{
		boolean connected = true;
		while (connected)
		{
			try
			{
				this.message = (Message) sInput.readObject();
			}
			catch (IOException e)
			{
				//� FAIRE : AFFICHE UNE ERREUR
				break;
			}
			catch (ClassNotFoundException e2)
			{
				break;
			}
			//Il faut extraire la partie texte du message
			String text = message.getMessage();
			if (message.getType() == Message.LOGOUT)
			{
				//FAIRE UN BROADCAST POUR ANNONCER LE LOGOUT D'UN CLIENT � TOUS LES CLIENTS
			}
			else if (message.getType() == Message.LOGGEDIN)
			{
				//FAIRE UN BROADCAST POUR ANNONCER LE LOGIN D'UN CLIENT � TOUS LES CLIENTS
			}
			else if (message.getType() == Message.MESSAGE)
			{
				//FAIRE UN BROADCAST POUR AFFICHER UN MESSAGE STANDARD � TOUS LES CLIENTS
			}
			else if (message.getType() == Message.ACTION)
			{
				//FAIRE UN BROADCAST POUR AFFICHER UNE ACTION � TOUS LES CLIENTS
			}
		}
		//La boucle est termin�e, s'enlever soi-m�me de la liste des clients
		this.server.disconnectClient(clientID);
		this.closeStreams();
	}
	/**
	 * Cette m�thode envoit un message au client
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
			//Informer le client que son message ne s'est pas rendu
			//� FAIRE
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
			//AFFICHER UNE ERREUR G�N�RIQUE
		}
	}
}

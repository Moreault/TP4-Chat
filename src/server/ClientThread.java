package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Message;
/**
 * Nous devrons avoir un ClientThread par client. 
 * Cette classe hérite de la classe Thread de Java.
 * @author Mathieu Moreault
 * @author Anthony Lavallée
 */
public class ClientThread extends Thread
{
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
	private Message message;

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
			//Le userName est dans le stream d'Input
			this.userName = (String) sInput.readObject();
			//À FAIRE : MÉTHODE POUR AFFICHER QUE LE CLIENT S'EST CONNECTÉ
		}
		catch (IOException e)
		{
			//À FAIRE : MÊME MÉTHODE QU'EN HAUT, AFFICHE UNE ERREUR
			//Annule la création du thread
			return;
		}
		catch (ClassNotFoundException e2)
		{
			//À FAIRE : MÊME MÉTHODE QU'EN HAUT, AFFICHE UNE ERREUR GÉNÉRIQUE
			return;
		}
	}
	/**
	 * Cette méthode continuera de s'exécuter tant que le client restera connecté.
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
				//À FAIRE : AFFICHE UNE ERREUR
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
				//FAIRE UN BROADCAST POUR ANNONCER LE LOGOUT D'UN CLIENT À TOUS LES CLIENTS
			}
			else if (message.getType() == Message.LOGGEDIN)
			{
				//FAIRE UN BROADCAST POUR ANNONCER LE LOGIN D'UN CLIENT À TOUS LES CLIENTS
			}
			else if (message.getType() == Message.MESSAGE)
			{
				//FAIRE UN BROADCAST POUR AFFICHER UN MESSAGE STANDARD À TOUS LES CLIENTS
			}
			else if (message.getType() == Message.ACTION)
			{
				//FAIRE UN BROADCAST POUR AFFICHER UNE ACTION À TOUS LES CLIENTS
			}
		}
		//La boucle est terminée, s'enlever soi-même de la liste des clients
		this.server.disconnectClient(clientID);
		this.closeStreams();
	}
	/**
	 * Cette méthode envoit un message au client
	 * @return True si le message a été envoyé, False sinon
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
			//Informer le client que son message ne s'est pas rendu
			//À FAIRE
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
			//AFFICHER UNE ERREUR GÉNÉRIQUE
		}
	}
}

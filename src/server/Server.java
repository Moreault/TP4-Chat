package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import common.Common;
import common.Message;

/**
 * Le server re�oit les connexions et les message du ou des clients et les envoit � tous les
 * clients connect�s.
 * @author Mathieu Moreault
 * @author Anthony Lavall�e
 */

public class Server 
{
	//Messages d'erreur
	static final String ERROR_CLIENT_DISCONNECTED = "a �t� d�connect� par manque de r�ponse.";
	static final String ERROR_CANNOT_CONNECT_CLIENT = "Erreur: la connexion au client a �chou�.";
	static final String ERROR_CANNOT_CREATE_SERVERSOCKET = "Erreur: la cr�ation du Server Socket � �chou�.";
	static final String ERROR_GENERIC = "Erreur: L'application a retourn� une erreur";
	static final String ERROR_INVALID_PORT = "Port invalide: Veuillez en choisir un entre 1 et 65535";
	static final int DEFAULT_PORT = 5000;
	//Cette option peut �tre chang�e si l'administrateur ne veut pas voir les timestamps
	static final boolean TIMESTAMPS = true;
	//La liste des clients connect�s au server
	private ArrayList<ClientThread> clientList;
	//Le port utilis� par le server. Les clients doivent utiliser le m�me
	private int port;
	//Cette variable s'incr�mente � chaque fois qu'un client se connecte
	//pour s'assurer que chacun aie un ID unique
	private static int currentID;
	//Variable qui indique que le server �coute
	private boolean listening;

	/**
	 * La m�thode main qui d�marre le server. Si aucun port n'est sp�cifi� � l'ouverture,
	 * le port par d�faut (DEFAULT_PORT) est choisit.
	 * @param args S'attends � recevoir le port en param�tres lors de l'ouverture.
	 */
	public static void main(String[] args) 
	{
		int tempPort = DEFAULT_PORT;
		if (args.length == 1)
		{
			try
			{
				tempPort = Integer.parseInt(args[0]);
			}
			catch (Exception e)
			{
				serverEcho(ERROR_INVALID_PORT);
			}
		}
		Server server = new Server(tempPort);
		server.start();
	}
	
	/**
	 * Le constructeur du server. Si le port fournit n'est pas valide, un port par d�faut sera choisit.
	 * @param _port Le port de connexion que les clients doivent utiliser pour s'y connecter.
	 */
	public Server(int _port) {
		this.setPort(_port);
	}
	/**
	 * Cette m�thode change la variable port du server.
	 * @param _port Le port � entrer dans les variables du server
	 */
	public void setPort(int _port)
	{
		if (Common.estUnPortValide(_port))
		{
			this.port = _port;
		}
	}
	/**
	 * Cette m�thode incr�mente l'ID courant pour qu'il demeure unique pour chaque
	 * client connect� au server.
	 * @return Le nouveau ID courant
	 */
	private int incrID()
	{
		currentID++;
		return currentID;
	}
	/**
	 * Cette m�thode commence l'�coute du server.
	 */
	private void start()
	{
		listening = true;
		try
		{
			//La cr�ation du socket utilis� par le server
			ServerSocket serverSocket = new ServerSocket(port);
			//Une boucle qui durera jusqu'� la fermeture du server
			while (listening)
			{
				//On accepte la connexion d'un client
				Socket clientSocket = serverSocket.accept();
				//Si le server a �t� arr�t�, on brise la boucle
				if (!listening)
				{
					break;
				}
				//Fait un nouveau thread et l'ajoute � la liste de client connect�s
				ClientThread thread = new ClientThread(this, clientSocket, incrID());
				clientList.add(thread);
				thread.start();
			}
			//On a tent� de dire au server d'arr�ter l'�coute
			try
			{
				serverSocket.close();
				for (int i = 0; i < clientList.size(); i++)
				{
					ClientThread tempThread = clientList.get(i);
					tempThread.closeStreams();
				}
			}
			catch (Exception e)
			{
				serverEcho(ERROR_CANNOT_CONNECT_CLIENT);
			}
		}
		catch (Exception e)
		{
			serverEcho(ERROR_CANNOT_CREATE_SERVERSOCKET);
		}
	}
	/**
	 * Cette m�thode arr�te le server.
	 */
	private void stop()
	{
		listening = false;
		//Il faut que le server se connecte � lui-m�me en tant que client
		//pour pouvoir sortir
		try
		{
			new Socket("localhost", port);
		}
		catch (Exception e)
		{
			serverEcho(ERROR_GENERIC);
		}
	}
	/**
	 * Affiche un message sur le server seulement. Utile pour les messages d'erreurs
	 * et les notifications en g�n�ral. Personne ne peut et ne devrait voir ce texte
	 * sauf les administrateurs du server.
	 * @param msg Le message � afficher
	 * @see Constante TIMESTAMPS pour afficher ou non le temps avant le message.
	 */
	static private void serverEcho(String msg)
	{
		String finalmsg = new String(msg);
		if (TIMESTAMPS)
		{
			finalmsg = Message.timeStamp() + " " + finalmsg;
		}
		System.out.println(finalmsg);
	}
	/**
	 * Affiche un message sur le server seulement. Utile pour les messages d'erreurs
	 * et les notifications en g�n�ral. Personne ne peut et ne devrait voir ce texte
	 * sauf les administrateurs du server.
	 * @param msg Le message � envoyer
	 * @see Constante TIMESTAMPS pour afficher ou non le temps avant le message.
	 */
	private synchronized void broadcast(String msg)
	{
		String finalmsg = new String(msg + "\n");
		if (TIMESTAMPS)
		{
			finalmsg = Message.timeStamp() + " " + msg;
		}
		System.out.print(finalmsg);
		//Il faut faire une boucle � l'envers pour enlever un client
		//si un d'eux � �t� d�connect�
		for (int i = clientList.size(); i >= 0; i--)
		{
			ClientThread tempThread = clientList.get(i);
			//On essaie d'envoyer le message au client, si �a ne fonctionne pas,
			//il a �t� d�connect�
			if (!tempThread.sendMessage(finalmsg))
			{
				clientList.remove(i);
				serverEcho(tempThread.getName() + " " + ERROR_CLIENT_DISCONNECTED);
				//� FAIRE : POSSIBLEMENT BROADCASTER LE LOGOUT OU LE TIMEOUT SUR LE CHAT
			}
		}
	}
	/**
	 * Cette m�thode force la d�connexion d'un client au server. 
	 * Utilis� dans le logout ou si un client s'est d�connect� suite � une erreur.
	 * @param _clientID L'ID unique du client connect�
	 */
	synchronized void disconnectClient(int _clientID)
	{
		//Il faut passer chacun des client dans la liste des threads pour trouver le bon
		for (int i = 0; i < clientList.size(); i++)
		{
			ClientThread tempThread = clientList.get(i);
			if (tempThread.getClientID() == _clientID)
			{
				//Nous l'avons trouv�. Aucune raison de continuer la recherche.
				clientList.remove(i);
				return;
			}
		}
	}
}

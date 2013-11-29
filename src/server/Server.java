package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import common.Common;

/**
 * Le server re�oit les connexions et les message du ou des clients et les envoit � tous les
 * clients connect�s.
 * @author Mathieu Moreault
 * @author Anthony Lavall�e
 * @author �lise Leclerc
 * @author Pierre Marion
 */

public class Server 
{
	//Messages d'erreur
	private static final String ERROR_CLIENT_DISCONNECTED = "a �t� d�connect� par manque de r�ponse.";
	private static final String ERROR_CANNOT_CONNECT_CLIENT = "Erreur: la connexion au client a �chou�.";
	private static final String ERROR_CANNOT_CREATE_SERVERSOCKET = "Erreur: la cr�ation du Server Socket � �chou�.";
	private static final String ERROR_INVALID_PORT = "Port invalide: Veuillez en choisir un entre 1 et 65535";
	//Cette option peut �tre chang�e si l'administrateur ne veut pas voir les timestamps
	private static final boolean TIMESTAMPS = true;
	//La liste des clients connect�s au server
	private ArrayList<ClientThread> clientList = new ArrayList<ClientThread>();
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
		int tempPort = Common.DEFAULT_PORT;
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
	 * Cette m�thode commence l'�coute du server pour les connexions de clients.
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
				//Broadcast pour dire qu'un client s'est connect�
				this.broadcast(thread.getUsername(), Common.LOGGEDIN);
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
			serverEcho(Common.ERROR_GENERIC);
		}
	}
	/**
	 * Affiche un message sur le server seulement. Utile pour les messages d'erreurs
	 * et les notifications en g�n�ral. Personne ne peut et ne devrait voir ce texte
	 * sauf les administrateurs du server.
	 * @param msg Le message � afficher
	 * @see Constante TIMESTAMPS pour afficher ou non le temps avant le message.
	 */
	static public void serverEcho(String msg)
	{
		String finalmsg = new String(msg);
		if (TIMESTAMPS)
		{
			finalmsg = Common.timeStamp() + " " + finalmsg;
		}
		System.out.println(finalmsg);
	}
	/**
	 * Envoie un message avec nom d'utilisateur � tous les clients connect�s.
	 * @param msgUsername Le nom de l'utilisateur qui envoit le message.
	 * @param msgText Le message � envoyer
	 * @param msgType Le type de message � envoyer
	 * @param msgRoom Le cannal dans lequel le message est envoy�
	 */
	public void broadcast(String msgText, String msgUsername, int msgType, String msgRoom)
	{
		this.broadcastFinal(msgText, msgUsername, msgType, "Default");
	}
	/**
	 * Envoie un message sans nom d'utilisateur � tous les clients connect�s.
	 * @param msg Le message � envoyer
	 */
	public void broadcast(String msgText, int msgType)
	{
		this.broadcastFinal(msgText, "Server", msgType, "Default");
	}
	/**
	 * Envoit un message � tous les clients connect�s
	 * @param msg Le message � envoyer.
	 * @param user Le nom de l'utilisateur qui envoit le message. "Server" ne met pas de nom.
	 * @see Constante TIMESTAMPS pour afficher ou non le temps avant le message.
	 */
	private synchronized void broadcastFinal(String msgText, String msgUserName, int msgType, String msgRoom)
	{
		//� FAIRE: ENVOYER DU XML AUX CLIENTS
		//Dans un format du genre <message><username></username><text></text><type></type><room></room></message>
		//LE CLIENT DEVRA ENSUITE INTERPR�TER CELA COMME UN DOCUMENT XML
		//Y LIRE LE NOM DE L'ENVOYEUR, LE CANNAL D'ENVOIE, ET LE TYPE DE MESSAGE, ETC...
		
		String xml = new String("<" + Common.TAG_MESSAGE + ">" 
		+ "<" + Common.TAG_USERNAME + ">" + msgUserName + "</" + Common.TAG_USERNAME + ">"
		+ "<" + Common.TAG_TEXT + ">" + msgText + "</" + Common.TAG_TEXT + ">"
		+ "<" + Common.TAG_TYPE + ">" + msgType + "</" + Common.TAG_TYPE + ">"
		+ "</" + Common.TAG_MESSAGE + ">");
		
		String serverSideMsg = new String (msgText + "\n");
		if (!msgUserName.equalsIgnoreCase("Server"))
		{
			serverSideMsg = Common.formatName(msgUserName) + " " + serverSideMsg;
		}
		if (TIMESTAMPS)
		{
			serverSideMsg = Common.timeStamp() + " " + serverSideMsg;
		}
		System.out.print(serverSideMsg);
		//Il faut faire une boucle � l'envers pour enlever un client
		//si un d'eux � �t� d�connect�
		for (int i = clientList.size() - 1; i >= 0; i--)
		{
			ClientThread tempThread = clientList.get(i);
			//On essaie d'envoyer le message au client, si �a ne fonctionne pas,
			//il a �t� d�connect�
			if (!tempThread.sendMessage(xml))
			{
				clientList.remove(i);
				serverEcho(tempThread.getUsername() + " " + ERROR_CLIENT_DISCONNECTED);
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

package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import chatInterface.MainScene;

import common.Common;

/**
 * Le client envoit des messages au server et le server les envoit à tous les clients.
 * C'est l'interface utilisée par l'usager moyen pour utiliser l'application.
 * @author Mathieu Moreault
 * @author Anthony Lavallée
 * @author Élise Leclerc
 * @author Pierre Marion
 */

public class Client {
	//Les constantes
	private static final String ERROR_CANNOT_CONNECT_TO_SERVER = "Erreur: Le client n'a pas pu se connecter au server.";
	private static final String ERROR_CANNOT_CREATE_IOSTREAM = "Erreur: Le client n'a pas pu créer le stream d'entrée-sortie.";
	private static final String ERROR_CANNOT_CREATE_SOCKET = "Erreur: Le client n'a pas pu créer le socket.";
	private static final String ERROR_CANNOT_SEND_MESSAGE = "Erreur: Le message n'a pas pu être envoyé.";
	private static final String ERROR_DISCONNECTED_BECAUSE_OF_USERNAME = "Erreur: Vous avez été déconnecté pour votre nom d'utilisateur.";
	private static final String INFO_CONNECTION_SUCESS = "La conenxion avec le serveur a été établie:";
	
	//Les variables de stream (Input, Output et socket)
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;
	//Les informations relatives au server et au nom d'utilisateur
	private String myServer;
	private String myUsername;
	private int myPort;
	private MainScene myGUI;
	//Option de mettre les timestamps pour le client
	private final boolean timeStamps = true;
	

	public static void main(String[] args) 
	{
		//Cet objet client est seulement à utiliser pour les tests sans interface graphique
		Client client = new Client("Anon", "localhost", Common.DEFAULT_PORT);
		//On teste la connexion au server
		if (!client.start())
		{
			return;
		}
		//Le client attends les messages de l'utilisateur
		Scanner scanner = new Scanner(System.in);
		//Il faut faire une boucle infinie pour qu'il continue 
		//d'écouter les messages de l'utilisateur
		while (true)
		{
			System.out.print("> ");
			String userMsg = scanner.nextLine();
			//Envoie de message au server
			if (userMsg.equalsIgnoreCase(Common.COMMAND_LOGOUT))
			{
				client.sendMessage("", Common.LOGOUT, "");
				break;
			}
			else
			{
				client.sendMessage(userMsg, Common.MESSAGE, "Default");
			}
		}
		//Lorsque la boucle se termine, on se déconnecte
		client.disconnect();
	}
	/**
	 * Le constructeur de la classe Client.
	 * @param _myUsername Le nom d'utilisateur choisit.
	 * @param _myServer L'adresse du server (Utiliser "localhost" pour se connecter en local.)
	 * @param _myPort Le port doit être le même que celui du server pour se connecter.
	 */
	public Client(String _myUsername, String _myServer, int _myPort)
	{
		//Si possible, le server devrait faire une validation sur le nom d'utilisateur
		this.setUsername(_myUsername);
		this.setServer(_myServer);
		this.setPort(_myPort);
	}
	/**
	 * Le constructeur de la classe Client avec interface graphique
	 * @param _myUsername Le nom d'utilisateur choisit.
	 * @param _myServer L'adresse du server (Utiliser "localhost" pour se connecter en local.)
	 * @param _myPort Le port doit être le même que celui du server pour se connecter.
	 * @param _myGUI L'interface graphique utilisée par le client.
	 */
	public Client(String _myUsername, String _myServer, int _myPort, MainScene _myGUI)
	{
		this.setUsername(_myUsername);
		this.setServer(_myServer);
		this.setPort(_myPort);
		this.myGUI = _myGUI;
	}
	/**
	 * Change le nom de l'utilisateur.
	 * @param _myUsername Le nom d'utilisateur choisit.
	 */
	public void setUsername(String _myUsername)
	{
		this.myUsername = _myUsername;
	}
	/**
	 * Change l'adresse du server. Une reconnexion doit être effectuée pour se connecter
	 * à un nouveau server.
	 * @param _myServer L'adresse du server (Utiliser "localhost" pour se connecter en local.)
	 */
	public void setServer(String _myServer)
	{
		this.myServer = _myServer;
	}
	/**
	 * Change le port de connexion. Une reconnexion doit être effectuée pour se connecter
	 * à un nouveau server.
	 * @param _myPort Le port doit être le même que celui du server pour se connecter.
	 */
	public void setPort(int _myPort)
	{
		if (Common.estUnPortValide(_myPort))
		{
			this.myPort = _myPort;
		}
	}
	/**
	 * Démarre la communication entre le client et le server.
	 * @return True si la connexion s'est effectué avec succès, False s'il y a une erreur
	 */
	public boolean start()
	{
		//Tentative de se connecter au server
		try
		{
			this.socket = new Socket(this.myServer, this.myPort);
		}
		catch (Exception e)
		{
			this.clientEcho(ERROR_CANNOT_CREATE_SOCKET);
			//Il faut l'arrêter dans ce cas.
			return false;
		}
		//Informe le client que la connexion a été établie
		this.clientEcho(INFO_CONNECTION_SUCESS + " " + socket.getInetAddress() + ", " + socket.getPort() + ")");
	
		//Il faut créer les stream d'entré-sortie
		try
		{
			this.sInput  = new ObjectInputStream(this.socket.getInputStream());
			this.sOutput = new ObjectOutputStream(this.socket.getOutputStream());
		}
		catch (IOException e)
		{
			this.clientEcho(ERROR_CANNOT_CREATE_IOSTREAM);
		}
		//Ceci créé un thread pour écouter le server
		new ServerListener(this, this.sInput, this.sOutput).start();
		//Nous devons ensuite envoyer notre nom d'utilisateur au server en format XML
		String xml = new String("<" + Common.TAG_USERNAME + ">" + this.myUsername + "</" + Common.TAG_USERNAME + ">");
		try
		{
			this.sOutput.writeObject(xml);
		}
		catch (IOException e)
		{
			this.clientEcho(ERROR_DISCONNECTED_BECAUSE_OF_USERNAME);
			this.disconnect();
			return false;
		}
		//Informer la méthode appelante que la connexion s'est effectué avec succès
		return true;
	}
	/**
	 * Force la déconnexion au server lorsqu'une erreur est rencontré et que la
	 * connexion doit être terminée. Les streams d'entrée-sortie sont fermés.
	 */
	public void disconnect()
	{
		try
		{
			if (this.sInput != null)
			{
				this.sInput.close();
			}
			if (this.sOutput != null)
			{
				this.sOutput.close();
			}
			if (this.socket != null)
			{
				this.socket.close();
			}
		}
		catch (Exception e)
		{
			this.clientEcho(Common.ERROR_GENERIC);
		}
	}
	/**
	 * Affiche un message que seulement le client peut voir. Cette méthode est
	 * utilisé pour les erreurs principalement. Elle est publique parce qu'elle
	 * est aussi utilisée par le ServerListener.
	 * @param msg Le message à afficher.
	 */
	public void clientEcho(String msg)
	{
		String finalmsg = "** " + msg;
		if (timeStamps)
		{
			finalmsg = Common.timeStamp() + " " + finalmsg;
		}
		//À FAIRE: Envoyer dans l'interface graphique
		System.out.println(finalmsg);
		
		
	}
	/**
	 * Cette méthode envoie un message au server pour être traité et envoyé aux
	 * autres clients connectés. Avant de l'envoyer, le message doit être formatté
	 * comme un document XML pouvant être lu convenablement par le server.
	 * @param msgText Le texte du message à envoyer
	 * @param msgType Le type du message en integer
	 * @param msgRoom Le cannal dans lequel le message est envoyé
	 */
	public void sendMessage(String msgText, int msgType, String msgRoom)
	{
		//La structure xml est <message><text></text><type></type><room></room></message>
		msgText = msgText.replace("<", "«");
		msgText = msgText.replace(">", "»");
		String xml = new String("<" + Common.TAG_MESSAGE + ">" 
		+ "<" + Common.TAG_TEXT + ">" + msgText + "</" + Common.TAG_TEXT + ">"
		+ "<" + Common.TAG_TYPE + ">" + msgType + "</" + Common.TAG_TYPE + ">"
		+ "<" + Common.TAG_ROOM + ">" + msgRoom + "</" + Common.TAG_ROOM + ">"
		+ "</" + Common.TAG_MESSAGE + ">");
		try
		{
			sOutput.writeObject(xml);
		}
		catch (IOException e)
		{
			this.clientEcho(ERROR_CANNOT_SEND_MESSAGE);
		}
	}
	public boolean getTimestamps()
	{
		return this.timeStamps;
	}
	public MainScene getGUI()
	{
		return this.myGUI;
	}
	
	public void updateNameUser(String newUser)
	{
		this.setUsername(newUser);
		
	}
}

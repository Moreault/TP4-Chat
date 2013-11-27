package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import common.Common;
import common.Message;

/**
 * Le client envoit des messages au server et le server les envoit � tous les clients.
 * C'est l'interface utilis�e par l'usager moyen pour utiliser l'application.
 * @author Mathieu Moreault
 * @author Anthony Lavall�e
 * @author �lise Leclerc
 * @author Pierre Marion
 */

public class Client {
	//Les constantes
	private static final String ERROR_CANNOT_CONNECT_TO_SERVER = "Erreur: Le client n'a pas pu se connecter au server.";
	private static final String ERROR_CANNOT_CREATE_IOSTREAM = "Erreur: Le client n'a pas pu cr�er le stream d'entr�e-sortie.";
	private static final String ERROR_CANNOT_SEND_MESSAGE = "Erreur: Le message n'a pas pu �tre envoy�.";
	
	//Les variables de stream (Input, Output et socket)
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;
	//Les informations relatives au server et au nom d'utilisateur
	private String myServer;
	private String myUsername;
	private int myPort;
	

	public static void main(String[] args) 
	{
		//Cet objet client est seulement � utiliser pour les tests sans interface graphique
		Client client = new Client("Anon", "localhost", Common.DEFAULT_PORT);
		//On teste la connexion au server
		if (!client.start())
		{
			return;
		}
		//Le client attends les messages de l'utilisateur
		Scanner scanner = new Scanner(System.in);
		//Il faut faire une boucle infinie pour qu'il continue 
		//d'�couter les messages de l'utilisateur
		while (true)
		{
			System.out.print("> ");
			String userMsg = scanner.nextLine();
			
			//Envoie de message au server
			if (userMsg.equalsIgnoreCase("LOGOUT"))
			{
				client.sendMessage(new Message(Message.LOGOUT, ""));
				break;
			}
			else
			{
				client.sendMessage(new Message(Message.MESSAGE, userMsg));
			}
		}
		//Lorsque la boucle se termine, on se d�connecte
		client.disconnect();
	}
	/**
	 * Le constructeur de la classe Client.
	 * @param _myUsername Le nom d'utilisateur choisit.
	 * @param _myServer L'adresse du server (Utiliser "localhost" pour se connecter en local.)
	 * @param _myPort Le port doit �tre le m�me que celui du server pour se connecter.
	 */
	public Client(String _myUsername, String _myServer, int _myPort)
	{
		//Si possible, le server devrait faire une validation sur le nom d'utilisateur
		this.setUsername(_myUsername);
		this.setServer(_myServer);
		this.setPort(_myPort);
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
	 * Change l'adresse du server. Une reconnexion doit �tre effectu�e pour se connecter
	 * � un nouveau server.
	 * @param _myServer L'adresse du server (Utiliser "localhost" pour se connecter en local.)
	 */
	public void setServer(String _myServer)
	{
		this.myServer = _myServer;
	}
	/**
	 * Change le port de connexion. Une reconnexion doit �tre effectu�e pour se connecter
	 * � un nouveau server.
	 * @param _myPort Le port doit �tre le m�me que celui du server pour se connecter.
	 */
	public void setPort(int _myPort)
	{
		if (Common.estUnPortValide(_myPort))
		{
			this.myPort = _myPort;
		}
	}
	/**
	 * D�marre la communication entre le client et le server.
	 * @return True si la connexion s'est effectu� avec succ�s, False s'il y a une erreur
	 */
	private boolean start()
	{
		//Tentative de se connecter au server
		try
		{
			this.socket = new Socket(this.myServer, this.myPort);
		}
		catch (Exception e)
		{
			//� FAIRE : MESSAGE D'ERREUR NE PEUT PAS SE CONNECTER AU SERVER
			//Il faut l'arr�ter dans ce cas.
			return false;
		}
		//� FAIRE : INFORMER QUE LA CONNEXION A BIEN EU LIEU AVEC LE SERVER
		//UTILISER socket.getInetAddress() ET socket.getPort() POUR MONTRER QUE �A
		//A BIEN �T� FAIT
		
		//Il faut cr�er les stream d'entr�-sortie
		try
		{
			this.sInput  = new ObjectInputStream(this.socket.getInputStream());
			this.sOutput = new ObjectOutputStream(this.socket.getOutputStream());
		}
		catch (IOException e)
		{
			//� FAIRE : AFFICHER UNE ERREUR DISANT QUE LE CLIENT N'A PAS PU
			//CR�ER DE STREAM D'ENTR�E-SORTIE
		}
		//Ceci cr�� un thread pour �couter le server
		new ServerListener(this.sInput, this.sOutput).start();
		//Nous devons ensuite envoyer notre nom d'utilisateur au server
		try
		{
			this.sOutput.writeObject(this.myUsername);
		}
		catch (IOException e)
		{
			//� FAIRE : AFFICHER UN MESSAGE D'ERREUR, D�CONNEXION/NOM REFUS�(?)
			this.disconnect();
			return false;
		}
		//Informer la m�thode appelante que la connexion s'est effectu� avec succ�s
		return true;
	}
	/**
	 * Force la d�connexion au server lorsqu'une erreur est rencontr� et que la
	 * connexion doit �tre termin�e. Les streams d'entr�e-sortie sont ferm�s.
	 */
	private void disconnect()
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
			//� FAIRE : AFFICHER UNE ERREUR G�N�RIQUE
		}
	}
	/**
	 * Affiche un message que seulement le client peut voir. Cette m�thode est
	 * utilis� pour les erreurs principalement. Elle est publique parce qu'elle
	 * est aussi utilis�e par le ServerListener.
	 * @param msg Le message � afficher.
	 */
	public void clientEcho(String msg)
	{
		String finalmsg = "(" + msg + ")";
		System.out.println(finalmsg);
	}
	/**
	 * Cette m�thode envoie un message au server pour �tre trait� et envoy� aux
	 * autres clients connect�s.
	 * @param message L'objet Message � envoyer.
	 */
	private void sendMessage(Message message)
	{
		try
		{
			sOutput.writeObject(message);
		}
		catch (IOException e)
		{
			//� FAIRE : AFFICHER UN MESSAGE D'ERREUR (LE MESSAGE N'A PAS PU �TRE ENVOY�)
		}
	}
	
	private static String convertMessageToXML(String message)
	{
		Serializer serializer = new Persister();
		
		File result = new File("example.xml");
		
		String line = null;

		
		try {
			serializer.write(message, result);
			BufferedReader br = new BufferedReader(new FileReader("example.xml"));
			line = br.readLine();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(line);
		return line;
	}
}

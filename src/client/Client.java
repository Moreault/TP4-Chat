package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.Common;

/**
 * Le client envoit des messages au server et le server les envoit à tous les clients.
 * C'est l'interface utilisée par l'usager moyen pour utiliser l'application.
 * @author Mathieu Moreault
 * @author Anthony Lavallée
 */

public class Client {
	//Les variables de stream (Input, Output et socket)
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;
	//Les informations relatives au server et au nom d'utilisateur
	private String myServer;
	private String myUsername;
	private int myPort;
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
	 */
	private boolean start()
	{
		//Tentative de se connecter au server
		try
		{
			
		}
		catch (Exception e)
		{
			
		}
		return true;
	}
}

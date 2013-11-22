package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
	
	public ServerListener(ObjectInputStream _sInput, ObjectOutputStream _sOutput)
	{
		super();
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
				//C'est cette ligne qui reçoit le message envoyé par le server
				String msg = this.sInput.readObject().toString();
				//À FAIRE : Lorsque nous aurons l'interface graphique, ces messages
				//s'afficheront dedans, pas dans la console. Duh.
				System.out.print(msg);
				System.out.print("> ");
			}
			catch (IOException e)
			{
				//À FAIRE : AFFICHER UN MESSAGE D'ERREUR (LE SERVER A FERMÉ LA CONNEXION)
				//Faire terminer la boucle
				break;
			} 
			catch (ClassNotFoundException e2) 
			{
				//À FAIRE : AFFICHER UN MESSAGE D'ERREUR GÉNÉRIQUE
			}
		}
	}
}

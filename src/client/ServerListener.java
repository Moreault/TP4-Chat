package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Le ServerListener est un thread du c�t� du client qui �coute le server.
 * Cette classe h�rite de classe Thread de Java.
 * @author Mathieu Moreault
 * @author Anthony Lavall�e
 * @author �lise Leclerc
 * @author Pierre Marion
 */

public class ServerListener extends Thread 
{
	//Constantes
	private static final String ERROR_SERVER_CLOSED_CONNECTION = "Erreur: Le server a ferm� la connexion.";
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
	 * Cette m�thode �coute le server et attends des messages puis les affiche
	 * lorsqu'ils arrivent.
	 */
	public void run()
	{
		//Nous voulons que cette m�thode roule sans arr�t, donc while (true)
		while (true)
		{
			try
			{
				//C'est cette ligne qui re�oit le message envoy� par le server
				String msg = this.sInput.readObject().toString();
				//� FAIRE : Lorsque nous aurons l'interface graphique, ces messages
				//s'afficheront dedans, pas dans la console. Duh.
				System.out.print(msg);
				System.out.print("> ");
			}
			catch (IOException e)
			{
				//� FAIRE : AFFICHER UN MESSAGE D'ERREUR (LE SERVER A FERM� LA CONNEXION)
				//Faire terminer la boucle
				break;
			} 
			catch (ClassNotFoundException e2) 
			{
				//� FAIRE : AFFICHER UN MESSAGE D'ERREUR G�N�RIQUE
			}
		}
	}
}

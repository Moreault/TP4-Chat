package common;

/**
 * Cette classe regroupe les fonctions non-sp�cifiques � aucune autre classe qui
 * sont utilis�es par le client ET par le server.
 * @author Mathieu Moreault
 * @author Anthony Lavall�e
 * @author �lise Leclerc
 * @author Pierre Marion
 */

public class Common {
	public static final String ERROR_GENERIC = "Erreur: L'application a rencontr� une erreur";
	public static final int DEFAULT_PORT = 5000;
	public static final String MESSAGE_CONNECTED = "s'est connect�.";
	/**
	 * Cette fonction valide le port avant de l'assigner au server. 
	 * � noter que plusieurs ports sont r�serv�s.
	 * @param _port Le port � valider. Doit �tre de 1 � 65535 inclusivement.
	 * @return True si le port est valide, False sinon.
	 */
	static public boolean estUnPortValide(int _port)
	{
		//Le port 1 est r�serv�
		if (_port > 0 && _port <= 65535)
		{
			return true;
		}
		return false;
	}
	/**
	 * Fonction qui formatte les noms d'utilisateur. Plac� ici pour changer ais�ment
	 * le formattage.
	 * @userName _port Le nom d'utilisateur � formatter.
	 * @return Le nom formatt�.
	 */
	static public String formatName(String userName)
	{
		return "<" + userName + ">";
	}
}

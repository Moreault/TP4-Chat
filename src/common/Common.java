package common;

/**
 * Cette classe regroupe les fonctions non-spécifiques à aucune autre classe qui
 * sont utilisées par le client ET par le server.
 * @author Mathieu Moreault
 * @author Anthony Lavallée
 */

public class Common {
	/**
	 * Cette fonction valide le port avant de l'assigner au server. 
	 * À noter que plusieurs ports sont réservés.
	 * @param _port Le port à valider. Doit être de 1 à 65535 inclusivement.
	 * @return True si le port est valide, False sinon.
	 */
	static public boolean estUnPortValide(int _port)
	{
		//Le port 1 est réservé
		if (_port > 0 && _port <= 65535)
		{
			return true;
		}
		return false;
	}
}

package common;

/**
 * Cette classe regroupe les fonctions non-sp�cifiques � aucune autre classe qui
 * sont utilis�es par le client ET par le server.
 * @author Mathieu Moreault
 * @author Anthony Lavall�e
 */

public class Common {
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
}
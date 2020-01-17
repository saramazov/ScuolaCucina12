package service;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.CalendarioDAO;
import dao.CalendarioDAOImpl;
import dao.FeedbackDAO;
import dao.FeedbackDAOImpl;
import dao.IscrizioneUtenteDAO;
import dao.IscrizioneUtenteDAOImpl;
import dto.EdizioneDTO;
import entity.Edizione;
import entity.Feedback;
import entity.Utente;
import exceptions.ConnessioneException;
import exceptions.DAOException;

public class EdizioneServiceImpl implements EdizioneService{

	//dichiarare qui tutti i dao di cui si ha bisogno
	private CalendarioDAO daoC;
	private IscrizioneUtenteDAO daoI;
	private FeedbackDAO daoF;
	
	//costruire qui tutti i dao di cui si ha bisogno
	public  EdizioneServiceImpl() throws ConnessioneException{
		daoC = new CalendarioDAOImpl();
		daoI = new IscrizioneUtenteDAOImpl();
		daoF = new FeedbackDAOImpl();
	}
	
	/*
	 * inserisce una nuova edizione 
	 */
	@Override
	public void inserisciEdizione(Edizione e) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	
	/*
	 * modifica tutti i dati di una edizione esistente 
	 */
	@Override
	public void modificaEdizione(Edizione e) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	/*
	 * cancella una edizione esistente.
	 * E' possibile cancellare una edizione soltanto se la data di inizio è antecedente a quella odierna
	 * Nel caso l'edizione sia cancellabile, è necessario cancellare l'iscrizione a tutti gli utenti iscritti
	 */
	@Override
	public void cancellaEdizione(int idEdizione) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	/*
	 * iscrive un utente ad una edizione 
	 * un utente si può iscrivere solo se ci sono ancora posti disponibili considerato che ogni corso a un numero massimo di partecipanti
	 */
	@Override
	public void iscriviUtente(int idEdizione, String idUtente) throws DAOException {
		try {
			daoI.iscriviUtente(idEdizione, idUtente);
		} catch (SQLException e) {
			throw new DAOException("utente o edizione non esistente ");
			
		}
		
		
	}

	/*
	 * cancella l'iscrizione ad un utente
	 */
	@Override
	public void cancellaIscrizioneUtente(int idEdizione, int idUtente) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	/*
	 * il metodo ritorna tutte le edizioni con relativi utenti e feedback dei corsi in calendario nel mese indicato dell'anno corrente
	 * se il metodi del/dei DAO invocati sollevano una eccezione, il metodo deve tornare una DAOException con all'interno l'exception originale
	 */
	@Override
	public ArrayList<EdizioneDTO> visualizzaEdizioniPerMese(int mese) throws DAOException {
	
		return null;
	}

	/*
	 * il metodo ritorna tutte le edizioni con relativi utenti e feedback dei corsi in calendario nel mese indicato dell'anno corrente
	 * se il metodi del/dei DAO invocati sollevano una eccezione, il metodo deve tornare una DAOException con all'interno l'exception originale
	 */
	@Override
	public ArrayList<EdizioneDTO> visualizzaEdizioniPerAnno(int anno) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * il metodo ritorna tutte le edizioni con relativi utenti e feedback del corso specificato presenti in calendario nell'anno corrente a partire dalla data odierna
	 * se il metodi del/dei DAO invocati sollevano una eccezione, il metodo deve tornare una DAOException con all'interno l'exception originale
	 */
	@Override
	public ArrayList<EdizioneDTO> visualizzaEdizioniPerCorso(int idCorso) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * il metodo ritorna tutte le edizioni dei corsi e relativi utenti e feedbacks in calendario dell'anno corrente a partire dalla data odierna
	 * se il metodi del/dei DAO invocati sollevano una eccezione, il metodo deve tornare una DAOException con all'interno l'exception originale
	 */
	@Override
	public EdizioneDTO visualizzaEdizione(int idEdizione) throws DAOException {
		Edizione edizioniCorsi= new Edizione();
		ArrayList<Utente> utentiIscrittiPerEd= new ArrayList<Utente>();
		ArrayList<Feedback> feedbacks= new ArrayList<Feedback>();
		EdizioneDTO edizioniDTO = new EdizioneDTO();
		try {
			edizioniCorsi=daoC.selectEdizione(idEdizione);
			utentiIscrittiPerEd=daoI.selectUtentiPerEdizione(idEdizione);
			feedbacks=daoF.selectPerEdizione(idEdizione);
			edizioniDTO = new EdizioneDTO(edizioniCorsi,feedbacks, utentiIscrittiPerEd);
			
			
		} catch (SQLException e) {
			throw new DAOException("");
			
		}
		
		return edizioniDTO;
	}
 public static void main(String[] args) throws ConnessioneException, DAOException {
	 EdizioneService service=new EdizioneServiceImpl();
	 //service.iscriviUtente(200, "veronica");
	 EdizioneDTO dto = new EdizioneDTO();
	  dto = service.visualizzaEdizione(98);
	  System.out.println(dto);
	 
	 
	 
 }

}

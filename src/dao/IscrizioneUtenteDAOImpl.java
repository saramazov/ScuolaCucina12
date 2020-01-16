package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import entity.Edizione;
import entity.Utente;
import exceptions.ConnessioneException;

public class IscrizioneUtenteDAOImpl implements IscrizioneUtenteDAO {

	private Connection conn;

	public IscrizioneUtenteDAOImpl() throws ConnessioneException{
		conn = SingletonConnection.getInstance();
	}
	
	/*
	 * iscrizione di un certo utente ad una certa edizione di un corso.
	 * sia l'utente che l'edizione devono già essere stati registrati in precedenza
	 * se l'utente e/o l'edizione non esistono o l'utente è già iscritto a quella edizione si solleva una eccezione
	 */
	//
	public void iscriviUtente(int idEdizione, String idUtente) throws SQLException {
		PreparedStatement ps=conn.prepareStatement("INSERT INTO iscritti(id_edizione,id_utente) VALUES (?,?)");
		ps.setInt(1, idEdizione);
		ps.setString(2, idUtente);
		int p=ps.executeUpdate();	
		if(p==0) {
			throw new SQLException("utente non trovato oppure edizione non esiste oppure utente già iscritto");
		}
		}

	/*
	 * cancellazione di una iscrizione ad una edizione
	 * nota: quando si cancella l'iscrizione, sia l'utente che l'edizione non devono essere cancellati
	 * se l'utente e/o l'edizione non esistono si solleva una eccezione
	 */
	@Override
	public void cancellaIscrizioneUtente(int idEdizione, String idUtente) throws SQLException {
	
		PreparedStatement ps = conn.prepareStatement("DELETE FROM iscritti WHERE id_edizione=? and id_Utente=? ");
		ps.setInt(1, idEdizione);
		ps.setString(2, idUtente);
		int n = ps.executeUpdate();
		if(n==0)
			throw new SQLException("edizione non presente oppure utente non presente");
		}

	/*
	 * lettura di tutte le edizioni a cui è iscritto un utente
	 * se l'utente non esiste o non è iscritto a nessuna edizione si torna una lista vuota
	 */
	@Override
	public ArrayList<Edizione> selectIscrizioniUtente(String idUtente) throws SQLException {
		ArrayList<Edizione> edizioni=new ArrayList<Edizione>();
		PreparedStatement ps=conn.prepareStatement("select c.id_edizione,id_utente,id_corso,dataInizio,durata,aula,docente from iscritti i inner join calendario c on i.id_edizione = c.id_edizione where i.id_utente =?");

		ps.setString(1, idUtente);
		ResultSet rs=ps.executeQuery();
		
		while(rs.next()){
		
		    String utente=rs.getString("id_utente");
		    int idEdizione = rs.getInt("id_edizione");
			int idCorso=rs.getInt("id_corso");
			Date dataInizio =rs.getDate("dataInizio");
			
			int durata= rs.getInt("durata");
			String aula=rs.getString("aula");
			String docente=rs.getString("docente");
			Edizione e=new Edizione( idEdizione , idCorso,  dataInizio,  durata,  aula, docente);
			
			edizioni.add(e);
		}

		return edizioni;
	}
		/*
	 * lettura di tutti gli utenti iscritti ad una certa edizione
	 * se l'edizione non esiste o non vi sono utenti iscritti si torna una lista vuota
	 */
	@Override
	public ArrayList<Utente> selectUtentiPerEdizione(int idEdizione) throws SQLException {
		ArrayList<Utente> utenti=new ArrayList<Utente>();
		PreparedStatement ps=conn.prepareStatement("select r.id_utente,r.password,nome,cognome,dataNascita,email,telefono from registrati r inner join iscritti i on i.id_utente = r.id_utente where i.id_edizione =?");
		ps.setInt(1,idEdizione);
		ResultSet rs=ps.executeQuery();
		
		while(rs.next()){
		String idUtente=rs.getString("id_utente");
		String password=rs.getString("password");
		String nome=rs.getString("nome");
		String cognome=rs.getString("cognome");
		Date dataNascita=rs.getDate("dataNascita");
		String email=rs.getString("email");
		String telefono=rs.getString("telefono");
		
		Utente u=new Utente(idUtente,password,nome,cognome,dataNascita,email,telefono,false);
		utenti.add(u);
		}
		return utenti;
	}
	
			
			
			
		
		
		
		
		
		
		
		
	

	/*
	 * ritorna il numero di utenti iscritti ad una certa edizione
	 */
	@Override
	public int getNumeroIscritti(int idEdizione) throws SQLException {
		ArrayList<Utente> u =selectUtentiPerEdizione(idEdizione);
		int numeroUtentiIscritti =u.size();
		return numeroUtentiIscritti;
	}
		
		
		
		
	





public static void main(String[] args) throws Exception{
	IscrizioneUtenteDAO dao= new IscrizioneUtenteDAOImpl();
	//Edizione e = new Edizione(4, null, 0, null, null,null);
	
	Utente u=new Utente("nome", null, null, null, null, null, null, false);
	ArrayList<Utente> u1 = dao.selectUtentiPerEdizione(95);
	System.out.println(u1);
	//dao.iscriviUtente(95, "fausto" );
	//dao.iscriviUtente(95,"davide");
	//dao.iscriviUtente(101,"michele");
	//dao.iscriviUtente(96, "veronica");
//	u.setCognome("Doria");
	//dao.cancellaIscrizioneUtente(95,"davide");
	//dao.cancellaIscrizioneUtente(95, "fausto");
	

	//dao.cancellaIscrizioneUtente(101, "michele");
//	dao.update(u);
	//ArrayList<Edizione> e = dao.selectIscrizioniUtente("veronica");
	//System.out.println(e);
	
	
	int i=dao.getNumeroIscritti(95);
	System.out.println("il numero degli iscritti è: " +i);
}


}










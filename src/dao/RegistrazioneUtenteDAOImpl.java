package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import entity.Utente;
import exceptions.ConnessioneException;

public class RegistrazioneUtenteDAOImpl implements RegistrazioneUtenteDAO {
	private static final String insertQuery = "insert into registrati(id_utente,password,nome,cognome,dataNascita,email,telefono)"
			+ "values (?,?,?,?,?,?,?)";
	
	private static final String deleteQuery = "delete from registrati where id_utente= ?";
	
	private static final String updateQuery  = "UPDATE registrati SET password=?, nome=?, cognome=?, dataNascita=?, email=?, telefono=? where id_utente=?";
	private static final String selectIdQuery = "SELECT * FROM registrati where id_utente =?";
	private static final String selectAllQuery = "SELECT * FROM registrati";
	private Connection conn;
	
	

	public RegistrazioneUtenteDAOImpl() throws ConnessioneException{
		conn = SingletonConnection.getInstance();
	}
	
	/*
	 * registrazione di un nuovo utente alla scuola di formazione 
	 * se l'utente già esiste si solleva una eccezione
	 */
	@Override
	public void insert(Utente u) throws SQLException {
		
		PreparedStatement ps=conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
		
			ps.setString(1, u.getIdUtente());
			ps.setString(2, u.getPassword());
			ps.setString(3, u.getNome());
			ps.setString(4, u.getCognome());
			ps.setDate(5, new java.sql.Date(u.getDataNascita().getTime()));
			ps.setString(6, u.getEmail());
			ps.setString(7, u.getTelefono());
			int n = ps.executeUpdate();
		
			if(n==0) {
				throw new SQLException("L'utente: " + u.getIdUtente()+ "è già presente");
			}
		
	}

	/*
	 * modifica di tutti i dati di un utente
	 * l'utente viene individuato dal suo idUtente
	 * se l'utente non esiste si solleva una exception
	 */
	@Override
	public void update(Utente u) throws SQLException {
		PreparedStatement ps=conn.prepareStatement(updateQuery);
		ps.setString(1, u.getPassword());
		ps.setString(2,u.getNome());
		ps.setString(3, u.getCognome());
		ps.setDate(4, new java.sql.Date(u.getDataNascita().getTime()));
		ps.setString(5, u.getEmail());
		ps.setString(6, u.getTelefono());
		ps.setString(7, u.getIdUtente());
		int n = ps.executeUpdate();
		if(n==0)
			throw new SQLException("utente: " + u.getIdUtente() + " non presente");

	}
	/*
	 * cancellazione di un singolo utente
	 * l'utente si può cancellare solo se non è correlato ad altri dati
	 * se l'utente non esiste o non è cancellabile si solleva una eccezione
	 */
	@Override
	
	
	public void delete(String idUtente) throws SQLException {
		PreparedStatement ps1= conn.prepareStatement("select f.id_utente from feedback f inner join\r\n" + 
				"iscritti i on (f.id_utente= i.id_utente) where f.id_utente ='" + idUtente +"'" +";");
		//PreparedStatement ps2= conn.prepareStatement("select id_utente from iscritti where id_utente=' "+idUtente+"'");
		ResultSet n = ps1.executeQuery();
		if(!n.next()) {
		PreparedStatement ps=conn.prepareStatement(deleteQuery);
		ps.setString(1, idUtente);
		ps.executeUpdate();
		
		}
		else {
			throw new SQLException("Non è possibile eliminare: "+idUtente +" legato ad altri dati");
		}
		
	}
	
	/*
	 * lettura di tutti gli utenti registrati
	 * se non ci sono utenti registrati il metodo ritorna una lista vuota
	 */
	@Override
	public ArrayList<Utente> select() throws SQLException {
		ArrayList<Utente> utenti = new ArrayList<Utente>(); 

		PreparedStatement ps=conn.prepareStatement(selectAllQuery);

		ResultSet rs = ps.executeQuery();
		
		;
		
		while(rs.next()== true){
			String idUtente = rs.getString("id_utente");
			String password= rs.getString("password");
			String nome= rs.getString("nome");
			String cognome= rs.getString("cognome");
			Date dataNascita = rs.getDate("dataNascita");
			String email= rs.getString("email");
			String telefono= rs.getString("telefono");

			Utente ut = new Utente(idUtente,password,nome,cognome,dataNascita,email,telefono, false);
			utenti.add(ut);
		} 
		
		return utenti;
	}

	
	/*
	 * lettura dei dati di un singolo utente
	 * se l'utente non esiste si solleva una eccezione
	 */
	@Override
	public Utente select(String idUtente) throws SQLException {
		PreparedStatement ps=conn.prepareStatement(selectIdQuery);

		ps.setString(1, idUtente);

		ResultSet rs = ps.executeQuery();
		Utente u =null;
		if(rs.next()){
			String idUtente1 = rs.getString("id_utente");
			String password= rs.getString("password");
			String nome= rs.getString("nome");
			String cognome= rs.getString("cognome");
			Date dataNascita = rs.getDate("dataNascita");
			String email= rs.getString("email");
			String telefono= rs.getString("telefono");

			u = new Utente(idUtente,password,nome,cognome,dataNascita,email,telefono, false);
			return u;
		}
		else
			throw new SQLException("utente: " + idUtente + " non presente");
	}
	public static void main(String[] args) throws Exception{
		RegistrazioneUtenteDAO dao= new RegistrazioneUtenteDAOImpl();
		Utente u = new Utente("chiaraS", "ciao", "chiara", "savoldi", new java.util.Date(), "savo.chiara@", "32443", false);
		Utente u1 = new Utente("Alberto", "ciao", "chiara", "savoldi", new java.util.Date(), "savo.chiara@", "32443", false);
		Utente u2 = new Utente("Greta", "ciao", "chiara", "savoldi", new java.util.Date(), "savo.chiara@", "32443", false);
		 ArrayList<Utente> b = dao.select();
		 System.out.println(b);
		 
		//dao.delete("chiaraS");
		//u1.setCognome("brusa");
		//dao.update(u1);
		
		//System.out.println(dao.select("Alberto"));

}
}

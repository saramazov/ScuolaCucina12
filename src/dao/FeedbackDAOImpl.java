package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import entity.Categoria;
import entity.Feedback;
import exceptions.ConnessioneException;

public class FeedbackDAOImpl implements FeedbackDAO {

	private Connection conn;

	public FeedbackDAOImpl() throws ConnessioneException{
		conn = SingletonConnection.getInstance();
	}
	
	/*
	 * inserimento di un singolo feedbak relativo ad una edizione di un corso da aprte di un utente
	 * se un utente ha già inserito un feedback per una certa edizione si solleva una eccezione
	 */
	@Override
	public void insert(Feedback feedback) throws SQLException {
		PreparedStatement ps=conn.prepareStatement
				("INSERT INTO feedback (id_edizione, id_utente,"
						+ "descrizione,voto) "
						+ "VALUES (?,?,?,?)");
		ps.setInt(1, feedback.getIdEdizione());
		ps.setString(2, feedback.getIdUtente());
		ps.setString(3, feedback.getDescrizione());
		ps.setInt(4, feedback.getVoto());
		ps.executeUpdate();
	}

	/*
	 * modifica di tutti i dati di un singolo feedback
	 * un feedback viene individuato attraverso l'idFeedback
	 * se il feedback non esiste si solleva una eccezione
	 */
	@Override
	public void update(Feedback feedback) throws SQLException {

		PreparedStatement ps=conn.prepareStatement("UPDATE feedback"
				+ " SET descrizione=?, voto=? where id_feedback=?");
		ps.setString(1, feedback.getDescrizione());
		ps.setInt(2, feedback.getVoto());
		ps.setInt(3, feedback.getIdFeedback());
		int n = ps.executeUpdate();
		if(n==0)
			throw new SQLException("feedback: " + feedback.getIdFeedback()+ " non presente");

	}

	/*
	 * cancellazione di un feedback
	 * se il feedback non esiste si solleva una eccezione
	 */
	@Override
	public void delete(int idFeedback) throws SQLException {

		PreparedStatement ps = conn.prepareStatement("DELETE FROM feedback"
				+ " WHERE id_feedback=?");
		ps.setInt(1, idFeedback);
		int n = ps.executeUpdate();
		if(n==0)
			throw new SQLException("feedback " + idFeedback+ " non presente");
	}
	
	/*
	 * lettura di un singolo feedback scritto da un utente per una certa edizione 
	 * se il feedback non esiste si solleva una eccezione
	 */
	@Override
	public Feedback selectSingoloFeedback(String idUtente, int idEdizione) throws SQLException {
		PreparedStatement ps=conn.prepareStatement("SELECT * FROM feedback"
				+ " where id_utente=? and id_edizione=?");

		ps.setString(1, idUtente);
		ps.setInt(2, idEdizione);

		ResultSet rs = ps.executeQuery();
		Feedback feedback=null;
		if(rs.next()){
			String descrizione= rs.getString("descrizione");
			Integer voto = rs.getInt("voto");
			Integer idFeedback = rs.getInt("id_feedback");
			feedback = new Feedback(idFeedback,idEdizione, idUtente, descrizione, voto);
			return feedback;
		}
		else
			throw new SQLException("feedback: " + idUtente+", "+idEdizione+ " non presente");
	
	}

	/*
	 * lettura di tutti i feedback di una certa edizione
	 * se non ci sono feedback o l'edizione non esiste si torna una lista vuota
	 */
	@Override
	public ArrayList<Feedback> selectPerEdizione(int idEdizione) throws SQLException {
		PreparedStatement ps=conn.prepareStatement("SELECT * FROM feedback"
				+ " where id_edizione=?");

		ps.setInt(1, idEdizione);

		ResultSet rs = ps.executeQuery();
		ArrayList<Feedback> feedbacks= new ArrayList<Feedback>(); 
		while(rs.next()){
			String descrizione= rs.getString("descrizione");
			Integer voto = rs.getInt("voto");
			Integer idFeedback = rs.getInt("id_feedback");
			String idUtente = rs.getString("id_utente");
			Feedback feedback = new Feedback(idFeedback,idEdizione, idUtente, descrizione, voto);
			feedbacks.add(feedback);
		}

		return feedbacks;
	
	}

	/*
	 * lettura di tutti i feedback scritti da un certo utente
	 * se non ci sono feedback o l'utente non esiste si torna una lista vuota
	 */
	@Override
	public ArrayList<Feedback> selectPerUtente(String idUtente) throws SQLException {
		PreparedStatement ps=conn.prepareStatement("SELECT * FROM feedback"
				+ " where id_utente=?");

		ps.setString(1, idUtente);

		ResultSet rs = ps.executeQuery();
		ArrayList<Feedback> feedbacks= new ArrayList<Feedback>(); 
		while(rs.next()){
			String descrizione= rs.getString("descrizione");
			Integer voto = rs.getInt("voto");
			Integer idFeedback = rs.getInt("id_feedback");
			Integer idEdizione = rs.getInt("id_edizione");
			Feedback feedback = new Feedback(idFeedback,idEdizione, idUtente, descrizione, voto);
			feedbacks.add(feedback);
		}

		return feedbacks;
	
	}

	/*
	 * lettura di tutti i feedback scritti per un certo corso (nota: non edizione ma corso)
	 * se non ci sono feedback o il corso non esiste si torna una lista vuota
	 */
	@Override
	public ArrayList<Feedback> selectFeedbackPerCorso(int idCorso) throws SQLException {
		PreparedStatement ps=conn.prepareStatement("select f.id_feedback,"
				+ "f.id_edizione,f.id_utente,f.descrizione,f.voto from feedback f "
				+ "inner join calendario c on f.id_edizione=c.id_edizione" 
				+ " where c.id_corso=?");

		ps.setInt(1, idCorso);

		ResultSet rs = ps.executeQuery();
		ArrayList<Feedback> feedbacks= new ArrayList<Feedback>(); 
		while(rs.next()){
			String descrizione= rs.getString("descrizione");
			Integer voto = rs.getInt("voto");
			Integer idFeedback = rs.getInt("id_feedback");
			String idUtente = rs.getString("id_utente");
			Integer idEdizione = rs.getInt("id_edizione");
			Feedback feedback = new Feedback(idFeedback,idEdizione, idUtente, descrizione, voto);
			feedbacks.add(feedback);
		}

		return feedbacks;
	
	}

	public static void main(String[] args) throws Exception{
	FeedbackDAO dao= new FeedbackDAOImpl();
	Feedback f = new Feedback(93,"fausto","brutto",5);
//	dao.insert(f);
	Feedback f2 = dao.selectSingoloFeedback("Michele", 99);
	System.out.println(dao.selectPerEdizione(93));
	System.out.println(dao.selectPerUtente("Alberto"));
	System.out.println(dao.selectFeedbackPerCorso(88));
	f2.setVoto(10);
//	dao.delete("aa");
//	dao.delete(57);
//	c.setDescrizione("crema di guacamole");
	dao.update(f2);
//	System.out.println(dao.select());
}
	
}

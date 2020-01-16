package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import entity.Corso;
import exceptions.ConnessioneException;

public class CatalogoDAOImpl implements CatalogoDAO {

	private Connection conn;

	public CatalogoDAOImpl() throws ConnessioneException{
		conn = SingletonConnection.getInstance();
	}
	
	/*
	 * registrazione di un nuovo corso nel catalogo dei corsi
	 */
	@Override
	public void insert(Corso corso) throws SQLException {
		
		PreparedStatement ps=conn.prepareStatement("INSERT INTO catalogo"
				+ "(titolo,id_categoria,numeroMaxPartecipanti,costo,descrizione)"
				+ " VALUES (?,?,?,?,?)");

		ps.setString(1, corso.getTitolo());
		ps.setInt(2, corso.getIdCategoria());
		ps.setInt(3, corso.getMaxPartecipanti());
		ps.setDouble(4, corso.getCosto());
		ps.setString(5, corso.getDescrizione());
		ps.executeUpdate();

	}

	/*
	 * modifica di tutti i dati di un corso nel catalogo dei corsi
	 * il corso viene individuato in base al idCorso
	 * se il corso non esiste si solleva una eccezione
	 */
	@Override
	public void update(Corso corso) throws SQLException {
		
		PreparedStatement ps=conn.prepareStatement("UPDATE catalogo SET titolo=?, "
				+ "id_categoria=?, numeroMaxPartecipanti=?, costo=?, descrizione=? "
				+ " where id_corso=?");

		ps.setString(1, corso.getTitolo());
		ps.setInt(2, corso.getIdCategoria());
		ps.setInt(3, corso.getMaxPartecipanti());
		ps.setDouble(4, corso.getCosto());
		ps.setString(5, corso.getDescrizione());
		ps.setInt(6, corso.getCodice());
		ps.executeUpdate();
		int n = ps.executeUpdate();
		if(n==0)
			throw new SQLException("corso: " + corso.getCodice() + " non presente");

	}

	/*
	 * cancellazione di un nuovo corso nel catalogo dei corsi
	 * questo potrà essere cancellato solo se non vi sono edizioni di quel corso o qualsiasi altro legame con gli altri dati 
	 * Se il corso non esiste si solleva una eccezione
	 * Se non è cancellabile si solleva una eccezione
	 */
	@Override
	public void delete(int idCorso) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("DELETE FROM catalogo"
				+ " WHERE id_corso=?");
		ps.setInt(1, idCorso);
		int n = ps.executeUpdate();
		if(n==0)
			throw new SQLException("corso " + idCorso + " non presente");

	}

	/*
	 * lettura di tutti i corsi dal catalogo
	 * se non ci sono corsi nel catalogo il metodo torna una lista vuota
	 */
	@Override
	public ArrayList<Corso> select() throws SQLException {
		ArrayList<Corso> corsi = new ArrayList<Corso>(); 

		PreparedStatement ps=conn.prepareStatement("SELECT * FROM catalogo");

		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			String titolo = rs.getString("titolo");
			Integer categoria= rs.getInt("id_categoria");
			Integer numeroMaxPartecipanti = rs.getInt("numeroMaxPartecipanti");
			Double costo= rs.getDouble("costo");
			String descrizione = rs.getString("descrizione");

			Corso cors = new Corso(titolo,categoria,numeroMaxPartecipanti,costo,descrizione);
			corsi.add(cors);
		}

		return corsi;

	}

	/*
	 * lettura di un singolo corso dal catalogo dei corsi
	 * se il corso non è presente si solleva una eccezione
	 */
	@Override
	public Corso select(int idCorso) throws SQLException {
		
		PreparedStatement ps=conn.prepareStatement("SELECT * FROM catalogo"
				+ " where id_corso=?");
		ps.setInt(1,idCorso);

		ResultSet rs = ps.executeQuery();

		Corso corso =null;
		if(rs.next()){
			String titolo = rs.getString("titolo");
			Integer categoria= rs.getInt("id_categoria");
			Integer numeroMaxPartecipanti = rs.getInt("numeroMaxPartecipanti");
			Double costo= rs.getDouble("costo");
			String descrizione = rs.getString("descrizione");

			corso = new Corso(idCorso,titolo,categoria,numeroMaxPartecipanti,costo,descrizione);
			return corso;
		}
		else
			throw new SQLException("corso: " + idCorso + " non presente");
	}


	public static void main(String[] args) throws Exception{
	CatalogoDAO dao= new CatalogoDAOImpl();
//	Corso corso = new Corso("pane e pace",48,100,0,"Presa del palazzo d'inverno");
//	dao.insert(corso);
//	Corso corso = dao.select(87);
//	corso.setCosto(1000);
//	dao.delete("aa");
//	dao.delete(97);
//	c.setDescrizione("crema di guacamole");
//	dao.update(corso);
//	System.out.println(dao.select());
}

	
	
}
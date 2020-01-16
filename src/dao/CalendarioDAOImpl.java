package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import entity.Corso;
import entity.Edizione;
import exceptions.ConnessioneException;
import exceptions.DAOException;



public class CalendarioDAOImpl implements CalendarioDAO {

	private Connection conn;

	public CalendarioDAOImpl() throws ConnessioneException{
		conn = SingletonConnection.getInstance();
	}

	/*
	 * registrazione di una nuova edizione nel calendario dei corsi
	 */
	@Override
	public void insert(Edizione ed) throws SQLException{

			PreparedStatement ps=conn.prepareStatement("insert into calendario(id_corso,dataInizio,durata,aula,docente) "
					+ "values (?,?,?,?,?)");

			ps.setInt(1, ed.getIdCorso());
			ps.setDate(2, new java.sql.Date(ed.getDataInizio().getTime()));
			ps.setInt(3, ed.getDurata());
			ps.setString(4, ed.getAula());
			ps.setString(5, ed.getDocente());
			ps.executeUpdate();

	}


	/*
	 * cancellazione di una edizione presente nel calendario dei corsi
	 * per cancellare una edizione è necessario prima cancellare le eventuali iscrizioni degli utenti e i feedbacks  //??
	 * l'edizione viene individuata in base a idEdizione
	 * se l'edizione non è presente si solleva una eccezione
	 */
	@Override
	public void delete(int idEdizione) throws SQLException{
		
		PreparedStatement sf = conn.prepareStatement("SELECT * FROM feedback "
				+ "WHERE id_edizione=?");
		PreparedStatement si = conn.prepareStatement("SELECT * FROM iscritti "
				+ "WHERE id_edizione=?");
		PreparedStatement df = conn.prepareStatement("DELETE FROM feedback "
				+ "WHERE id_edizione=?");
		PreparedStatement di = conn.prepareStatement("DELETE FROM iscritti "
				+ "WHERE id_edizione=?");
		PreparedStatement dc = conn.prepareStatement("DELETE FROM calendario "
				+ "WHERE id_edizione=?");
		sf.setInt(1, idEdizione);
		si.setInt(1, idEdizione);
		df.setInt(1, idEdizione);
		di.setInt(1, idEdizione);
		dc.setInt(1, idEdizione);
		
		ResultSet rsf = sf.executeQuery();
		if(rsf.next()) {
			df.executeUpdate();
		}
		
		ResultSet rsi = si.executeQuery();
		if(rsi.next()) {
			di.executeUpdate();
		}
		
		int n = dc.executeUpdate();
		if(n==0) {
			throw new SQLException("edizione "+idEdizione+" non presente");
		}
				
	}


	/*
	 * modifica di tutti i dati di una edizione presente nel calendario dei corsi
	 * l'edizione viene individuata in base al idEdizione
	 * se l'edizione non è presente si solleva una eccezione
	 */
	@Override
	public void update(Edizione ed) throws SQLException{
			PreparedStatement ps=conn.prepareStatement("update calendario set dataInizio=?, durata=?, aula=?, docente=? "
					+ "where id_edizione= ?");

			ps.setDate(1,new java.sql.Date(ed.getDataInizio().getTime()));
			ps.setInt(2,ed.getDurata());
			ps.setString(3,ed.getAula());
			ps.setString(4,ed.getDocente());
			ps.setInt(5, ed.getCodice());

			int n = ps.executeUpdate();
			if(n==0) throw new SQLException("edizione " + ed.getCodice() + " non presente");

	}


	/*
	 * lettura di tutte le edizioni di una certa categoria, presenti nel calendario dei corsi
	 * le edizioni vengono individuate in base al idCategoria
	 * se non vi sono edizioni per quella categoria o la categoria non esiste viene ritornata una lista vuota
	 */
	@Override
	public ArrayList<Edizione> select(int idCategoria) throws SQLException{
			ArrayList<Edizione> edizioni=new ArrayList<Edizione>();
			PreparedStatement ps=conn.prepareStatement("select * from calendario, catalogo "
					+ "where calendario.id_corso = catalogo.id_corso and id_categoria=?");

			ps.setInt(1, idCategoria);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				int idEdizione=rs.getInt("id_Edizione");
				int idCorso=rs.getInt("id_corso");
				Date dataInizio=rs.getDate("dataInizio");
				int durata=rs.getInt("durata");
				String titolo =rs.getString("titolo");
				Integer maxPartecipanti = rs.getInt("numeroMaxPartecipanti");
				Double costo = rs.getDouble("costo");
				String aula=rs.getString("aula");
				String docente=rs.getString("docente");
				String descrizione=rs.getString("descrizione");

				Edizione e=new Edizione(idCorso,dataInizio,durata,aula,docente);
				e.setCodice(idEdizione);
				e.setCorso(new Corso(idCorso, titolo, idCategoria, maxPartecipanti,
						costo, descrizione));

				long dataM = dataInizio.getTime();
				long durataM= durata*86400000L;
				Date dataFine = new Date(dataM+durataM);
			

				if (dataFine.before(new java.util.Date()))
					e.setTerminata(true);	

				edizioni.add(e);

			}

			return edizioni;

	}


	/*
	 * lettura dei dati di una edizione presente nel calendario dei corsi
	 * l'edizione viene individuata in base al idEdizione
	 * se l'edizione non è presente si solleva una eccezione
	 */
	@Override
	public Edizione selectEdizione(int idEdizione) throws SQLException{
	  
		PreparedStatement ps=conn.prepareStatement("select * from calendario,catalogo"
				+ " where catalogo.id_corso=calendario.id_corso"
				+ " and id_edizione = ?");
		ps.setInt(1, idEdizione);
		ResultSet rs=ps.executeQuery();
		
		if(rs.next()){
			int idCorso=rs.getInt("id_corso");
			Date dataInizio=rs.getDate("dataInizio");
			int durata=rs.getInt("durata");
			String titolo =rs.getString("titolo");
			Integer maxPartecipanti = rs.getInt("numeroMaxPartecipanti");
			Double costo = rs.getDouble("costo");
			String aula=rs.getString("aula");
			String docente=rs.getString("docente");
			String descrizione=rs.getString("descrizione");
			Integer idCategoria = rs.getInt("id_categoria");

			Edizione ed=new Edizione(idCorso,dataInizio,durata,aula,docente);
			ed.setCodice(idEdizione);
			ed.setCorso(new Corso(idCorso, titolo, idCategoria, maxPartecipanti,
					costo, descrizione));

			long dataM = dataInizio.getTime();
			long durataM= durata*86400000L;
			Date dataFine = new Date(dataM+durataM);

			if (dataFine.before(new java.util.Date()))
				ed.setTerminata(true);
			return ed;
		} else 
			throw new SQLException("edizione " + idEdizione + " non presente");
	  
	  
	}

	/*
	 * lettura di tutte le edizioni presenti nel calendario dei corsi
	 * se non vi sono edizioni registrate viene ritornata una lista vuota
	 */
	@Override
	public ArrayList<Edizione> select()throws SQLException{
	  
		ArrayList<Edizione> edizioni=new ArrayList<Edizione>();
		PreparedStatement ps=conn.prepareStatement("select * from calendario,catalogo"
				+ " where catalogo.id_corso=calendario.id_corso");
		ResultSet rs=ps.executeQuery();
		
		while(rs.next()){
			int idEdizione=rs.getInt("id_Edizione");
			int idCorso=rs.getInt("id_corso");
			Date dataInizio=rs.getDate("dataInizio");
			int durata=rs.getInt("durata");
			String titolo =rs.getString("titolo");
			Integer maxPartecipanti = rs.getInt("numeroMaxPartecipanti");
			Double costo = rs.getDouble("costo");
			String aula=rs.getString("aula");
			String docente=rs.getString("docente");
			String descrizione=rs.getString("descrizione");
			Integer idCategoria = rs.getInt("id_categoria");

			Edizione ed=new Edizione(idCorso,dataInizio,durata,aula,docente);
			ed.setCodice(idEdizione);
			ed.setCorso(new Corso(idCorso, titolo, idCategoria, maxPartecipanti,
					costo, descrizione));

			long dataM = dataInizio.getTime();
			long durataM= durata*86400000L;
			Date dataFine = new Date(dataM+durataM);

			if (dataFine.before(new java.util.Date()))
				ed.setTerminata(true);
			
			edizioni.add(ed);
		}
		return edizioni;
	  

		
	}

	
	/*
	 * 
	 * lettura di tutte le edizioni a cui un certo utente è iscritto o è stato iscritto in passato (vale a dire tutte), presenti nel calendario dei corsi
	 * le edizioni vengono individuate in base al idUtente dell'utente
	 * se non vi sono edizioni per quell'utente o l'utente non esiste viene ritornata una lista vuota
	 */
	@Override
	public ArrayList<Edizione> select(String idUtente) throws SQLException{

			ArrayList<Edizione> edizioni=new ArrayList<Edizione>();
			PreparedStatement ps=conn.prepareStatement("select * "
					+ "from calendario,iscritti,catalogo"
					+ " where calendario.id_edizione=iscritti.id_edizione"
					+ " and calendario.id_corso=catalogo.id_corso"
					+ " and iscritti.id_utente=?");

			ps.setString(1, idUtente);
			ResultSet rs=ps.executeQuery();
			
			while(rs.next()){
				int idEdizione=rs.getInt("id_Edizione");
				int idCorso=rs.getInt("id_corso");
				Date dataInizio=rs.getDate("dataInizio");
				int durata=rs.getInt("durata");
				String aula=rs.getString("aula");
				String docente=rs.getString("docente");
				String titolo =rs.getString("titolo");
				Integer idCategoria = rs.getInt("id_categoria");
				Integer maxPartecipanti = rs.getInt("numeroMaxPartecipanti");
				Double costo = rs.getDouble("costo");
				String descrizione=rs.getString("descrizione");


				Edizione e=new Edizione(idCorso,dataInizio,durata,aula,docente);
				e.setCodice(idEdizione);
				e.setCorso(new Corso(idCorso, titolo, idCategoria, maxPartecipanti,
						costo, descrizione));

				long dataM = dataInizio.getTime();
				long durataM= durata*86400000L;
				Date dataFine = new Date(dataM+durataM);
				java.util.Date d =  new java.util.Date();

				if (dataFine.before(d) )
					e.setTerminata(true);	
				
				edizioni.add(e);
			}

			return edizioni;
	}
	
	/*
	 * leggere tutte le edizioni presenti nel calendario nel range delle date da, a (inclusi)
	 * se non vi sono edizioni in quel range di date viene ritornata una lista vuota
	 */
	@Override
	public ArrayList<Edizione> select(java.util.Date da, java.util.Date a) throws SQLException {
	
			ArrayList<Edizione> edizioni=new ArrayList<Edizione>();
			PreparedStatement ps=conn.prepareStatement("select * "
					+ "from calendario,catalogo where "
					+ "calendario.id_corso=catalogo.id_corso"
					+" and calendario.dataInizio >= ? and calendario.dataInizio <= ?");

			ps.setDate(1, new java.sql.Date(da.getTime()));
			ps.setDate(2, new java.sql.Date(a.getTime()));
			ResultSet rs=ps.executeQuery();
			
			while(rs.next()){
				int idEdizione=rs.getInt("id_Edizione");
				int idCorso=rs.getInt("id_corso");
				Date dataInizio=rs.getDate("dataInizio");
				int durata=rs.getInt("durata");
				String aula=rs.getString("aula");
				String docente=rs.getString("docente");
				String titolo =rs.getString("titolo");
				Integer idCategoria = rs.getInt("id_categoria");
				Integer maxPartecipanti = rs.getInt("numeroMaxPartecipanti");
				Double costo = rs.getDouble("costo");
				String descrizione=rs.getString("descrizione");


				Edizione e=new Edizione(idCorso,dataInizio,durata,aula,docente);
				e.setCodice(idEdizione);
				e.setCorso(new Corso(idCorso, titolo, idCategoria, maxPartecipanti,
						costo, descrizione));

				long dataM = dataInizio.getTime();
				long durataM= durata*86400000L;
				Date dataFine = new Date(dataM+durataM);
				java.util.Date d =  new java.util.Date();

				if (dataFine.before(d) )
					e.setTerminata(true);	
				
				edizioni.add(e);
			}

			return edizioni;
		
	}

	/*	 
	 * lettura di tutte le edizioni di una certa categoria, presenti nel calendario dei corsi
	 * se future = true, le edizioni lette devono essere solo quelle a partire dalla data in odierna e dell'anno corrente ?
	 * se future = false devono essere lette tutte le edizioni ?
	 * le edizioni vengono individuate in base al idCategoria
	 * se non vi sono edizioni per quella categoria o la categoria non esiste viene ritornata una lista vuota
	 */
	@Override
	public ArrayList<Edizione> select(int idCategoria, boolean future) throws SQLException {
		ArrayList<Edizione> edizioni=new ArrayList<Edizione>();
		
		if(future) {
			PreparedStatement ps=conn.prepareStatement("select * "
					+ "from calendario,catalogo where"
					+ " calendario.id_corso=catalogo.id_corso "
					+ "and catalogo.id_categoria = ? and "
					+ "calendario.dataInizio >= ?");

			ps.setInt(1, idCategoria);
			ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
			ResultSet rs=ps.executeQuery();
			
			while(rs.next()){
				int idEdizione=rs.getInt("id_Edizione");
				int idCorso=rs.getInt("id_corso");
				Date dataInizio=rs.getDate("dataInizio");
				int durata=rs.getInt("durata");
				String aula=rs.getString("aula");
				String docente=rs.getString("docente");
				String titolo =rs.getString("titolo");
				Integer maxPartecipanti = rs.getInt("numeroMaxPartecipanti");
				Double costo = rs.getDouble("costo");
				String descrizione=rs.getString("descrizione");

				Edizione e=new Edizione(idCorso,dataInizio,durata,aula,docente);
				e.setCodice(idEdizione);
				e.setCorso(new Corso(idCorso, titolo, idCategoria, maxPartecipanti,
						costo, descrizione));

				long dataM = dataInizio.getTime();
				long durataM= durata*86400000L;
				Date dataFine = new Date(dataM+durataM);
				java.util.Date d =  new java.util.Date();

				if (dataFine.before(d) )
					e.setTerminata(true);	
				
				edizioni.add(e);
			}
		}
			else {
				PreparedStatement ps=conn.prepareStatement("select * from calendario, catalogo "
				+ "where calendario.id_corso = catalogo.id_corso and id_categoria=?");
		

		ps.setInt(1, idCategoria);
		
		ResultSet rs=ps.executeQuery();
		while(rs.next()){
			int idEdizione=rs.getInt("id_Edizione");
			int idCorso=rs.getInt("id_corso");
			Date dataInizio=rs.getDate("dataInizio");
			int durata=rs.getInt("durata");
			String aula=rs.getString("aula");
			String docente=rs.getString("docente");
			String titolo =rs.getString("titolo");
			Integer maxPartecipanti = rs.getInt("numeroMaxPartecipanti");
			Double costo = rs.getDouble("costo");
			String descrizione=rs.getString("descrizione");

			Edizione e=new Edizione(idCorso,dataInizio,durata,aula,docente);
			e.setCodice(idEdizione);
			e.setCorso(new Corso(idCorso, titolo, idCategoria, maxPartecipanti,
					costo, descrizione));

			long dataM = dataInizio.getTime();
			long durataM= durata*86400000L;
			Date dataFine = new Date(dataM+durataM);
		

			if (dataFine.before(new java.util.Date()))
				e.setTerminata(true);	

			edizioni.add(e);

		}
		
	}

		return edizioni;
	}

	/*
	 * lettura di tutte le edizioni presenti nel calendario dei corsi
	 * se future = true, le edizioni lette devono essere solo quelle a partire dalla data in odierna e dell'anno corrente 
	 * se future = false devono essere lette tutte le edizioni
	 * se non vi sono edizioni viene ritornata una lista vuota
	 */
	@Override
	public ArrayList<Edizione> select(boolean future) throws SQLException {
		ArrayList<Edizione> edizioni = new ArrayList<Edizione>();
		if(future) {
			PreparedStatement ps=conn.prepareStatement("select * "
					+ "from calendario,catalogo where"
					+ " calendario.id_corso=catalogo.id_corso and "
					+ "calendario.dataInizio >= ?");

			ps.setDate(1, new java.sql.Date(System.currentTimeMillis()));
			ResultSet rs=ps.executeQuery();
			
			while(rs.next()){
				int idEdizione=rs.getInt("id_Edizione");
				int idCorso=rs.getInt("id_corso");
				Date dataInizio=rs.getDate("dataInizio");
				int durata=rs.getInt("durata");
				String aula=rs.getString("aula");
				String docente=rs.getString("docente");
				String titolo =rs.getString("titolo");
				Integer idCategoria = rs.getInt("id_categoria");
				Integer maxPartecipanti = rs.getInt("numeroMaxPartecipanti");
				Double costo = rs.getDouble("costo");
				String descrizione=rs.getString("descrizione");


				Edizione e=new Edizione(idCorso,dataInizio,durata,aula,docente);
				e.setCodice(idEdizione);
				e.setCorso(new Corso(idCorso, titolo, idCategoria, maxPartecipanti,
						costo, descrizione));

				long dataM = dataInizio.getTime();
				long durataM= durata*86400000L;
				Date dataFine = new Date(dataM+durataM);
				java.util.Date d =  new java.util.Date();

				if (dataFine.before(d) )
					e.setTerminata(true);	
				
				edizioni.add(e);
			}

		} else {
			edizioni = select();
		}
		
		return edizioni;
	}

	/*
	 * lettura di tutte le edizioni a cui è iscritto una certo utente, presenti nel calendario dei corsi
	 * se future = true, le edizioni lette devono essere solo quelle a partire dalla data in odierna e dell'anno corrente 
	 * se future = false devono essere lette tutte le edizioni
	 * le edizioni vengono individuate in base al idUtente
	 * se non vi sono edizioni per quella categoria o la categoria non esiste viene ritornata una lista vuota
	 */
	@Override
	public ArrayList<Edizione> select(String idUtente, boolean future) throws SQLException {
ArrayList<Edizione> edizioni=new ArrayList<Edizione>();
		
		if(future) {
			PreparedStatement ps=conn.prepareStatement("select * "
					+ "from calendario,catalogo,iscritti where"
					+ " calendario.id_corso=catalogo.id_corso "
					+ "and iscritti.id_utente = ? and "
					+ " calendario.id_edizione=iscritti.id_edizione and "
					+ "calendario.dataInizio >= ?");

			ps.setString(1, idUtente);
			ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
			ResultSet rs=ps.executeQuery();
			
			while(rs.next()){
				int idCategoria=rs.getInt("id_categoria");
				int idCorso=rs.getInt("id_corso");
				Date dataInizio=rs.getDate("dataInizio");
				int durata=rs.getInt("durata");
				String aula=rs.getString("aula");
				String docente=rs.getString("docente");
				String titolo =rs.getString("titolo");
				Integer maxPartecipanti = rs.getInt("numeroMaxPartecipanti");
				Double costo = rs.getDouble("costo");
				String descrizione=rs.getString("descrizione");
				int idEdizione=rs.getInt("id_edizione");

				Edizione e=new Edizione(idCorso,dataInizio,durata,aula,docente);
				e.setCodice(idEdizione);
				e.setCorso(new Corso(idCorso, titolo, idCategoria, maxPartecipanti, costo, descrizione));

				long dataM = dataInizio.getTime();
				long durataM= durata*86400000L;
				Date dataFine = new Date(dataM+durataM);
				java.util.Date d =  new java.util.Date();

				if (dataFine.before(d) )
					e.setTerminata(true);	
				
				edizioni.add(e);
			}
		}
			else {
				PreparedStatement ps=conn.prepareStatement("select * from calendario, catalogo, iscritti "
				+ "where calendario.id_corso = catalogo.id_corso and"
				+ " calendario.id_edizione = iscritti.id_edizione and "
				+ "id_utente=?");
		

		ps.setString(1, idUtente);
		
		ResultSet rs=ps.executeQuery();
		while(rs.next()){
			int idCategoria=rs.getInt("id_categoria");
			int idCorso=rs.getInt("id_corso");
			Date dataInizio=rs.getDate("dataInizio");
			int durata=rs.getInt("durata");
			String aula=rs.getString("aula");
			String docente=rs.getString("docente");
			String titolo =rs.getString("titolo");
			Integer maxPartecipanti = rs.getInt("numeroMaxPartecipanti");
			Double costo = rs.getDouble("costo");
			String descrizione=rs.getString("descrizione");
			int idEdizione=rs.getInt("id_edizione");

			Edizione e=new Edizione(idCorso,dataInizio,durata,aula,docente);
			e.setCodice(idEdizione);
			e.setCorso(new Corso(idCorso, titolo, idCategoria, maxPartecipanti, costo, descrizione));


			long dataM = dataInizio.getTime();
			long durataM= durata*86400000L;
			Date dataFine = new Date(dataM+durataM);
		

			if (dataFine.before(new java.util.Date()))
				e.setTerminata(true);	

			edizioni.add(e);

		}
		
	}

		return edizioni;

	}
	
//	public static void main(String[] args) throws Exception{
//		CalendarioDAO dao= new CalendarioDAOImpl();
//		Edizione ed  = new Edizione(88,new java.util.Date(),3,"Aula 1","Bettino Craxi");
////		System.out.println(dao.select(48));
////		System.out.println(dao.select());
////		System.out.println(dao.select("veronica"));
////		System.out.println(dao.selectEdizione(96));
////		System.out.println(dao.select(new java.util.Date(System.currentTimeMillis()-11*365*24*60*60*1000L),new java.util.Date()));
//		
////		System.out.println(dao.select(true));
////		System.out.println(dao.select(55, false));
////		System.out.println(dao.select("veronica", true));
//		
//		
//		
//		
////		dao.insert(ed);
////		dao.delete(94);
////		u.setCognome("Doria");
////		dao.delete("aa");
////		dao.update(u);
////		System.out.println(dao.select("marco81"));
//	}
}

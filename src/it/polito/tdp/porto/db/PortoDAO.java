package it.polito.tdp.porto.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import it.polito.tdp.porto.model.Author;
import it.polito.tdp.porto.model.Paper;

public class PortoDAO {

	/*
	 * Dato l'id ottengo l'autore.
	 */
	public Author getAutore(int id) {

		final String sql = "SELECT * FROM author where id=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {

				Author autore = new Author(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname"));
				rs.close();
				conn.close();
				return autore;
			}
			rs.close();
			conn.close();
			return null;

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}

	/*
	 * Dato l'id ottengo l'articolo.
	 */
	public Paper getArticolo(int eprintid) {

		final String sql = "SELECT * FROM paper where eprintid=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, eprintid);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				Paper paper = new Paper(rs.getInt("eprintid"), rs.getString("title"), rs.getString("issn"),
						rs.getString("publication"), rs.getString("type"), rs.getString("types"));
				rs.close();
				conn.close();
				return paper;
			}
			rs.close();
			conn.close();
			return null;

		} catch (SQLException e) {
			 e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}
	/*
	 * Dato l'id ottengo la lista degli id degli autori.
	 */
	public List<Author> getCreators(int eprintid, Map<Integer, Author> map) {

		final String sql = "SELECT * FROM creator WHERE eprintid=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, eprintid);

			ResultSet rs = st.executeQuery();
			List<Author> list = new LinkedList<>() ;
//			System.out.println(map);
			while (rs.next()) {
				
				int id = rs.getInt("authorid") ;
				list.add(map.get(id)) ;	
			}		
//			System.out.println(list);

			rs.close();
			conn.close();
			return list;

		} catch (SQLException e) {
			 e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
		
	}
	/*
 	ottengo la mappa di tutti quanti gli autori
 	 */
	public Map<Integer,Author> getAutori() {

		final String sql = "SELECT * FROM author";
		Map<Integer, Author> autori = new HashMap<Integer, Author>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int idAutore = rs.getInt("id");
				Author autore = new Author(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname"));
				autori.put(idAutore, autore);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return autori;
	}

	/**
	 * @return the map of all articles.
	 */
	
	public Map<Integer, Paper> getArticoli() {
		final String sql = "SELECT * FROM paper";
		Map<Integer, Paper> papers = new HashMap<Integer, Paper>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int idPaper = rs.getInt("eprintid");
				Paper paper = new Paper(rs.getInt("eprintid"), rs.getString("title"), rs.getString("issn"),
						rs.getString("publication"), rs.getString("type"), rs.getString("types"));				
				
				papers.put(idPaper, paper);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return papers ;
	}

	public void setArticoli(Map<Integer, Author> autoriMap, Map<Integer, Paper> articoliMap) {
		final String sql = "SELECT * FROM creator";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int idPaper = rs.getInt("eprintid");
				int idAuthor = rs.getInt("authorid");

				
				
				autoriMap.get(idAuthor).addArticolo(articoliMap.get(idPaper));
				articoliMap.get(idPaper).addAutore(autoriMap.get(idAuthor));
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}		
		
	}


}
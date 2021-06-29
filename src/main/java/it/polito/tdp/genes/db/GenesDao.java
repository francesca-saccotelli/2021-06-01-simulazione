package it.polito.tdp.genes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.genes.model.Adiacenza;
import it.polito.tdp.genes.model.Genes;


public class GenesDao {
	
	public void getAllGenes(Map<String,Genes>idMap){
		String sql = "SELECT DISTINCT GeneID, Essential, Chromosome FROM Genes";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getString("GeneID"))) {
				Genes g = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				idMap.put(g.getGeneId(), g);
				}
			}
			res.close();
			st.close();
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
    public List<Genes>getVertici(Map<String,Genes>idMap){
    	String sql="SELECT DISTINCT g.* "
    			+ "FROM genes AS g "
    			+ "WHERE g.Essential='Essential' "
    			+ "GROUP BY g.GeneID";
    	List<Genes>vertici=new ArrayList<Genes>();
    	try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
		    ResultSet res = st.executeQuery() ;
		    while(res.next()) {
		    	if(idMap.containsKey(res.getString("g.GeneID"))) {
		    		vertici.add(idMap.get(res.getString("g.GeneID")));
		    	}
		    }
		    conn.close();
		    return vertici;
		}catch(SQLException e) {
			e.printStackTrace();
			return null ;
		}
    }

	public List<Adiacenza>getAdiacenze(Map<String,Genes>idMap){
		String sql="SELECT g1.GeneID,g2.GeneID, ABS(i.Expression_Corr) AS peso "
				+ "FROM genes AS g1, genes AS g2,interactions AS i "
				+ "WHERE g1.GeneID>g2.GeneID AND "
				+ "((i.GeneID1=g1.GeneID AND i.GeneID2=g2.GeneID) OR "
				+ "(i.GeneID1=g2.GeneID AND i.GeneID2=g1.GeneID)) AND "
				+ "g1.Essential='Essential' AND g2.Essential='Essential' "
				+ "GROUP BY g1.GeneID,g2.GeneID";
		List<Adiacenza>adiacenze=new ArrayList<Adiacenza>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
		    ResultSet res = st.executeQuery() ;
		    while(res.next()) {
		    	adiacenze.add(new Adiacenza(idMap.get(res.getString("g1.GeneID")),idMap.get(res.getString("g2.GeneID")),res.getDouble("peso")));
		    }
		    res.close();
			st.close();
			conn.close();
			return adiacenze;
		}catch(SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
}

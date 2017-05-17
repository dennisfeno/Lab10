package it.polito.tdp.porto.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import it.polito.tdp.porto.db.PortoDAO;

public class Model {

	private Map<Integer, Author> autoriMap;
	private Map<Integer, Paper> articoliMap;

	/*
	private List<Link> pathEdgeList = null;
	private double pathTempoTotale = 0;
	 */
	private PortoDAO portoDAO ;

	// Undirected not weighted Graph
	private UndirectedGraph<Author, DefaultEdge> grafo = null;

	public Model() {
		portoDAO = new PortoDAO();
	}
	
	public List<Author> getAutori() {
		if (autoriMap == null)
			autoriMap = portoDAO.getAutori();
		return new ArrayList<Author>(autoriMap.values());
	}
	
	public List<Paper> getArticoli() {
		if (articoliMap == null)
			articoliMap = portoDAO.getArticoli();
		return new ArrayList<Paper>(articoliMap.values());
	}
	
	public void creaGrafo() {
		System.out.println("Building the graph...");
		articoliMap = portoDAO.getArticoli();
		autoriMap = portoDAO.getAutori();
		portoDAO.setArticoli(autoriMap, articoliMap);


		// not Directed and not Weighted
		grafo = new SimpleGraph<Author, DefaultEdge>(DefaultEdge.class);

		// FASE1: Aggiungo un vertice per ogni autore
		Graphs.addAllVertices(grafo, autoriMap.values());

		// FASE2: Aggiungo tutte le connessioni tra tutte le fermate
//		System.out.println(articoliMap.keySet().size());
		for(int id : articoliMap.keySet()){
			
//			System.out.println(id);
			
			List<Author> l = portoDAO.getCreators(id, autoriMap) ;
					
			if(l.size()>1){
				for (Author a : l ){
					for (Author b : l ){
						if(!a.equals(b))
							if(!grafo.containsEdge(a, b) && !grafo.containsEdge(b, a))
								grafo.addEdge(a, b) ; 
					}
				}
			}
		}
		
		System.out.println("Grafo creato: " + grafo.vertexSet().size() + " nodi, " + grafo.edgeSet().size() + " archi");
	}

	public List<Author> getCoautori(Author autore) {

		//List<Author> coautori = new LinkedList<>(); 
		
		return Graphs.neighborListOf(this.getGrafo(), autore) ; 
		
	}

	public UndirectedGraph<Author, DefaultEdge> getGrafo() {
		if(grafo==null)
			this.creaGrafo();
		return grafo;
	}

	
	
	public List<DefaultEdge> getPercorso(Author a1, Author a2) {

		DijkstraShortestPath<Author, DefaultEdge> dijkstra = new DijkstraShortestPath<>(grafo, a1, a2);
		
		return dijkstra.getPathEdgeList() ; 
		
	}

	public String getStringPercorso(Author a1, Author a2) {

		List<DefaultEdge> pathEdgeList = this.getPercorso(a1, a2) ; 
		
		if (pathEdgeList == null)
			throw new RuntimeException("Non è stato creato un percorso.");

		StringBuilder risultato = new StringBuilder();

		Author prev = a1 ; 
		
		for (DefaultEdge d : pathEdgeList){
			
			Author now = Graphs.getOppositeVertex(this.getGrafo(), d, prev);
			
			List<Paper> support = new ArrayList<Paper>(prev.getArticoli()) ;
			/**
			 * qua è importante fare la NEW altrimenti agisce sull'array nella mappa 
			 */
			support.retainAll(now.getArticoli());
			
//			System.out.println(support.toString());
			
//			risultato.append(prev.toString() + " - " + now.toString()) ; 
			risultato.append(prev.toString() + " - " + now.toString()+" / "+ support.get(0)+"\n\n") ; 

			prev = now ;
		}

		return risultato.toString();
		
	}
	
	
}

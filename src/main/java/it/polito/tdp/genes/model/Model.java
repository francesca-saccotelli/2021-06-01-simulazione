package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.genes.db.GenesDao;

public class Model {
	
	GenesDao dao;
	private Graph<Genes,DefaultWeightedEdge>grafo;
	Map<String,Genes>idMap;
	
	public Model() {
		dao=new GenesDao();
		idMap=new HashMap<>();
		this.dao.getAllGenes(idMap);
	}
	
	public void creaGrafo() {
		grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(idMap));
		for(Adiacenza a:this.dao.getAdiacenze(idMap)) {
			if(this.grafo.containsVertex(a.getG1())&& this.grafo.containsVertex(a.getG2())) {
				if(a.getG1().getChromosome()!=a.getG2().getChromosome()) {
				Graphs.addEdgeWithVertices(this.grafo, a.getG1(), a.getG2(), a.getPeso());
				}else {
					Graphs.addEdgeWithVertices(this.grafo, a.getG1(), a.getG2(), (2*a.getPeso()));
				}
			}
		}
	}
	public int vertexNumber() {
		return this.grafo.vertexSet().size();
	}
	
	public int edgeNumber() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Genes>getVertici(){
		List<Genes>vertici=new ArrayList<Genes>(this.grafo.vertexSet());
		return vertici;
	}
	
	public Graph<Genes,DefaultWeightedEdge> getGrafo() {
		return this.grafo;
	}
	public List<ComponenteConnessa>getConnessi(Genes genes){
		List<Genes>vicini=Graphs.neighborListOf(this.grafo,genes);
		List<ComponenteConnessa>lista=new ArrayList<>();
		for(Genes v: vicini) {
			double peso = this.grafo.getEdgeWeight(this.grafo.getEdge(genes, v));
			lista.add(new ComponenteConnessa(v, peso));
		}
        Collections.sort(lista);
		return lista;
	} 
	
}

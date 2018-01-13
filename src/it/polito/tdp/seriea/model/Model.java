package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;
import it.polito.tdp.seriea.exception.SerieAException;

public class Model {

	private final static SerieADAO dao = new SerieADAO();
	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> graph;
	private Map<Integer, List<Team>> squadre = new HashMap<Integer, List<Team>>();

	public List<Season> allSeasons() throws SerieAException {
		return this.dao.listSeasons();
	}

	private SimpleDirectedWeightedGraph<Team, DefaultWeightedEdge> creaGrafo(int seasonId) throws SerieAException {

		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		List<Team> vertexList = listTeamsInBySeason(seasonId);
		System.out.println("<creaGrafo> numero vertici/squadre: " + vertexList.size());
		// crea i vertici del grafo
		Graphs.addAllVertices(graph, vertexList);

		// crea gli archi del grafo --versione 3
		// faccio fare tutto il lavoro al dao
		// che mi dà la lista della coppia dei vertici
		List<Match> matches = dao.listMatchBySeason(seasonId);
		for (Match m : matches) {
			DefaultWeightedEdge dwe = graph.addEdge(m.getHomeTeam(), m.getAwayTeam());
			this.graph.setEdgeWeight(dwe, "H".equals(m.getFtr()) ? 1 : ("D".equals(m.getFtr()) ? 0 : -1));
		}

		return this.graph;

	}

	private List<Team> listTeamsInBySeason(int seasonId) throws SerieAException {
		if (this.squadre.containsKey(seasonId)) {
			return this.squadre.get(seasonId);
		}
		List<Team> squadre = this.dao.listTeamsBySeason(seasonId);
		this.squadre.put(seasonId, squadre);
		return squadre;
	}

	public List<Team> classifica(int seasonId) throws SerieAException {
		creaGrafo(seasonId);

		List<Team> squadre = new ArrayList<Team>();
		squadre.addAll(listTeamsInBySeason(seasonId));

		for (Team t : squadre) {

			for (DefaultWeightedEdge arch : graph.outgoingEdgesOf(t)) {
				if (this.graph.getEdgeWeight(arch) > 0) {
					t.addPunti(3);

				} else if (this.graph.getEdgeWeight(arch) == 0) {
					t.addPunti(1);
				}
			}

			for (DefaultWeightedEdge arch : graph.incomingEdgesOf(t)) {
				if (this.graph.getEdgeWeight(arch) < 0) {
					t.addPunti(3);

				} else if (this.graph.getEdgeWeight(arch) == 0) {
					t.addPunti(1);
				}
			}
		}

		Collections.sort(squadre, new SquadreClassificaComparator());
		return squadre;
	}

	public String domino(Team startTeam) {

		List<DefaultWeightedEdge> soluzioneParziale = new ArrayList<DefaultWeightedEdge>();
		List<DefaultWeightedEdge> soluzioneOttima = new ArrayList<DefaultWeightedEdge>();

		recursive(soluzioneParziale, soluzioneOttima, startTeam);

		StringBuffer domino = new StringBuffer();
		for (DefaultWeightedEdge match : soluzioneOttima) {
			domino.append(String.format("%s-%s, ", this.graph.getEdgeSource(match), this.graph.getEdgeTarget(match)));
		}

		return domino.toString();

	}

	private void recursive(List<DefaultWeightedEdge> soluzioneParziale, List<DefaultWeightedEdge> soluzioneOttima,
			Team startTeam) {

		if (checkStopConditions(soluzioneParziale, soluzioneOttima, startTeam)) {
			// toDO
			System.out.println("<recursive> soluzione ottima: " + soluzioneOttima);
			soluzioneOttima.clear();
			soluzioneOttima.addAll(soluzioneParziale);
			System.out.println("<recursive> soluzione ottima: " + soluzioneOttima);
			return;
		}

		for (DefaultWeightedEdge match : graph.outgoingEdgesOf(startTeam)) {
			System.out.println("<recursive> " + match);
			if (!soluzioneParziale.contains(match) && this.graph.getEdgeWeight(match) == 1) {
				// genera soluzione parziale
				soluzioneParziale.add(match);
				startTeam = this.graph.getEdgeTarget(match);
				recursive(soluzioneParziale, soluzioneOttima, startTeam);
				startTeam = this.graph.getEdgeSource(match);
				soluzioneParziale.remove(match);

			}

		}

	}

	private boolean checkStopConditions(List<DefaultWeightedEdge> soluzioneParziale,
			List<DefaultWeightedEdge> soluzioneOttima, Team startTeam) {

		int availableMatches = 0;
		for (DefaultWeightedEdge match : graph.outgoingEdgesOf(startTeam)) {
			if (!soluzioneParziale.contains(match) && this.graph.getEdgeWeight(match) == 1) {
				availableMatches++;
			}
		}
		System.out.println("<checkStopConditions> availableMatches: " + availableMatches);

		return availableMatches == 0 && soluzioneParziale.size() > soluzioneOttima.size();
	}

	class SquadreClassificaComparator implements Comparator<Team> {

		@Override
		public int compare(Team o1, Team o2) {
			// TODO Auto-generated method stub
			return o2.getPunteggio() - o1.getPunteggio();
		}

	}

}

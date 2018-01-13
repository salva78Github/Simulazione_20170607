package it.polito.tdp.seriea.model;

import java.util.List;

import it.polito.tdp.seriea.exception.SerieAException;

public class TestModel {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			Model model = new Model();
			List<Team> classifica =  model.classifica(2013);
			
			for(Team t : classifica){
				System.out.println("<" + t.getTeam() + "> --> " + t.getPunteggio() + "\n");
			}
			
			System.out.println("\n\n" + model.domino(new Team("Juventus")));
			
			
			
		} catch (SerieAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

}

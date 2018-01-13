/**
 * Sample Skeleton for 'SerieA.fxml' Controller Class
 */

package it.polito.tdp.seriea;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.seriea.exception.SerieAException;
import it.polito.tdp.seriea.model.Model;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class SerieAController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxSeason"
    private ChoiceBox<Season> boxSeason; // Value injected by FXMLLoader

    @FXML // fx:id="boxTeam"
    private ChoiceBox<?> boxTeam; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

	private Model model;

    @FXML
    void handleCarica(ActionEvent event) {
    	Season s = this.boxSeason.getValue();
    	System.out.println("<handleCarica> stagione: " + s.getDescription());
    	
    	try {
			List<Team> classifica = this.model.classifica(s.getSeason());
			this.txtResult.setText("CLASSIFICA stagione " + s.getDescription() + "\n\n");
			for(Team t : classifica){
				txtResult.appendText(String.format("%s --> %d \n", t.getTeam(), t.getPunteggio()));
			}
			
		} catch (SerieAException e) {
			e.printStackTrace();
			this.txtResult.setText("Errore nel calcolo della classifica.");
		}
    	
    	
    	
    }

    @FXML
    void handleDomino(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxSeason != null : "fx:id=\"boxSeason\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert boxTeam != null : "fx:id=\"boxTeam\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'SerieA.fxml'.";
    }

	public void setModel(Model model) {
		this.model = model;
		try {
			this.boxSeason.getItems().addAll(this.model.allSeasons());
		} catch (SerieAException e) {
			this.txtResult.setText("Errore nel caricamento delle stagioni.");
		}
	}
}

package it.polito.tdp.porto;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import it.polito.tdp.porto.model.Author;
import it.polito.tdp.porto.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class PortoController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Author> boxPrimo;

    @FXML
    private ComboBox<Author> boxSecondo;

    @FXML
    private TextArea txtResult;

	private Model model;

    @FXML
    void handleCoautori(ActionEvent event) {

		Author a1 = boxPrimo.getValue();

		if (a1 == null){ // || a2 == null) {
			txtResult.setText("\nInserire una autore di partenza.\n");
			return;
		}
	
		
		try {
			List<Author> coAutori = model.getCoautori(a1) ;
			
			if(coAutori == null){
				
				txtResult.appendText("\nNessun co-autore per "+ a1+".\n") ; 
				
			}
			else{
				txtResult.appendText("\nCoautori per "+ a1+": \n") ; 
				for (Author a : coAutori){
					txtResult.appendText(a + ", " ); 
				}	
				txtResult.appendText(".\n");
			}
			
		} catch (RuntimeException e) {
			txtResult.setText(e.getMessage());
		}
    
    }

    @FXML
    void handleSequenza(ActionEvent event) {
		Author a1 = boxPrimo.getValue();
		Author a2 = boxSecondo.getValue();

		if (a1 == null || a2 == null) {
			txtResult.appendText("\nInserire una autore di partenza.\n");
			return;
		}

		if (a1.equals(a2)) {
			txtResult.appendText("\nInserire due autori diversi.\n");
			return;
		}
    	
		
		try {
			
			if(model.getCoautori(a1).contains(a2)){
				txtResult.appendText("\nSono co-autori: non posso generare il cammino.\n") ;
			}
			else{
			
				txtResult.appendText("Percorso: \n") ;
				txtResult.appendText( model.getStringPercorso(a1,a2) ); 
				txtResult.appendText(".\n");
			}
			
		} catch (RuntimeException e) {
			txtResult.setText(e.getMessage());
		}
    
    }

    @FXML
    void initialize() {
        assert boxPrimo != null : "fx:id=\"boxPrimo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert boxSecondo != null : "fx:id=\"boxSecondo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Porto.fxml'.";

    }

	public void setModel(Model model) {
		this.model=model ; 
		try {
			model.creaGrafo();

			List<Author> autori = model.getAutori() ;
			boxPrimo.getItems().addAll(autori);
			boxSecondo.getItems().addAll(autori);
			
			if(boxPrimo.getItems().size() > 0 && boxSecondo.getItems().size() > 0) {
	        	boxPrimo.setValue(boxPrimo.getItems().get(0)); 
	        	boxSecondo.setValue(boxSecondo.getItems().get(1)); 
			}
			
		} catch (RuntimeException e) {
			txtResult.setText(e.getMessage());
		}
	}
	
}

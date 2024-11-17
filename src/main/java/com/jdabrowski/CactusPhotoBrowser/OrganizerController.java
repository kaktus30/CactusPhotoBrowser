package com.jdabrowski.CactusPhotoBrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import com.jdabrowski.CactusPhotoBrowser.models.ImageFile;
import com.jdabrowski.CactusPhotoBrowser.services.ImageService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;

public class OrganizerController {

	@FXML
	private Label path;
	
	@FXML
	private Label done;
	
	@FXML
	private TextField prefix_id;
	
	private final ImageService imageService = new ImageService();
		
	
	@FXML
	private void selectToOrganize() {
		try {
			
			DirectoryChooser directoryChooser = new DirectoryChooser();
	        
			File selectedDirectory = directoryChooser.showDialog(null);

	        if (selectedDirectory != null) {
	            
	        	path.visibleProperty().set(true);
	        	path.setText(selectedDirectory.getAbsolutePath());
	        } 
		}
		catch(Exception e) {
			Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Error");
    		alert.setHeaderText(e.toString());			
			e.printStackTrace();
		}
	}
	
	@FXML
	private void organizeImages() {
		try {
			if(!path.getText().equals("path")) {
				String path = this.path.getText().toString();
				String prefix = prefix_id.getText().toString();
				
				// Get files list from selected path
				ArrayList<ImageFile> list = imageService.imagesList(path);
				 
				// Sort files list by date
		        list.sort(Comparator.comparing(ImageFile::getDate));
		        
		        //Create new directory
		        imageService.SaveFileList(list, path, prefix);
		        
		        //Set information of succes
		        done.setText("Done!");
		        done.setVisible(true);
			}else {
				Alert alert = new Alert(AlertType.INFORMATION);
	    		alert.setTitle("Alert");
	    		alert.setHeaderText("Please choose folder with images to organize");	
	    		alert.showAndWait();
			}
		}
		catch(Exception e){
			Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText(e.toString());			
			e.printStackTrace();
			alert.showAndWait();
		}
	}
}

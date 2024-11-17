package com.jdabrowski.CactusPhotoBrowser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import com.jdabrowski.CactusPhotoBrowser.models.ImageFile;
import com.jdabrowski.CactusPhotoBrowser.services.ImageService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {
	
	private final ImageFile imagefile = new ImageFile();
	
	private final ImageService imageService = new ImageService(); 
		
	private ArrayList<String> imageFiles = new ArrayList<>();
	
	
	
	private double targetHeight;
	private double targetWidth;
	
	@FXML
	private ImageView imgaeViewer1;
	
	
	@FXML
	private StackPane stackPane1;
	
	
	@FXML
    public void initialize() {
		// Use cache of GPU to restore image
		this.imgaeViewer1.setCache(true);
    }
	
	//Create listener to set window dimension
	public void setStage(Stage stage) {
        // Listening for changes of width
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
        	this.targetWidth = (double)newValue;

        });

        // Listening for changes of height        
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
        	this.targetHeight= (double)newValue -100; // I minus 100px because of the menus size

        });
    }
	
	
	/**
	 * Load image to imagewiver control
	 */
	@FXML
	private void viewImage() {
		// FileChooser for select image
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        
        // Set path of the file into object
        imagefile.setPath(Paths.get(fileChooser.showOpenDialog(null).getPath()));
        // Set file name from path file
        imagefile.setName(imagefile.getPath().getFileName().toString());
        
               
        // Load image into imageViewer1 by use imageService method 
        imageService.loadImage(imgaeViewer1, imagefile.getPath(), targetHeight);
        
    	
        // Get parent directory of loaded file
        Path parentDir = imagefile.getPath().getParent();
        
        // Clean imageFiles list
        if(!this.imageFiles.isEmpty()) {
        	this.imageFiles.clear();
        }
            

        // Load image files names into list
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parentDir, "*.{jpg,JPG}")) {
        	
            for (Path entry : stream) {
                 // Pobieranie daty utworzenia pliku
                 BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                    
                 // Dodawanie pliku do listy wraz z datą utworzenia
                 this.imageFiles.add(entry.getFileName().toString());
             }
        } catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}


                
	}	

	
	@FXML
	private void nextImg() {	
		
		String paretDir = imagefile.getPath().getParent().toString();
        

        imageService.loadImage(imgaeViewer1, Paths.get(paretDir+"/"+getNextFileName()), targetHeight);
        
        imagefile.setPath(Paths.get(paretDir+"/"+getNextFileName()));
    	imagefile.setName(getNextFileName());
        

	}
	
	@FXML
	private void previoustImg() {
		
		String paretDir = imagefile.getPath().getParent().toString();
               
        //loadImg(Paths.get(pathToFile.getParent().toString()+"/"+getPreviousFileName()));
		imageService.loadImage(imgaeViewer1, Paths.get(paretDir+"/"+getPreviousFileName()), targetHeight);
        
        imagefile.setPath(Paths.get(paretDir+"/"+getPreviousFileName()));
    	imagefile.setName(getPreviousFileName());


	}
	
	private String getNextFileName() {
        for (int i = 0; i < this.imageFiles.size(); i++) {
            if (this.imageFiles.get(i).equals(imagefile.getName())) {
                // Oblicz indeks następnego pliku (lub 0, jeśli to ostatni element)
                int nextIndex = (i + 1) % imageFiles.size();
                return imageFiles.get(nextIndex);
            }
        }
        return null; // Zwraca null, jeśli pliku o podanej nazwie nie znaleziono
    }
	
	private String getPreviousFileName() {
		for (int i = 0; i < this.imageFiles.size(); i++) {
	        if (this.imageFiles.get(i).equals(imagefile.getName())) {
	            // Oblicz poprzedni indeks (lub ostatni indeks, jeśli i = 0)
	            int previousIndex = (i - 1 >= 0) ? i - 1 : imageFiles.size() - 1;
	            return imageFiles.get(previousIndex);
	        }
	    }
	    return null; // Zwraca null, jeśli pliku o podanej nazwie nie znaleziono
    }
	
	@FXML
	private void zoomIn() {
		Double height = imgaeViewer1.getFitHeight();
		Double width = imgaeViewer1.getFitWidth();
		
		height = height+ (height*0.1);
		width = width + (width*0.1);
		
		imgaeViewer1.setFitHeight(height);
		imgaeViewer1.setFitWidth(width);
	}
	
	@FXML
	private void zoomOut() {
		Double height = imgaeViewer1.getFitHeight();
		Double width = imgaeViewer1.getFitWidth();
		
		height = height - (height*0.1);
		width = width -  (width*0.1);
		
		imgaeViewer1.setFitHeight(height);
		imgaeViewer1.setFitWidth(width);
	}
	
	/**
	 * This function open new scene with image orgainzer 
	 */
	@FXML
	private void organize() {
		try {
			
			Parent root = FXMLLoader.load(getClass().getResource("organize.fxml"));
			
			Scene scene = new Scene(root);
	        Stage stage = new Stage();
	        stage.setTitle("Image Organizer");
	        stage.setScene(scene);
	        stage.initModality(Modality.NONE);
	        stage.show();

		}
		catch(Exception e){
			
			Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Error");
    		alert.setHeaderText(e.toString());			
			e.printStackTrace();
			alert.showAndWait();
		}
	}
	
}

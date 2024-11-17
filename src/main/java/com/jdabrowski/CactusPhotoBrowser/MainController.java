package com.jdabrowski.CactusPhotoBrowser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import com.jdabrowski.CactusPhotoBrowser.models.ImageFile;
import com.jdabrowski.CactusPhotoBrowser.services.ImageService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class MainController {
	
	private final ImageFile imagefile = new ImageFile();
	
	private final ImageService imageService = new ImageService(); 
	
	private Path pathToFile;
	
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
	
	public void setStage(Stage stage) {
        // Nasłuchiwanie zmiany szerokości okna
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
        	this.targetWidth = (double)newValue;

        });

        // Nasłuchiwanie zmiany wysokości okna
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
        	this.targetHeight= (double)newValue -100;

        });
    }
	
	
	/**
	 * Load image to imagewiver control
	 */
	@FXML
	private void viewImage() {
		// FileChooser do wybierania plików z obrazami
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        
        // Przycisk do załadowania obrazu
        imagefile.setPath(Paths.get(fileChooser.showOpenDialog(null).getPath()));
        //this.pathToFile =  Paths.get(fileChooser.showOpenDialog(null).getPath());
        //System.out.println(pathToFile);
        //Path path = Paths.get(fileChooser.showOpenDialog(null).getPath());
  
        Path parentDir = imagefile.getPath().getParent();
        
        //loadImg(this.pathToFile);
        imageService.loadImage(imgaeViewer1, imagefile.getPath(), targetHeight);
        
    	this.imagefile.setName(imagefile.getPath().getFileName().toString());
        
        if(!this.imageFiles.isEmpty()) {
        	this.imageFiles.clear();
        }
        
        
        if(this.imageFiles.isEmpty()) {
        	// Pobieranie listy plików
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

                
	}	
	/*
	private void loadImg(Path path) {
		System.out.println(path.toUri().toString());
		this.pathToFile = path;
		if (path != null) {
            try {
                
            	
            	// Load the image from the file            	
            	Image image = new Image(path.toUri().toString());
            	this.imagefile.setPath(path);
            	this.imagefile.setName(path.getFileName().toString());
                
            	// Get dimension of loaded image
            	double originalWidth = image.getWidth();
            	double originalHeight = image.getHeight();
            	image= null;
            	
            	//Get the height of are to view picture
            	//double targetHeight = stackPane1.getHeight();
            	
            	if(originalHeight<this.targetHeight){
            		image = new Image(path.toUri().toString(), originalWidth, originalHeight, true, true);
            	}else {
            		// Calculate new width from target height used proportion
                	double newTargetWidth = (this.targetHeight / originalHeight) * originalWidth;

                	// Overwrite content of image 
                	image = new Image(path.toUri().toString(), newTargetWidth, this.targetHeight, true, true);
                	
            	}
            	this.imgaeViewer1.setImage(null);
            	this.imgaeViewer1.setDisable(true);
            	          
            	ImageService imageService = new ImageService();
                      	
                int orientation = imageService.getOrientation(path);
                if (orientation >1) {
                	this.imgaeViewer1.setImage(imageService.rotateImage(image, orientation));
                	this.imgaeViewer1.setDisable(false);
                }else {
                	this.imgaeViewer1.setImage(image);
                	this.imgaeViewer1.setDisable(false);
                }
                
            	
                // Cleaning data of image (bitmap)
                image = null;
                //imageService = null;
                //System.gc();
                //Ustawienie orientacji zdjęcia
                
                // this.imgaeViewer1.setPreserveRatio(true);  // Zachowanie proporcji        
                
                // imgaeViewer1.setFitHeight(targetHeight);
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	} */
	
	@FXML
	private void nextImg() {	
		
		String paretDir = imagefile.getPath().getParent().toString();
        
        //loadImg(Paths.get(pathToFile.getParent().toString()+"/"+getNextFileName()));
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

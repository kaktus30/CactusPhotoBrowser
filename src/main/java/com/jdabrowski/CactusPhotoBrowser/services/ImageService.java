package com.jdabrowski.CactusPhotoBrowser.services;


import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.jdabrowski.CactusPhotoBrowser.models.ImageFile;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ImageService{
	// Construktor
	public ImageService() {
		super();
	}

	/**
	 * Return list of image files in provide folder as argument 
	 * @param directory
	 * @return
	 */
	public ArrayList<ImageFile> imagesList(String directory) {
		
		ArrayList<ImageFile> imageFiles = new ArrayList<>();

        // Ścieżka do katalogu
        Path path = Paths.get(directory);

        // Pobieranie listy plików
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{jpg,JPG}")) {
            for (Path entry : stream) {
                // Pobieranie daty utworzenia pliku
                BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                FileTime creationDate = attrs.lastModifiedTime();
                
                // Dodawanie pliku do listy wraz z datą utworzenia
                imageFiles.add(new ImageFile(entry.getFileName().toString(), creationDate));
            }
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return imageFiles;

	}

	/**
	 * Save all files from priove file list. Method don't affect files, just copy files into new folder
	 * "OrganizedPhotos" with rename using prefix
	 * @param fileList
	 * @param directory
	 * @param prefix
	 */
	public void SaveFileList(ArrayList<ImageFile> fileList, String directory, String prefix) {	
		
		String path = directory+"//OrganizedPhotos";
		int i = 1;
		new File(path).mkdirs();
		
		for (ImageFile imageFile : fileList) {
			Path sourcePath = Paths.get(directory+"//"+imageFile.getName());
			Path targetPath = Paths.get(path+"//"+prefix+"_"+i+".JPG");
			try {
				Files.copy(sourcePath, targetPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            i++; 
        }
		
	}
	
	/**
	 * Return value of orientation the image
	 * 1 - normal orientation,
	 * 6 - rotated by 90 degrees
	 * 3 - rotaded by 180 degrees
	 * 8 - rotaded by 270 degrees
	 * @param path
	 * @return
	 */
	public int getOrientation(Path path) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(Files.newInputStream(path));
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1; // Return normal orientation if there aren't data about orientation
    }
	
	
	
	public Image rotateImage(Image image, int orientation) {
		
		int angle =0;
		
		switch (orientation) {
        case 6: // Rotate 90 degrees right 
            angle =90;
            break;
        case 3: // Rotate 180 degrees
            angle =180;
            break;
        case 8: //  Rotate 90 degrees left 
            angle=270;
            break;
        default: // Don't rotate
            angle =0;
		}		
		
		
		int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage rotatedImage;
        PixelReader reader = image.getPixelReader();

        // Determines the size of the rotated image based on the angle
        if (angle == 90 || angle == 270) {
            rotatedImage = new WritableImage(height, width);
        } else {
            rotatedImage = new WritableImage(width, height);
        }
        
        PixelWriter writer = rotatedImage.getPixelWriter();

        // Rotate bitmap base on angle
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (angle) {
                    case 90:
                        writer.setArgb(height - y - 1, x, reader.getArgb(x, y));
                        break;
                    case 180:
                        writer.setArgb(width - x - 1, height - y - 1, reader.getArgb(x, y));
                        break;
                    case 270:
                        writer.setArgb(y, width - x - 1, reader.getArgb(x, y));
                        break;
                    default:
                        writer.setArgb(x, y, reader.getArgb(x, y));
                        break;
                }
            }
        }

        return rotatedImage;
    }
	
	public void loadImage(ImageView imageView, Path path, double targetHeight) {
		
		if (path != null) {
            try {
                            	
            	// Load the image from the file            	
            	Image image = new Image(path.toUri().toString());

                
            	// Get dimension of loaded image
            	double originalWidth = image.getWidth();
            	double originalHeight = image.getHeight();
            	image= null;
            	
            	//Get the height of are to view picture
            	//double targetHeight = stackPane1.getHeight();
            	
            	if(originalHeight<targetHeight){
            		image = new Image(path.toUri().toString(), originalWidth, originalHeight, true, true);
            	}else {
            		// Calculate new width from target height used proportion
                	double newTargetWidth = (targetHeight / originalHeight) * originalWidth;

                	// Overwrite content of image 
                	image = new Image(path.toUri().toString(), newTargetWidth, targetHeight, true, true);
                	
            	}
            	imageView.setImage(null);
            	
                      	
                int orientation = getOrientation(path);
                if (orientation >1) {
                	imageView.setImage(rotateImage(image, orientation));
                	

                }else {
                	imageView.setImage(image);
                	imageView.setFitHeight(image.getHeight());
                	imageView.setFitWidth(image.getWidth());
                }
                
            	
                // Cleaning data of image (bitmap)
                image = null;
                System.gc();

                
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		
	}
	
	

}

package com.jdabrowski.CactusPhotoBrowser.models;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import javafx.scene.image.Image;

public class ImageFile {
	
	private String name;
	private FileTime date;
	private Path path;
	
	public String getName() {
		return name;
	}	
		
	public ImageFile() {
		super();
	}
	
	public ImageFile(String name, FileTime date) {
		super();
		this.name = name;
		this.date = date;
	}

	public ImageFile(Image image, Path path) {
		super();
		this.path = path;
	}

	public void setName(String name) {
		this.name = name;
	}
	public FileTime getDate() {
		return date;
	}
	public void setDate(FileTime date) {
		this.date = date;
	}

	public Path getPath() {
		return path;
	}


	public void setPath(Path path) {
		this.path = path;
	}
	
	
	
	

}

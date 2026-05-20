package app.util;

import java.io.InputStream;

import app.exception.ResourceLoadingException;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class AppUtils{
	
	public VBox createResultBox(TextField resultField, Button copyButton, Label errorLabel) {
	    HBox resultBox = new HBox(10);
	    resultBox.setAlignment(Pos.CENTER);
	    HBox.setHgrow(resultField, Priority.ALWAYS);
	    resultField.setMaxWidth(Double.MAX_VALUE);
	    resultField.setAlignment(Pos.CENTER);
	    copyButton.setMinWidth(35);
	    resultBox.getChildren().addAll(resultField, copyButton);
	    
	    HBox errorBox = new HBox();
	    errorBox.setAlignment(Pos.CENTER);
	    HBox.setHgrow(errorLabel, Priority.ALWAYS);
	    errorLabel.setMaxWidth(Double.MAX_VALUE);
	    errorLabel.setAlignment(Pos.CENTER);
	    errorBox.getChildren().add(errorLabel);
	    
	    VBox container = new VBox(2); 
	    container.setAlignment(Pos.CENTER);
	    container.getChildren().addAll(resultBox, errorBox);
	    
	    resultField.setVisible(false);
	    copyButton.setVisible(false);
	    errorLabel.setVisible(false);
	    
	    resultField.setManaged(false);
	    copyButton.setManaged(false);
	    errorLabel.setManaged(false);
	    
	    return container;
	}

    public ImageView loadImage(String resourcePath) {
        InputStream stream = AppUtils.class.getResourceAsStream(resourcePath);
        if (stream == null) 
            throw new ResourceLoadingException(resourcePath);
        
        Image image = new Image(stream);
        return new ImageView(image);
    }

    public ImageView loadImage(String resourcePath, double fitWidth, double fitHeight) {
        ImageView imageView = loadImage(resourcePath);
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    public void applyInvertEffect(Node node, boolean inverted) {
        node.setEffect(inverted ? new ColorAdjust(0, 0, 1, 0) : null);
    }
}

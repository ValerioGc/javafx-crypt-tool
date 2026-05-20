package app.gui.builder.component;

import app.text.AppMessages;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


public class CustomRadioBuilder {

    public static final String OPERATION_ENCRYPT = "encrypt";
    public static final String OPERATION_DECRYPT = "decrypt";
    private static final String MODE_TOGGLE_BUTTON_CLASS = "mode_toggle_btn";
    private static final String MODE_TOGGLE_ACTIVE_CLASS = "mode_toggle_btn_active";

    private Button encryptBtn;
    private Button decryptBtn;
    private String selectedOperation;

    public HBox createRadioButtonsRow() {
        selectedOperation = OPERATION_ENCRYPT;

        encryptBtn = new Button(AppMessages.radioEncrypt());
        decryptBtn = new Button(AppMessages.radioDecrypt());

        encryptBtn.getStyleClass().addAll(MODE_TOGGLE_BUTTON_CLASS, MODE_TOGGLE_ACTIVE_CLASS);
        decryptBtn.getStyleClass().add(MODE_TOGGLE_BUTTON_CLASS);

        encryptBtn.setMaxWidth(Double.MAX_VALUE);
        decryptBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(encryptBtn, Priority.ALWAYS);
        HBox.setHgrow(decryptBtn, Priority.ALWAYS);

        encryptBtn.setCursor(Cursor.HAND);
        decryptBtn.setCursor(Cursor.HAND);

        encryptBtn.setOnAction(e -> {
            selectedOperation = OPERATION_ENCRYPT;
            if (!encryptBtn.getStyleClass().contains(MODE_TOGGLE_ACTIVE_CLASS))
                encryptBtn.getStyleClass().add(MODE_TOGGLE_ACTIVE_CLASS);
            
            decryptBtn.getStyleClass().remove(MODE_TOGGLE_ACTIVE_CLASS);
        });

        decryptBtn.setOnAction(e -> {
            selectedOperation = OPERATION_DECRYPT;
            if (!decryptBtn.getStyleClass().contains(MODE_TOGGLE_ACTIVE_CLASS))
                decryptBtn.getStyleClass().add(MODE_TOGGLE_ACTIVE_CLASS);
           
            encryptBtn.getStyleClass().remove(MODE_TOGGLE_ACTIVE_CLASS);
        });

        HBox container = new HBox(encryptBtn, decryptBtn);
        container.setMaxWidth(Double.MAX_VALUE);
        container.getStyleClass().add("mode_toggle_container");
        return container;
    }

    public String getSelectedOperation() {
        return selectedOperation != null ? selectedOperation : OPERATION_ENCRYPT;
    }

    public void updateTexts() {
        if (encryptBtn != null)
            encryptBtn.setText(AppMessages.radioEncrypt());
        
        if (decryptBtn != null)
            decryptBtn.setText(AppMessages.radioDecrypt());
    }
}
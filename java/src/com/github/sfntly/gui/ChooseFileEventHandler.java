/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sfntly.gui;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author ranger
 */
public class ChooseFileEventHandler implements EventHandler<ActionEvent> {

    private Stage stage;
    private TextField ttfDir;

    public ChooseFileEventHandler(Stage stage, TextField fNameFld) {
        this.stage = stage;
        this.ttfDir = fNameFld;
    }

    public void handle(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Please Choose Directory");
        File ttfDirFile = chooser.showDialog(stage);

        if (ttfDirFile != null) {
            ttfDir.setText(ttfDirFile.toString());
        }
    }

}

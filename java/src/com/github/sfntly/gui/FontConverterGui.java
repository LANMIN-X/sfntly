/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sfntly.gui;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * main class
 * @author ranger
 */
public class FontConverterGui extends Application {

    @Override
    public void start(Stage primaryStage) {
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(50, 50, 50, 50));
        gridpane.setHgap(5);
        gridpane.setVgap(5);

        ColumnConstraints column1 = new ColumnConstraints(150);
        ColumnConstraints column2 = new ColumnConstraints(300, 350, 350);
        ColumnConstraints column3 = new ColumnConstraints(100, 150, 250);
        column2.setHgrow(Priority.ALWAYS);
        gridpane.getColumnConstraints().addAll(column1, column2, column3);

        TextField ttfDirField = new TextField();
        ttfDirField.setTooltip(new Tooltip("Directory containing ttf fonts"));
        ttfDirField.setPromptText("Directory containing ttf fonts");
        GridPane.setHalignment(ttfDirField, HPos.LEFT);
        gridpane.add(ttfDirField, 1, 0);

        Button chooseDirBtn = new Button();
        chooseDirBtn.setText("Select Directory");
        chooseDirBtn.setOnAction(new ChooseFileEventHandler(primaryStage, ttfDirField));
        gridpane.add(chooseDirBtn, 2, 0);

        Label ttfDirLabel = new Label("TTF Directory:");
        GridPane.setHalignment(ttfDirLabel, HPos.RIGHT);
        gridpane.add(ttfDirLabel, 0, 0);

        Label subsetTxtLabel = new Label("Substring Chars:");
        subsetTxtLabel.setTooltip(new Tooltip("Substring chars,  chars the font file hold"));
        GridPane.setHalignment(subsetTxtLabel, HPos.RIGHT);
        gridpane.add(subsetTxtLabel, 0, 1);

        TextArea subsetTxtArea = new TextArea();
        subsetTxtArea.setTooltip(new Tooltip("leave blank  will fully convert ttf to woff/eot"));
        subsetTxtArea.setPromptText("default blank");
        gridpane.add(subsetTxtArea, 1, 1);

        CheckBox stripHintCb = new CheckBox("strip Hinting?");
        stripHintCb.setSelected(true);
        GridPane.setHalignment(stripHintCb, HPos.RIGHT);
        gridpane.add(stripHintCb, 2, 1);
        Button sbtBtn = new Button();

        Label subsetFormatLabel = new Label("Output Format:");
        subsetFormatLabel.setTooltip(new Tooltip("woff2/woff/eof/ttf"));
        GridPane.setHalignment(subsetFormatLabel, HPos.RIGHT);
        gridpane.add(subsetFormatLabel, 0, 2);
        
        final ToggleGroup formatGroup = new ToggleGroup();
        RadioButton woff2 = new RadioButton("WOFF2");
        woff2.setToggleGroup(formatGroup);
        woff2.setSelected(true);
        woff2.setUserData("woff2");

        RadioButton woff = new RadioButton("WOFF");
        woff.setToggleGroup(formatGroup);
        woff.setUserData("woff");
        RadioButton eot = new RadioButton("EOT");
        eot.setUserData("eot");

        eot.setToggleGroup(formatGroup);
        RadioButton ttf = new RadioButton("TTF");
        ttf.setUserData("ttf");
        ttf.setToggleGroup(formatGroup);

        HBox hbox = new HBox(woff2, woff, eot, ttf);
        gridpane.add(hbox, 1, 2);

        sbtBtn.setText("Start Convert");
        sbtBtn.setOnAction(new FontSubEventHandler(ttfDirField, subsetTxtArea, stripHintCb, formatGroup));
        GridPane.setHalignment(sbtBtn, HPos.RIGHT);
        gridpane.add(sbtBtn, 2, 2);
        BorderPane root = new BorderPane();
        root.setCenter(gridpane);
        Scene scene = new Scene(root, 700, 350);
        primaryStage.setTitle("Batch TrueType Font Converter (wechat:foolbeing)");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

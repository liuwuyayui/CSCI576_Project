package player;


import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.HBox;


public class MediaBar extends HBox {
    Image playerPlay = new Image(Utils.icon_playerPlay, 20, 20, true, true);
    Image playerPause = new Image(Utils.icon_playerPause, 20, 20, true, true);
    Image iconMusic = new Image(Utils.ICON_MUSIC, 20, 20, true, true);
    Image iconFolder = new Image(Utils.ICON_FOLDER, 20, 20, true, true);
//    Image iconVol = new Image(Utils.ICON_VOL, 20, 20, true, true);
//    Image iconMute = new Image(Utils.ICON_MUTE, 20, 20, true, true);
    
    
    Button playOrPause = new Button();
//    Button bgmChooser = new Button();
    Button directoryChooser = new Button();
    Slider progressSlider;
//    Button volButton;
//    Slider volSlider;
//    boolean isMute;
//    double prevVolume;
    
    public MediaBar(int barLength) {
        directoryChooser.setGraphic(new ImageView(iconFolder));
        directoryChooser.setTooltip(new Tooltip("Select a Folder"));
        
        
        playOrPause.setGraphic(new ImageView(playerPlay));
//        bgmChooser.setTooltip(new Tooltip("Select Corresponding BGM"));
//        bgmChooser.setGraphic(new ImageView(iconMusic));
        
        progressSlider = new Slider(0, barLength, 0);
        progressSlider.setPrefWidth(Utils.PROGRESS_SLIDER_PRE_WIDTH);
        
//        volButton = new Button();
//        volButton.setGraphic(new ImageView(iconVol));
//
//
//        volSlider = new Slider(0, 1, 1);
//        volSlider.setOrientation(Orientation.VERTICAL);
//        volSlider.setPrefHeight(70);
//        volSlider.isShowTickLabels();
//        volSlider.setShowTickMarks(true);
//        volSlider.setMajorTickUnit(0.25);
//        volSlider.setMinorTickCount(1);
        
//        getChildren().addAll(playOrPause, directoryChooser, bgmChooser, progressSlider, volButton, volSlider);
        getChildren().addAll(playOrPause, directoryChooser, progressSlider);
        setHeight(50.0);
        setAlignment(Pos.CENTER);
        setSpacing(5.0);
        
        
    }
    
    
}

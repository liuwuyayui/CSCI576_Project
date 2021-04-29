package sample;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MediaBar extends HBox {
    Image playerPlay = new Image(Utils.icon_playerPlay, 20, 20, true, false);
    Image playerPause = new Image(Utils.icon_playerPause, 20, 20, true, false);
    Image iconMovie = new Image(Utils.icon_movie, 20, 20, true, false);
    Image iconFolder = new Image(Utils.ICON_FOLDER, 20, 20, true, false);
    
    Button playOrPause = new Button();
    Button movieChooser = new Button();
    Button directoryChooser = new Button();
    Slider progressSlider;

    public MediaBar(int barLength) {
        this.progressSlider = new Slider(0, barLength, 0);
        playOrPause.setGraphic(new ImageView(playerPlay));
//        playOrPause.setPrefWidth(100);
        movieChooser.setGraphic(new ImageView(iconMovie));
        movieChooser.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = new Stage();
                FileChooser fc = new FileChooser();
                fc.setTitle("Please choose an MPEG4 file");
                File file = fc.showOpenDialog(stage);
                if (file != null) {
                    System.out.println(file.getAbsolutePath());
                }
            }
        });
        
        directoryChooser.setGraphic(new ImageView(iconFolder));
        directoryChooser.setTooltip(new Tooltip("Select a Folder"));
        directoryChooser.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = new Stage();
                DirectoryChooser dc = new DirectoryChooser();

//                dc.setInitialDirectory(new File(""));
                File directory = dc.showDialog(stage);
                if (directory != null) {
                    
                    Utils.directory = "file:" + directory.getAbsolutePath();
                    System.out.println(Utils.directory);
                    File[] files = directory.listFiles();
                    
                    if (files != null) {
                        Utils.FRAMES_LENGTH = files.length;
                        progressSlider.setMax(Utils.FRAMES_LENGTH);
                        System.out.println("contains " + files.length + "frames");
//                        for (File f : files) {
//                            System.out.println(f.getName());
//                        }
                    }
                }
            }
        });
        
        progressSlider.setPrefWidth(Utils.PROGRESS_SLIDER_PRE_WIDTH);
        
        getChildren().addAll(playOrPause, movieChooser, directoryChooser, progressSlider);
        setHeight(50.0);
        setAlignment(Pos.CENTER);
        setSpacing(10.0);

        
    }
    
    
}

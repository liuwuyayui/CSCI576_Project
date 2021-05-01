package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    int curFrame = 0;
    Map<Integer, Double> frameToBright;
    boolean isPlaying = false;
    final CanvasSchedule[] canvasSchedule = new CanvasSchedule[1];
    Media media;
    MediaPlayer bgmPlayer;
    
    @Override
    public void init() throws Exception {
        super.init();
        frameToBright = new HashMap<>();
        interpolate(frameToBright, 0, 30, true);
        interpolate(frameToBright, 31, 60, false);
//        traverseMap(frameToBright, 0, 50);
    }
    
    @Override
    public void start(Stage primaryStage) {
        MediaBar mediaBar = new MediaBar(0);
        Canvas canvas = new Canvas(Utils.STAGE_WIDTH, Utils.STAGE_HEIGHT);
        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        canvas.getGraphicsContext2D().fillRect(0, 0, Utils.REQUEST_WIDTH, Utils.REQUEST_HEIGHT);
        
        // handlers
        mediaBar.progressSlider.setOnMouseClicked(event -> {
            double x = Math.max(0, event.getX());
            int pointedFrame = (int) (x / Utils.PROGRESS_SLIDER_PRE_WIDTH * Utils.FRAMES_LENGTH);
            System.out.println(pointedFrame);  // TODO: remove
            mediaBar.progressSlider.setValue(pointedFrame);
            bgmPlayer.seek(Duration.millis(pointedFrame * 1000.0 / Utils.FRAME_RATE));
            if (isPlaying) {
                canvasSchedule[0].cancel();
                canvasSchedule[0] = new CanvasSchedule(canvas, mediaBar.progressSlider, pointedFrame, frameToBright, bgmPlayer);
                canvasSchedule[0].start();
                
            } else {    // update frame when pausing
                curFrame = pointedFrame;
                System.out.println(pointedFrame);
                double brightness = frameToBright.getOrDefault(pointedFrame, 0.0);
                setBrightness(canvas, brightness);
                canvas.getGraphicsContext2D().drawImage(new Image(Utils.directory + "/frame" + pointedFrame + ".jpg",
                        Utils.REQUEST_WIDTH,
                        Utils.REQUEST_HEIGHT,
                        true,
                        true), 0, 0);
            }
        });
        mediaBar.playOrPause.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().name().equals(MouseButton.PRIMARY.name())) {  // left clip
                if (!isPlaying) {  // in pausing state
                    if (Utils.directory == null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Frames Folder Undefined");
                        alert.setContentText("Please choose a folder containing jpeg frames");
                        alert.show();
                        return;
                    } else if (Utils.bgmDir == null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("BGM File Undefined");
                        alert.setContentText("Please choose the corresponding bgm file");
                        alert.show();
                        return;
                    }
                    // update icon
                    mediaBar.playOrPause.setGraphic(new ImageView(mediaBar.playerPause));
                    // play from "curFrame"
                    canvasSchedule[0] = new CanvasSchedule(canvas, mediaBar.progressSlider, curFrame, frameToBright, bgmPlayer);
                    canvasSchedule[0].start();
                    System.out.println("next:" + curFrame * 1000.0 / Utils.FRAME_RATE);
                    bgmPlayer.seek(Duration.millis(curFrame * 1000.0 / Utils.FRAME_RATE));
                    
                    bgmPlayer.play();
                } else {   // in playing state
                    mediaBar.playOrPause.setGraphic(new ImageView(mediaBar.playerPlay));
                    
                    updateCurFrame(canvasSchedule[0]);
                    canvasSchedule[0].cancel();
                    bgmPlayer.pause();
                }
                isPlaying = !isPlaying;
            }
        });
        mediaBar.directoryChooser.setOnMouseClicked(event -> {
            Stage stage = new Stage();
            DirectoryChooser dc = new DirectoryChooser();
            
            dc.setInitialDirectory(new File(Utils.DEFAULT_OPEN_DIR));
            File directory = dc.showDialog(stage);
            if (directory != null) {
                Utils.directory = "file:" + directory.getAbsolutePath();
                System.out.println(Utils.directory);
                File[] files = directory.listFiles();
                
                if (files != null) {
                    Utils.FRAMES_LENGTH = files.length;
                    mediaBar.progressSlider.setMax(Utils.FRAMES_LENGTH);
                    System.out.println("contains " + files.length + "frames");
                    Image initialImage = new Image(Utils.directory + "/frame100.jpg",
                            Utils.REQUEST_WIDTH,
                            Utils.REQUEST_HEIGHT,
                            true,
                            true);
                    
                    canvas.getGraphicsContext2D().drawImage(initialImage, 0, 0);
                }
            }
        });
        mediaBar.bgmChooser.setOnMouseClicked(event -> {
            Stage stage = new Stage();
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(Utils.DEFAULT_OPEN_DIR));
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                System.out.println(file.getAbsolutePath());
                Utils.bgmDir = "file:" + file.getAbsolutePath();
                media = new Media(Utils.bgmDir);
                bgmPlayer = new MediaPlayer(media);
            }
        });
        mediaBar.volButton.setOnMouseClicked(event -> {
            if (!mediaBar.isMute) {
                if (bgmPlayer != null) {
                    mediaBar.prevVolume = bgmPlayer.getVolume();
                    bgmPlayer.setVolume(0);
                }
                mediaBar.volButton.setGraphic(new ImageView(mediaBar.iconMute));
                mediaBar.volSlider.setValue(0);
                
            } else {
                if (bgmPlayer != null) {
                    bgmPlayer.setVolume(mediaBar.prevVolume);
                    mediaBar.volSlider.setValue(mediaBar.prevVolume);
                } else {
                    mediaBar.volSlider.setValue(0.5);
                }
                mediaBar.volButton.setGraphic(new ImageView(mediaBar.iconVol));
            }
            mediaBar.isMute = !mediaBar.isMute;
        });
        mediaBar.volSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (bgmPlayer != null && newValue != null) {
                bgmPlayer.setVolume(newValue.doubleValue());
            }
        });
        
        // layout
        AnchorPane ap = new AnchorPane();
        ap.getChildren().addAll(mediaBar, canvas);
        AnchorPane.setBottomAnchor(mediaBar, 10.0);
        AnchorPane.setLeftAnchor(mediaBar, 10.0);
        
        
        // Stage
        primaryStage.setTitle("Media Player for Video Summarization");
        primaryStage.setScene(new Scene(ap, Utils.STAGE_WIDTH, Utils.STAGE_HEIGHT + 50));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/icon/-VideoPlayer.png"));
        primaryStage.show();
        
    }
    
    private void updateCurFrame(CanvasSchedule cs) {
        curFrame = cs.startFrame;
    }
    
    private void interpolate(Map<Integer, Double> frameToBright, int startFrame, int endFrame, boolean isFading) {
        if (endFrame - startFrame <= 0) {
            throw new IllegalArgumentException("endFrame must greater than startFrame");
        }
        double noOfFrame = endFrame - startFrame;
        double delta = 1.0 / noOfFrame;
        if (isFading) {
            for (int i = 0; i <= noOfFrame; i++) {
                frameToBright.put(startFrame + i, -i * delta);
            }
        } else {
            for (int i = 0; i <= noOfFrame; i++) {
                frameToBright.put(startFrame + i, -1 + i * delta);
            }
        }
    }
    
    private void traverseMap(Map<Integer, Double> map, int start, int end) {
        while (start <= end) {
            System.out.println(map.get(start++));
        }
    }
    
    private void setBrightness(Canvas canvas, double brightness) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(brightness);
        canvas.getGraphicsContext2D().setEffect(colorAdjust);
    }
    
    private double brightnessToVol(double brightness) {
        return brightness + 1;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}





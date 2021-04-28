package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    String framePath = "/soccer/frame";
    int curFrame = 0;
    boolean isPlaying = false;
    final CanvasSchedule[] canvasSchedule = new CanvasSchedule[1];
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        MediaBar mediaBar = new MediaBar(Utils.FRAMES_LENGTH);
        Canvas canvas = new Canvas(Utils.STAGE_WIDTH, Utils.STAGE_HEIGHT);
        //display the first frame on the canvas
        canvas.getGraphicsContext2D().drawImage(new Image(framePath + 0 + ".jpg"), 0, 0);
        
        // handlers
        mediaBar.progressSlider.setOnMouseClicked(event -> {
            int pointedFrame = (int) event.getX();
            System.out.println(pointedFrame);  // TODO: remove
            mediaBar.progressSlider.setValue(pointedFrame);
            
            if (isPlaying) {
                canvasSchedule[0].cancel();
                canvasSchedule[0] = new CanvasSchedule(canvas, mediaBar.progressSlider, pointedFrame);
                canvasSchedule[0].start();
            } else {
                curFrame = pointedFrame;
                canvas.getGraphicsContext2D().drawImage(new Image(framePath + pointedFrame + ".jpg"), 0, 0);
            }
        });
        mediaBar.playOrPause.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().name().equals(MouseButton.PRIMARY.name())) {  // left clip
                if (!isPlaying) {
                    // update icon
                    mediaBar.playOrPause.setGraphic(new ImageView(mediaBar.playerPause));
                    // play from "curFrame"
                    canvasSchedule[0] = new CanvasSchedule(canvas, mediaBar.progressSlider, curFrame);
                    canvasSchedule[0].start();
                } else {   // is pausing
                    mediaBar.playOrPause.setGraphic(new ImageView(mediaBar.playerPlay));
                    
                    updateCurFrame(canvasSchedule[0]);
                    canvasSchedule[0].cancel();
                }
                isPlaying = !isPlaying;
            }
        });
        
        // layout
        AnchorPane ap = new AnchorPane();
        ap.getChildren().addAll(mediaBar, canvas);
        AnchorPane.setBottomAnchor(mediaBar, 20.0);
        AnchorPane.setLeftAnchor(mediaBar, 20.0);
        
        
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
    
    public static void main(String[] args) {
        launch(args);
    }
}





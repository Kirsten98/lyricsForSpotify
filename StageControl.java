
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageControl extends LyricsForSpotifyApplication{
    /**
     * Variables used that control the pane, scene, and Stage.
     */
    private static VBox mainPane = new VBox(20);
    private Scene mainScene = new Scene(mainPane,600,900);
    private Stage mainStage;
    protected LyricsForSpotifyApplication parent;
    private MenuBar mainMenu;
    double xOffset;
    double yOffset;


    public StageControl(LyricsForSpotifyApplication parent) {
        mainStage = new Stage();
        mainStage.initStyle(StageStyle.UNDECORATED);
        mainStage.setScene(mainScene);

        mainMenu = parent.getMenubar();

        mainScene.setOnMousePressed(mouseClickEvent -> {
                setXOffset(mainStage.getX() - mouseClickEvent.getScreenX());
                setYOffset(yOffset = mainStage.getY() - mouseClickEvent.getScreenY());
        });
        mainScene.setOnMouseDragged(mouseDragEvent -> {
            mainStage.setX(mouseDragEvent.getScreenX() + xOffset );
            mainStage.setY(mouseDragEvent.getScreenY() + yOffset);
        });

        mainMenu.setOnMousePressed(menuMouseClickEvent -> {
            setXOffset(mainStage.getX() - menuMouseClickEvent.getScreenX());
            setYOffset(yOffset = mainStage.getY() - menuMouseClickEvent.getScreenY());
        });
        mainMenu.setOnMouseDragged(menuMouseDragEvent -> {
            mainStage.setX(menuMouseDragEvent.getScreenX() + xOffset );
            mainStage.setY(menuMouseDragEvent.getScreenY() + yOffset);
        });

        mainScene.setFill(Color.TRANSPARENT);

        mainPane.getChildren().clear();
        mainPane.setPrefSize(mainScene.getWidth(),mainScene.getHeight());
        this.parent = parent;

    }

    public void setParent(LyricsForSpotifyApplication parent) {
        this.parent = parent;
    }

    /**
     * Sets the mainStage to the configurations for the home screen prior to authentication.
     * @return mainStage with configurations applied.
     */
    public void homeStage() {
        parent.applyConfigurations();
        mainPane.getChildren().clear();
        mainPane.setAlignment(Pos.TOP_CENTER);
        mainPane.setStyle("-fx-background-radius: 10; -fx-background-color: "+ textConfigurations.getBackgroundColor() +"; -fx-border-color: "+ textConfigurations.getBorderColor() + "; -fx-border-radius: 10; -fx-border-width: "+textConfigurations.getBorderSize()+";");
        Label spotifyLyrics = new Label("Lyrics for");
        spotifyLyrics.setTextFill(Paint.valueOf(Color.FLORALWHITE.toString()));
        spotifyLyrics.setStyle("-fx-font-size: 72");

        Button signIntoSpotify = new Button("Sign into Spotify");
        signIntoSpotify.setTextFill(Paint.valueOf(Color.WHITE.toString()));
        signIntoSpotify.setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.TRANSPARENT.toString()),new CornerRadii(0),new Insets(0))));

        signIntoSpotify.setOnAction(signIntoSpotifyAction ->{
            authorizationStage();
        });
        signIntoSpotify.setPrefWidth(200);
        signIntoSpotify.setStyle("-fx-font-size: 22;");

        mainPane.getChildren().addAll(mainMenu, spotifyLyrics,new ImageView(new Image(this.getClass().getResourceAsStream("Spotify_Logo_RGB_White.png"),295.25,88.5,false,true)),new Label("\n\n\n\n"),signIntoSpotify);

    }

    /**
     * Stage to display the webview to the user for Spotify authentication
     * @return mainStage with the configurations applied
     */
    public void authorizationStage() {
        parent.applyConfigurations();
        mainPane.getChildren().clear();
        mainPane.setAlignment(Pos.CENTER_LEFT);

        WebView webView= parent.connectToSpotify();
        webView.setPrefHeight(mainPane.getHeight());

        mainPane.getChildren().addAll(mainMenu, webView);
    }

    /**
     * Sets the mainStage to the configuration to display artist, song, and lyrics.
     * @return mainStage with the configurations applied
     */
    public void lyricsStage(ScrollPane scrollPane) {
        parent.applyConfigurations();

        mainPane.getChildren().clear();
        mainPane.setAlignment(Pos.TOP_LEFT);
        mainPane.setStyle("-fx-background-radius: 10; -fx-background-color: "+ textConfigurations.getBackgroundColor() +"; -fx-border-color: "+ textConfigurations.getBorderColor() + "; -fx-border-radius: 10; -fx-border-width: "+textConfigurations.getBorderSize() +";");

        scrollPane.setStyle("-fx-background-radius: 10; -fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent; -fx-body-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVmin(0);
        scrollPane.setVmax(100);

        mainPane.getChildren().addAll(mainMenu,song,artist, scrollPane);
    }

    /**
     * Sets the stage to display the provided error message and information for song controls if forLyrics is true.
     * @param errorMessage Error message to display to the user
     * @param forLyrics True if the error was thrown during the lyrics process, false otherwise.
     * @return mainStage with the configurations applied
     */
    public void errorStage(String errorMessage, Boolean forLyrics) {
        mainPane.getChildren().clear();
        parent.applyConfigurations();


        if(forLyrics) {
            mainPane.setAlignment(Pos.TOP_LEFT);
            mainPane.setStyle("-fx-background-radius: 10; -fx-background-color: "+ textConfigurations.getBackgroundColor() +"; -fx-border-color: "+ textConfigurations.getBorderColor() + "; -fx-border-radius: 10; -fx-border-width: "+textConfigurations.getBorderSize() +";");


            Text errorLabel = new Text(errorMessage);
            errorLabel.setStrokeWidth(textConfigurations.getTextOutlineWidth());
            errorLabel.setFill(Paint.valueOf(textConfigurations.getTextColor()));
            errorLabel.setStroke(Paint.valueOf(textConfigurations.getTextOutLine()));
            errorLabel.setStyle("-fx-font-size: "+ textConfigurations.getLyricTextSize() +"; -fx-text-fill: " + textConfigurations.getTextColor() +"; ");
            Button skipSong = new Button("Next Song");
            skipSong.setOnAction(skipSongAction ->{
                skipSong.setDisable(true);
                parent.nextSong();
                skipSong.setDisable(false);
            });

            Button retry = new Button("Retry");
            retry.setOnAction(retryAction ->{
                parent.getCurrentSongInfo();
            });

            HBox songOptions = new HBox(30);
            songOptions.getChildren().addAll(retry,skipSong);
            songOptions.setAlignment(Pos.CENTER);
            mainPane.getChildren().clear();
            scrollPane.setContent(errorLabel);
            mainPane.getChildren().addAll(mainMenu,song == null? new Text("No song playing"): song,artist == null? new Text("No song playing"): artist, errorLabel);

            if (threadControl.currentSongCheckThread == null || threadControl.currentSongCheckThread.isInterrupted() || !threadControl.currentSongCheckThread.isAlive()){
                threadControl.startCurrentSongCheckThread();
            }

        } else {
            mainPane.setAlignment(Pos.CENTER);
            Button retry = new Button("Retry");
            retry.setPrefWidth(150);
            retry.setOnAction(retryAction ->{
                homeStage();
            });
            mainPane.getChildren().addAll(mainMenu,new Label(errorMessage),retry);
        }

    }

    private void setXOffset(double newXOffset){
        xOffset = newXOffset;
    }

    private void setYOffset(double newYOffset){
        yOffset = newYOffset;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public VBox getMainPane() {
        return mainPane;
    }


    public double getStageHeight() {
        return mainStage.getHeight();
    }

}

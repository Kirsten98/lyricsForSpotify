import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Settings {

    public static void settingsPanePopUp(TextConfigurations textConfigurations){
        Stage settingStage = new Stage();
        settingStage.initModality(Modality.APPLICATION_MODAL);
        VBox settingsPane = new VBox(30);
        settingsPane.setPadding(new Insets(50,25,50,25));
        settingsPane.setAlignment(Pos.CENTER);
        Scene settingScene = new Scene(settingsPane,400,500);
        settingStage.setScene(settingScene);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(closeStage -> settingStage.close());

        ObservableList colors = FXCollections.observableArrayList();
        colors.addAll("White","Black", "Aquamarine","Blue Violet","Coral","Crimson","Cyan","Dark Blue","Dark Cyan","Dark Grey","Dark Magenta","Dark Orchid","Dark Red","Dark Turquoise","Dark Violet","Deep Pink","Deep Sky Blue","Fuchsia","Dim Grey");
        ChoiceBox<String> textColor = new ChoiceBox<>(colors);
        textColor.setValue(textConfigurations.getTextColor());

        ChoiceBox<String> textOutlineColor = new ChoiceBox<>(colors);
        textOutlineColor.setValue(textConfigurations.getTextOutLine());

        ChoiceBox<String> borderColor = new ChoiceBox<>(colors);
        borderColor.setValue(textConfigurations.getBorderColor());

        ChoiceBox<String> backgroundColor = new ChoiceBox<>(colors);
        backgroundColor.setValue(textConfigurations.getBackgroundColor());


        Slider lyricSizeSlider = new Slider();
        lyricSizeSlider.setMax(30);
        lyricSizeSlider.setMin(1);
        lyricSizeSlider.setValue(textConfigurations.getLyricTextSize());

        Slider artistSizeSlider = new Slider();
        artistSizeSlider.setMax(30);
        artistSizeSlider.setMin(1);
        artistSizeSlider.setValue(textConfigurations.getArtistTextSize());

        Slider songSizeSlider = new Slider();
        songSizeSlider.setMax(30);
        songSizeSlider.setMin(1);
        songSizeSlider.setValue(textConfigurations.getSongTextSize());

        Slider textOutLineWidthSlider = new Slider();
        textOutLineWidthSlider.setMax(5);
        textOutLineWidthSlider.setMin(.1);
        textOutLineWidthSlider.setValue(textConfigurations.getTextOutlineWidth());

        Button applySettings = new Button("Apply");
        applySettings.setOnAction(apply ->{
            textConfigurations.setTextColor(textColor.getValue().replace(" ",""));
            textConfigurations.setBorderColor(borderColor.getValue().replace(" ",""));
            textConfigurations.setBackgroundColor(backgroundColor.getValue().replace(" ",""));
            textConfigurations.setLyricTextSize((int) lyricSizeSlider.getValue());
            textConfigurations.setArtistTextSize((int) artistSizeSlider.getValue());
            textConfigurations.setSongTextSize((int) songSizeSlider.getValue());
            textConfigurations.setTextOutlineWidth((double) textOutLineWidthSlider.getValue());
            textConfigurations.setTextOutLine(textOutlineColor.getValue().replace(" ",""));
            LyricsForSpotifyApplication.applyConfigurations();
        });


        Button closeSettings = new Button("Close");
        closeSettings.setOnAction(closeAction -> {
            settingStage.close();
        });

        //Fields for Settings
        HBox textColorHBOX= new HBox(new Label("Text Color: "), textColor);
        HBox textOutlineColorHBox = new HBox(new Label("Text Outline Color: "), textOutlineColor);
        HBox borderColorHBOX = new HBox(new Label("Border Color: "), borderColor);
        HBox backgroundColorHBOX = new HBox(new Label("Background Color: "),backgroundColor);
        HBox artistTextSizeHBOX = new HBox(new Label("Artist Text Size: "), artistSizeSlider);
        HBox songTextSizeHBOX = new HBox(new Label("Song Text Size: "), songSizeSlider);
        HBox lyricTextSizeHBOX= new HBox(new Label("Lyric Text Size: "), lyricSizeSlider);
        HBox textOutlineWidthHBOX= new HBox(new Label("Text Outline Width: "), textOutLineWidthSlider);


        HBox buttons = new HBox(applySettings,closeButton);
        buttons.setSpacing(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPrefWidth(100);

        settingsPane.getChildren().addAll(textColorHBOX, textOutlineColorHBox, backgroundColorHBOX, borderColorHBOX, songTextSizeHBOX, artistTextSizeHBOX, lyricTextSizeHBOX, textOutlineWidthHBOX, buttons);

        settingStage.showAndWait();
    }
}

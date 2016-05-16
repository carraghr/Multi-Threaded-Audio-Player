import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    ToggleButton playPauseButton;
    @FXML
    ImageView playPauseImage;
    @FXML
    Button stopButton;
    @FXML
    Button nextButton;
    @FXML
    Button previousButton;
    @FXML
    Slider VolumeControl;
    @FXML
    Text songNameDisplay;
    @FXML
    Button songSelectionButton;

    AudioPlayer player;

    String selectedFile;

    String [] songsInDir;

    int numberOfFiles, index = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        VolumeControl.setValue(50);

        VolumeControl.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Slider Value Changed (newValue: " + newValue.intValue() + ")");
            try {
                if (player != null) {
                    player.changeVolume(newValue.floatValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        setMessage("Please select a song to play");

    }

    @FXML
    private void playPauseEvent(){
        if (selectedFile == null){
            return;
        }
        if (playPauseButton.isSelected()){
            if (player == null){
                player = new AudioPlayer(selectedFile,this);
            }else{
                player.resume();
            }
            Image image = new Image("pause.png");
            playPauseImage.setImage(image);
        }else{
            Image image = new Image("play.png");
            playPauseImage.setImage(image);
            if (player != null){
                player.pause();
            }
        }
    }

    public void stopEvent(){
        if (player != null){
            player.stop();
            Image playImage = new Image("play.png");
            playPauseImage.setImage(playImage);
            player = null;
        }
    }

    @FXML
    private void selectSong(){
        FileChooser fileChooser = new FileChooser();
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        selectedFile = file.toString();
        songNameDisplay.setText(selectedFile);

        String dirLoc = file.getParent();

        File dir = new File(dirLoc);
        int count = 0;
        songsInDir = new String[dir.listFiles().length];
        for (File fileInDir: dir.listFiles()){
            if(fileInDir.isFile() && fileInDir.toString().contains(".wav")){
                songsInDir[count] = fileInDir.toString();
                System.out.println(songsInDir[count]);
                count++;
            }
        }
        numberOfFiles = count;
        }



    @FXML
    public void setMessage(String message){
        songNameDisplay.setText(message);
    }

    public void removePlayer(){
        player = null;
        Image playImage = new Image("play.png");
        playPauseImage.setImage(playImage);
    }

    @FXML
    public void nextSong(){
        if (numberOfFiles > 0){
            index = (index + 1) % numberOfFiles;
            selectedFile = songsInDir[index];
        }
    }

    @FXML
    public void previousSong(){
        if (numberOfFiles > 0) {
            index = ((index - 1) % numberOfFiles);
            selectedFile = songsInDir[index];
        }
    }
}
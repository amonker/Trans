package cn.jingyun.trans;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import cn.jingyun.convertor.ffmpeg.FFmpeg;
import cn.jingyun.convertor.mp4box.MP4BoxUtils;
import cn.jingyun.javafx.amonker.ui.FXMLVBox;
import cn.jingyun.trans.ImageResources.Type;

public class PathSetPane extends FXMLVBox
{
	@FXML private ResourceBundle resources;
	@FXML private URL location;
    @FXML private TextField txtFFmpegPath;
    @FXML private TextField txtMP4BoxPath;
    @FXML private Button btnFFmpeg;
    @FXML private Button btnMP4Box;
    
    private FileChooser chooser;
    
	public PathSetPane()
	{
		super("PathSetPane.fxml");
		txtFFmpegPath.setText(FFmpeg.getFFmpegPath());
		txtMP4BoxPath.setText(MP4BoxUtils.getMP4BoxPath());
		chooser = new FileChooser();
	}

	@FXML
    void onBtnChoiceFFmpegPathClicked(ActionEvent event) 
	{
		chooser.setTitle("FFmpeg");
		File f = new File(txtFFmpegPath.getText());
		if (f.exists())
			chooser.setInitialDirectory(f);
		else 
			chooser.setInitialDirectory(null);
		
		f = chooser.showOpenDialog(null);
		if (f != null && f.exists()) {
			FFmpeg.setFFmpegPath(f.getAbsolutePath());
			txtFFmpegPath.setText(FFmpeg.getFFmpegPath());
		}
		f = null;
	}

    @FXML
    void onBtnChoiceMP4BoxPathClicked(ActionEvent event) 
    {
    	chooser.setTitle("MP4Box");
    	File f = new File(txtMP4BoxPath.getText());
    	if (f.exists())
    		chooser.setInitialDirectory(f);
    	else 
			chooser.setInitialDirectory(null);
		
    	f = chooser.showOpenDialog(null);
		if (f != null && f.exists()) {
			MP4BoxUtils.setMP4BoxPath(f.getAbsolutePath());
			txtMP4BoxPath.setText(MP4BoxUtils.getMP4BoxPath());
		}
		f = null;
    }

    @FXML
    void initialize() 
    {
    	assert btnFFmpeg != null : "fx:id=\"btnFFmpeg\" was not injected: check your FXML file 'PathSetPane.fxml'.";
        assert btnMP4Box != null : "fx:id=\"btnMP4Box\" was not injected: check your FXML file 'PathSetPane.fxml'.";
        assert txtFFmpegPath != null : "fx:id=\"txtFFmpegPath\" was not injected: check your FXML file 'PathSetPane.fxml'.";
        assert txtMP4BoxPath != null : "fx:id=\"txtMP4BoxPath\" was not injected: check your FXML file 'PathSetPane.fxml'.";
        
        btnFFmpeg.setPrefSize(30, 30);
        btnFFmpeg.setGraphic(ImageResources.getImage(Type.Open));
        
        btnMP4Box.setPrefSize(30, 30);
        btnMP4Box.setGraphic(ImageResources.getImage(Type.Open));
    }
}

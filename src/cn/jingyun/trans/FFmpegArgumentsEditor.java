package cn.jingyun.trans;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import cn.jingyun.javafx.amonker.ui.AmonkerUIApplication;
import cn.jingyun.javafx.amonker.ui.FXMLVBox;
import cn.jingyun.javafx.amonker.ui.IFunction1Operator;
import cn.jingyun.javafx.amonker.ui.IPagePushListener;
import cn.jingyun.javafx.amonker.ui.PageReBackResult;

public class FFmpegArgumentsEditor extends FXMLVBox implements IFunction1Operator, IPagePushListener
{
	@FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private TextArea txtArguments;
    @FXML private TextField txtName;
    @FXML private TextField txtFileExtension;
    
    private PageReBackResult result = PageReBackResult.Cancel;
    private AmonkerUIApplication app = null;

	public FFmpegArgumentsEditor()
	{
		super("FFmpegArgumentsEditor.fxml");
		initialize();
	}
	
	public PageReBackResult getResult()
	{
		return result;
	}
	
	public void setModifiedMode(boolean isModified)
	{
		txtName.setEditable(!isModified);
	}
	
	public void setValue(String name, String arguments, String extension)
	{
		txtName.setText(name);
		txtArguments.setText(arguments);
		txtFileExtension.setText(extension);
	}
	
	public String getName()
	{
		return txtName.getText();
	}
	
	public String getArguments()
	{
		return txtArguments.getText();
	}
	
	public String getFileExtension()
	{
		return txtFileExtension.getText();
	}
	
    @FXML
    void initialize() 
    {
        assert txtArguments != null : "fx:id=\"txtArguments\" was not injected: check your FXML file 'FFmpegArgumentsEditors.fxml'.";
        assert txtFileExtension != null : "fx:id=\"txtFileExtension\" was not injected: check your FXML file 'FFmpegArgumentsEditor.fxml'.";
        assert txtName != null : "fx:id=\"txtName\" was not injected: check your FXML file 'FFmpegArgumentsEditors.fxml'.";
    }

    //TODO: IFunction1Operator Methods
	@Override
	public void function1Clicked()
	{
		result = PageReBackResult.OK;
		app.popPage();
	}

	@Override
	public String getFunction1Title()
	{
		return "OK";
	}

	@Override
	public Image getFunction1Image()
	{
		return null;
	}

	//TODO: IPagePushListener Methods
	@Override
	public void willPushPage(AmonkerUIApplication app) {}

	@Override
	public void didPushPage(AmonkerUIApplication app)
	{
		this.app = app;
	}
}

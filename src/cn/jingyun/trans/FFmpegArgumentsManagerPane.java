package cn.jingyun.trans;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import cn.jingyun.convertor.ffmpeg.FFmpegArguments;
import cn.jingyun.convertor.ffmpeg.FFmpegArgumentsManager;
import cn.jingyun.javafx.amonker.ui.AmonkerUIApplication;
import cn.jingyun.javafx.amonker.ui.AmonkerUIPage;
import cn.jingyun.javafx.amonker.ui.FXMLVBox;
import cn.jingyun.javafx.amonker.ui.IPagePushListener;
import cn.jingyun.javafx.amonker.ui.IPageReBackToTopListener;
import cn.jingyun.javafx.amonker.ui.PageReBackResult;
import cn.jingyun.trans.ImageResources.Type;

public class FFmpegArgumentsManagerPane extends FXMLVBox implements IPagePushListener, IPageReBackToTopListener
{
	@FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private TableView<FFmpegArguments> tblArguments;
    @FXML private TableColumn<FFmpegArguments, String> tblcolArguments;
    @FXML private TableColumn<FFmpegArguments, String> tblcolFileExtension;
    @FXML private TableColumn<FFmpegArguments, String> tblcolName;
    @FXML private Button btnAdd;
    @FXML private Button btnAdvModified;
    @FXML private Button btnDelete;
    @FXML private Button btnModified;
    
    private AmonkerUIApplication app = null;
    
    private FFmpegArgumentsEditor editor = new FFmpegArgumentsEditor();
    private FFmpegArgumentsCreator creator = new FFmpegArgumentsCreator();
    private AmonkerUIPage creatorPage = new AmonkerUIPage(creator);
    private AmonkerUIPage advancedEditorPage = new AmonkerUIPage(editor);
    private boolean isModified = false;
//    private boolean isAdvanced = false;
    private ObservableList<FFmpegArguments> argumentsList;
    private FFmpegArguments currentSelectedArguments = null;
    private int currentSelectedIndex = -1;
    
	public FFmpegArgumentsManagerPane()
	{
		super("FFmpegArgumentsManagerPane.fxml");
		initialize();
	}
	
	@FXML
    void onBtnAddClicked(ActionEvent event) 
	{
		isModified = false;
//		editor.setModifiedMode(false);
//		getApplication().pushNode(node);
		creator.setModifiedMode(false);
		app.pushPage(creatorPage);
    }

    @FXML
    void onBtnDeleteClicked(ActionEvent event) 
    {
    	FFmpegArgumentsManager.getInstance().deleteArgumentsAt(tblArguments.getSelectionModel().getSelectedIndex());
    }

    @FXML
    void onBtnModifiedClicked(ActionEvent event) 
    {
    	currentSelectedIndex = tblArguments.getSelectionModel().getSelectedIndex();
    	if (currentSelectedIndex < 0 || currentSelectedIndex >= argumentsList.size())
    		return;
    	
    	currentSelectedArguments = argumentsList.get(currentSelectedIndex);
    	isModified = true;
//    	isAdvanced = false;
//    	editor.setValue(currentSelectedArguments.getName(), currentSelectedArguments.getArguments(), currentSelectedArguments.getFileExtension());
//    	editor.setModifiedMode(true);
//    	getApplication().pushNode(node);
    	creator.setArguments(currentSelectedArguments);
    	creator.setModifiedMode(true);
    	app.pushPage(creatorPage);
    }
    
    @FXML
    void onBtnAdvancedModifiedClicked(ActionEvent event) 
    {
    	currentSelectedIndex = tblArguments.getSelectionModel().getSelectedIndex();
    	if (currentSelectedIndex < 0 || currentSelectedIndex >= argumentsList.size())
    		return;
    	
    	currentSelectedArguments = argumentsList.get(currentSelectedIndex);
    	isModified = true;
//    	isAdvanced = true;
    	editor.setValue(currentSelectedArguments.getName(), currentSelectedArguments.getArguments(), currentSelectedArguments.getFileExtension());
    	editor.setModifiedMode(true);
    	app.pushPage(advancedEditorPage);
    }


    @FXML
    void initialize() 
    {
    	assert btnAdd != null : "fx:id=\"btnAdd\" was not injected: check your FXML file 'FFmpegArgumentsManagerPane.fxml'.";
        assert btnAdvModified != null : "fx:id=\"btnAdvModified\" was not injected: check your FXML file 'FFmpegArgumentsManagerPane.fxml'.";
        assert btnDelete != null : "fx:id=\"btnDelete\" was not injected: check your FXML file 'FFmpegArgumentsManagerPane.fxml'.";
        assert btnModified != null : "fx:id=\"btnModified\" was not injected: check your FXML file 'FFmpegArgumentsManagerPane.fxml'.";
        assert tblArguments != null : "fx:id=\"tblArguments\" was not injected: check your FXML file 'FFmpegArgumentsManager.fxml'.";
        assert tblcolArguments != null : "fx:id=\"tblcolArguments\" was not injected: check your FXML file 'FFmpegArgumentsManagerPane.fxml'.";
        assert tblcolFileExtension != null : "fx:id=\"tblcolFileExtension\" was not injected: check your FXML file 'FFmpegArgumentsManagerPane.fxml'.";
        assert tblcolName != null : "fx:id=\"tblcolName\" was not injected: check your FXML file 'FFmpegArgumentsManagerPane.fxml'.";
        
        tblcolName.setCellValueFactory(new PropertyValueFactory<FFmpegArguments, String>("name"));
        tblcolFileExtension.setCellValueFactory(new PropertyValueFactory<FFmpegArguments, String>("fileExtension"));
        tblcolArguments.setCellValueFactory(new PropertyValueFactory<FFmpegArguments, String>("arguments"));
       
        argumentsList = FFmpegArgumentsManager.getInstance().getArgumentsList();
        tblArguments.setItems(argumentsList);
        
        btnAdd.setPrefSize(30, 30);
        btnAdd.setGraphic(ImageResources.getImage(Type.Add));
        
        btnAdvModified.setPrefSize(30, 30);
        btnAdvModified.setGraphic(ImageResources.getImage(Type.AdvModified));
        
        btnDelete.setPrefSize(30, 30);
        btnDelete.setGraphic(ImageResources.getImage(Type.Delete));
        
        btnModified.setPrefSize(30, 30);
        btnModified.setGraphic(ImageResources.getImage(Type.Modified));
    }

	//TODO: IPagePushListener methods
	@Override
	public void willPushPage(AmonkerUIApplication app) {}

	@Override
	public void didPushPage(AmonkerUIApplication app)
	{
		this.app = app;
	}

	//TODO: IPageReBackToTopListener Methods
	@Override
	public void didReBackTop(AmonkerUIApplication app, AmonkerUIPage didPopPage)
	{
		if (didPopPage.equals(creatorPage) && creator.getResult() == PageReBackResult.OK) {
			if (isModified) {
				FFmpegArgumentsManager.getInstance().modifiedArgumentsAt(currentSelectedIndex, creator.getArguments(), creator.getExtension());
				
				currentSelectedArguments = null;
				currentSelectedIndex = -1;
				
			} else {
				FFmpegArgumentsManager.getInstance().addArguments(creator.getName(), creator.getArguments(), creator.getExtension());
			}
		}
		
		if (didPopPage.equals(advancedEditorPage) && editor.getResult() == PageReBackResult.OK) {
			FFmpegArgumentsManager.getInstance().modifiedArgumentsAt(currentSelectedIndex, editor.getArguments(), editor.getFileExtension());
			currentSelectedArguments = null;
			currentSelectedIndex = -1;
		}
	}
}

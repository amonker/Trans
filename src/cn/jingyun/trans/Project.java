package cn.jingyun.trans;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import cn.jingyun.convertor.ffmpeg.FFmpeg;
import cn.jingyun.convertor.ffmpeg.FFmpegArguments;
import cn.jingyun.convertor.ffmpeg.FFmpegArgumentsManager;
import cn.jingyun.convertor.utils.ConvertMediaInfo;
import cn.jingyun.javafx.amonker.ui.AmonkerUIApplication;
import cn.jingyun.javafx.amonker.ui.FXMLVBox;
import cn.jingyun.javafx.amonker.ui.IFunction1Operator;
import cn.jingyun.javafx.amonker.ui.IPagePushListener;
import cn.jingyun.javafx.amonker.ui.PageReBackResult;
import cn.jingyun.trans.ImageResources.Type;

public class Project extends FXMLVBox implements IFunction1Operator, IPagePushListener
{
	@FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private ComboBox<String> cbTransMethodsClicked;
    @FXML private TableView<ConvertMediaInfo> tblFiles;
    @FXML private TableColumn<ConvertMediaInfo, String> tblcolDestinationFile;
    @FXML private TableColumn<ConvertMediaInfo, String> tblcolSourceFile;
    @FXML private TableColumn<ConvertMediaInfo, String> tblcolTransMethods;
    @FXML private Button btnAdd;
    @FXML private Button btnApply;
    @FXML private Button btnClear;
    @FXML private Button btnDelete;


    private ObservableList<ConvertMediaInfo> infoList = FXCollections.observableArrayList();
    private ObservableList<String> methodsNameList = FXCollections.observableArrayList();
    private ObservableList<FFmpegArguments> argumentList = FFmpegArgumentsManager.getInstance().getArgumentsList();
    private FileChooser chooser;
    private PageReBackResult result = PageReBackResult.Cancel;
    private AmonkerUIApplication app = null;
    
//    private FFmpeg ffmpegInstance = null;
	
	public Project(FFmpeg ffmpegInstance)
	{
		super();
		
//		this.ffmpegInstance = ffmpegInstance;
		initialize();
		reloadMethods();
		
	}
	
	public PageReBackResult getResult() { return result; }
	
	public void reloadMethods()
	{
		methodsNameList.clear();
		for (FFmpegArguments arg : argumentList)
			methodsNameList.add(arg.getName());
		cbTransMethodsClicked.getSelectionModel().select(0);
	}
	
	public void clear()
	{
		infoList.removeAll(infoList.toArray(new ConvertMediaInfo[1]));
	}
	
	public ObservableList<ConvertMediaInfo> getConvertMediaInfoList()
	{
		return infoList;
	}
	
	private String changeExtension(String filename, String extension)
	{
		String ret = null;
		if (filename.contains("."))
			ret = filename.substring(0, filename.lastIndexOf('.')) + "." + extension;
		else
			ret = filename + "." + extension;
		return ret;
	}
	
	@FXML
    void onBtnAddFilesClicked(ActionEvent event) 
	{
		List<File> files = chooser.showOpenMultipleDialog(null);
	 	if (files != null) {
			FFmpegArguments arguments = argumentList.get(cbTransMethodsClicked.getSelectionModel().getSelectedIndex());
//			String extension = argumentList.get(0).getFileExtension();
			for (File file : files) {
				ConvertMediaInfo info = new ConvertMediaInfo();
				String str = file.getAbsolutePath();
				info.setSourceMediaName(str);
//				str.lastIndexOf('.');
//				str = str.substring(0, str.lastIndexOf('.')) + "." + argument.getFileExtension();
				info.setDestinationMediaName(changeExtension(str, arguments.getFileExtension()));
				info.setController(new FFmpeg());
				info.setCommandLine(arguments.getArguments());
				info.setMethod(arguments.getName());
				
				infoList.add(info);
			}
		}
    }

	@FXML
    void onBtnApplyMethodOnAllFilesClicked(ActionEvent event) 
	{
		int index = cbTransMethodsClicked.getSelectionModel().getSelectedIndex();
		if (index < 0 || index >= methodsNameList.size())
			return;
		
		FFmpegArguments arguments = argumentList.get(index);
		
		for (ConvertMediaInfo info : infoList) {
			info.setCommandLine(arguments.getArguments());
			info.setMethod(arguments.getName());
//			info.setDestinationMediaName(changeExtension(info.getSourceMediaName(), arguments.getFileExtension()));
		}
//		tblcolTransMethods.setVisible(false);
//		tblcolTransMethods.setVisible(true);
    }

    @FXML
    void onBtnClearListClicked(ActionEvent event) 
    {
    	clear();
    }

    @FXML
    void onBtnDeleteFileClicked(ActionEvent event) 
    {
    	ObservableList<Integer> intList = tblFiles.getSelectionModel().getSelectedIndices();
    	ObservableList<ConvertMediaInfo> infos = FXCollections.observableArrayList();
    	for (Integer integer : intList) 
			infos.add(infoList.get(integer));
    	infoList.removeAll(infos);
    	infos.clear();
    	intList = null;
    	infos = null;
    }

    @FXML
    void initialize() 
    {
    	assert btnAdd != null : "fx:id=\"btnAdd\" was not injected: check your FXML file 'Project.fxml'.";
        assert btnApply != null : "fx:id=\"btnApply\" was not injected: check your FXML file 'Project.fxml'.";
        assert btnClear != null : "fx:id=\"btnClear\" was not injected: check your FXML file 'Project.fxml'.";
        assert btnDelete != null : "fx:id=\"btnDelete\" was not injected: check your FXML file 'Project.fxml'.";
        assert cbTransMethodsClicked != null : "fx:id=\"cbTransMethodsClicked\" was not injected: check your FXML file 'Project.fxml'.";
        assert tblFiles != null : "fx:id=\"tblFiles\" was not injected: check your FXML file 'Project.fxml'.";
        assert tblcolDestinationFile != null : "fx:id=\"tblcolDestinationFile\" was not injected: check your FXML file 'Project.fxml'.";
        assert tblcolSourceFile != null : "fx:id=\"tblcolSourceFile\" was not injected: check your FXML file 'Project.fxml'.";
        assert tblcolTransMethods != null : "fx:id=\"tblcolTransMethods\" was not injected: check your FXML file 'Project.fxml'.";
        
        tblcolSourceFile.setCellValueFactory(new PropertyValueFactory<ConvertMediaInfo, String>("sourceMediaName"));
        tblcolDestinationFile.setCellValueFactory(new PropertyValueFactory<ConvertMediaInfo, String>("destinationMediaName"));
        tblcolTransMethods.setCellValueFactory(new PropertyValueFactory<ConvertMediaInfo, String>("method"));
//        tblcolTransMethods.setCellFactory(ComboBoxTableCell.<ConvertMediaInfo, String>forTableColumn(methodsNameList));
        
        tblcolTransMethods.setCellFactory(new Callback<TableColumn<ConvertMediaInfo,String>, TableCell<ConvertMediaInfo,String>>() {
			
			@Override
			public TableCell<ConvertMediaInfo, String> call(
					TableColumn<ConvertMediaInfo, String> param)
			{
				ComboBoxTableCell<ConvertMediaInfo, String> cell = new ComboBoxTableCell<ConvertMediaInfo, String>() {
					ComboBox<String> cb = null;
					
					@Override
					public void updateItem(String item, boolean empty)
					{
						// TODO Auto-generated method stub
//						super.updateItem(item, empty);
						if (empty || !(this.isVisible()))
							return;
						final int index = this.getIndex();
						if (cb == null) {
							cb = new ComboBox<String>();
							cb.setItems(methodsNameList);
							cb.valueProperty().addListener(new ChangeListener<String>() {

								@Override
								public void changed(
										ObservableValue<? extends String> observable,
										String oldValue, String newValue)
								{
									if (newValue == null)
										return;
									
									FFmpegArguments arguments = argumentList.get(methodsNameList.indexOf(newValue));
									
									ConvertMediaInfo info = infoList.get(index);
									info.setCommandLine(arguments.getArguments());
									info.setMethod(arguments.getName());
									info.setDestinationMediaName(changeExtension(info.getSourceMediaName(), arguments.getFileExtension()));
//									tblcolDestinationFile.setVisible(false);
//									tblcolDestinationFile.setVisible(true);
								}
							});
							setGraphic(cb);
							setAlignment(Pos.CENTER);
						}
						String selectItem = cb.getSelectionModel().getSelectedItem();
						if (selectItem == null || !item.equals(selectItem))
							cb.getSelectionModel().select(methodsNameList.indexOf(item));
						
					}
				};
				return cell;
			}
		});
        
        tblFiles.setItems(infoList);
        tblFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        btnAdd.setPrefSize(30, 30);
        btnAdd.setGraphic(ImageResources.getImage(Type.AddMovie));
        
        btnDelete.setPrefSize(30, 30);
        btnDelete.setGraphic(ImageResources.getImage(Type.Delete));
        
        btnClear.setPrefSize(30, 30);
        btnClear.setGraphic(ImageResources.getImage(Type.Clear));
        
        btnApply.setPrefSize(30, 30);
        btnApply.setGraphic(ImageResources.getImage(Type.Apply));
        
        cbTransMethodsClicked.setItems(methodsNameList);
        cbTransMethodsClicked.getSelectionModel().select(0);
        
        chooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("All Moive Files (*.mp4;*.mov;*.flv;*.ts)", "*.mp4", "*.mov", "*.flv", "*.ts");
		chooser.getExtensionFilters().add(filter);
		filter = new FileChooser.ExtensionFilter("MPEG-4 Moive File (*.mp4)", "*.mp4");
		chooser.getExtensionFilters().add(filter);
		filter = new FileChooser.ExtensionFilter("Adobe Flash Video File (*.flv)", "*.flv");
		chooser.getExtensionFilters().add(filter);
		filter = new FileChooser.ExtensionFilter("Apple QuickTime Video File (*.mov)", "*.mov");
		chooser.getExtensionFilters().add(filter);
		filter = new FileChooser.ExtensionFilter("MPEGTS Video File (*.ts)", "*.ts");
		chooser.getExtensionFilters().add(filter);
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
		result = PageReBackResult.Cancel;
	}
}

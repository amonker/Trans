package cn.jingyun.trans;

import java.net.URL;
import java.util.ResourceBundle;

import cn.jingyun.convertor.ffmpeg.FFmpeg;
import cn.jingyun.convertor.utils.ConvertMediaInfo;
import cn.jingyun.convertor.utils.ConvertMediaInfo.Status;
import cn.jingyun.convertor.utils.IConvertListener;
import cn.jingyun.javafx.amonker.ui.AmonkerUIApplication;
import cn.jingyun.javafx.amonker.ui.AmonkerUIPage;
import cn.jingyun.javafx.amonker.ui.FXMLBorderPane;
import cn.jingyun.javafx.amonker.ui.IApplicationStatusListener;
import cn.jingyun.javafx.amonker.ui.IFunction1Operator;
import cn.jingyun.javafx.amonker.ui.IPagePushListener;
import cn.jingyun.javafx.amonker.ui.IPageReBackToTopListener;
import cn.jingyun.javafx.amonker.ui.PageReBackResult;
import cn.jingyun.trans.ImageResources.Type;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class MainPane extends FXMLBorderPane implements IConvertListener, IPagePushListener, IPageReBackToTopListener, IFunction1Operator, IApplicationStatusListener
{
	@FXML private ResourceBundle resources;
	@FXML private URL location;
	@FXML private Button btnOpen;
	@FXML private Button btnStartOrStop;
	@FXML private Button btnClear;
	@FXML private Button btnDelete;
	@FXML private Button btnApplyMethods;
	@FXML private Button btnApplyPath;
	@FXML private Button btnChoicePath;
	@FXML private Button btnEditItem;
	@FXML private TextField txtPath;
	@FXML private ComboBox<?> cbMethods;
	@FXML private TableView<ConvertMediaInfo> tblConvertInfo;
	@FXML private TableColumn<ConvertMediaInfo, String> tblcolFilename;
	@FXML private TableColumn<ConvertMediaInfo, String> tblcolMethod;
	@FXML private TableColumn<ConvertMediaInfo, Double> tblcolProgress;
	@FXML private TableColumn<ConvertMediaInfo, ConvertMediaInfo.Status> tblcolStatus;
	
	private final ObservableList<ConvertMediaInfo> infos = FXCollections.observableArrayList();
	private FileChooser chooser;
	private FFmpeg ffmpegConvertor = new FFmpeg();
	private boolean isRunning = false;
	private ConvertMediaInfo currentMediaInfo = null;
	private AmonkerUIApplication app = null;
	
	private Project projectPane = new Project(ffmpegConvertor);
//	private MultiPageTableListPane settingPane = null;
	private FFmpegArgumentsManagerPane settingPane = null;
	
	private ImageView imgStart = ImageResources.getImage(Type.Start);
    private ImageView imgStop = ImageResources.getImage(Type.Stop);
//	@FXML
//	void onBtnMethodManagerClicked(ActionEvent event)
//	{
//		app.pushPage(new AmonkerUIPage("Setting", settingPane));//new FFmpegArgumentsManagerPane()));
//	}

	@FXML
	void onBtnOpenClicked(ActionEvent event)
	{
		projectPane.clear();
		projectPane.reloadMethods();
		app.pushPage(new AmonkerUIPage(projectPane));
	}

	public void setRunningStatus(boolean running)
	{
		if (running) {
			btnStartOrStop.setGraphic(imgStop);
			btnStartOrStop.getTooltip().setText("停止转换");
		} else {
			btnStartOrStop.setGraphic(imgStart);
			btnStartOrStop.getTooltip().setText("开始转换");
		}
		isRunning = running;
		btnOpen.setDisable(isRunning);
		btnEditItem.setDisable(isRunning);
		btnApplyMethods.setDisable(isRunning);
		btnApplyPath.setDisable(isRunning);
		btnChoicePath.setDisable(isRunning);
		btnDelete.setDisable(isRunning);
		btnClear.setDisable(isRunning);
		btnStartOrStop.setGraphic(imgStop);
	}
	
	@FXML
	void onBtnStartOrStopClicked(ActionEvent event)
	{
		if (infos.size() <= 0)
			return;
		if (isRunning) {
			if (currentMediaInfo != null)
				currentMediaInfo.stop();
			setRunningStatus(false);
			return;
		}
		currentMediaInfo = null;
		for (ConvertMediaInfo info : infos) {
			if (info.getCurrentStatus() == Status.Ready ||
				info.getCurrentStatus() == Status.Failed) {
				info.start();
				currentMediaInfo = info;
				setRunningStatus(true);
				break;
			}
		}
	}

	@FXML
	void onBtnDeleteClicked(ActionEvent event)
	{
		int index = tblConvertInfo.getSelectionModel().getSelectedIndex();
		if (index < 0 || index >= infos.size())
			return;
		
		infos.remove(index);
	}
	
	@FXML
	void onBtnClearClicked(ActionEvent event)
	{
		infos.removeAll(infos.toArray(new ConvertMediaInfo[1]));
	}
	
	@FXML
	void onBtnApplyMethodsClicked(ActionEvent event) 
	{
	}

	@FXML
	void onBtnApplyPathClicked(ActionEvent event) 
	{
	}

	@FXML
	void onBtnChoicePathClicked(ActionEvent event) 
	{
	}
	    
	@FXML
	void onBtnEditItemClicked(ActionEvent event) 
	{
	}

	@FXML
	void initialize()
	{
		assert btnApplyMethods != null : "fx:id=\"btnApplyMethods\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert btnApplyPath != null : "fx:id=\"btnApplyPath\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert btnChoicePath != null : "fx:id=\"btnChoicePath\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert btnClear != null : "fx:id=\"btnClear\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert btnDelete != null : "fx:id=\"btnDelete\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert btnEditItem != null : "fx:id=\"btnEditItem\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert btnOpen != null : "fx:id=\"btnOpen\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert btnStartOrStop != null : "fx:id=\"btnStartOrStop\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert cbMethods != null : "fx:id=\"cbMethods\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert tblConvertInfo != null : "fx:id=\"tblConvertInfo\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert tblcolFilename != null : "fx:id=\"tblcolFilename\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert tblcolMethod != null : "fx:id=\"tblcolMethod\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert tblcolProgress != null : "fx:id=\"tblcolProgress\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert tblcolStatus != null : "fx:id=\"tblcolStatus\" was not injected: check your FXML file 'MainPane.fxml'.";
        assert txtPath != null : "fx:id=\"txtPath\" was not injected: check your FXML file 'MainPane.fxml'.";
        System.out.println("");
	}

	public MainPane()
	{
		super();
//		initialize();
		
		settingPane = new FFmpegArgumentsManagerPane();
        
        tblcolProgress
				.setCellValueFactory(new PropertyValueFactory<ConvertMediaInfo, Double>(
						"currentProgress"));// "currentProgress"));
		tblcolFilename
				.setCellValueFactory(new PropertyValueFactory<ConvertMediaInfo, String>(
						"destinationMediaName"));
		tblcolStatus
				.setCellValueFactory(new PropertyValueFactory<ConvertMediaInfo, ConvertMediaInfo.Status>(
						"currentStatus"));
		tblcolMethod
				.setCellValueFactory(new PropertyValueFactory<ConvertMediaInfo, String>(
						"method"));
		tblcolProgress.setCellFactory(ProgressBarTableCell
				.<ConvertMediaInfo> forTableColumn());

		tblConvertInfo.setEditable(true);
		tblConvertInfo.setItems(infos);
		tblConvertInfo.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		tblConvertInfo.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends Integer> arg0) {
				boolean isSelected = false;
				if (arg0.getList().size() > 0) {
					isSelected = true;
					btnClear.setDisable(false);
				} else {
					if (infos.size() <= 0)
						btnClear.setDisable(true);
				}
				btnEditItem.setDisable(!isSelected);
				btnDelete.setDisable(!isSelected);
				btnApplyMethods.setDisable(!isSelected);
				btnApplyPath.setDisable(!isSelected);
			}
			
		});
		
		btnStartOrStop.setTooltip(new Tooltip("开始转换"));
		btnStartOrStop.setPrefSize(30, 30);
		btnStartOrStop.setText("");
		btnStartOrStop.setDisable(true);
		btnStartOrStop.setGraphic(ImageResources.getImage(Type.Start));
		
		btnEditItem.setTooltip(new Tooltip("编辑选择项信息"));
		btnEditItem.setPrefSize(30, 30);
		btnEditItem.setText("");
		btnEditItem.setDisable(true);
		btnEditItem.setGraphic(ImageResources.getImage(Type.Modified));
		
		btnOpen.setTooltip(new Tooltip("增加待转换的视频文件"));
		btnOpen.setPrefSize(30, 30);
		btnOpen.setText("");
		btnOpen.setGraphic(ImageResources.getImage(Type.AddMovie));
		
		btnDelete.setTooltip(new Tooltip("删除所有选择项"));
		btnDelete.setPrefSize(30, 30);
		btnDelete.setText("");
		btnDelete.setDisable(true);
		btnDelete.setGraphic(ImageResources.getImage(Type.Delete));
		
		btnClear.setTooltip(new Tooltip("删除所有项"));
		btnClear.setPrefSize(30, 30);
		btnClear.setText("");
		btnClear.setDisable(true);
		btnClear.setGraphic(ImageResources.getImage(Type.Clear));
		
		btnApplyMethods.setTooltip(new Tooltip("应用转换方式到所有选择项"));
		btnApplyMethods.setPrefSize(30, 30);
		btnApplyMethods.setText("");
		btnApplyMethods.setDisable(true);
		btnApplyMethods.setGraphic(ImageResources.getImage(Type.Apply));
		
		btnChoicePath.setTooltip(new Tooltip("选择目标路径"));
		btnChoicePath.setPrefSize(30, 30);
		btnChoicePath.setText("");
		btnChoicePath.setGraphic(ImageResources.getImage(Type.Open));
		
		txtPath.setText("原始文件夹");
		
		btnApplyPath.setTooltip(new Tooltip("应用目标路径到所有选择项"));
		btnApplyPath.setPrefSize(30, 30);
		btnApplyPath.setText("");
		btnApplyPath.setDisable(true);
		btnApplyPath.setGraphic(ImageResources.getImage(Type.Apply));
		
		chooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("MPEG-4 Moive File (*.mp4)", "*.mp4");
		chooser.getExtensionFilters().add(filter);
		filter = new FileChooser.ExtensionFilter("Adobe Flash Video File (*.flv)", "*.flv");
		chooser.getExtensionFilters().add(filter);
		filter = new FileChooser.ExtensionFilter("Apple QuickTime Video File (*.mov)", "*.mov");
		chooser.getExtensionFilters().add(filter);
		filter = new FileChooser.ExtensionFilter("MPEGTS Video File (*.ts)", "*.ts");
		chooser.getExtensionFilters().add(filter);

	}

	///TODO:IConvertListener methods
	@Override
	public void start(ConvertMediaInfo info) 
	{
		currentMediaInfo = info;
	}

	@Override
	public void running(ConvertMediaInfo info, float progress) {}

	@Override
	public void failed(ConvertMediaInfo info) 
	{
		currentMediaInfo = null;
	}

	@Override
	public void finished(ConvertMediaInfo info) 
	{
		currentMediaInfo = null;
		int index = infos.indexOf(info) + 1;
		if (index < 0 || index >= infos.size()) {
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					setRunningStatus(false);
				}
			});
			return;
		}
		
		ConvertMediaInfo newInfo = infos.get(index);
		newInfo.start();
		currentMediaInfo = newInfo;
		
	}

	//TODO: IPagePushListener Methods
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
		if (didPopPage.getNode().equals(projectPane) && projectPane.getResult() == PageReBackResult.OK) {
			ObservableList<ConvertMediaInfo> list = projectPane.getConvertMediaInfoList();
			for (ConvertMediaInfo convertMediaInfo : list) {
				convertMediaInfo.setListener(this);
				infos.add(convertMediaInfo);
			}
			if (list.size() > 0) {
				btnStartOrStop.setDisable(false);
				btnClear.setDisable(false);
			}
		}
	}

	//TODO: IFunction1Operator Methods
	@Override
	public void function1Clicked() 
	{
		app.pushPage(new AmonkerUIPage("Setting", settingPane));
	}

	@Override
	public String getFunction1Title() 
	{
		return null;//"⊙";
	}

	@Override
	public Image getFunction1Image() 
	{
		return new Image("/resources/buttonImage/Setting.png");
	}

	//TODO: IApplicationStatusListener Methods
	@Override
	public void willExitApplication(AmonkerUIApplication app)
	{
		if (isRunning) {
			if (currentMediaInfo != null)
				currentMediaInfo.stop();
			setRunningStatus(false);
			return;
		}
	}

	@Override
	public void didExitApplication(AmonkerUIApplication app)
	{
	}
}

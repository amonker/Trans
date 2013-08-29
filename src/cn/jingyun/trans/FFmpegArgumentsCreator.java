package cn.jingyun.trans;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import cn.jingyun.convertor.ffmpeg.CodecInfo;
import cn.jingyun.convertor.ffmpeg.CodecManager;
import cn.jingyun.convertor.ffmpeg.CodecManager.CodecType;
import cn.jingyun.convertor.ffmpeg.FFmpegArguments;
import cn.jingyun.javafx.amonker.ui.AmonkerUIApplication;
import cn.jingyun.javafx.amonker.ui.FXMLVBox;
import cn.jingyun.javafx.amonker.ui.IFunction1Operator;
import cn.jingyun.javafx.amonker.ui.IPagePushListener;
import cn.jingyun.javafx.amonker.ui.PageReBackResult;

public class FFmpegArgumentsCreator extends FXMLVBox implements IFunction1Operator, IPagePushListener
{
	@FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private ComboBox<String> cbAudioBitrate;//-ab -ab 96
    @FXML private ComboBox<String> cbAudioCodec;//-acodec -acodec aac
    @FXML private ComboBox<String> cbChannel;//-ac 1=mono 2=stereo -ac 2
    @FXML private ComboBox<String> cbFrameRate;//-r -r 15
    @FXML private ComboBox<String> cbResolution;//-s -s 320*240
    @FXML private ComboBox<String> cbSampleRate;//-ar -ar 44100
    @FXML private ComboBox<String> cbVideoCodec;//-vcodec
    @FXML private ComboBox<String> cbVideoCompressBitrate;//-b -b 1500
    @FXML private Slider sliderVolume;//-vol -vol 200
    @FXML private TextField txtExtension;
    @FXML private TextField txtName;
    
    private PageReBackResult result = PageReBackResult.Cancel;
    private AmonkerUIApplication app = null;
    
    private final static Map<String, String> audioSampleRateMap = new HashMap<String, String>();
    private final static Map<String, String> audioChannelsMap = new HashMap<String, String>();
    private final static Map<String, String> audioBitrateMap = new HashMap<String, String>();
    private final static Map<String, String> audioCodecMap = new HashMap<String, String>();
    
    private final static Map<String, String> videoCompressBitrateMap = new HashMap<String, String>();
    private final static Map<String, String> videoFrameRateMap = new HashMap<String, String>();
    private final static Map<String, String> videoResolutionMap = new HashMap<String, String>();
    private final static Map<String, String> videoCodecMap = new HashMap<String, String>();
    
    static {
    	CodecInfo[] infos = CodecManager.getInstance().getCodecs(CodecType.VideoEncorder);
    	videoCodecMap.put("NotSet", "-vn");
        for (CodecInfo codecInfo : infos) {
			videoCodecMap.put(codecInfo.toString(), "-vcodec " + codecInfo.getName());//.add(codecInfo.toString());
		}
        
        infos = CodecManager.getInstance().getCodecs(CodecType.AudioEncorder);
        audioCodecMap.put("NotSet", "-an");
        for (CodecInfo codecInfo : infos) {
			audioCodecMap.put(codecInfo.toString(), "-acodec " + codecInfo.getName());//.add(codecInfo.toString());
		}
        
        audioSampleRateMap.put("NotSet", "");
        audioSampleRateMap.put("4000 Hz", "-ar 4000");
        audioSampleRateMap.put("8000 Hz", "-ar 8000");
        audioSampleRateMap.put("11025 Hz", "-ar 11025");
        audioSampleRateMap.put("22050 Hz", "-ar 22050");
        audioSampleRateMap.put("44100 Hz", "-ar 44100");
        audioSampleRateMap.put("48000 Hz", "-ar 48000");
        
        audioChannelsMap.put("NotSet", "");
        audioChannelsMap.put("Mono", "-ac 1");
        audioChannelsMap.put("Stereo", "-ac 2");
        
        audioBitrateMap.put("NotSet", "");
        audioBitrateMap.put("64 kbps", "-ab 64");
        audioBitrateMap.put("80 kbps", "-ab 80");
        audioBitrateMap.put("96 kbps", "-ab 96");
        audioBitrateMap.put("112 kbps", "-ab 112");
        audioBitrateMap.put("128 kbps", "-ab 128");
        audioBitrateMap.put("160 kbps", "-ab 160");
        audioBitrateMap.put("192 kbps", "-ab 192");
        audioBitrateMap.put("224 kbps", "-ab 224");
        audioBitrateMap.put("256 kbps", "-ab 256");
        audioBitrateMap.put("320 kbps", "-ab 320");
        audioBitrateMap.put("384 kbps", "-ab 384");
        
        videoCompressBitrateMap.put("NotSet", "");
        videoCompressBitrateMap.put("VoIP(16 kbps)", "-b 16");
        videoCompressBitrateMap.put("Video Conference(128 kbps)", "-b 128");
        videoCompressBitrateMap.put("Video Conference(256 kbps)", "-b 256");
        videoCompressBitrateMap.put("Video Conference(384 kbps)", "-b 384");
        videoCompressBitrateMap.put("VHS(1 Mbps)", "-b 1024");
        videoCompressBitrateMap.put("VCD(1.25 Mbps)", "-b 1280");
        videoCompressBitrateMap.put("DVD(1.5 Mbps)", "-b 1536");
        videoCompressBitrateMap.put("HDTV(8 Mbps)", "-b 8096");
        videoCompressBitrateMap.put("HDTV(10 Mbps)", "-b 10240");
        videoCompressBitrateMap.put("HDTV(15 Mbps)", "-b 15360");
        videoCompressBitrateMap.put("HD-DVD(29.4 Mbps)", "-b 30106");
        videoCompressBitrateMap.put("Blu-ray Disc(40 Mbps)", "-b 40960");
        videoCompressBitrateMap.put("SonyHDCAM SR(SQ)(440 Mbps)", "-b 450560");
        videoCompressBitrateMap.put("SonyHDCAM SR(HQ)(440 Mbps)", "-b 901120");
        
        videoFrameRateMap.put("NotSet", "");
        videoFrameRateMap.put("5 frames/sec", "-r 5");
        videoFrameRateMap.put("10 frames/sec", "-r 10");
        videoFrameRateMap.put("12 frames/sec", "-r 12");
        videoFrameRateMap.put("15 frames/sec", "-r 15");
        videoFrameRateMap.put("NTSC Film(23.976 frames/sec)", "-r 23.976");
        videoFrameRateMap.put("24 frames/sec", "-r 24");
        videoFrameRateMap.put("PAL Film/Video(25 frames/sec)", "-r 25");
        videoFrameRateMap.put("NTSC Video(29.97 frames/sec)", "-r 29.97");
        videoFrameRateMap.put("30 frames/sec", "-r 30");
        videoFrameRateMap.put("50 frames/sec", "-r 50");
        videoFrameRateMap.put("59.94 frames/sec", "-r 59.94");
        videoFrameRateMap.put("60 frames/sec", "-r 60");
        
        videoResolutionMap.put("NotSet", "");
        videoResolutionMap.put("QQCIF(88×72)", "-s 88*72");
        videoResolutionMap.put("SUB-QCIF(128×96)", "-s 128*96");
        videoResolutionMap.put("QQVGA(160×120)", "-s 160*120");
        videoResolutionMap.put("QCIF(176×144)", "-s 176*144");
        videoResolutionMap.put("SUB-QVGA-(208×176)", "-s 208*176");
        videoResolutionMap.put("SUB-QVGA(220×176)", "-s 220*176");
        videoResolutionMap.put("SUB-QVGA+(240×176)", "-s 240*176");
        videoResolutionMap.put("CGA(320×200)", "-s 320*200");
        videoResolutionMap.put("QVGA(320×240)", "-s 320*240");
        videoResolutionMap.put("CIF(352×288)", "-s 352*288");
        videoResolutionMap.put("nhd(640×360)", "-s 640*360");
        videoResolutionMap.put("WQVGA(400×240)", "-s 400*240");
        videoResolutionMap.put("WQVGA(400×320)", "-s 400*320");
        videoResolutionMap.put("WQVGA(480×240)", "-s 480*240");
        videoResolutionMap.put("WQVGA(480×272)", "-s 480*272");
        videoResolutionMap.put("HQVGA(480×320)", "-s 480*320");
        videoResolutionMap.put("VGA(640×480)", "-s 640*480");
        videoResolutionMap.put("EGA(640×350)", "-s 640*350");
        videoResolutionMap.put("VGA+(720×480)", "-s 720*480");
        videoResolutionMap.put("PAL(768×576)", "-s 768*576");
        videoResolutionMap.put("WVGA(800×480)", "-s 800*480");
        videoResolutionMap.put("FWVGA(854×480)", "-s 854*480");
        videoResolutionMap.put("SVGA(800×600)", "-s 800*600");
        videoResolutionMap.put("DVGA(960×640)", "-s 960*640");
        videoResolutionMap.put("WSVGA(1024×600)", "-s 1024*600");
        videoResolutionMap.put("XGA(1024×768)", "-s 1024*768");
        videoResolutionMap.put("WXGA(1280×768)", "-s 1280*768");
        videoResolutionMap.put("(1280×800)", "-s 1280*800");
        videoResolutionMap.put("UxGA/XVGA(1280×960)", "-s 1280*960");
        videoResolutionMap.put("SXGA+(1280×1024)", "-s 1280*1024");
        videoResolutionMap.put("SXGA+(1400×1050)", "-s 1400*1050");
        videoResolutionMap.put("WXGA+(1440×900)", "-s 1440*900");
        videoResolutionMap.put("WSXGA(1600×1024)", "-s 1600*1024");
        videoResolutionMap.put("(1600×1050)", "-s 1600*1050");
        videoResolutionMap.put("USVGA/UXGA/UGA(1600×1200)", "-s 1600*1200");
        videoResolutionMap.put("WSXGA+(1680×1050)", "-s 1680*1050");
        videoResolutionMap.put("UXGA(1900×1200)", "-s 1900*1200");
        videoResolutionMap.put("WSUVGA+(WSUGA/HDTV)(1920×1080)", "-s 1920*1080");
        videoResolutionMap.put("WUXGA(1920×1200)", "-s 1920*1200");
        videoResolutionMap.put("SUVGA(QXGA)(2048×1536)", "-s 2048*1536");
        videoResolutionMap.put("UWXGA(2560×1600)", "-s 2560*1600");
        videoResolutionMap.put("USXGA(2560×2048)", "-s 2560*2048");
        videoResolutionMap.put("QUXGA(3200×2400)", "-s 3200*2400");
        videoResolutionMap.put("WQUXGA(3840×2400)", "-s 3840*2400");
    }
    
    private String arguments = "";
    
	public FFmpegArgumentsCreator()
	{
		super();
		initialize();
	}
	
	public String getArguments() { return arguments; }
	public String getName() { return txtName.getText(); }
	public String getExtension() { return txtExtension.getText(); }
	public PageReBackResult getResult() { return result; } 

    @FXML
    void initialize() 
    {
    	assert cbAudioBitrate != null : "fx:id=\"cbAudioBitrate\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";
        assert cbAudioCodec != null : "fx:id=\"cbAudioCodec\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";
        assert cbChannel != null : "fx:id=\"cbChannel\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";
        assert cbFrameRate != null : "fx:id=\"cbFrameRate\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";
        assert cbResolution != null : "fx:id=\"cbResolution\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";
        assert cbSampleRate != null : "fx:id=\"cbSampleRate\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";
        assert cbVideoCodec != null : "fx:id=\"cbVideoCodec\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";
        assert cbVideoCompressBitrate != null : "fx:id=\"cbVideoCompressBitrate\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";
        assert sliderVolume != null : "fx:id=\"sliderVolume\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";
        assert txtExtension != null : "fx:id=\"txtExtension\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";
        assert txtName != null : "fx:id=\"txtName\" was not injected: check your FXML file 'FFmpegArgumentsCreator.fxml'.";

        cbAudioBitrate.setItems(FXCollections.observableArrayList(audioBitrateMap.keySet()));
        cbAudioCodec.setItems(FXCollections.observableArrayList(audioCodecMap.keySet()));
        cbChannel.setItems(FXCollections.observableArrayList(audioChannelsMap.keySet()));
        cbFrameRate.setItems(FXCollections.observableArrayList(videoFrameRateMap.keySet()));
        cbResolution.setItems(FXCollections.observableArrayList(videoResolutionMap.keySet()));
        cbSampleRate.setItems(FXCollections.observableArrayList(audioSampleRateMap.keySet()));
        cbVideoCodec.setItems(FXCollections.observableArrayList(videoCodecMap.keySet()));
        cbVideoCompressBitrate.setItems(FXCollections.observableArrayList(videoCompressBitrateMap.keySet()));
        resetValue();
    }
    
    private void argumentsToString(ComboBox<String> cb, Map<String, String> map, StringBuilder sb)
    {
    	int index = cb.getSelectionModel().getSelectedIndex();
    	if (index >= 0 || index < map.size())
    		sb.append(map.get(cb.getSelectionModel().getSelectedItem()) + " ");
    }
    
    private String getArgumentKey(String value, Map<String, String> map)
    {
    	String ret = null;
    	if (map.containsValue(value))
    		return ret;
    	value = value.trim();
    	Collection<Entry<String, String>> col = map.entrySet();
    	for (Entry<String, String> entry : col) {
    		if (entry.getValue().equals(value)) {
				ret = entry.getKey();
				break;
			}
		}
    	return ret;
    }
    
    public void setArguments(FFmpegArguments arguments)
    {
    	resetValue();
    	txtName.setText(arguments.getName());
    	txtExtension.setText(arguments.getFileExtension());
    	
    	Pattern p = Pattern.compile("(-\\S+)\\s+([^-]+)");
    	Matcher m = p.matcher(arguments.getArguments());
    	String str, arg;
    	while (m.find()) {
    		str = m.group();
    		arg = m.group(1);
    		
    		switch (arg) {
    		case "-ab":
    			str = getArgumentKey(str, audioBitrateMap);
    			if (str != null)
    				cbAudioBitrate.getSelectionModel().select(str);
    			break;
    		case "-acodec":
    			str = getArgumentKey(str, audioCodecMap);
    			if (str != null)
    				cbAudioCodec.getSelectionModel().select(str);
    			break;
    		case "-ac":
    			str = getArgumentKey(str, audioChannelsMap);
    			if (str != null)
    				cbChannel.getSelectionModel().select(str);
    			break;
    		case "-ar":
    			str = getArgumentKey(str, audioSampleRateMap);
    			if (str != null)
    				cbSampleRate.getSelectionModel().select(str);
    			break;
    		case "-an":
    			cbAudioCodec.getSelectionModel().select("NotSet");
    			break;
    		case "-vol":
    			sliderVolume.setValue(Double.valueOf(m.group(2)));
    			break;
    		case "-r":
    			str = getArgumentKey(str, videoFrameRateMap);
    			if (str != null)
    				cbFrameRate.getSelectionModel().select(str);
    			break;
    		case "-s":
    			str = getArgumentKey(str, videoResolutionMap);
    			if (str != null)
    				cbResolution.getSelectionModel().select(str);
    			break;
    		case "-b":
    			str = getArgumentKey(str, videoCompressBitrateMap);
    			if (str != null)
    				cbVideoCompressBitrate.getSelectionModel().select(str);
    			break;
    		case "-vcodec":
    			str = getArgumentKey(str, videoCodecMap);
    			if (str != null)
    				cbVideoCodec.getSelectionModel().select(str);
    			break;
    		case "-vn":
    			cbVideoCodec.getSelectionModel().select("NotSet");
    			break;
    		}
    	}
    }
    
    public void resetValue()
    {
    	txtName.setText("");
    	txtExtension.setText("");
    	cbAudioBitrate.getSelectionModel().select("NotSet");
    	cbAudioCodec.getSelectionModel().select("NotSet");
    	cbChannel.getSelectionModel().select("NotSet");
    	cbFrameRate.getSelectionModel().select("NotSet");
    	cbResolution.getSelectionModel().select("NotSet");
    	cbSampleRate.getSelectionModel().select("NotSet");
    	cbVideoCodec.getSelectionModel().select("NotSet");
    	cbVideoCompressBitrate.getSelectionModel().select("NotSet");
    	sliderVolume.setValue(100);
    }
    
    public void setModifiedMode(boolean isModified)
	{
		txtName.setEditable(!isModified);
		if (!isModified)
			resetValue();
	}
    
//    @FXML
//    void btnOKClicked(ActionEvent event) 
//    {
//    	StringBuilder sb = new StringBuilder();
// 
//    	argumentsToString(cbAudioBitrate, audioBitrateMap, sb);
//    	argumentsToString(cbAudioCodec, audioCodecMap, sb);
//    	argumentsToString(cbChannel, audioChannelsMap, sb);
//    	argumentsToString(cbSampleRate, audioSampleRateMap, sb);
//    	int volume = (int)sliderVolume.getValue();
//    	if (volume != 100)
//    		sb.append("-vol " + volume + " ");
//    	else if (volume == 0)
//    		sb.append("-an");
//    	
//    	argumentsToString(cbVideoCodec, videoCodecMap, sb);
//    	argumentsToString(cbVideoCompressBitrate, videoCompressBitrateMap, sb);
//    	argumentsToString(cbFrameRate, videoFrameRateMap, sb);
//    	argumentsToString(cbResolution, videoResolutionMap, sb);
//    	
//    	arguments = sb.toString();
//    	
//    	Pattern p = Pattern.compile("(-\\S+)\\s+([^-]+)");
//    	Matcher m = p.matcher(arguments);
//    	
//    	while(m.find()) {
//    		int count = m.groupCount();
//    		System.out.print(m.group());
//    		for (int i = 1; i <= count; i++) {
//    			System.out.print(" " + m.group(i));
//    		}
//    		System.out.println();
//    	}
//    	
//    	back();
//		if (getListener() != null)
//			getListener().okClicked();
//    }
//
//    @FXML
//    void onBtnBackClicked(ActionEvent event) 
//    { 
//    	back();
//		if (getListener() != null)
//			getListener().cancelClicked();
//    }

    //TODO: IFunction1Operator Methods
	@Override
	public void function1Clicked()
	{
		StringBuilder sb = new StringBuilder();
		 
    	argumentsToString(cbAudioBitrate, audioBitrateMap, sb);
    	argumentsToString(cbAudioCodec, audioCodecMap, sb);
    	argumentsToString(cbChannel, audioChannelsMap, sb);
    	argumentsToString(cbSampleRate, audioSampleRateMap, sb);
    	int volume = (int)sliderVolume.getValue();
    	if (volume != 100)
    		sb.append("-vol " + volume + " ");
    	else if (volume == 0)
    		sb.append("-an");
    	
    	argumentsToString(cbVideoCodec, videoCodecMap, sb);
    	argumentsToString(cbVideoCompressBitrate, videoCompressBitrateMap, sb);
    	argumentsToString(cbFrameRate, videoFrameRateMap, sb);
    	argumentsToString(cbResolution, videoResolutionMap, sb);
    	
    	arguments = sb.toString();
    	
    	Pattern p = Pattern.compile("(-\\S+)\\s+([^-]+)");
    	Matcher m = p.matcher(arguments);
    	
    	while(m.find()) {
    		int count = m.groupCount();
    		System.out.print(m.group());
    		for (int i = 1; i <= count; i++) {
    			System.out.print(" " + m.group(i));
    		}
    		System.out.println();
    	}
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

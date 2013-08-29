package cn.jingyun.convertor.ffmpeg;

import cn.jingyun.convertor.utils.ParameterDBM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FFmpegArgumentsManager
{
	private static final FFmpegArgumentsManager instance = new FFmpegArgumentsManager();
	private ObservableList<FFmpegArguments> list = FXCollections.observableArrayList();
	private ParameterDBM dbm = ParameterDBM.getInstance();
	
	public static FFmpegArgumentsManager getInstance() { return instance; }
	
	private FFmpegArgumentsManager() 
	{
		dbm.loadAllFFmpegArguments(list);
		
		if (list.size() == 0) {
			addArguments("iPhone Video", "-s 320*240 -acodec libvo_aacenc -ar 22050 -ab 128k -vcodec libx264 -threads 0 -f ipod", "mp4");
			addArguments("iPhone 3G/3GS Video", "-s 480*320 -acodec libvo_aacenc -ar 44100 -ab 128k -vcodec libx264 -threads 0 -f ipod", "mp4");
			addArguments("iPhone 4/4S Video", "-s 640*480 -acodec libvo_aacenc -ar 48000 -ab 160k -vcodec libx264 -threads 0 -f ipod", "mp4");
			addArguments("iPad Video", "-s 640*480 -acodec libvo_aacenc -ar 48000 -ab 160k -vcodec libx264 -threads 0 -f ipod", "mp4");
			addArguments("iPad Video HD", "-s 1280*720 -acodec libvo_aacenc -ar 48000 -ab 160k -vcodec libx264 -threads 0 -f ipod", "mp4");
			addArguments("PSP Video", "-vcodec libxvid -s 320*240 -r 29.97 -b 1500 -acodec libfacc -ac 2 -ar 24000 -ab 65535 -f psp", "mp4");
			addArguments("Extract AAC", "-vn", "aac");
			addArguments("Extract MP3", "-vn", "mp3");
			addArguments("Extract Motion JPEG", "-an", "mjpg");
		}
	}
	
	public ObservableList<FFmpegArguments> getArgumentsList() { return list; }
	
	public boolean addArguments(String name, String arguments, String extension)
	{
		boolean ret = false;
		if (name == null || arguments == null || extension == null)
			return ret;
		FFmpegArguments arg = new FFmpegArguments(name, arguments, extension);
		ret = dbm.addFFmpegArgument(arg);
		if (ret)
			list.add(arg);
		return ret;
	}
	
	public boolean deleteArgumentsAt(int index)
	{
		boolean ret = false;
		if (index < 0 || index >= list.size())
			return ret;
		ret = dbm.deleteFFmpegArgumentName(list.get(index).getName());
		if (ret)
			list.remove(index);
		return ret;
	}
	
	public boolean modifiedArgumentsAt(int index, String arguments, String extension)
	{
		boolean ret = false;
		if (index < 0 || index >= list.size() || arguments == null || extension == null)
			return ret;
		FFmpegArguments arg = list.get(index);
		
		ret = dbm.updateFFmpegArgumentsName(arg.getName(), arguments, extension);
		if (ret) {
			arg.setArguments(arguments);
			arg.setFileExtension(extension);
			list.set(index, arg);
		}
		
		return ret;
	}
}

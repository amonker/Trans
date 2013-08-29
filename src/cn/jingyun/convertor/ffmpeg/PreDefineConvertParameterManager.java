package cn.jingyun.convertor.ffmpeg;

import java.util.ArrayList;
import java.util.List;

public class PreDefineConvertParameterManager {

	private static final PreDefineConvertParameterManager instance = new PreDefineConvertParameterManager();
	private List<ConvertParameter> parameterList = new ArrayList<ConvertParameter>();
	
	private PreDefineConvertParameterManager() 
	{
		ConvertParameter parameter = new ConvertParameter("MJPEG Convertor", null, null, "mjpg");
		parameter.setSameQuality(true);
		parameterList.add(parameter);
		CodecInfo codec = CodecInfo.getCopyCodecInfo();
		parameter = new ConvertParameter("Copy Convertor", codec, codec, "mp4");
		parameterList.add(parameter);
		parameter = new ConvertParameter("CopyAAC Convertor", null, codec, "aac");
		parameterList.add(parameter);
	}
	
	public static PreDefineConvertParameterManager getInstance() { return instance; }
	
	public int getCount() { return parameterList.size(); }
	public ConvertParameter getParameterAtIndex(int index) 
	{
		if (index < 0 || index >= getCount())
			return null;
		return parameterList.get(index);
	}
}

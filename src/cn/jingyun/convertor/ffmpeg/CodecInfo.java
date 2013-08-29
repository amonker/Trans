package cn.jingyun.convertor.ffmpeg;

public class CodecInfo {
	private boolean _isDecoder;
	private boolean _isEncoder;
	private boolean _isVideoCodec;
	private boolean _isAudioCodec;
	private boolean _isSubtitleCodec;
	private boolean _supportDrawHorizBand;
	private boolean _supportDirectRenderingMethod;
	private boolean _supportWeirdFrameTruncation;
	
	private String _name;
	private String description;
	private static final CodecInfo copyCodec = new CodecInfo(" DEV.SD  copy            Copy Encorder ");
	
	static {
		copyCodec._isAudioCodec = true;
		copyCodec._isSubtitleCodec = true;
		copyCodec._isVideoCodec = true;
	}
	
	public static CodecInfo getCopyCodecInfo()
	{
		return copyCodec;
	}
	
	public CodecInfo(String strInfo) {
		_isDecoder = _isAudioCodec = _isDecoder = _isSubtitleCodec = _isVideoCodec = _supportDirectRenderingMethod = _supportDrawHorizBand = _supportWeirdFrameTruncation = false;
		char[] tagBit = strInfo.substring(1, 8).toCharArray();
		if (tagBit[0] != '.')
			_isDecoder = true;
		if (tagBit[1] != '.')
			_isEncoder = true;
		switch(tagBit[2]) {
		case 'V':
			_isVideoCodec = true;
			break;
		case 'A':
			_isAudioCodec = true;
			break;
		case 'S':
			_isSubtitleCodec = true;
			break;
		}
		if (tagBit[4] != '.')
			_supportDrawHorizBand = true;
		if (tagBit[5] != '.')
			_supportDirectRenderingMethod = true;
		if (tagBit[6] != '.')
			_supportWeirdFrameTruncation = true;
		strInfo = strInfo.substring(8).trim();
		String[] strParses = strInfo.split(" ");
		_name = strParses[0];
		description = strInfo.substring(_name.length()).trim();
//		System.out.println(_name + "\t\t" + description);
	}
	
	@Override
	public String toString() {
		return description;
	}

	public boolean isDecoder() {
		return _isDecoder;
	}

	public boolean isEncoder() {
		return _isEncoder;
	}

	public boolean isVideoCodec() {
		return _isVideoCodec;
	}

	public boolean isAudioCodec() {
		return _isAudioCodec;
	}

	public boolean isSubtitleCodec() {
		return _isSubtitleCodec;
	}

	public boolean isSupportDrawHorizBand() {
		return _supportDrawHorizBand;
	}

	public boolean isSupportDirectRenderingMethod() {
		return _supportDirectRenderingMethod;
	}

	public boolean isSupportWeirdFrameTruncation() {
		return _supportWeirdFrameTruncation;
	}

	public String getName() {
		return _name;
	}
}

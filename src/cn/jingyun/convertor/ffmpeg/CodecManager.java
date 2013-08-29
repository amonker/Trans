package cn.jingyun.convertor.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CodecManager {
	public enum CodecType {
		VideoDecorder,
		AudioDecorder,
		VideoEncorder,
		AudioEncorder;
	}
	
	private static CodecManager instance = null;
	
	public static synchronized CodecManager getInstance()
	{
		if (instance == null)
			instance = new CodecManager();
		return instance;
	}
	
	private ArrayList<CodecInfo> codecs;
	
	public CodecInfo[] getCodecs(CodecType type)
	{
		ArrayList<CodecInfo> c = new ArrayList<CodecInfo>();
		for (CodecInfo codecInfo : codecs) {
			switch (type) {
			case VideoDecorder:
				if (codecInfo.isDecoder() && codecInfo.isVideoCodec())
					c.add(codecInfo);
				break;
			case VideoEncorder:
				if (codecInfo.isEncoder() && codecInfo.isVideoCodec())
					c.add(codecInfo);
				break;
			case AudioDecorder:
				if (codecInfo.isDecoder() && codecInfo.isAudioCodec())
					c.add(codecInfo);
				break;
			case AudioEncorder:
				if (codecInfo.isEncoder() && codecInfo.isAudioCodec())
					c.add(codecInfo);
				break;
			}
		}
		return (CodecInfo[])c.toArray(new CodecInfo[0]);
	}
	
	private CodecManager() 
	{
		ProcessBuilder pb = new ProcessBuilder(FFmpeg.getFFmpegPath(), "-codecs");
		try {
			Process process = pb.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = in.readLine();
			codecs = new ArrayList<CodecInfo>();
			codecs.add(CodecInfo.getCopyCodecInfo());
			boolean isPrint = false;
			while(line != null) {
				if (line.trim().length() == 0) {
					isPrint = false;
				}
				if (isPrint) {
					codecs.add(new CodecInfo(line));
				} else {
					if (line.trim().equals("-------")) {
						isPrint = true;
					}
				}
				line = in.readLine();
			}
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

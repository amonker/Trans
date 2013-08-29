package cn.jingyun.convertor.ffmpeg;

import java.util.ArrayList;
import java.util.List;

public final class ConvertParameter
{
	private CodecInfo videoCodec;
	private CodecInfo audioCodec;
	private String formatSuffix;
	private String name;
	private boolean sameQuality = true;

	public ConvertParameter(String name, CodecInfo videoCodec,
			CodecInfo audioCodec, String formatSuffix)
	{
		setName(name);
		setVideoCodec(videoCodec);
		setAudioCodec(audioCodec);
		setFormatSuffix(formatSuffix);
	}
	
	public ConvertParameter(String parameterCmdLine)
	{
		
	}

	public boolean isSameQuality()
	{
		return sameQuality;
	}

	public void setSameQuality(boolean sameQuality)
	{
		this.sameQuality = sameQuality;
	}

	public CodecInfo getVideoCodec()
	{
		return videoCodec;
	}

	public void setVideoCodec(CodecInfo videoCodec)
	{
		if (this.videoCodec == videoCodec)
			return;
		if (videoCodec == null)
			this.videoCodec = null;
		else if (videoCodec.isEncoder() && videoCodec.isVideoCodec())
			this.videoCodec = videoCodec;
	}

	public CodecInfo getAudioCodec()
	{
		return audioCodec;
	}

	public void setAudioCodec(CodecInfo audioCodec)
	{
		if (this.audioCodec == audioCodec)
			return;
		if (audioCodec == null)
			this.audioCodec = null;
		else if (audioCodec.isEncoder() && audioCodec.isAudioCodec())
			this.audioCodec = audioCodec;
	}

	public String getFormatSuffix()
	{
		return formatSuffix;
	}

	public void setFormatSuffix(String formatSuffix)
	{
		this.formatSuffix = formatSuffix;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<String> getFFmpegArgs()
	{
		List<String> args = new ArrayList<String>();
		if (videoCodec != null) {
			args.add("-vcodec");
			args.add(videoCodec.getName());
		} else if (!sameQuality) {
			args.add("-vn");
		}
		if (audioCodec != null) {
			args.add("-acodec");
			args.add(audioCodec.getName());
		} else if (!sameQuality) {
			args.add("-an");
		}
		if (((audioCodec != CodecInfo.getCopyCodecInfo()) || (videoCodec != CodecInfo
				.getCopyCodecInfo())) && sameQuality)
			args.add("-sameq");
		return args;
	}
	
	public String getCommandLine()
	{
		StringBuilder sb = new StringBuilder();
		List<String> args = getFFmpegArgs();
		for (String arg : args)
			sb.append(arg + " ");
		return sb.toString();
	}

	@Override
	public String toString()
	{
		return name + "( " + formatSuffix + "=> VideoCodec:"
				+ (videoCodec != null ? videoCodec.getName() : "NonVideo")
				+ "; AudioCodec:"
				+ (audioCodec != null ? audioCodec.getName() : "NonAudio")
				+ " )";
	}
}

package cn.jingyun.convertor.ffmpeg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jingyun.convertor.utils.IController;
import cn.jingyun.convertor.utils.IStatusListener;
import cn.jingyun.convertor.utils.ParameterDBM;
import cn.jingyun.shell.Command;
import cn.jingyun.shell.CommandListener;

public class FFmpeg implements CommandListener, IController
{
	private static String ffmpegPath = null;
	private String sourceMedia;
	private String destinationMedia;
	private List<String> args = new ArrayList<String>();
	private IStatusListener listener = null;
	private int position = 0;
	private int length = 0;
	// private LinkedList<String> qList = new LinkedList<String>();
	// private Queue<String> messageQueue = (Queue<String>)qList;
	private static final Pattern pDuration = Pattern
			.compile("^Duration: ([^,]+).*");
	private static final Pattern pFrame = Pattern.compile(".*time=(\\S+).*");
	private static final Pattern patternTime = Pattern
			.compile("(\\d\\d)[:](\\d\\d)[:](\\d\\d)[.](\\d+)");
	private static final Pattern pNotPermitted = Pattern
			.compile(".*Operation not permitted.*");
	private static final Pattern pFailed = Pattern.compile(".*failed.*");
	private static final Pattern pUnknownDecoder = Pattern.compile("^Unknown\\s+decoder.*");//Unknown decoder 'copy'
	private static final Pattern pEnd = Pattern
			.compile("^video:\\d+kB\\s+audio:\\d+kB\\s+global\\s+headers:\\d+kB\\s+muxing\\s+overhead.*");// video:152161kB
																											// audio:14945kB
																											// global
																											// headers:0kB
																											// muxing
																											// overhead
																											// 0.413980%
	private Command cmd = new Command();

	public static void setFFmpegPath(String path)
	{
		File file = new File(path);
		if (file.exists()) {
			ffmpegPath = path;
			ParameterDBM.getInstance().setParameter("FFmpegPath", path);
		}
	}
	
	public static String getFFmpegPath()
	{
		return ffmpegPath;
	}
	
	static {
//		ffmpegPath = ParameterDBM.getInstance().getParameter("FFmpegPath");
//		if (ffmpegPath == null || ffmpegPath.length() == 0)
//			ffmpegPath = "/opt/Trans/app/tools/ffmpeg";
		String strFFmpegName = null;
		if (System.getProperty("os.name").toLowerCase().contains("windows"))
			strFFmpegName = "ffmpeg.exe";
		else
			strFFmpegName = "ffmpeg";
		ffmpegPath = ParameterDBM.finishPathEnd(ParameterDBM.finishPathEnd(new File("").getAbsolutePath()) + "tools") + strFFmpegName;
	}
	
	public int getPosition()
	{
		return position;
	}

	public int getLength()
	{
		return length;
	}

	public IStatusListener getListener()
	{
		return listener;
	}

	public void setListener(IStatusListener listener)
	{
		this.listener = listener;
	}

	public String[] getParameter()
	{
		return args.toArray(new String[1]);
	}

	public void setParameter(ConvertParameter parameter)
	{
		List<String> listArgs = parameter.getFFmpegArgs();
		args.clear();
		for (String arg : listArgs)
			args.add(arg);
	}
	
	public void setParameter(String commandLine)
	{
		String[] cmdLineArgs = commandLine.split(" ");
		args.clear();
		for (String arg : cmdLineArgs)
			if (arg != null && arg.length() > 0)
				args.add(arg);
	}

	/* (non-Javadoc)
	 * @see cn.jingyun.convertor.ffmpeg.IController#start()
	 */
	@Override
	public synchronized void start()
	{
		if (cmd.isRunning())
			cmd.stop();
		ArrayList<String> arg = new ArrayList<String>();
		arg.add(ffmpegPath);
		arg.add("-y");
		arg.add("-i");
		arg.add(sourceMedia);
		if (args.size() > 0)
			arg.addAll(args);
		arg.add(destinationMedia);
		System.out.println(sourceMedia + " => " + destinationMedia);
		for (String string : arg) {
			System.out.println(string);
		}
		cmd.setListener(this);
		cmd.start(arg.toArray(new String[1]));
	}

	/* (non-Javadoc)
	 * @see cn.jingyun.convertor.ffmpeg.IController#stop()
	 */
	@Override
	public synchronized void stop()
	{
		if (cmd.isRunning())
			cmd.stop();
	}

	public String getSourceMedia()
	{
		return sourceMedia;
	}

	public void setSourceMedia(String sourceMedia)
	{
		this.sourceMedia = sourceMedia;
	}

	public String getDestinationMedia()
	{
		return destinationMedia;
	}

	public void setDestinationMedia(String destinationMedia)
	{
		this.destinationMedia = destinationMedia;
	}

	public boolean isRunning()
	{
		return cmd.isRunning();
	}

	private int convertToMilliSecond(String millisecond)
	{
		int ret = -1;
		Matcher m = patternTime.matcher(millisecond.trim());
		if (m.matches()) {
			ret = Integer.parseInt(m.group(1));
			ret = ret * 60 + Integer.parseInt(m.group(2));
			ret = ret * 60 + Integer.parseInt(m.group(3));
			ret = ret * 100 + Integer.parseInt(m.group(4));
		}
		return ret;
	}

	@Override
	public void running()
	{
		if (listener != null)
			listener.start();
	}

	@Override
	public void outputLine(String value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void errorLine(String line)
	{
		line = line.trim();
		if (listener != null && pNotPermitted.matcher(line).matches()) {
			listener.failed();
			return;
		}
		if (listener != null && 
			(pFailed.matcher(line).matches() ||
			 pUnknownDecoder.matcher(line).matches())) {
			listener.failed();
			return;
		}
		if (listener != null && pEnd.matcher(line).matches()) {
			listener.finished();
			return;
		}

		Matcher m = pFrame.matcher(line);
		if (m.matches()) {
			String str = m.group(1);
			// System.out.println(str);
			if (str.contains(":"))
				position = convertToMilliSecond(str);
			else
				position = (int) Double.parseDouble(str) * 100;// convertToMilliSecond(str);
		} else {
			m = pDuration.matcher(line);
			if (m.matches()) {
				String str = m.group(1);
				// System.out.println(str);
				length = convertToMilliSecond(str);
			}
		}
		if (listener != null)
			listener.currentProgress(position / (float) length);
	}

	@Override
	public void finished(int exitValue)
	{
		if (listener != null) {
			listener.finished();
			return;
		}
	}

	public static void main(String[] args)
	{
		FFmpeg ff = new FFmpeg();
		ff.setSourceMedia("/tmp/1.flv");
		ff.setDestinationMedia("/tmp/2.mp4");
		ff.setParameter(new ConvertParameter("ss", null, null, "mp4"));
		ff.setListener(new IStatusListener() {
			
			@Override
			public void start()
			{
				System.out.println("start");
			}
			
			@Override
			public void paused()
			{
				System.out.println("paused");
			}
			
			@Override
			public void finished()
			{
				System.out.println("finished");
			}
			
			@Override
			public void failed()
			{
				System.out.println("failed");
			}
			
			@Override
			public void currentProgress(float progress)
			{
				System.out.println(progress);
			}
		});
		ff.start();
	}
}

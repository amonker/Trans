package cn.jingyun.convertor.mp4box;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import cn.jingyun.convertor.utils.IStatusListener;
import cn.jingyun.shell.Command;
import cn.jingyun.shell.CommandListener;

public class Split
{
	private String workMoive;
	private int startSecond;
	private int endSecond;
	private String extractFilename;
	private float startPosition;
	private float endPosition;
	private Command cmd = new Command();
	
	private int mulitRangeIndex = 0;
	private IStatusListener listener = null;
	
	public void split(String videoFile, int startSecond, int endSecond)
	{
		this.workMoive = videoFile;
		this.startSecond = startSecond;
		this.endSecond = endSecond;
		start();
	}
	
	public void splitMultiRange(String sourceVideoFile, final String destVideoFile, final int... time)
	{
		final IStatusListener oldListener = listener;
		final int countOfTime = time.length;
		mulitRangeIndex = 0;
		if (countOfTime < 2) {
			return;
		}
		
		listener = new IStatusListener() {
			List<String> filenames = new ArrayList<String>();
			private boolean startJoin = false;
			private int index = 0;
			@Override
			public void start() {
				if (!startJoin)
					if (oldListener != null)
						oldListener.start();
			}
			
			@Override
			public void finished() {
				if (startJoin) {
					for (String file : filenames) {
						File f = new File(file);
						if (f.exists())
							f.delete();
						f = null;
					}
					if (oldListener != null)
						oldListener.finished();
					return;
				}
				
				///split
				filenames.add(extractFilename);
				if (mulitRangeIndex + 1 >= countOfTime) {
					Join join = new Join();
					startJoin = true;
					Split.this.setListener(oldListener);
					index ++;
					join.setListener(this);
					join.join(destVideoFile, filenames.toArray(new String[1]));
					
					return;
				}
				startSecond = time[mulitRangeIndex++];
				endSecond = time[mulitRangeIndex++];
				index ++;
				start();
			}
			
			@Override
			public void currentProgress(float progress) {
				float unitProgress = 1.0f / (float)(countOfTime / 2 + 1);
				float tmpProgress = unitProgress * index + unitProgress * progress;
//				System.out.println(index + ":  " + progress + " SplitX: " + tmpProgress + "%");
				if (oldListener != null)
					oldListener.currentProgress(tmpProgress);
			}

			@Override
			public void failed()
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void paused()
			{
				// TODO Auto-generated method stub
				
			}
		};
		this.workMoive = sourceVideoFile;
		this.startSecond = time[mulitRangeIndex++];
		this.endSecond = time[mulitRangeIndex++];
		
		start();
	}
	
	public IStatusListener getListener() 
	{
		return listener;
	}

	public void setListener(IStatusListener listener) 
	{
		this.listener = listener;
	}
	
	public int getStartSecond() 
	{
		return startSecond;
	}

	public String getExtractFilename() 
	{
		return extractFilename;
	}

	public float getEndPosition() 
	{
		return endPosition;
	}

	public synchronized void start()
	{
		File file = new File(workMoive);
		if (!file.exists() || endSecond <= startSecond) {
			file = null;
			return;
		}
		file = null;
		List<String> args = new ArrayList<String>();
		args.add(MP4BoxUtils.mp4boxPath);
		args.add("-split-chunk");
		args.add(String.format("%d:%d", startSecond, endSecond));
		args.add(workMoive);
//		for (String string : args) {
//			System.out.println(string);
//		}
		cmd.setListener(new CommandListener() {
			

			@Override
			public void running()
			{
				if (listener != null)
					listener.start();
			}
			
			@Override
			public void finished(int exitValue)
			{
				System.out.println("Extract Filename: " + extractFilename + "  " + startPosition + "s => " + endPosition + "s");
				if (listener != null)
					listener.finished();
			}
			
			private void analyseCommandLine(String value)
			{
//				System.out.println(value);
				Matcher m = MP4BoxUtils.pSplitting.matcher(value);
				if (m.matches()) {
					float progress = Integer.parseInt(m.group(1)) / 200.0f;
					if (listener != null)
						listener.currentProgress(progress);
					return;
				}
				
				m = MP4BoxUtils.pISOFileWriting.matcher(value);
				if (m.matches()) {
					float progress = (Integer.parseInt(m.group(1)) + 100) / 200.0f;
//					System.out.println(m.group(1) + ": " + progress + "%");
					if (listener != null)
						listener.currentProgress(progress);
					return;
				}
				
				m = MP4BoxUtils.pExtractingChunk.matcher(value);
				if (m.matches()) {
					extractFilename = m.group(1);
					startPosition = Float.parseFloat(m.group(3));
					endPosition = Float.parseFloat(m.group(4));
				}
			}

			@Override
			public void outputLine(String value) 
			{
				analyseCommandLine(value);
			}

			@Override
			public void errorLine(String value) 
			{
				analyseCommandLine(value);
			}
		});
		cmd.start(args.toArray(new String[1]));
	}
	
	public synchronized void stop()
	{
		if (cmd.isRunning())
			cmd.stop();
	}
}

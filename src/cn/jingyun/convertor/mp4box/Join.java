package cn.jingyun.convertor.mp4box;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import cn.jingyun.convertor.utils.IController;
import cn.jingyun.convertor.utils.IStatusListener;
import cn.jingyun.shell.Command;
import cn.jingyun.shell.CommandListener;

public class Join implements IController
{

	private String[] filenames = null;
	private String outputFilename = null;
	private IStatusListener listener = null;
	private Command cmd = new Command();
	
	public void join(String outputFilename, String... filenames)
	{
		this.filenames = filenames;
		this.outputFilename = outputFilename;
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
	
	public synchronized void start()
	{
		List<String> args = new ArrayList<String>();
		args.add(MP4BoxUtils.mp4boxPath);
		for (String filename : filenames) {
			File file = new File(filename);
			if (!file.exists()) {
				file = null;
				args.clear();
				args = null;
				System.out.println("Filename:" + filename + " can not find!");
				return;
			}
			args.add("-cat");
			args.add(filename);
			file = null;
		}
		
		args.add("-new");
		args.add(outputFilename);
//		for (String string : args) {
//			System.out.println(string);
//		}
		cmd.setListener(new CommandListener() {
			
			private int index = -1;
			private int countOfPart = 3;
			private int partID = 0;
			private float unitProgress;

			@Override
			public void running()
			{
				index = -1;
				unitProgress = 1.0f / (float)(filenames.length + 1);
				if (listener != null)
					listener.start();
			}
			
			@Override
			public void finished(int exitValue)
			{
				if (listener != null)
					listener.finished();
			}

			@Override
			public void outputLine(String value) 
			{
				analyseCommandLine(value);
			}
			
			private void calcProgress(int currentProgressValue)
			{
				float currentProgress = currentProgressValue / 100.0f;
				float partOfUnitProgress = unitProgress / (float)countOfPart;
				float progress = unitProgress * index + partOfUnitProgress * partID + partOfUnitProgress * currentProgress;
//				System.out.println("partID" + partID + "  index" + index + " progress" + progress + "%");
				if (listener != null)
					listener.currentProgress(progress);
			}
			
			private boolean hasIsoMediaInfo = false;
			private void analyseCommandLine(String value)
			{
//				System.out.println(value);
				Matcher m = MP4BoxUtils.pImporting.matcher(value);
				if (m.matches()) {
					calcProgress(Integer.parseInt(m.group(1)));
				}
				
				m = MP4BoxUtils.pAppending.matcher(value);
				if (m.matches()) {
					calcProgress(Integer.parseInt(m.group(1)));
				}
				
				m = MP4BoxUtils.pIsoMediaInfo.matcher(value); 
				if (m.matches()) {
					hasIsoMediaInfo = true;
					partID = Integer.parseInt(m.group(2)) - 1;
					if (partID == 0) {
						countOfPart = 3;
						index++;
					} else if (partID >= (countOfPart - 1))
						countOfPart = partID + 2;
					
					if (!(m.group(1).equals(filenames[index]))) {
						
					}
					return;
				}
				
				m = MP4BoxUtils.pAppendingFile.matcher(value);
				if (m.matches()) {
					if (!hasIsoMediaInfo)
						index++;
					hasIsoMediaInfo = false;
					partID = countOfPart - 1;
					return;
				}
				
				m = MP4BoxUtils.pSavingFile.matcher(value);
				if (m.matches()) {
					index++;
					return;
				}
				
				m = MP4BoxUtils.pISOFileWriting.matcher(value);
				if (m.matches()) {
					countOfPart = 1;
					partID = 0;
					calcProgress(Integer.parseInt(m.group(1)));
					return;
				}
			}

			@Override
			public void errorLine(String value) 
			{
				analyseCommandLine(value);
			}
		});
		cmd.start(args.toArray(new String[1]));
	}

	@Override
	public void stop() 
	{
		if (cmd.isRunning())
			cmd.stop();
	}
}

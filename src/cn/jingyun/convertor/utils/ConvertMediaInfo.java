package cn.jingyun.convertor.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import cn.jingyun.convertor.ffmpeg.FFmpeg;

public class ConvertMediaInfo implements IController
{
	private StringProperty sourceMediaName = new SimpleStringProperty();
	private StringProperty destinationMediaName = new SimpleStringProperty();
	private StringProperty method = new SimpleStringProperty();
	private StringProperty status = new SimpleStringProperty(Status.Ready.toString());
	private String commandLine;
	private Status currentStatus = Status.Ready;
	private IController controller = null;
	private IConvertListener listener = null;
	private DoubleProperty currentProgress = new SimpleDoubleProperty(-1);
	private boolean isStop = false;

	private IStatusListener statusListener = new IStatusListener() {
		@Override
		public void finished()
		{
			if (!isStop) {
				setCurrentStatus(Status.Finished);
				currentProgress.setValue(1);
				setCurrentStatus(Status.Finished);
				currentProgress.setValue(1);
				if (listener != null)
					listener.finished(ConvertMediaInfo.this);
			}
			isStop = false;
		}

		@Override
		public void failed()
		{
			setCurrentStatus(Status.Failed);
			currentProgress.setValue(1);
			if (listener != null)
				listener.failed(ConvertMediaInfo.this);
		}

		@Override
		public void paused()
		{
		}

		@Override
		public void currentProgress(final float progress)
		{
			currentProgress.setValue(progress > 1 ? 1 : progress);
			if (listener != null)
				listener.running(ConvertMediaInfo.this, progress);
		}

		@Override
		public void start()
		{
			isStop = false;
			setCurrentStatus(Status.Running);
			currentProgress.setValue(1);
			if (listener != null)
				listener.start(ConvertMediaInfo.this);
		}
	};

	public StringProperty sourceMediaNameProperty()
	{
		return sourceMediaName;
	}
	
	public String getSourceMediaName()
	{
		return sourceMediaName.getValue(); 
	}

	public void setSourceMediaName(String sourceMediaName)
	{
		currentProgress.setValue(0);
		setCurrentStatus(Status.Ready);
		this.sourceMediaName.setValue(sourceMediaName);
	}

	public ReadOnlyStringProperty destinationMediaNameProperty()
	{
		return destinationMediaName;
	}
	
	public String getDestinationMediaName()
	{
		return destinationMediaName.getValue();
	}

	public void setDestinationMediaName(String destinationMediaName)
	{
		currentProgress.setValue(0);
		setCurrentStatus(Status.Ready);
		this.destinationMediaName.setValue(destinationMediaName);
	}

	public IController getController()
	{
		return controller;
	}

	public void setController(IController controller)
	{
		currentProgress.setValue(0);
		setCurrentStatus(Status.Ready);
		this.controller = controller;
	}

	public IConvertListener getListener() 
	{
		return listener;
	}

	public void setListener(IConvertListener listener) 
	{
		this.listener = listener;
	}

	public String getCommandLine()
	{
		return commandLine;
	}

	public void setCommandLine(String cmdLine)
	{
		commandLine = cmdLine;
		currentProgress.setValue(0);
		setCurrentStatus(Status.Ready);
	}

	public ReadOnlyStringProperty currentStatusProperty()
	{
		return status;
	}
	
	public Status getCurrentStatus()
	{
		return currentStatus;
	}
	
	private void setCurrentStatus(Status status)
	{
		currentStatus = status;
		this.status.setValue(status.toString());
	}

	public ReadOnlyStringProperty methodProperty()
	{
		return method;
	}
	
	public String getMethod()
	{
		return method.getValue();
	}
	
	public void setMethod(String method)
	{
		this.method.setValue(method);
	}

	public ReadOnlyDoubleProperty currentProgressProperty() 
	{
		return currentProgress;
	}
	
	public double getCurrentProgress()
	{
		return currentProgress.getValue();
	}

	@Override
	public void start()
	{
		if (controller != null && controller instanceof FFmpeg) {
			currentStatus = Status.Ready;
			FFmpeg ff = (FFmpeg) controller;
			ff.setSourceMedia(sourceMediaName.getValue());
			ff.setDestinationMedia(destinationMediaName.getValue());
			ff.setListener(statusListener);
			ff.setParameter(commandLine);
			ff.start();
		}
	}

	@Override
	public void stop()
	{
		isStop = true;
		if (controller != null)
			controller.stop();
		if (currentStatus == Status.Running) {
			setCurrentStatus(Status.Failed);
			if (listener != null)
				listener.failed(this);
		}
	}

	public enum Status {
		Ready, Running, Finished, Failed
	}

//	@Override
//	protected Void call() throws Exception
//	{
//		System.out.println("StartCall" + currentStatus);
//		updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
//		updateMessage(currentStatus.toString());
//		while (currentStatus == Status.Ready)
//			Thread.sleep(500);
//		if (currentStatus == Status.Failed)
//			for (int i = 0; i < 20; i++)
//				Thread.sleep(500);
//		while(currentStatus == Status.Running) {
//			updateMessage(currentStatus.toString());
//			updateProgress(currentProgress > 1 ? 1 : currentProgress, 1);
//			Thread.sleep(500);
//		}
//		
//		updateMessage(currentStatus.toString());
//		updateProgress(1, 1);
//		Thread.sleep(500);
		
//		return null;
//	}

}

package cn.jingyun.convertor.ffmpeg;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FFmpegArguments
{
	private StringProperty name = new SimpleStringProperty();
	private StringProperty arguments = new SimpleStringProperty();
	private StringProperty fileExtension = new SimpleStringProperty();
	
	public FFmpegArguments(String name, String arguments, String extension)
	{
		this.name.setValue(name);
		this.arguments.setValue(arguments);
		fileExtension.setValue(extension);
	}

	public ReadOnlyStringProperty nameProperty()
	{
		return name;
	}
	
	public String getName()
	{
		return name.getValue();
	}

	public void setName(String name)
	{
		this.name.setValue(name);
	}
	
	public ReadOnlyStringProperty argumentsProperty()
	{
		return arguments;
	}
	
	public String getArguments()
	{
		return arguments.getValue();
	}

	public void setArguments(String arguments)
	{
		this.arguments.setValue(arguments);
	}
	
	public ReadOnlyStringProperty fileExtensionProperty()
	{
		return fileExtension;
	}
	
	public String getFileExtension()
	{
		return fileExtension.getValue();
	}
	
	public void setFileExtension(String ext)
	{
		fileExtension.setValue(ext);
	}
}

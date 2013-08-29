package cn.jingyun.convertor.utils;

public interface IConvertListener 
{
	void start(ConvertMediaInfo info);
	void running(ConvertMediaInfo info, float progress);
	void failed(ConvertMediaInfo info);
	void finished(ConvertMediaInfo info);
}

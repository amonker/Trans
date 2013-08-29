package cn.jingyun.convertor.utils;

public interface IStatusListener
{
	void finished();
	void failed();
	void paused();
	void currentProgress(float progress);
	void start();
}

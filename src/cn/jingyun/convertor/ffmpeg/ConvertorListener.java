package cn.jingyun.convertor.ffmpeg;

public interface ConvertorListener {
	void running();
	void finished();
	void failed();
	void paused();
}

package cn.jingyun.convertor.mp4box;

import java.io.File;
import java.util.regex.Pattern;

import cn.jingyun.convertor.utils.IStatusListener;
import cn.jingyun.convertor.utils.ParameterDBM;

public class MP4BoxUtils
{
	static String mp4boxPath = "MP4Box";//"C:\\MP4Box.exe";
	//id:1 => current procent id:2 => 100
	static Pattern pISOFileWriting = Pattern.compile("^ISO File Writing:[\\|\\=\\s]+\\((\\d+)/(\\d+)\\)$");//ISO File Writing: |=================== | (99/100)
	static Pattern pSplitting = Pattern.compile("^Splitting:[\\|\\=\\s]+\\((\\d+)/(\\d+)\\)$");//Splitting: |=================   | (86/100)
	
	//id:1 => Filename id:2 => duration id:3 => start id:4 => end
	static Pattern pExtractingChunk = Pattern.compile("^Extracting chunk (\\S+)\\s+\\-\\s+duration\\s+(\\d+\\.\\d+)s\\s+\\((\\d+\\.\\d+)s\\->(\\d+\\.\\d+)s\\)$");//Extracting chunk c:\1_0_290.mp4 - duration 290.00s (0.00s->290.00s)
	
	
	//IsoMedia import 1_0_10.mp4 - track ID 1 - Video (size 480 x 360)
	//Importing ISO File: |==                  | (14/100)
	//IsoMedia import 1_0_10.mp4 - track ID 2 - Audio (SR 24000 - 1 channels)
	//Importing ISO File: |=========           | (46/100)
	//Appending file C:\1_0_10.mp4
	//No suitable destination track found - creating new one (type vide)
	//No suitable destination track found - creating new one (type soun)
	//Appending: |=======             | (37/100)
	//....
	//Saving c:\2.mp4: 0.500 secs Interleaving
	//ISO File Writing: |========            | (42/100)
	
	//id:1 => current procent id:2 => 100
	static Pattern pImporting = Pattern.compile("^Importing ISO File:[\\|\\=\\s]+\\((\\d+)/(\\d+)\\)$");//Importing ISO File: |==                  | (14/100)
	static Pattern pAppending = Pattern.compile("^Appending:[\\|\\=\\s]+\\((\\d+)/(\\d+)\\)$");//Appending: |=======             | (37/100)
	//id:1 => filename
	static Pattern pAppendingFile = Pattern.compile("^Appending file\\s+(\\S+)$");//Appending file C:\1_0_10.mp4
	//id:1 => filename id:2 => time
	static Pattern pSavingFile = Pattern.compile("^Saving\\s+(\\S+)\\s+(\\S+)\\s+secs\\s+Interleaving$");//Saving c:\2.mp4: 0.500 secs Interleaving
	//id:1 => Filename id:2 => trackID id:3 => Audio/Video id:4 => Track parameters
	static Pattern pIsoMediaInfo = Pattern.compile("^IsoMedia import (\\S+)\\s+\\-\\s+track\\s+ID\\s+(\\d+)\\s+\\-\\s+(\\w+)\\s+\\(([^\\)]+)\\)$");//IsoMedia import 1_0_10.mp4 - track ID 2 - Audio (SR 24000 - 1 channels)
	
	static {
		mp4boxPath = ParameterDBM.getInstance().getParameter("MP4BoxPath");
		if (mp4boxPath == null || mp4boxPath.length() != 0)
			mp4boxPath = "MP4Box";
	}
	
	public static void setMP4BoxPath(String path)
	{
		File file = new File(path);
		if (file.exists()) {
			mp4boxPath = path;
			ParameterDBM.getInstance().setParameter("MP4BoxPath", path);
		}
	}
	
	public static String getMP4BoxPath()
	{
		return mp4boxPath;
	}
	
	public static void main(String[] args)
	{
//		split("/tmp/cctv1-2013-04-29-08-001.mp4", 50, 70);
//		split("/tmp/cctv1-2013-04-29-08-001.mp4", 90, 100);
//		List<String> files = new ArrayList<String>();
//		files.add("/tmp/1.mp4");
//		files.add("/tmp/2.mp4");
//		files.add("/tmp/3.mp4");
//		join(files, "/tmp/out.mp4");
		
//		Split.split("c:\\1.mp4", 2, 10);
//		Split.split("c:\\1.mp4", 20, 50);
//		Split.split("c:\\1.mp4", 70, 110);
//		Split.split("c:\\1.mp4", 120, 180);
//		Split.split("c:\\1.mp4", 200, 290);
		
//		Join join = new Join();
//		join.join("c:\\2.mp4", "C:\\1_0_10.mp4", "C:\\1_20_50.mp4", "C:\\1_67_110.mp4", "C:\\1_113_180.mp4", "C:\\1_196_290.mp4");
		
		Split split = new Split();
		split.setListener(new IStatusListener() {
			
			@Override
			public void start()
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void finished()
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void currentProgress(float progress)
			{
				System.out.println(progress);
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
		});
		split.splitMultiRange("/tmp/1.mp4", "/tmp/3.mp4", 2, 10, 20, 50, 70, 110, 120, 180, 200, 290);
	}
}

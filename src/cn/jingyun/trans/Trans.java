package cn.jingyun.trans;

import cn.jingyun.javafx.amonker.ui.AmonkerUIApplication;
import cn.jingyun.javafx.amonker.ui.AmonkerUIPage;

public class Trans extends AmonkerUIApplication
{
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	@Override
	protected void initUI()
	{
		super.initUI();
		setIcon("Trans.png");
		setRootPage(new AmonkerUIPage("Trans", new MainPane(), 750, 500));

	}

//	@Override
//	public StackNode getRootNode()
//	{
//		return new StackNode(new MainPane(), 750, 500, Color.GHOSTWHITE);//.web("#666970"));
//	}
//	
//	@Override
//	public void willShowStage()
//	{
//		super.willShowStage();
//		setTitle("Trans");
//	}
}

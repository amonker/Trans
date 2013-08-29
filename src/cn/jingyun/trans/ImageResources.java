package cn.jingyun.trans;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageResources
{
	public enum Type { Open, AddMovie, Start, Stop, Clear, Delete, Modified, AdvModified, Apply, Add };
	
	public static ImageView getImage(Type type)
	{
		ImageView view = null;
		switch (type) {
		case Open:
			view = new ImageView(new Image("/resources/buttonImage/open.png"));
			break;
		case AddMovie:
			view = new ImageView(new Image("/resources/buttonImage/AddMovie.png"));
			break;
		case Start:
			view = new ImageView(new Image("/resources/buttonImage/play.png"));
			break;
		case Stop:
			view = new ImageView(new Image("/resources/buttonImage/stop.png"));
			break;
		case Clear:
			view = new ImageView(new Image("/resources/buttonImage/clear.png"));
			break;
		case Delete:
			view = new ImageView(new Image("/resources/buttonImage/delete.png"));
			break;
		case Modified:
			view = new ImageView(new Image("/resources/buttonImage/Modified.png"));
			break;
		case AdvModified:
			view = new ImageView(new Image("/resources/buttonImage/AdvModified.png"));
			break;
		case Apply:
			view = new ImageView(new Image("/resources/buttonImage/Apply.png"));
			break;
		case Add:
			view = new ImageView(new Image("/resources/buttonImage/Add.png"));
			break;
		}
		return view;
	}
}

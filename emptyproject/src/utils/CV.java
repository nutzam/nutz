package utils;

import org.nutz.mvc.View;
import org.nutz.mvc.view.ServerRedirectView;
import org.nutz.mvc.view.ViewWrapper;

public class CV {
	
	public static View redirect(String url,Object data){
		return new ViewWrapper(new ServerRedirectView(url), data);
	}
}

package crawling;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Version;
import com.restfb.types.Page;

public class Facebook {

	private static final String FB_APP_ID = "1124036170943933";
	private static final String FB_APP_SECRET = "cb3b0fa5c4cc2ffb59b3267ec2cc7c2b";
	
	private static AccessToken accessToken;
	private static Version version;
	
	public static void experiment() {
		
		@SuppressWarnings("deprecation")
		FacebookClient client = new DefaultFacebookClient();
		accessToken = client.obtainAppAccessToken(FB_APP_ID, FB_APP_SECRET);
		version = Version.VERSION_2_3;
		client = new DefaultFacebookClient(accessToken.getAccessToken(), version);
		
		Page page = client.fetchObject("139062546167362", Page.class);
		
		System.out.println("page : " + page.getAbout());
	}
}

package datatransfer;

import java.io.File;

import persistence.SiteCrawl;
import utilities.DSFormatter;

public class SiteCrawlImporter {

	public static SiteCrawl importSiteCrawl(File storageFolder) {
		System.out.println("Importing SiteCrawl from storageFolder : " + storageFolder);
		String seed = DSFormatter.decode(storageFolder.getName());
		System.out.println("decoded seed : " + seed);
		SiteCrawl siteCrawl = new SiteCrawl(seed);
		
		return null;
	}
}

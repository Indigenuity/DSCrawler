package datatransfer;

import global.Global;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import persistence.SiteCrawl;

public class FileMover {
	
	public static final double BYTES_PER_GB = 1073741824.0;
	public static final double STORAGE_BUFFER = BYTES_PER_GB;
	
	public static boolean sufficientSpace(File source, File destination) {
		if(!destination.exists() || source.exists()){
			return false;
		}
		if(getUsableBytes(destination) - getUsableBytes(source) > STORAGE_BUFFER) {
			return true;
		}
		return false;
	}
	
	public static Long getMainUsableBytes() {
		return getUsableBytes(new File(Global.getStorageFolder()));
	}
	
	public static Long getSecondaryUsableBytes() {
		return getUsableBytes(new File(Global.getSecondaryStorageFolder()));
	}
	
	public static Long getUsableBytes(File file) {
		if(!file.exists())
			return new Long(0);
		return file.getUsableSpace();
	}
	
	public static String getMainUsableGb() {
		return getUsableGb(new File(Global.getStorageFolder()));
	}
	
	public static String getSecondaryUsableGb() {
		return getUsableGb(new File(Global.getSecondaryStorageFolder()));
	}
	
	public static String getUsableGb(File file) {
		if(!file.exists())
			return "0";
		long usable = file.getUsableSpace();
		double usableGb = usable / BYTES_PER_GB;
		return String.format("%.2f", usableGb);
	}
	
	public static void crawlToLocal(SiteCrawl siteCrawl) throws IOException {
		if(crawlIsOnLocal(siteCrawl)){
			return;
		}
		File source = new File(Global.getSecondaryCrawlStorageFolder() + siteCrawl.getStorageFolder());
		File destination = new File(Global.getCrawlStorageFolder() + siteCrawl.getStorageFolder());
		moveDirectory(source, destination);
		
	}
	
	public static void crawlToSecondary(SiteCrawl siteCrawl) throws IOException {
		if(crawlIsOnSecondary(siteCrawl)){
			return;
		}
		File source = new File(Global.getCrawlStorageFolder() + siteCrawl.getStorageFolder());
		File destination = new File(Global.getSecondaryCrawlStorageFolder() + siteCrawl.getStorageFolder());
		moveDirectory(source, destination);
	}
	
	public static boolean crawlIsOnLocal(SiteCrawl siteCrawl) {
		File localFolder = new File(Global.getCrawlStorageFolder() + siteCrawl.getStorageFolder());
		return localFolder.exists() && localFolder.isDirectory();
	}
	
	public static boolean crawlIsOnSecondary(SiteCrawl siteCrawl) {
		File secondaryFolder = new File(Global.getCrawlStorageFolder() + siteCrawl.getStorageFolder());
		return secondaryFolder.exists() && secondaryFolder.isDirectory();
	}
	
	public static void combinedToLocal(SiteCrawl siteCrawl) throws IOException {
		if(combinedIsOnLocal(siteCrawl)){
			return;
		}
		File source = new File(Global.getSecondaryCombinedStorageFolder() + siteCrawl.getStorageFolder());
		File destination = new File(Global.getCombinedStorageFolder() + siteCrawl.getStorageFolder());
		moveDirectory(source, destination);
	}
	
	public static void combinedToSecondary(SiteCrawl siteCrawl) throws IOException {
		if(combinedIsOnSecondary(siteCrawl)){
			return;
		}
		File source = new File(Global.getCombinedStorageFolder() + siteCrawl.getStorageFolder());
		File destination = new File(Global.getSecondaryCombinedStorageFolder() + siteCrawl.getStorageFolder());
		moveDirectory(source, destination);
	}
	
	public static boolean combinedIsOnLocal(SiteCrawl siteCrawl) {
		File localFolder = new File(Global.getCombinedStorageFolder() + siteCrawl.getStorageFolder());
		return localFolder.exists() && localFolder.isDirectory();
	}
	
	public static boolean combinedIsOnSecondary(SiteCrawl siteCrawl) {
		File secondaryFolder = new File(Global.getSecondaryCombinedStorageFolder() + siteCrawl.getStorageFolder());
		return secondaryFolder.exists() && secondaryFolder.isDirectory();
	}
	
	public static void allToLocal(SiteCrawl siteCrawl) throws IOException {
		crawlToLocal(siteCrawl);
		combinedToLocal(siteCrawl);
	}
	
	public static void allToSecondary(SiteCrawl siteCrawl) throws IOException {
		crawlToSecondary(siteCrawl);
		combinedToSecondary(siteCrawl);
	}
	
	public static void moveDirectory(File source, File destination) throws IOException{
		if(!source.exists()){
			return;
		}
		if(sufficientSpace(source, destination)){
			FileUtils.moveDirectory(source, destination);
		}
		else{
			throw new IllegalStateException("Can't move directory; not enough storage space");
		}
	}
	
}

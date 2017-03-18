package crawling.discovery.html;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.io.Files;

import crawling.HttpFetcher;
import crawling.anansi.UriFetch;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.SourcePool;
import crawling.discovery.execution.CrawlContext;
import persistence.PageCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.db.jpa.JPA;
import utilities.DSFormatter;

public class PageCrawlPlan {

//	
//	public PageCrawlPlan(CrawlContext crawlContext, HttpConfig config) {
//		super(crawlContext, config);
//	}
//
//	public PageCrawlPlan(CrawlContext crawlContext, SourcePool sourcePool, HttpConfig config) {
//		super(crawlContext, sourcePool, config);
//	}
//
//	public PageCrawlPlan(String name, CrawlContext crawlContext, SourcePool sourcePool, HttpConfig config) {
//		super(name, crawlContext, sourcePool, config);
//	}
//
//	@Override
//	public Set<Resource> fetchResource(Object source) throws Exception {
//		Set<Resource> resources = super.fetchResource(source);
//		Set<Resource> newResources = new HashSet<Resource>();
//		for(Resource resource : resources){
//			PageCrawl pageCrawl = toPageCrawl((UriFetch)resource.getValue());
//			Long siteCrawlId = (Long) crawlContext.getContextObject("siteCrawlId");
//			persistPageCrawl(pageCrawl, siteCrawlId);
//			newResources.add(new PageCrawlResource(pageCrawl.getPageCrawlId(), pageCrawl));
//		}
//		return newResources;
//	}
//	
//	protected PageCrawl toPageCrawl(UriFetch pageFetch) throws IOException{
//		PageCrawl pageCrawl = new PageCrawl();
//		pageCrawl.setUrl(pageFetch.getUri().toString());
//		pageCrawl.setStatusCode(pageFetch.getStatusCode());
//		pageCrawl.setFilename(storePageFetch(pageFetch).getAbsolutePath());
//		return pageCrawl;
//	}
//	
//	protected void persistPageCrawl(PageCrawl pageCrawl, Long siteCrawlId){
//		JPA.withTransaction(() -> {
//			SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
//			pageCrawl.setSiteCrawl(siteCrawl);
//			JPA.em().persist(pageCrawl);
//		});
//	}
//	
//	protected File storePageFetch(UriFetch pageFetch) throws IOException{
//		File storageFolder = new File((String)crawlContext.getContextObject("storageFolderName"));
//        if (!storageFolder.exists()) {
//          storageFolder.mkdirs();
//        }
//        
//        System.out.println("creating file for URI : " + pageFetch.getUri());
//        String path = pageFetch.getUri().getPath();
//        if(StringUtils.isEmpty(path)){
//        	path = "/";
//        }
//        if(!StringUtils.isEmpty(pageFetch.getUri().getQuery())){
//        	path += "?" + pageFetch.getUri().getQuery();
//        }
//        File file = new File(storageFolder + "/" + DSFormatter.makeSafeFilePath(path));
//        
//        Files.write(pageFetch.getResultText().getBytes(), file);
//		return file;
//	}

}

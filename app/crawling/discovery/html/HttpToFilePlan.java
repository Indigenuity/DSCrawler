package crawling.discovery.html;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;

import crawling.HttpFetcher;
import crawling.anansi.UriFetch;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.SourcePool;
import crawling.discovery.entities.SourceQualification;
import crawling.discovery.entities.SourceQualification.QualificationStatus;
import crawling.discovery.execution.CrawlContext;
import crawling.discovery.execution.ResourceContext;
import crawling.discovery.execution.ResourceSupervisor;
import crawling.discovery.execution.ResourceWorkOrder;
import crawling.discovery.execution.ResourceWorkResult;
import crawling.discovery.planning.ResourceFetchTool;
import crawling.discovery.planning.ResourcePlan;
import global.Global;

public class HttpToFilePlan extends ResourcePlan{

	private HttpConfig config;
	
	public HttpToFilePlan(HttpConfig config) {
		super();
		this.config = config;
		this.setFetchTool(new HttpToFileTool());
	}
	
	@Override
	public HttpToFileTool getFetchTool() {
		return (HttpToFileTool)super.getFetchTool();
	}

	@Override
	public void setFetchTool(ResourceFetchTool fetchTool) {
		if(!(fetchTool instanceof HttpToFileTool)){
			throw new IllegalArgumentException("Cannot create HttpResponseFilePlan with a fetch tool that doesn't extends HttpToFileTool");
		}
		super.setFetchTool(fetchTool);
	}

	public boolean matchesSeedHost(URI uri, CrawlContext crawlContext) {
		return StringUtils.equals(uri.getHost(), (String) crawlContext.getContextObject("seedHost"));
	}

	@Override
	public synchronized ResourceContext generateContext(CrawlContext crawlContext) {
		ResourceContext context = super.generateContext(crawlContext);
		context.putContextObject("httpConfig", config);
//		System.out.println("config : " + config.getProxyAddress());
		context.putContextObject("httpClient", config.buildHttpClient());
		return context;
	}
	
	

}

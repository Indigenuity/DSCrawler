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
import crawling.discovery.planning.FetchTool;
import crawling.discovery.planning.ResourcePlan;
import crawling.discovery.planning.ResourceTool;
import global.Global;

public class HttpToFilePlan extends ResourcePlan{

	private HttpConfig config;
	
	public HttpToFilePlan(HttpConfig config) {
		super();
		this.config = config;
		this.setResourceTool(new HttpToFileTool());
	}
	
	@Override
	public HttpToFileTool getResourceTool() {
		return (HttpToFileTool)super.getResourceTool();
	}

	@Override
	public void setResourceTool(ResourceTool resourceTool) {
		if(!(resourceTool instanceof HttpToFileTool)){
			throw new IllegalArgumentException("Cannot create HttpResponseFilePlan with a resource tool that doesn't extend HttpToFileTool");
		}
		super.setResourceTool(resourceTool);
	}

	public boolean matchesSeedHost(URI uri, CrawlContext crawlContext) {
		return StringUtils.equals(uri.getHost(), (String) crawlContext.get("seedHost"));
	}

	@Override
	public synchronized ResourceContext generateContext(CrawlContext crawlContext) throws Exception {
		ResourceContext context = super.generateContext(crawlContext);
		context.put("httpConfig", config);
//		System.out.println("config : " + config.getProxyAddress());
		context.put("httpClient", config.buildHttpClient());
		return context;
	}
	
	

}

package forms;

import java.util.List;

import play.data.validation.*;
import async.work.WorkType;

public class CrawlSetJob {
	
	@Constraints.Required
	public long crawlSetId;
	
	public WorkType workType;
	
	public int numToProcess;
	
	public int offset;
	
	public List<Integer> numbers;

}

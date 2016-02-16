package forms;

import java.util.List;

import async.work.WorkType;
import play.data.validation.*;

public class CrawlSetJob {
	
	@Constraints.Required
	public long crawlSetId;
	
	public WorkType workType;
	
	public int numToProcess;
	
	public int offset;
	
	public List<Integer> numbers;

}

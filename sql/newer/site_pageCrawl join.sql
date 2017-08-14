SELECT count(*), s.* FROM ds_new.site s
join site_sitecrawl ssc on ssc.Site_siteId = s.siteid
join sitecrawl sc on ssc.crawls_siteCrawlId = sc.sitecrawlid
join sitecrawl_pagecrawl scpc on sc.sitecrawlid = scpc.SiteCrawl_siteCrawlId
join pagecrawl pc on pc.pagecrawlid = scpc.pageCrawls_pageCrawlId
group by sc.sitecrawlid
order by count(*) asc
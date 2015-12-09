select s.homepage, s.siteId, count(*) as count, sum(numRetrievedFiles) as sum, min(numRetrievedFiles) as min from site s
join site_sitecrawl ssc on s.siteId = ssc.Site_siteId
join siteCrawl sc on ssc.crawls_siteCrawlId = sc.siteCrawlId
group by s.siteId
having min = 0

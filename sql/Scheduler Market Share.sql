select s.description, count(*) as numSites from sitecrawl sc
join sitecrawl_schedulers ss on sc.siteCrawlId = ss.SiteCrawl_siteCrawlId
join scheduler s on s.schedulerId = ss.schedulers
group by s.description
order by numSites desc
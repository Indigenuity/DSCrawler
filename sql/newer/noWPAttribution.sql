select * from sitecrawl scouter 
where scouter.siteCrawlId not in (
select sc.sitecrawlid from sitecrawl sc
join sitecrawl_wpattributions scwp on sc.siteCrawlId = scwp.sitecrawl_sitecrawlid)

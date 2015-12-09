select siteCrawlId, crawlDate, seed, count(wp.label) as numWp, wp.label from sitecrawl sc
join sitecrawl_webproviders scwp on sc.siteCrawlId = scwp.SiteCrawl_siteCrawlId
join webprovider wp on scwp.webproviders = wp.webproviderid
group by wp.label

order by numWp desc
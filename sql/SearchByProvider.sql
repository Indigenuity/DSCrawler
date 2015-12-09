select siteCrawlId, inferredWebProvider, crawlDate, seed, count(wp.label) as numWp, group_concat(distinct wp.label order by wp.label desc) as allWp from sitecrawl sc
join sitecrawl_webproviders scwp on sc.siteCrawlId = scwp.SiteCrawl_siteCrawlId
join webprovider wp on scwp.webproviders = wp.webproviderid
group by siteCrawlId
having allwp like '%FOX_DEALER_INTERACTIVE%'

order by numWp asc
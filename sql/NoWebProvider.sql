select * from siteCrawl
where siteCrawlId not in
(select siteCrawlId from sitecrawl sc
join sitecrawl_webproviders scwp on sc.siteCrawlId = scwp.SiteCrawl_siteCrawlId
join webprovider wp on scwp.webproviders = wp.webproviderid
group by sitecrawlId)
and seed not like '%maserati%'
and seed not like '%bmw%'
and seed not like '%ferrari%'
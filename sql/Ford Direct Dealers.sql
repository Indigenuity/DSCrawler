select * from dealer d
where d.DEALER_MAIN_SITE_ID in 
(select siteId from site s
join site_siteCrawl ssc on s.siteId = ssc.site_siteId
join sitecrawl sc on sc.sitecrawlid = ssc.crawls_sitecrawlid
join sitecrawl_webproviders swp on sc.sitecrawlid = swp.sitecrawl_sitecrawlid
join webprovider wp on swp.webproviders = wp.webproviderid
group by sc.sitecrawlid
having group_concat(wp.name) like '%FORD_DIRECT%')
create view WebProviderList as

select siteCrawlId, group_concat(webProviders) from sitecrawl sc
join sitecrawl_webproviders wp on sc.siteCrawlId = wp.SiteCrawl_siteCrawlId
group by siteCrawlId
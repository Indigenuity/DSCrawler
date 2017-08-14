SELECT * FROM ds_new.site s
join site_sitecrawl ssc on ssc.Site_siteId = s.siteid
join sitecrawl sc on ssc.crawls_siteCrawlId = sc.sitecrawlid
select * from SiteCrawl s 
join sitecrawl_crawledurls cu on s.sitecrawlid = cu.sitecrawl_sitecrawlid
left join sitecrawl_extractedstring es on s.sitecrawlid = es.sitecrawl_sitecrawlid
left join sitecrawl_externallinks el on s.sitecrawlid = el.sitecrawl_sitecrawlid

where s.numRetrievedFiles < 8
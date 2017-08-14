create view latestCrawl as

SELECT s.siteid, homepage, max(crawldate) as latestCrawl, numpages FROM ds_new.site s
left join site_sitecrawl ssc on s.siteid = ssc.site_siteid
left join (select sitecrawlid, crawldate, count(*) as numpages from sitecrawl sc
			join sitecrawl_uniquecrawledpageurls ucp on sc.sitecrawlid = ucp.sitecrawl_sitecrawlid
			group by sc.sitecrawlid) scOuter on scOuter.sitecrawlid = ssc.crawls_sitecrawlid

where (s.sitestatus = 'APPROVED' or s.sitestatus = 'needs_review')
group by s.siteid
order by latestCrawl asc;
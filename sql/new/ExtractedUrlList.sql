create view ExtractedUrlList as

select siteCrawlId, group_concat(value) from sitecrawl sc
join sitecrawl_extractedurl ex on sc.siteCrawlId = ex.SiteCrawl_siteCrawlId
join extractedurl es on ex.extractedUrls_extractedUrlId = es.extractedUrlId
group by siteCrawlId
create view ExtractedStringList as

select siteCrawlId, group_concat(value) from sitecrawl sc
join sitecrawl_extractedstring ex on sc.siteCrawlId = ex.SiteCrawl_siteCrawlId
join extractedstring es on ex.extractedStrings_extractedStringId = es.extractedStringId
group by siteCrawlId
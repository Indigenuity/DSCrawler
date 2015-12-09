create view GeneralMatchesList as

select siteCrawlId, group_concat(generalMatches) from sitecrawl sc
join sitecrawl_generalmatches gen on sc.siteCrawlId = gen.SiteCrawl_siteCrawlId
group by siteCrawlId
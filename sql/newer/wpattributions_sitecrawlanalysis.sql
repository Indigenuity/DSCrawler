SELECT sfa.salesforceid, sfa.name, seed, group_concat(wpattributions, ','), group_concat(testmatches, ',') from sitecrawl sc
join sitecrawlanalysis sca on sca.sitecrawl_sitecrawlid = sc.sitecrawlid
left join sitecrawlanalysis_wpattributions scawp on scawp.SiteCrawlAnalysis_siteCrawlAnalysisId = sca.sitecrawlanalysisid
left join sitecrawlanalysis_testmatches scatm on scatm.SiteCrawlAnalysis_siteCrawlAnalysisId = sca.sitecrawlanalysisid
join site_sitecrawl ssc on ssc.crawls_sitecrawlid = sc.sitecrawlid
join site s on s.siteid = ssc.site_siteid
join salesforceaccount sfa on sfa.site_siteid = s.siteid
where sc.crawldate > '2016-10-01'
group by sc.sitecrawlid
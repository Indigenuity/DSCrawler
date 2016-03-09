select seed, numretrievedfiles, sitecrawlid from sitecrawl sc
join sitecrawl_wpattributions scwp on sc.siteCrawlId = scwp.sitecrawl_sitecrawlid
where wpattributions = 'DEALER_COM2'
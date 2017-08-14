SELECT concat('https://dealersocket.my.salesforce.com/', salesforceid) as 'Salesforce URL', 
	sa.name as 'Account Name', 
    seed, 
    invTypes, 
    numgoodcrawls, 
    numCrawls, 
    combinedrootinventorycount, 
    highestinventorycount, 
    newrootinventorycount, 
    usedrootinventorycount, 
    numvins, 
    numvehicles  
FROM salesforceaccount sa 
join site s on sa.site_siteId = s.siteid
join sitecrawl sc on sc.sitecrawlid = s.lastcrawl_siteCrawlId
join sitecrawlanalysis sca on sca.sitecrawl_siteCrawlId = sc.sitecrawlid
where sa.lastUpdated > '2017-7-4'
and sc.crawlDate > '2017-6-1'
order by numvehicles desc

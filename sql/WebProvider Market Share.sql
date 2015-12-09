SELECT siteCrawlId, seed, description, count(*) as numsites FROM ds.siteCrawl sc
join webprovider wp on sc.inferredWebProvider = wp.webProviderid
where sc.numRetrievedFiles >= 8
group by description
order by numsites desc

create view SchedulersList as

select siteCrawlId, group_concat(schedulers) from sitecrawl sc
join sitecrawl_schedulers sched on sc.siteCrawlId = sched.SiteCrawl_siteCrawlId
group by siteCrawlId
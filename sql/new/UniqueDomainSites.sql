create view UniqueDomainSites as

select s.siteId from site s
join dealer on s.siteId = dealer.DEALER_MAIN_SITE_ID
where dealer.datasource = 'salesforce'
and homepageNeedsReview = false
and reviewLater = false
and maybeDefunct = false
and defunct = false
group by homepage
having count(*) < 2


select * from site where homepageNeedsReview
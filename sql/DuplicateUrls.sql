select s.domain, c, dealerId, dealer.dealerName, capdb, s.homepage from site s
join (SELECT domain, COUNT(*) c FROM site 
where homepageNeedsReview = false
and reviewLater = false
and maybeDefunct = false
and defunct = false
GROUP BY domain 
HAVING c < 2 
order by c desc) dup on s.domain = dup.domain
join dealer on s.siteId = dealer.DEALER_MAIN_SITE_ID
group by domain
order by c desc
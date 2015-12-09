update dealer 
set sharesDomain = true 
where dealerId in (select dealerId from (select dealerId from places_dealers pd
join site s on pd.DEALER_MAIN_SITE_ID = s.siteId
where exists(select * from duplicate_domains dd
where dd.domain = s.domain)) temp)

	select  count(*), site_id, p.PAGE_ID, max(DEALER_COM), max(XTIME_SCHEDULER), max(CONTACTATONCE), max(FACEBOOK), max(EMAIL_ADDRESS) from 
	pageinformation p left join WebProviderMatches on p.PAGE_ID = WebProviderMatches.PAGE_ID
	left join SchedulerMatches on p.PAGE_ID = SchedulerMatches.PAGE_ID
	left join GeneralMatches on p.PAGE_ID = GeneralMatches.PAGE_ID
	left join UrlExtractions on p.PAGE_ID = UrlExtractions.PAGE_ID
	left join StringExtractions on p.PAGE_ID = StringExtractions.PAGE_ID
	group by SITE_ID


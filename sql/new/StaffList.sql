create view StaffList as

SELECT siteCrawlId, fn, name, title, email, cell, phone, other
from siteCrawl sc
join sitecrawl_staff ss on sc.siteCrawlId = ss.SiteCrawl_siteCrawlId
join staff s on ss.allStaff_staffId = s.staffId
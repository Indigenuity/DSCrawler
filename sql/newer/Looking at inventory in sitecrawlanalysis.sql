SELECT sc.sitecrawlid,
	sc.seed, 
	sca.invtypes, 
    sca.numcrawls, 
    sca.numgoodcrawls, 
    sca.combinedrootinventorycount, 
    sca.newrootinventorycount, 
    sca.usedrootinventorycount, 
    sca.numvins, 
    sca.numvehicles,
    sc.storagefolder,
    newpc.filename as 'New Root Filename',
    usedpc.filename as 'Used Root Filename'
FROM sitecrawl sc
join sitecrawlanalysis sca on sc.siteCrawlId = sca.sitecrawl_siteCrawlId
left join pagecrawl newpc on sc.newinventoryroot_pageCrawlId = newpc.pagecrawlid
left join pagecrawl usedpc on sc.usedinventoryroot_pageCrawlId = usedpc.pagecrawlid


SELECT pagecrawlanalysisid, url FROM ds_new.pagecrawlanalysis_generalmatches pcagm
join pagecrawlanalysis pca on pcagm.pagecrawlanalysis_pagecrawlanalysisid = pca.pagecrawlanalysisid
join pagecrawl pc on pca.pagecrawl_pagecrawlid = pc.pagecrawlid
where generalmatches = 'DRUPAL'
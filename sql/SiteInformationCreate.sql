
create table SiteInformation (
SITE_ID int AUTO_INCREMENT primary key,
SITE_NAME varchar(255),
SITE_URL varchar(300) not null,
NIADA_ID varchar(15),
CAPDB_ID varchar(15),
GIVEN_ADDRESS varchar(100),
GIVEN_URL varchar(300),
INTERMEDIATE_URL varchar (300),
CRAWL_FROM_GIVEN_URL boolean,
CRAWL_DATE datetime
)
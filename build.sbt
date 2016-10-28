name := """DSCrawler"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq( 
  	javaJdbc,
  	javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api"), 
  	"org.hibernate" % "hibernate-entitymanager" % "4.3.0.Final",
  	"org.hibernate" % "hibernate-envers" % "4.3.0.Final",
  	javaEbean,
  	cache,
  	javaWs,
  	"com.google.guava" % "guava" % "18.0",
  	"edu.uci.ics" % "crawler4j" % "4.1",
  	"org.jsoup" % "jsoup" % "1.8.2",
  	"commons-io" % "commons-io" % "2.4",
  	"com.google.code.gson" % "gson" % "2.3.1",
  	"org.apache.commons" % "commons-csv" % "1.1",
  	"mysql" % "mysql-connector-java" % "5.1.35",
  	"net.sf.sprockets" % "sprockets" % "3.1.0",
  	"com.restfb" % "restfb" % "1.11.0",
  	"org.seleniumhq.selenium" % "selenium-chrome-driver" % "2.48.2",
  	"org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.48.2",
  	"commons-beanutils" % "commons-beanutils" % "1.9.2",
	"com.google.maps" % "google-maps-services" % "0.1.10"
)

fork in run := true
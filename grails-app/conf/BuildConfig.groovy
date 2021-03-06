grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolver="maven"
grails.project.dependency.resolution = {
    inherits("global") {}
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        mavenCentral()
        mavenRepo "http://repo.grails.org/grails/core"
    }

    dependencies {
        compile 'net.sf.opencsv:opencsv:2.3'

        compile ("com.lowagie:itext:2.1.7") { 		// Birt 4.3 issue
				  excludes 'bcprov-jdk14'
				  excludes 'bcmail-jdk14'
				}
        compile ('com.lowagie:itext-rtf:2.1.7') { 	// Birt 4.3 issue
				  excludes 'bcprov-jdk14'
				  excludes 'bcmail-jdk14'
				}
        runtime 'xerces:xercesImpl:2.9.1'
        compile 'org.odftoolkit:simple-odf:0.6.6'
        compile 'net.sourceforge.jexcelapi:jxl:2.6.12'
        compile 'commons-beanutils:commons-beanutils:1.8.3'
    }

    plugins {

    }
}

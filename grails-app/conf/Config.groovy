/*********************************************************************************
 Copyright 2009-2011 SunGard Higher Education. All Rights Reserved.
 This copyrighted software contains confidential and proprietary information of 
 SunGard Higher Education and its subsidiaries. Any use of this software is limited 
 solely to SunGard Higher Education licensees, and is further subject to the terms 
 and conditions of one or more written license agreements between SunGard Higher 
 Education and the licensee in question. SunGard is either a registered trademark or
 trademark of SunGard Data Systems in the U.S.A. and/or other regions and/or countries.
 Banner and Luminis are either registered trademarks or trademarks of SunGard Higher 
 Education in the U.S.A. and/or other regions and/or countries.
 **********************************************************************************/


// Note: Since there is a separate test app (see test-banner-core under tests), this Config.groovy is 
// only used for tailoring the documentation generated via 'grails Doc'. 

grails.doc.authors = '''Prepared by: SunGard Higher Education
                        |4 Country View Road Malvern, Pennsylvania 19355 United States of America (800) 522 - 4827'''.stripMargin()
                        
grails.doc.footer = '''Contains confidential and proprietary information of SunGard and its subsidiaries.'''

grails.doc.license = '''Use of these materials is limited to SunGard Higher Education licensees, and is subject to the terms and conditions of one or more written license agreements between SunGard Higher Education and the licensee in question.
                        |In preparing and providing this publication, SunGard Higher Education is not rendering legal, accounting, or other similar professional services. SunGard Higher Education makes no claims that an institution's use of this publication or the software for which it is provided will insure compliance with applicable federal or state laws, rules, or regulations. Each organization should seek legal, accounting and other similar professional services from competent providers of the organization’s own choosing.
                        |'''.stripMargin()

grails.doc.copyright = '''© 2010-2011 SunGard. All rights reserved.
                          |Use of these materials is limited to SunGard Higher Education licensees, and is subject to the terms and conditions of one or more written license agreements between SunGard Higher Education and the licensee in question.
                          |In preparing and providing this publication, SunGard Higher Education is not rendering legal, accounting, or other similar professional services. SunGard Higher Education makes no claims that an institution's use of this publication or the software for which it is provided will insure compliance with applicable federal or state laws, rules, or regulations. Each organization should seek legal, accounting and other similar professional services from competent providers of the organization’s own choosing.
                          |'''.stripMargin()

grails.doc.images = new File( "src/docs/images" )
grails.doc.logo = '''<img src="../img/main-banner-image.png">'''
grails.doc.sponsorLogo = '''<img src="../img/sghe_global.logo.jpg">'''

grails.doc.alias.release_notes = "1. Release Notes"
grails.doc.alias.user          = "2. User Guide"
grails.doc.alias.overview      = "2.1 Architecture Overview"
grails.doc.alias.security      = "2.2 Application Security"
grails.doc.alias.services      = "2.3 Services"
grails.doc.alias.rest          = "2.4 REST"
grails.doc.alias.dev           = "3. Developer Guide"


// CodeNarc rulesets
codenarc.ruleSetFiles="rulesets/banner.groovy"
codenarc.reportName="target/CodeNarcReport.html"
codenarc.propertiesFile="grails-app/conf/codenarc.properties"


// Code Coverage configuration
coverage {
	enabledByDefault = false
}

grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"

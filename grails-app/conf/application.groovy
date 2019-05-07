/*******************************************************************************
 Copyright 2009-2012 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
// configuration for plugin testing - will not be included in the plugin zip


grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"

dataSource {
//    pooled = true
//    driverClassName = "oracle.jdbc.OracleDriver"
//    dialect = "org.hibernate.dialect.Oracle10gDialect"
//    username = "banproxy"
//    password = "u_pick_it"
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
    development {
        dataSource {
        }
    }
    test {
        dataSource {
        }
    }
    production {
        dataSource {
        }
    }
}

/*******************************************************************************
 Copyright 2009-2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package i18n.core

class I18nUrlMappings {

	static mappings = {

        "/dateConverter/$date/$fromULocale?/$toULocale?/$fromDateFormat?/$toDateFormat?/$adjustDays?"(controller: "dateConverter")

		//TM interface
		"/admin/i18n/$id"(controller:'resourceBundle') {
			action = [GET: "show", PUT: "save",
					  DELETE: "delete"]
			parseRequest = false
			constraints {
				// to constrain the id to numeric, uncomment the following:
				// id matches: /\d+/
			}
		}
		"/admin/i18n/"(controller:'resourceBundle') {
			action = [GET: "list", POST: "save"]
			parseRequest = false
		}
	}
}

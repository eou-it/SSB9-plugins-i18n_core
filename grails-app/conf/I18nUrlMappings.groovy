class I18nUrlMappings {

	static mappings = {

        "/dateConverter/$date/$fromULocale?/$toULocale?/$fromDateFormat?/$toDateFormat?/$adjustDays?"(controller: "dateConverter")

        "/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}

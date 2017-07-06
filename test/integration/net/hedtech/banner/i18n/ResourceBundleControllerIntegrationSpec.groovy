package net.hedtech.banner.i18n

import grails.test.spock.IntegrationSpec

class ResourceBundleControllerIntegrationSpec extends IntegrationSpec {

    def resourcBundle
    public void setup() {
        resourcBundle = new ResourceBundleController()
    }


    void cleanup() {
    }


    void "Test for fetching list of values"(){
        when:
        resourcBundle.list();
        then:
        resourcBundle.response.text !=null
    }

    void "Test for fetching values based on id and type"(){
        given:
        resourcBundle.params.id =40
        resourcBundle.params.name="PLUGINS/CSV/MESSAGES"
        resourcBundle.params.locale= "en_US"
        when:
        resourcBundle.show();
        then:
        resourcBundle.response.text !=null
    }

    void "Test for saving values"() {
        given:
        def data = ['id': '40', 'name': 'PLUGINS/CSV/MESSAGES', 'locale': 'en_US']
        resourcBundle.request.JSON = data
        when:
        resourcBundle.save();
        then:
        resourcBundle.response.text != null
    }
}

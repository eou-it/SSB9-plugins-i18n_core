/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.i18n

import org.junit.Before
import org.junit.Test

class ResourceBundleControllerIntegrationTests {

    def resourceBundleController

    @Before
    public void setUp() {
        resourceBundleController = new ResourceBundleController()
    }

    @Test
    void testListOfValues(){
        resourceBundleController.list()
        assert 200,resourceBundleController.response.status
    }

    @Test
    void testFetchValuesWithIdAndType(){
        resourceBundleController.params.id =40
        resourceBundleController.params.name="PLUGINS/CSV/MESSAGES"
        resourceBundleController.params.locale= "en_US"
        resourceBundleController.show()
        assert 200,resourceBundleController.response.status
    }

    @Test
    void testSavingValues(){
        def data = ['id': '40', 'name': 'PLUGINS/CSV/MESSAGES', 'locale': 'en_US']
        resourceBundleController.request.JSON = data
        resourceBundleController.save()
        assert 200,resourceBundleController.response.status
    }

//
//
//
//    void "Test for fetching list of values"(){
//        when:
//        resourcBundle.list();
//        then:
//        resourcBundle.response.text !=null
//    }
//
//    void "Test for fetching values based on id and type"(){
//        given:
//        resourcBundle.params.id =40
//        resourcBundle.params.name="PLUGINS/CSV/MESSAGES"
//        resourcBundle.params.locale= "en_US"
//        when:
//        resourcBundle.show();
//        then:
//        resourcBundle.response.text !=null
//    }
//
//    void "Test for saving values"() {
//        given:
//        def data = ['id': '40', 'name': 'PLUGINS/CSV/MESSAGES', 'locale': 'en_US']
//        resourcBundle.request.JSON = data
//        when:
//        resourcBundle.save();
//        then:
//        resourcBundle.response.text != null
//    }
}

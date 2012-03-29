//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//
ant.move(file: "${pluginBasedir}/src/java/metainfo/zk/lang-addon.xml", tofile: "${basedir}/web-app/WEB-INF/i18n-core-lang-addon.xml", overwrite: "true")

def zkFile = "${basedir}/web-app/WEB-INF/zk.xml"
def targetLocation = "${pluginBasedir}/src/java/web"


if ((new File(zkFile).exists())) {

    if ((new File(targetLocation).exists())) {
        ant.delete(dir: targetLocation)
    }

    ant.move(todir: targetLocation + "/js") {
        fileset(dir: "${pluginBasedir}/web-app/js")
    }
    ant.move(todir: targetLocation + "/css") {
        fileset(dir: "${pluginBasedir}/web-app/css")
    }
}
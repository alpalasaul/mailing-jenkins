#!groovy

package workflowlibs.manager;

import groovy.text.StreamingTemplateEngine

/**
 * This method returns a string with the template filled with groovy variables
 */
def emailTemplate(params) {

    def fileContents = readFile("email.groovy")
    def engine = new StreamingTemplateEngine()

    return engine.createTemplate(fileContents).make(params).toString()
}

/**
 * This method send an email generated with data from Jenkins
 * @param buildStatus String with job result
 * @param emailRecipients Array with emails: emailRecipients = []
 */
def call(buildStatus, emailRecipients) {

    try {

        def icon = "✅"
        def statusSuccess = true
        def hasArtifacts = true

        if(buildStatus != "SUCCESS") {
            icon = "❌"
            statusSuccess = false
            hasArtifacts = false
        }

        def body = emailTemplate([
            "jenkinsText"   :   env.JOB_NAME,
            "jenkinsUrl"    :   env.BUILD_URL,
            "statusSuccess" :   statusSuccess,
            "hasArtifacts"  :   hasArtifacts,
            "downloadUrl"   :   "www.downloadurl.com"
        ]);

        mail (to: emailRecipients.join(","),
            subject: "${icon} [ ${env.JOB_NAME} ] [${env.BUILD_NUMBER}] - ${buildStatus} ",
            body: body,
            mimeType: 'text/html'
        );

    } catch (e){
        println "ERROR SENDING EMAIL ${e}"
    }
}

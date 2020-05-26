#!groovy

def workerNode = "devel10"

pipeline {
    agent {label workerNode}

    options {
        timestamps()
    }

    triggers {
        pollSCM('H/20 * * * *')
    }

    tools {
        maven 'maven 3.5'
    }

    stages {
        stage('Clear workspace') {
            steps {
                deleteDir()
                checkout scm
            }
        }

        stage('Build updateservice facade') {
            steps {
                withMaven(maven: 'maven 3.5', options: [
                        findbugsPublisher(disabled: true),
                        openTasksPublisher(highPriorityTaskIdentifiers: 'todo', ignoreCase: true, lowPriorityTaskIdentifiers: 'review', normalPriorityTaskIdentifiers: 'fixme,fix')
                ]) {
                    sh "mvn verify pmd:pmd findbugs:findbugs"
                    archiveArtifacts(artifacts: "target/*.war,target/*.log", onlyIfSuccessful: true, fingerprint: true)
                    junit "**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml"
                }
            }
        }

        stage('Warnings') {
            steps {
                warnings consoleParsers: [
                        [parserName: "Java Compiler (javac)"],
                        [parserName: "JavaDoc Tool"]
                ],
                        unstableTotalAll: "0",
                        failedTotalAll: "0"
            }
        }

        stage('PMD') {
            steps {
                step([
                        $class          : 'hudson.plugins.pmd.PmdPublisher',
                        pattern         : '**/target/pmd.xml',
                        unstableTotalAll: "0",
                        failedTotalAll  : "0"
                ])
            }
        }
    }
}
#!groovy

def workerNode = "devel10"

pipeline {
    agent {label workerNode}

    options {
        timestamps()
    }

    environment {
        DOCKER_IMAGE_NAME = "docker-io.dbc.dk/updateservice-facade"
        DOCKER_IMAGE_VERSION = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
        GITLAB_PRIVATE_TOKEN = credentials("metascrum-gitlab-api-token")
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

        stage("Docker") {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                script {
                    def image = docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_VERSION}")
                    image.push()
                }
            }
        }

        stage("Deploy") {
            agent {
                docker {
                    label workerNode
                    image "docker.dbc.dk/build-env:latest"
                    alwaysPull true
                }
            }
            when {
                expression {
                    (currentBuild.result == null || currentBuild.result == 'SUCCESS') && env.BRANCH_NAME == 'master'
                }
            }
            steps {
                script {
                    dir("deploy") {
                        sh """
                            set-new-version services/update-facade-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/dit-gitops-secrets ${DOCKER_IMAGE_VERSION} -b master

                            // This file doesn't exist yet
                            //set-new-version k8s/services/update-facade-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/updateservice-performance-test ${DOCKER_IMAGE_VERSION} -b master

							set-new-version update-facade-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/updateservice-facade-deploy ${DOCKER_IMAGE_VERSION} -b basismig
                            set-new-version update-facade-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/updateservice-facade-deploy ${DOCKER_IMAGE_VERSION} -b fbstest
                            set-new-version update-facade-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/updateservice-facade-deploy ${DOCKER_IMAGE_VERSION} -b metascrum-staging
						"""
                    }
                }
            }
        }
    }
}
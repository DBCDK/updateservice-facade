#!groovy

def workerNode = "devel10"

pipeline {
    agent { label workerNode }

    options {
        timestamps()
    }

    environment {
        DOCKER_IMAGE_NAME = "docker-metascrum.artifacts.dbccloud.dk/updateservice-facade"
        DOCKER_IMAGE_VERSION = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
        GITLAB_PRIVATE_TOKEN = credentials("metascrum-gitlab-api-token")
    }

    triggers {
        pollSCM('H/20 * * * *')
    }

    tools {
        jdk 'jdk11'
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
                sh "mvn -B verify pmd:pmd"
                junit "**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml"
            }
        }

        stage('Warnings') {
            steps {
                script {
                    junit allowEmptyResults: true, testResults: '**/target/*-reports/*.xml'

                    def java = scanForIssues tool: [$class: 'Java']
                    publishIssues issues: [java], unstableTotalAll: 10

                    def pmd = scanForIssues tool: [$class: 'Pmd']
                    publishIssues issues: [pmd], unstableTotalAll: 1

                    def spotbugs = scanForIssues tool: [$class: 'SpotBugs']
                    publishIssues issues: [spotbugs], unstableTotalAll: 1
                }
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
                    image "docker-dbc.artifacts.dbccloud.dk/build-env:latest"
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

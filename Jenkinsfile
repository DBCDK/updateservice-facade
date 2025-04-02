#!groovy

def workerNode = "devel11"

pipeline {
    agent { label workerNode }

    options {
        timestamps()
    }

    environment {
        DOCKER_IMAGE_NAME = "docker-metascrum.artifacts.dbccloud.dk/updateservice-facade"
        DOCKER_IMAGE_VERSION = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
        GITLAB_PRIVATE_TOKEN = credentials("metascrum-gitlab-api-token")
        MAVEN_OPTS="-Dmaven.repo.local=.repo"
    }

    triggers {
        pollSCM('H/20 * * * *')
    }

    tools {
        jdk 'jdk17'
        maven 'Maven 3'
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
                sh "mvn -version"
                sh "mvn -B verify pmd:pmd"
                junit "**/target/surefire-reports/TEST-*.xml,**/target/failsafe-reports/TEST-*.xml"
            }
        }

        stage('Warnings') {
            steps {
                script {
                    junit allowEmptyResults: true, testResults: '**/target/*-reports/*.xml'

                    def java = scanForIssues tool: [$class: 'Java']
                    publishIssues issues: [java], unstableTotalAll: 0

                    def pmd = scanForIssues tool: [$class: 'Pmd']
                    publishIssues issues: [pmd], unstableTotalAll: 0

                    def spotbugs = scanForIssues tool: [$class: 'SpotBugs']
                    publishIssues issues: [spotbugs], unstableTotalAll: 0
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
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                script {
                    dir("deploy") {
                        sh """							
							set-new-version update-dataio-facade-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/updateservice-facade-deploy ${DOCKER_IMAGE_VERSION} -b boblebad
							set-new-version update-fbs-facade-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/updateservice-facade-deploy ${DOCKER_IMAGE_VERSION} -b boblebad
							
							set-new-version update-facade-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/updateservice-facade-deploy ${DOCKER_IMAGE_VERSION} -b basismig
                            set-new-version update-facade-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/updateservice-facade-deploy ${DOCKER_IMAGE_VERSION} -b fbstest
						"""
                    }
                }
            }
        }
    }
}

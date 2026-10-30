#!groovy

def workerNode = "devel12"

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
        upstream(upstreamProjects: "Docker-payara6-bump-trigger",
                threshold: hudson.model.Result.SUCCESS)
    }

    tools {
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

        stage("sonarqube") {
            steps {
                ansiColor('xterm') {
                    withSonarQubeEnv(installationName: 'sonarqube.dbc.dk') {
                        script {
                            def status = 0

                            def sonarOptions = "-Dsonar.branch.name=${BRANCH_NAME}"
                            if (env.BRANCH_NAME != 'master') {
                                sonarOptions += " -Dsonar.newCode.referenceBranch=master"
                            }

                            // Do sonar via maven
                            status += sh returnStatus: true, script: """
                                mvn -B -Dmaven.repo.local=$WORKSPACE/.repo --no-transfer-progress $sonarOptions sonar:sonar
                            """

                            if (status != 0) {
                                error("build failed")
                            }
                        }
                    }
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

        stage("quality gate") {
            steps {
                // wait for analysis results
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage("supply-chain gate") {
            steps {
                script {
                    dependencyTrackGate(
                        projectBom:  'target/sbom-java.json',
                        projectTeam: 'febib',
                        projectType: 'java'
                    )
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
							set-new-version services/rawrepo/update-facade-service.yml ${env.GITLAB_PRIVATE_TOKEN} metascrum/dit-gitops-secrets ${DOCKER_IMAGE_VERSION} -b master

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

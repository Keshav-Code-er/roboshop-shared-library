def call (Map configMap) {
      //mapName.get("key-name")
      def component = configMap.get('component')
      pipeline {
            agent { node { label 'Agent-1' } }
            environment {
                  //here if you create any variable you will have global access, since it is environment no need of def
                  packageVersion = ''
            }

            stages {
                  stage('Get Version') {
                        steps {
                              script {
                                    def packageJson = readJSON file: 'package.json'
                                    packageVersion = packageJson.version
                                    echo "version: ${packageVersion}"
                              }
                        }
                  }
                  stage('Install dependencies') {
                        steps {
                              sh 'npm install'
                        }
                  }
                  stage('Unit test') {
                        steps {
                              echo 'unit testing is done here'
                        }
                  }
                  //sonar-scanner command  expect sonar-project.properties should be available
                  stage('Sonar Scan') {
                        steps {
                              echo 'Sonar scan done'
                        }
                  }

                  stage('Build') {
                        steps {
                              sh 'ls -ltr'
                              sh "zip -r ${component}.zip ./* --exclude=.git --exclude=.zip"
                        }
                  }

                  stage('SAST') {
                        steps {
                              echo 'SAST Done'
                        }
                  }

            //install pipeline utility steps plugin, if not installed

                  stage(' Publish Artifact') {
                        steps {
                              nexusArtifactUploader(
                              nexusVersion: 'nexus3',
                              protocol: 'http',
                              nexusUrl: '3.94.9.53:8081/',
                              groupId: 'com.roboshop',
                              version: "$packageVersion",
                              repository: "${component}",
                              credentialsId: 'nexus-auth',
                              artifacts: [
                                    [artifactId: "${component}",
                                    classifier: '',
                                    file: "${component}.zip",
                                    type: 'zip']
            ]
     )

                        }
                  }

                  // here I need to configure downstream job. I have to pass packages version for deployment
                  // This job will wait until downstream job is over
                  stage('Deploy') {
                        steps {
                              script {
                                    echo 'Deployment'
                                    def params = [
                              string(name: 'version' , value: "$packageVersion")
                        ]
                                    build job: "../${component}-deploy", wait: true, parameters:params
                              }
                        }
                  }
            }

            post {
                  always {
                        echo 'cleaning up workspace'
                        deleteDir()
                  }
            }
      }
}

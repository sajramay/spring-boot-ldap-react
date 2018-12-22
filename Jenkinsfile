pipeline {
    agent any
    stages {
        stage('Build NPM') {
            steps {
                nodejs(nodeJSInstallationName: 'NodeJS_11_4_0') {
                    sh 'cd src/main/frontend && npm install && npm build'
                }
            }
        }
        stage('Build Java') {
            steps {
                withMaven(maven : 'maven_3_6_0') {
                    sh 'mvn clean compile'
                }
                nodejs(nodeJSInstallationName: 'NodeJS_11_4_0') {
                    sh 'cd src/main/frontend && npm install && npm run-script build'
                }
            }
        }
        stage('Install') {
            steps {
                withMaven(maven : 'maven_3_6_0') {
                    sh 'mvn install -DskipTests'
                }
            }
        }

    }
}

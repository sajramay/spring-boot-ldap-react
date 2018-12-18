pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                withMaven(maven : 'maven_3_6_0') {
                    sh 'mvn clean compile'
                }
                nodejs(nodeJSInstallationName: 'NodeJS_11_4_0') {
                    sh 'cd src/main/frontend && npm install && npm build'
                }
            }
        }

        stage('Deploy Stage') {
            steps {
                withMaven(maven : 'maven_3_6_0') {
                    sh 'mvn install -DskipTests'
                }
            }
        }

    }
}

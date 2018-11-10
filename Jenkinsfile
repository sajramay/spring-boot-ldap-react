pipeline {
    stages {

        stage('Preparation') { // for display purposes
            agent any
            def mvnHome
            // ** NOTE: This 'M3' Maven tool must be configured
            // **       in the global configuration.
            mvnHome = tool 'M3'
        }
        stage('Build') {
            // Run the maven build
            if (isUnix()) {
             sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean install"
            } else {
             bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean install/)
            }
        }
        stage('Results') {
            junit '**/target/surefire-reports/TEST-*.xml'
            archive 'target/*.jar'
        }
    }
}
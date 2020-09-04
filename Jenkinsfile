pipeline {
    agent any
    tools {
        nodejs 'node-10.13.0'
        jdk 'jdk-11'
        maven 'maven-3.6.1'
    }
    triggers {
        pollSCM('H/5 6-23 * * 1-6')
    }
    stages {
        stage('Build NPM front end') {
            steps {
                sh 'cd src/main/frontend && npm install'
                sh 'cd src/main/frontend && npm run-script build'
            }
        }
        stage('Build Java') {
            steps {
                sh 'mvn clean deploy'
            }
        }
    }
}

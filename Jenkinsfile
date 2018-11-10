pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                withMaven(maven : 'maven_3_6_0') {
                    sh 'mvn clean compile'
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
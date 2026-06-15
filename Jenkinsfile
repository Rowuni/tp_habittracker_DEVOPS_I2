pipeline {
    agent any

    tools {
        jdk   'Java21'
        maven 'Maven'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Code récupéré.'
            }
        }

        stage('Build') {
            steps {
                dir('backend') {
                    sh 'mvn package -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                dir('backend') {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    pkill -f habit-tracker || true
                    sleep 2
                    export JENKINS_NODE_COOKIE=dontKillMe
                    nohup java -jar backend/target/habit-tracker-*.jar \
                        --server.port=9090 \
                        > /tmp/habittracker.log 2>&1 &
                    echo "Backend démarré sur le port 9090. Logs : /tmp/habittracker.log"
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline réussi ! Backend accessible sur http://localhost:9090/api/habits'
        }
        failure {
            echo 'Pipeline échoué. Consulter les logs ci-dessus.'
        }
    }
}
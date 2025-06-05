pipeline {
    agent any

    environment {
        JAVA_VERSION = '21'
        DISCORD_WEBHOOK_DEVELOP = credentials('DISCORD_WEBHOOK_DEVELOP')
        DEV_BASE_URL = credentials('DEV_BASE_URL')
        RAILWAY_API_TOKEN = credentials('RAILWAY_API_TOKEN')
    }

    tools {
        // Si usas Jenkins con JDK tool configurado, puedes declarar aquí
        jdk 'jdk-21'
        maven 'maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set up JDK') {
            steps {
                script {
                    def jdkHome = tool name: 'jdk-21', type: 'jdk'
                    env.JAVA_HOME = "${jdkHome}"
                    env.PATH = "${jdkHome}/bin:${env.PATH}"
                    sh 'java -version'
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests=true'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Notify Develop') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    def commitMessage = sh(script: "git log -1 --pretty=%s", returnStdout: true).trim()
                    def commitUrl = "https://github.com/${env.JOB_NAME}/commit/${env.GIT_COMMIT}"

                    sh """
                    curl -H "Content-Type: application/json" \
                        -X POST \
                        -d '{
                            "content": "🛠️ **Push a `develop`** en `${env.JOB_NAME}` por **${env.BUILD_USER_ID}**.\\n\\n\
                            🔗 **Swagger:** ${DEV_BASE_URL}/swagger-ui.html\\n\
                            🌐 **Home:** ${DEV_BASE_URL}\\n\
                            📝 **Commit:** ${commitMessage}\\n\
                            🔍 [Ver commit](${commitUrl})"
                        }' \
                        ${DISCORD_WEBHOOK_DEVELOP}
                    """
                }
            }
        }

        stage('Deploy to Railway') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    curl -sSL https://railway.app/install.sh | sh
                    ./bin/railway login --service-token ${RAILWAY_API_TOKEN}
                    ./bin/railway up
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}

pipeline {
     environment {
       ID_DOCKER = "lamtara"
       IMAGE_NAME = "alpinehelloworld"
       IMAGE_TAG = "latest"
       STAGING = "${ID_DOCKER}-staging"
       PRODUCTION = "${ID_DOCKER}-production"
     }
     agent none
     stages {
         stage('Build image') {
             agent any
             steps {
                script {
                  sh 'docker build -t ${ID_DOCKER }/${IMAGE_NAME}:${IMAGE_TAG} .'
                }
             }
        }
        stage('Run container based on builded image') {
            agent any
            steps {
               script {
                 sh '''
			echo "Clean Environment"
			docker rm -f ${IMAGE_NAME} || echo "container does not exist"
			docker run -d -p 80:5000 -e PORT=5000 --name ${IMAGE_NAME} ${ID_DOCKER }/${IMAGE_NAME}:${IMAGE_TAG} 
			sleep 5
                 '''
               }
            }
       }
       stage('Test image') {
           agent any
           steps {
              script {
                sh '''
			curl http://172.17.0.1 | grep -q "Hello world!"
		
                '''
              }
           }
      }
      stage('Clean Container') {
          agent any
          steps {
             script {
               sh '''
			docker stop ${IMAGE_NAME}
			docker rm  ${IMAGE_NAME}
               '''
             }
          }
     }

     stage ('Login and Push Image on docker hub') {
          agent any
          steps {
             script {
               sh '''
			docker login -u "lamtara" -p ${Pass_DockerHub}
			docker push  ${ID_DOCKER }/${IMAGE_NAME}:${IMAGE_TAG}
               '''
             }
          }
      }    
     
     stage('Push image in staging and deploy it') {
       when {
              expression { GIT_BRANCH == 'origin/master' }
            }
      agent any
      environment {
          HEROKU_API_KEY = credentials('heroku_api_key')
      }  
      steps {
          script {
            sh '''
              heroku container:login
              heroku create $STAGING || echo "project already exist"
              heroku container:push -a $STAGING web
              heroku container:release -a $STAGING web
            '''
          }
        }
     }



     stage('Push image in production and deploy it') {
       when {
              expression { GIT_BRANCH == 'origin/main' }
            }
      agent any
      environment {
          HEROKU_API_KEY = credentials('heroku_api_key')
      }  
      steps {
          script {
            sh '''
              heroku container:login
              heroku create $PRODUCTION || echo "project already exist"
              heroku container:push -a $PRODUCTION web
              heroku container:release -a $PRODUCTION web
            '''
          }
        }
     }
  }
}

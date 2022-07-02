  post {
    always {
      script {
        slackNotifier currentBuild.result
      }
    }  
  }
#!/usr/bin/env groovy

def call(Map config = [:]) {
    def imageTag = config.imageTag ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'github-credentials'
    def gitUserName = config.gitUserName ?: 'Jenkins CI'
    def gitUserEmail = config.gitUserEmail ?: 'sauravmishra499@gmail.com'
    
    echo "Updating ${manifestsPath} with tag: ${imageTag}"
    echo "App URL: http://54.76.194.89.nip.io"
    
    withCredentials([usernamePassword(
        credentialsId: gitCredentials,
        usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD'
    )]) {
        sh """
            git config user.name "${gitUserName}"
            git config user.email "${gitUserEmail}"
        """
        
        sh """
            sed -i 's|image: sauravmishra07/jobportal:.*|image: sauravmishra07/jobportal:${imageTag}|g' ${manifestsPath}/07-jobportal-deployment.yml
            if [ -f "${manifestsPath}/09-ingress.yml" ]; then
                sed -i 's|host: .*|host: 54.76.194.89.nip.io|g' ${manifestsPath}/09-ingress.yml
            fi
        """
        
        sh(script: """
            if ! git diff --quiet; then
                git add ${manifestsPath}/*.yml
                git commit -m "chore: bump image ${imageTag} + ingress [ci skip]"
                
                # FIXED: Correct Git remote URL with password
                git remote set-url origin https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/sauravmishra07/Jobportal-DevOps-Project.git
                
                git push origin HEAD:main 2>&1 || git push origin HEAD:master
                echo "✅ Pushed to GitHub successfully!"
            else
                echo "ℹ️ No manifest changes"
            fi
        """, returnStatus: true)
    }
}

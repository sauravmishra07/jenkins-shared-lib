#!/usr/bin/env groovy

/**
 * Update Kubernetes manifests - Fixed for .yml files
 */
def call(Map config = [:]) {
    def imageTag = config.imageTag ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'github-credentials'
    def gitUserName = config.gitUserName ?: 'Jenkins CI'
    def gitUserEmail = config.gitUserEmail ?: 'sauravmishra499@gmail.com'
    
    echo "Updating ${manifestsPath} manifests with image tag: ${imageTag}"
    echo "Access: http://54.76.194.89.nip.io"
    
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
            # Update deployment (matches your exact image: sauravmishra07/jobportal)
            sed -i 's|image: sauravmishra07/jobportal:.*|image: sauravmishra07/jobportal:${imageTag}|g' ${manifestsPath}/07-jobportal-deployment.yml
            
            # Update ingress host to IP.nip.io
            if [ -f "${manifestsPath}/09-ingress.yml" ]; then
                sed -i 's|host: .*|host: 54.76.194.89.nip.io|g' ${manifestsPath}/09-ingress.yml
            fi
        """
        
        sh """
            if ! git diff --quiet; then
                git add ${manifestsPath}/*.yml
                git commit -m "chore: bump image tag ${imageTag} + ingress host [ci skip]"
                git remote set-url origin https://${GIT_USERNAME}@github.com/sauravmishra07/Jobportal-DevOps-Project.git
                git push origin HEAD:\${GIT_BRANCH}
                echo "✅ Successfully updated and pushed manifests"
            else
                echo "ℹ️ No changes detected"
            fi
        """
    }
}

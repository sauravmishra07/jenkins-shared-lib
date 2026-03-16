#!/usr/bin/env groovy

/**
 * Update Kubernetes manifests with new image tags
 */
def call(Map config = [:]) {
    def imageTag = config.imageTag ?: error("Image tag is required")
    def manifestsPath = config.manifestsPath ?: 'kubernetes'
    def gitCredentials = config.gitCredentials ?: 'github-credentials'
    def gitUserName = config.gitUserName ?: 'Jenkins CI'
    def gitUserEmail = config.gitUserEmail ?: 'jenkins@example.com'
    
    echo "Updating Kubernetes manifests with image tag: ${imageTag}"
    echo "Ingress host set to: 54.76.194.89.nip.io"
    echo "Access app at: http://54.76.194.89.nip.io"
    
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
            # Update deployment image
            sed -i "s|image: sauravmishra07/jobportal:.*|image: sauravmishra07/jobportal:${imageTag}|g" ${manifestsPath}/07-jobportal-deployment.yml
            
            # Ensure ingress uses IP-based nip.io hostname
            if [ -f "${manifestsPath}/09-ingress.yml" ]; then
                sed -i "s|host: .*|host: 54.76.194.89.nip.io|g" ${manifestsPath}/09-ingress.yml
            fi
        """
        
        sh """
            if git diff --quiet; then
                echo "No changes to commit"
            else
                git add ${manifestsPath}/*.yaml ${manifestsPath}/*.yml
                git commit -m "chore: update image tag ${imageTag} + ingress host [ci skip]" || true
                git remote set-url origin https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/sauravmishra07/Jobportal-DevOps-Project.git
                git push origin HEAD:\${GIT_BRANCH}
            fi
        """
    }
}

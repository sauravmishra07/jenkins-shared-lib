# Jenkins Shared Library

A collection of reusable Jenkins pipeline steps for CI/CD automation.

## Features

- **Clean Workspace** - Clean up Jenkins workspace
- **Git Clone** - Clone git repositories with branch support
- **Docker Build** - Build Docker images with flexible configuration
- **Docker Push** - Push Docker images to container registry
- **Build Reports** - Generate and archive build reports
- **Security Scan** - Run Trivy vulnerability scanning
- **K8s Updates** - Update Kubernetes manifests with new image tags

## Usage

### Clean Workspace

```groovy
cleanWs()
```

### Clone Repository

```groovy
// Simple usage
clone url: 'https://github.com/user/repo.git', branch: 'main'

// With map
clone url: 'https://github.com/user/repo.git', branch: 'develop'
```

### Docker Build

```groovy
dockerBuild(
    imageName: 'myapp',
    imageTag: 'v1.0.0',
    dockerfile: 'Dockerfile',
    context: '.'
)
```

### Docker Push

```groovy
dockerPush(
    imageName: 'myapp',
    imageTag: 'v1.0.0',
    credentials: 'docker-hub-credentials'
)
```

### Generate Report

```groovy
generatesReport(
    projectName: 'MyProject',
    imageName: 'myapp',
    imageTag: 'v1.0.0'
)
```

### Trivy Scan

```groovy
trivyScan()
```

### Update Kubernetes Manifests

```groovy
updateK8sManifests(
    imageTag: 'v1.0.0',
    manifestsPath: 'kubernetes',
    gitCredentials: 'github-credentials',
    gitUserName: 'Jenkins CI',
    gitUserEmail: 'jenkins@example.com'
)
```

## Requirements

- Jenkins with Pipeline support
- Docker installed on build agents
- Trivy security scanner
- Kubernetes cluster access
- Git credentials configured in Jenkins

## Installation

1. Create a shared library repository
2. Configure it in Jenkins under **Manage Jenkins > System > Global Pipeline Libraries**
3. Use in your Jenkinsfile:

```groovy
@Library('jenkins-shared-lib') _

pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                dockerBuild imageName: 'myapp', imageTag: '1.0.0'
            }
        }
    }
}
```

## License

MIT


def call(String configFilePath = 'resources/ansible_config.yaml') {
    // Load configuration
    def config = readYaml file: "${configFilePath}"

    stage('Clone Repo') {
        echo "Cloning Ansible role from GitHub..."
        git branch: 'saransh', url: 'https://github.com/OT-MyGurukulam/Ansible_33.git'
    }

    if (config.KEEP_APPROVAL_STAGE) {
        stage('User Approval') {
            input message: "Deploy Kubernetes to ${config.ENVIRONMENT}?", ok: 'Deploy'
        }
    }

    stage('Execute Ansible Playbook') {
        echo "Running Ansible playbook..."
        sh """
            ansible-playbook -i inventory Assignment-5/kuberole/playbook.yml
        """
    }

    stage('Notification') {
        echo "Sending notification to Slack channel ${config.SLACK_CHANNEL_NAME}"
        slackSend channel: config.SLACK_CHANNEL_NAME, 
                  color: 'good', 
                  message: config.ACTION_MESSAGE
    }
}


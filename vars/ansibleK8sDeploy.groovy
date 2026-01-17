def call(Map config = [:]) {

    pipeline {
        agent any

        environment {
            SLACK_CHANNEL = config.SLACK_CHANNEL_NAME
            ENV = config.ENVIRONMENT
            CODE_PATH = config.CODE_BASE_PATH
            MESSAGE = config.ACTION_MESSAGE
            KEEP_APPROVAL = config.KEEP_APPROVAL_STAGE
        }

        stages {

            stage('Clone Repo') {
                steps {
                    git url: 'https://github.com/Saranshrai23/practice-git.git',
                        branch: 'master'
                }
            }

            stage('User Approval') {
                when {
                    expression { KEEP_APPROVAL == true }
                }
                steps {
                    input message: "Deploy to ${ENV} environment?"
                }
            }

            stage('Ansible Playbook Execution') {
                steps {
                    sh """
                    cd ${CODE_PATH}
                    ansible-playbook playbook.yml
                    """
                }
            }

            stage('Notification') {
                steps {
                    echo "Sending notification to ${SLACK_CHANNEL}"
                    echo "${MESSAGE}"
                }
            }
        }
    }
}

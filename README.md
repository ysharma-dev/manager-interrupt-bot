# manager-interrupt-bot
A slack bot to tag managers on interrupt duty

Tasks
1. Figure out how to read data from Google sheet in JSON or CSV format.
2. Parse the JSON or CSV response
3. Send a message to test slack channel to print name of Managers.

Action Items  
1. Yugam - Add Alex as Collaborator to this repository :white_check_mark:
2. Alex - Read [Slack SDK for Java](https://slack.dev/java-slack-sdk/)
3. Yugam - Read [Node Slack SDK](https://slack.dev/node-slack-sdk/) and [tutorials](https://www.freecodecamp.org/learn) from freeCodeCamp
4. Alex & Yugam - Implement task(s) from the Tasks section above either using Slack SDK for Java or Node 


[Playbook and notes to write a Java Slack App](playbook.md)


manifest.yml:

---
applications:
- name: manager-interrupt-bot
  instances: 1
  buildpacks:
    - java_buildpack_offline
  path: ./target/interrupt-bot-1.0.0.jar
  env:
    SLACK_TOKEN: ""
    SLACK_SIGNING_SECRET: ""
    JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 11.+ } }'
    APP_PORT: 8080

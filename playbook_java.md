## Playbook - Setting up a Slack App in Java using Bolt for Java

`Note the link are pointing to Node SDK for Slack but the process of setting up the application is common across different SDKs`

### Create and Set up a `Slack` app
- [Create an App](https://slack.dev/node-slack-sdk/tutorials/local-development#create-an-app)
- [Token & Installing apps](https://slack.dev/node-slack-sdk/tutorials/local-development#tokens-and-installing-apps)
- [Understand Request URL & Setting up ngrok as development proxy](https://slack.dev/node-slack-sdk/tutorials/local-development#what-is-a-request-URL)
  - __Notes__
    - Setting up [ngrok](https://ngrok.com/) development proxy is not required when deploying the application to Cloud Foundry or Kubernetes
    - We can add the Request URL after we have deployed the application with a public route. ```[Syntax: https://<public-route>/slack/events]```

> Outcome: You must at least have the following after this step:
- `Bot User OAuth Access Token` &
- Your app's `Signing secret`

> Note: Listening to Slack Workspace Events and Adding Event Subscriptions will be covered later in this guide.  

### Set up a `Maven` project in `Visual Studio Code`

> Note: [Bolt for Java](https://api.slack.com/start/building/bolt-java) will be used for this project so make sure that you have JRE 11+
- [Set up a Maven project from scratch](https://spring.io/guides/gs/maven/#scratch) - Understand how to compile and package a maven project using `mvn` client
- Get [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) for Visual Studio Code (includes `mvn` client in the pack)
- [Java Project Management](https://code.visualstudio.com/docs/java/java-project#_project-management) - Understand how to [configure JDK](https://code.visualstudio.com/docs/java/java-project#_configure-jdk) in Visual Studio Code

`Maven Quick Reference`
- `mvn compile` - compiles the source code of the project
- `mvn package` - builds the project and packages the resulting JAR file(by default as a `.jar` file under `target` directory)
- `mvn clean` - cleans the target directory into which maven normally builds your project

### Building an application with [`Bolt for Java`](https://api.slack.com/start/building/bolt-java)

- After getting an initial draft of your `pom.xml` from the previous steps, begin adding dependecies that will be required for integration with Slack Platform. For example, this could be used as a reference
```
<dependencies>
    <dependency>
        <groupId>com.slack.api</groupId>
        <artifactId>bolt-jetty</artifactId>
        <version>[1,)</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.30</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>1.7.30</version>
    </dependency>
</dependencies>
```
- Set up application config - a good practice in regards to reading token and signing secret within your code using environment variables. For example,
```
var config = new AppConfig();
config.setSingleTeamBotToken(System.getenv("SLACK_TOKEN"));
config.setSigningSecret(System.getenv("SLACK_SIGNING_SECRET"));
```
- [Initialize your app](https://api.slack.com/start/building/bolt-java#initalize)
- Understand Event package and the classes implemented [com.slack.api.model.event](https://oss.sonatype.org/service/local/repositories/releases/archive/com/slack/api/slack-api-model/1.6.1/slack-api-model-1.6.1-javadoc.jar/!/com/slack/api/model/event/package-summary.html) For example, `AppMentionEvent` which can be used to listen for an event when a user mentions the Slack app name.
- Understand [Threading messages lingo](https://api.slack.com/messaging/managing#threading)
- Understand how to [spot a thread in Slack](https://api.slack.com/messaging/retrieving#finding_threads) - this will helpful in scenarios where the app is mentioned multiple times in the same slack thread and you do not want to post the response as an unthreaded message.
- Refer to the [Sample code](https://api.slack.com/methods/chat.postMessage/code) to understand how to utilize chatPostMessage method to post a message in Slack.

### Deploy to Cloud Foundry
- Example `manifest.yml` (replace token and signing secret to relevant values obtained previously + Specify the correct path of the packaged jar)
```
---
applications:
- name: interruptbot
  instances: 1
  buildpacks: 
    - java_buildpack_offline
  path: <path-relative-or-absolute>
  env:
    SLACK_TOKEN: "<Bot User OAuth Access Token>"
    SLACK_SIGNING_SECRET: "<App's Signing Secret>"
    JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 11.+ }}'
    APP_PORT: 8080
```
 

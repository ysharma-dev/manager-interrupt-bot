package interrupt;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jetty.SlackAppServer;

import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.event.AppMentionEvent;

public class Interrupt {
  public static void main(String[] args) throws Exception {
     // Set app config
    var config = new AppConfig();
    config.setSingleTeamBotToken(System.getenv("SLACK_TOKEN"));
    config.setSigningSecret(System.getenv("SLACK_SIGNING_SECRET"));
    // Set the application with the config
    var app = new App(config);
    // All the room in the world for your code
    app.event(AppMentionEvent.class, (payload, ctx) -> {
      System.out.println("Slack event received");
      AppMentionEvent event = payload.getEvent();
      System.out.println("ThreadTs: " + event.getThreadTs());
      System.out.println("Ts: " + event.getTs());
      System.out.println(event.getThreadTs() != null);
      if(event.getThreadTs() != null){
        if(event.getThreadTs() != event.getTs()) {
          ChatPostMessageResponse message = ctx.client().chatPostMessage(r -> r
          .token(ctx.getBotToken())
          .channel(event.getChannel())
          .threadTs(event.getThreadTs())
          .text("<@" + event.getUser() + "> Thank you! We greatly appreciate your efforts :two_hearts:"));
          if (!message.isOk()) {
            ctx.logger.error("chat.postMessage failed: {}", message.getError());
          }
        }
      } else {
        ChatPostMessageResponse message = ctx.client().chatPostMessage(r -> r
          .token(ctx.getBotToken())
          .channel(event.getChannel())
          .threadTs(event.getTs())
          .text("<@" + event.getUser() + "> Thank you! We greatly appreciate your efforts :raised_hands:"));
          if (!message.isOk()) {
            ctx.logger.error("chat.postMessage failed: {}", message.getError());
          }
      }
      return ctx.ack();
    });
    var server = new SlackAppServer(app, Integer.parseInt(System.getenv("APP_PORT")));
    server.start();
  }

}

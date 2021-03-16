package interrupt;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jetty.SlackAppServer;

import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.event.AppMentionEvent;

/**
 * Listens for app mention in Slack channels and pings appropriate manager
 * interrupt if within valid time window. Resources: Spring (https://spring.io/guides/gs/maven/#scratch),
 * Java/Bolt Slack API (https://api.slack.com/start/building/bolt-java).
 * @author Yugam Sharma
 * @author Alex Cail
 */
public class Interrupt {

  /** Instance of ScheduleUtil class. Assists with time/date validation and retrieving manager IDs. */
  private static ScheduleUtil scheduleUtil;

  /**
   * Listens for app mention in Slack channels and calls upon scheduleUtil to
   * validate time/date and return the ID of the manager interrupt on-duty. Given
   * there is a manager, pings said manager. Otherwise, returns message stating that
   * no manager interrupt is currently available.
   * @param args Commmand line arguments.
   * @throws Exception if any issues arise.
   */
  public static void main(String[] args) throws Exception {
    // Initalize ScheduleUtil object
    scheduleUtil = new ScheduleUtil();
    // Set app config
    var config = new AppConfig();
    config.setSingleTeamBotToken(System.getenv("SLACK_TOKEN"));
    config.setSigningSecret(System.getenv("SLACK_SIGNING_SECRET"));
    // Set the application with the config
    var app = new App(config);
    // All the room in the world for your code
    app.event(AppMentionEvent.class, (payload, ctx) -> {
      app.executorService().submit(() -> {
        try {
          System.out.println("Slack event received");
          AppMentionEvent event = payload.getEvent();
          System.out.println("ThreadTs: " + event.getThreadTs());
          System.out.println("Ts: " + event.getTs());
          System.out.println(event.getThreadTs() != null);
          String manager = scheduleUtil.getManagerName(); // Validates date and time, parses Google sheet
          String managerMessage;
          if (manager == null) {
            managerMessage = "No available manager interrupt on-duty at this time. Interrupt duty is within the hours of 12p-5p ET / 9a-2p PT on regular business days.";
          } else {
            managerMessage = "<@" + scheduleUtil.getManagerID(manager) + ">"; // Parses env var
          }
          if(event.getThreadTs() != null){
            if(event.getThreadTs() != event.getTs()) {
              ChatPostMessageResponse message = ctx.client().chatPostMessage(r -> r
              .token(ctx.getBotToken())
              .channel(event.getChannel())
              .threadTs(event.getThreadTs())
              .text(managerMessage));
              if (!message.isOk()) {
                ctx.logger.error("chat.postMessage failed: {}", message.getError());
              }
            }
          } else {
            ChatPostMessageResponse message = ctx.client().chatPostMessage(r -> r
              .token(ctx.getBotToken())
              .channel(event.getChannel())
              .threadTs(event.getTs())
              .text(managerMessage));
              if (!message.isOk()) {
                ctx.logger.error("chat.postMessage failed: {}", message.getError());
              }
          }
        } catch (Exception e) {
          System.out.println(e);
        }
      });
      return ctx.ack();
    });
    var server = new SlackAppServer(app, Integer.parseInt(System.getenv("APP_PORT")));
    server.start();
  }

}

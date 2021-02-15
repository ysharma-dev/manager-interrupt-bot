// Initialize using signing secret from environment variables
const { createEventAdapter } = require('@slack/events-api');
const slackEvents = createEventAdapter(process.env.SLACK_SIGNING_SECRET);
const port = process.env.PORT || 3000;
let parsedData = {};

// Attach listeners to events by Slack Event "type". See: https://api.slack.com/events/
slackEvents.on('app_mention', (event) => {
  console.log(`Received a message event: user ${event.user} in channel ${event.channel} says ${event.text}`);
  let eventValidation = require('./validateSlackEvent');
  let isValidEvent = eventValidation.validateEventTime();
  if(!isValidEvent.valid){
    // Get interrupt on duty from the Google spreadsheet
    let parseData = require('./parseGoogleSheet');
    parseData.parseGoogleSheet().then((result) => {
      parsedData = result;
      console.log(parsedData[isValidEvent.dayName].slackID);
      // Check if the message is part of a thread. Existence of thread_ts indicates that the received message is part of a thread.
      if(event.thread_ts){
        // Threaded reply - Different thread_ts and ts value indicates that the message is a reply. The condition is true if user mentions the app again in the same thread.
        if(event.thread_ts !== event.ts){
          sendMessage(event.channel, event.thread_ts, parsedData[isValidEvent.dayName].slackID);
        }
      }else {
        sendMessage(event.channel, event.ts, parsedData[isValidEvent.dayName].slackID);
      }
    });
  }else {
    // Send no interrupt available message in the response
    console.log("Event received outside of working hours or during weekend");
  }
});

// Handle errors (see `errorCodes` export)
slackEvents.on('error', console.error);

// Start a basic HTTP server
slackEvents.start(port).then(() => {
  // Listening on path '/slack/events' by default
  console.log(`server listening on port ${port}`);
});

async function sendMessage(channelID, timestamp, slackID){
  const { WebClient } = require('@slack/web-api');
  const client = new WebClient(process.env.SLACK_TOKEN);
  try {
    // Call the chat.postMessage method using the built-in WebClient
    const result = await client.chat.postMessage({
      // The token you used to initialize your app
      token: process.env.SLACK_TOKEN,
      channel: channelID,
      thread_ts: timestamp,
      text: `<@${slackID}>`
    });
  }
  catch (error) {
    console.error(error);
  }
}
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
function validateEventTime() {
    const daysMapping = {
      0: "Sunday",
      1: "Monday",
      2: "Tuesday",
      3: "Wednesday", 
      4: "Thursday", 
      5: "Friday", 
      6: "Saturday" 
    };
    // Get current Date & Time
    const currentDate = new Date();
    console.log(`Current Date in Local TZ: ${currentDate}`);
  
    //const currentHour = 24;
    const currentHour = currentDate.getUTCHours();
    console.log(`Current Hour in UTC: ${currentHour}`);
  
    // Check if the current time falls between 5:00 pm UTC and 10:00 pm UTC (24hrs clock - 17:00 - 22:00 UTC)
    if(currentHour >= 17 && currentHour < 22){
        console.log("Slack event received inside of 12:00 pm EST - 5:00 pm EST");
        //Check if current day is a weekday
        if(currentDate.getUTCDay() === 0 || currentDate.getUTCDay() === 6){
          return {valid: false, dayName: "Monday"};
        }else {
          return {valid: true, dayName: daysMapping[currentDate.getUTCDay()]};
        }
    }else {
        console.log("Slack event received outside of 12:00 pm EST - 5:00 pm EST");
        return {valid: false, dayName: "Monday"};
    }
}

module.exports = {
    validateEventTime
}
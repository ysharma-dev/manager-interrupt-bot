# Manager Interrupt Slack App
A simple slack app to tag managers on interrupt duty  
<img src="images/slack_logo.png" width="100">  
----

The app first verifies that the date and time are within the valid interrupt window. If the date and time are invalid, the app responds with a message telling the user that there is currently no manager interrupt on duty.  
If the time and date are valid, the app looks up the name of the manager corresponding to the current date on the schedule spreadsheet.  
If a non-manager input is under the “Interrupt” column in the spreadsheet (i.e. “Company Holiday”), the app responds with a message telling the user that there is currently no manager interrupt on duty.  
If there is a manager name listed, the app tags the manager’s Slack ID.

----  

## To start building on top of this code, see our playbooks

> Note: To be compliant with [12-factor principles](https://12factor.net/), we are learning to apply these principles for this project and will be making changes accordingly. Currently, all the configurations that we need for the project to work (For example, Slack credentials, Application port, JRE details, etc.) are consumed using a Cloud foundry application manifest. 

    
- [Playbook and notes to write a Java Slack App](playbook_java.md)
- (__In Progress__) [Playbook and notes to write a Nodejs Slack App](playbook_nodejs.md)

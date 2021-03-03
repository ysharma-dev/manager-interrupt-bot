package interrupt;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.ArrayList;
import java.io.IOException;

import java.util.Iterator;

import org.json.*;

/**
 * Checks to see if current date and time are valid. Then retrieves
 * the ID of the Manager Interrupt On-Duty.
 * @author Alex Cail
 */
public class ScheduleUtil {

    /** ArrayList of managers' names. */
    private ArrayList<String> managers;

    /**
     * Initializes list of managers from environment variables.
     */
    private void initializeManagers() {
        managers = new ArrayList<String>();
        JSONObject jObject = new JSONObject(System.getenv("MANAGER_JSON"));
        
        Iterator<String> keys = jObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            managers.add(key);
        }
    }

    /**
     * Validates current date and time, then returns the name of manager interrupt on duty,
     * or null if no manager interrupt for that day.
     * @throws IOException if calls to JSON I/O functionality fail.
     * @return Name of Manager Interrupt On-Duty
     */
    public String getManagerName() throws IOException {
        
        initializeManagers();

        // Get current time and date in UTC
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Date date = new Date();
        
        // Checks if current date and time is within window
        if (validTimeDate(date)) {
            // Search for corresponding manager

            int day = getDayCalNum(new GregorianCalendar()); // Gets day num (1 = Sunday, 2 = Monday, ...)
            int month = getMonthNum(new GregorianCalendar()) + 1; // January = 0
            int year = getYearNum(new GregorianCalendar());

            // Formatted date string
            String dateString = "";

            if (month < 10) {
                dateString += "0" + month;
            } else {
                dateString += month;
            }

            if (day < 10) {
                dateString += "-0" + day;
            } else {
                dateString += "-" + day;
            }

            dateString += "-" + year;
            
            // Find the manager for specific day
            String manager = findManager(dateString);

            // Check to see if there is a manager on duty (name is on manager list)
            boolean onDuty = false;
            for (String name: managers)
                if (name.equals(manager))
                    onDuty = true;
            

            if (onDuty)
                return manager;
            else
                return null; // NOTE: Add logic to Interrupt.java to handle this

        }
         
        // NOTE: Add logic to Interrupt.java to handle this
        return null;

    }

    /**
     * Validates that time and date are within interrupt window (regular business days
     * 5p-10p UTC).
     * @param date Date object representing current time and date
     * @return True if time and date are valid, false otherwise.
     */
    private boolean validTimeDate(Date date) {

        // Check day (valid = weekday)
		Calendar calendar = new GregorianCalendar();
        int day = getDayNum((GregorianCalendar)calendar);
		
		if (day < 2 || day > 6)
			return false;
		
		// Check hour (valid = 5pm - 10pm UTC)
		int hour = getHourNum((GregorianCalendar)calendar);
		
		if (hour < 17 || hour >= 22)
			return false;
		
		return true;

    }
    
    /**
     * Returns numerical representation of the current day of the week
     * (1 = Sunday, 2 = Monday, ...).
     * @param calendar GregorianCalendar used to assist in Calendar functionality.
     * @return Numerial representation of the current day of the week.
     */
    private int getDayNum (GregorianCalendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Returns numerical representation of the current day of the month.
     * @param calendar GregorianCalendar used to assist in Calendar functionality.
     * @return Numerial representation of the current day of the month.
     */
    private int getDayCalNum (GregorianCalendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns numerical representation of the current month
     * (0 = January, 1 = February, ...).
     * @param calendar GregorianCalendar used to assist in Calendar functionality.
     * @return Numerial representation of the current month.
     */
    private int getMonthNum (GregorianCalendar calendar) {
        return calendar.get(Calendar.MONTH);
    }

    /**
     * Returns the current year.
     * @param calendar GregorianCalendar used to assist in Calendar functionality.
     * @return Current year.
     */
    private int getYearNum (GregorianCalendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    /**
     * Returns the current hour in UTC (based on 24 hour
     * clock).
     * @param calendar GregorianCalendar used to assist in Calendar functionality.
     * @return Current hour.
     */
    private int getHourNum(GregorianCalendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Given a numerical representation of the day of the week,
     * this method translates that number into the day name as 
     * a String.
     * @param day Numerical representation of day of the week.
     * @return Day of the week as a String.
     */
    private String getDayOfWeek(int day) {
        if (day == 1)
            return "Sunday";
        else if (day == 2)
            return "Monday";
        else if (day == 3)
            return "Tuesday";
        else if (day == 4)
            return "Wednesday";
        else if (day == 5)
            return "Thursday";
        else if (day == 6)
            return "Friday";
        else
            return "Saturday";
    }

    /**
     * Finds the name of the manager for the day (assumes time and date to be valid,
     * as those validation methods are called first) by finding the day's date and
     * looking down the row.
     * @param date Date object representing current time and date.
     * @throws IOException if there is an issue reading data in from JSON document.
     * @return Name of manager as String.
     */
    private String findManager(String date) throws IOException {

        JSONObject schedule = JSONUtil.readJsonFromUrl(System.getenv("JSON_URL"));
        JSONObject feed = schedule.getJSONObject("feed");
		JSONArray entry = feed.getJSONArray("entry");

        int dayRow = -1;
			
		for (int i = 4; i < entry.length(); i += 4) {
			// Grab date
            JSONObject dateEntry = (JSONObject)entry.get(i);
			JSONObject cell = dateEntry.getJSONObject("gs$cell");
			String thisDate = cell.getString("inputValue");
				
			int row = cell.getInt("row");

            // Check to see if date matches today's
			if (thisDate.equals(date)) {
				dayRow = row;
			}
				
		}
		JSONObject managerEntry = (JSONObject)entry.get((dayRow * 3) + (dayRow - 2));
		JSONObject cell = managerEntry.getJSONObject("gs$cell");
		String manager = cell.getString("inputValue");

		return manager;

    }

    /**
     * Given the name of the manager to ping, returns the corresponding Slack ID 
     * from JSONObject of manager IDs from the environment variables.
     * @param managerName Name of the manager to ping.
     * @throws IOException if there is an issue reading data from JSONObject from the environment variables.
     * @return Slack ID of the manager to ping.
     */
    public String getManagerID(String managerName) throws IOException {
        // Time and date assumed to be valid (called after validation logic)
        JSONObject managerList = new JSONObject(System.getenv("MANAGER_JSON"));

        // Manager name assumed to be valid (checked for name in ArrayList prior to method call from main)
        return managerList.getString(managerName);
    }

}
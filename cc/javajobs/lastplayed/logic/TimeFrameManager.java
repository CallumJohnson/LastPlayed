package cc.javajobs.lastplayed.logic;

/*
    
    Created By:     JavaJobs
    Created In:     Nov/2020
    Project Name:   LastPlayed
    Package Name:   cc.javajobs.lastplayed.logic
    Class Purpose:  Handle all TimeFrame related queries/functionality.
    
*/

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class TimeFrameManager {

    private static TimeFrameManager instance;

    public TimeFrameManager() {
        instance = this;
    }

    /**
     * Method to return an instance of TimeManager to make the project more portable and quick to modify.
     *
     * @return - TimeFrameManager Object.
     */
    public static TimeFrameManager get() {
        return instance;
    }

    /**
     * Method to return if a TimeFrame is valid.
     *
     * @param input - User input.
     * @return - True = Recognised, False = Not recognised.
     */
    public boolean recognisedTimeFrame(String input) {
        return Arrays.stream(new String[]{"d", "w", "m", "y"}).anyMatch(input::equalsIgnoreCase);
    }

    /**
     * Method to return what the Distance or Count of the user input is.
     *
     * @param queryTime - User input (Example: 9)
     * @param timeFrame - User input (Example: m)
     * @return - 9 * m = 9 Months in Milliseconds.
     */
    public long getDistance(int queryTime, String timeFrame) {
        switch (timeFrame.toLowerCase()) {
            case "d":
                // Days
                return TimeUnit.DAYS.toMillis(queryTime);
            case "w":
                // Weeks
                return TimeUnit.DAYS.toMillis(queryTime*7);
            case "m":
                // Months
                return (queryTime * 31556952L / 12) * 1000;
            case "y":
                // Years
                return TimeUnit.DAYS.toMillis(queryTime*365);
            default:
                return -1;
        }
    }

}

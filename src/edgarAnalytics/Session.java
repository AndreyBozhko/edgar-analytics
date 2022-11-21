package edgarAnalytics;

import java.util.*;
import java.text.SimpleDateFormat;


/**
 * Class that provides statistics for a user's session.
 */
public class Session {

    private Calendar start, end;
    private final int firstentry;
    private int count = 0;


    /**
     * Constructor that creates the class instance from LogEntry.
     *
     * @param entry user event data
     */
    public Session(LogEntry entry) {
        start = entry.getTime();
        end = entry.getTime();

        firstentry = LogEntry.getEntryNumber();
        count = 1;
    }

    /**
     * Method that handles the new webpage request made by user.
     *
     * @param entry user event data
     */
    public void addVisitedWebpage(LogEntry entry) {
        count += 1;
        end = entry.getTime();
    }

    /**
     * Returns start time of the session.
     *
     * @return start time
     */
    public Calendar getStartTime() {
        return (Calendar) start.clone();
    }

    /**
     * Returns end time of the session.
     *
     * @return end time
     */
    public Calendar getEndTime() {
        return (Calendar) end.clone();
    }

    /**
     * Returns number of the entry that corresponds to the first user event in the current session.
     *
     * @return entry number
     */
    public int getEntryNumber() {
        return firstentry;
    }

    /**
     * Method that calculates time period in seconds between the given timestamp and last user's activity timestamp.
     *
     * @param current current timestamp
     * @return period between timestamps
     */
    public int getDuration(Calendar current) {
        return (int) Math.abs(current.getTimeInMillis() - end.getTimeInMillis()) / 1000;
    }

    /**
     * Method that calculates the duration of the session.
     *
     * @return duration of session
     */
    public int getDuration() {
        return getDuration(start);
    }

    /**
     * Returns comma-separated information about the session.
     * Overrides method {@code toString}.
     */
    @Override
    public String toString() {
        StringJoiner output = new StringJoiner(",");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        output.add(sdf.format(start.getTime()))
                .add(sdf.format(end.getTime()))
                .add(Integer.toString(getDuration() + 1))
                .add(Integer.toString(count));

        return output.toString();
    }

}

package edgarAnalytics;

import java.util.*;



/**
 * Class that provides the session statistics for all users
 */
public class ActiveSessions {

    private final int inact_time;
    private Calendar  current;
    
    private TreeSet<ActiveUser>   active_users;
    private final Map<String, Session>  user_sessions;
    
            
    
    /**
     * Constructor that initializes the class 
     * @param time the period of inactivity, after which a session is considered expired
     */
    public ActiveSessions(int time)
    {
        inact_time    = time;
        active_users  = new TreeSet<>();
        user_sessions = new HashMap<>();
    }
    
    
    
    /**
     * Updates current time which is needed to calculate inactivity periods
     * @param time current time
     */
    public void updateCurrentTime(Calendar time)
    { current = (Calendar) time.clone(); }
    
    
    
    /**
     * Method that processes new user event data
     * @param entry user event data
     */
    public void add(LogEntry entry)
    {
        // if user is still active
        if (user_sessions.containsKey(entry.getIP()))
        {
            // update his last event timestamp
            ActiveUser existing_user = new ActiveUser(entry.getIP(),
                    user_sessions.get(entry.getIP()).getEndTime());
            active_users.remove(existing_user);
            
            // and process new webpage request
            user_sessions.get(entry.getIP()).addVisitedWebpage(entry);
        }
        else
        {
            // otherwise start a new session record
            user_sessions.put(entry.getIP(), new Session(entry));
        }

        active_users.add(new ActiveUser(entry));
    }
    
    
    
    /**
     * Returns true if not all the sessions are finalized
     * @return {@code true} if there still are sessions to finalize
     */
    public boolean hasSessions()
    { return (!active_users.isEmpty()); }
    
    
    
    /**
     * Returns true if there is a session that expired
     * @return {@code true} if there is an expired session
     */
    public boolean hasExpiredSessions()
    {
        if (!hasSessions()) return false;
        
        String ip = active_users.first().getIP();
        
        return (getDuration(current, user_sessions.get(ip).getEndTime()) > inact_time);
    }
    
    
    
    /**
     * Finalize a session and produce an output string
     * @return output string
     */
    public String closeSession()
    {
        String ip = active_users.pollFirst().getIP();
        
        Calendar start = user_sessions.get(ip).getStartTime();
        Calendar end   = user_sessions.get(ip).getEndTime();
        int duration   = getDuration(start, end);
        int count      = user_sessions.get(ip).getWebpageCount();
        
        user_sessions.remove(ip);
        
        StringJoiner output = new StringJoiner(",");
        
        output.add(ip)
              .add(calendarToString(start))
              .add(calendarToString(end))
              .add(Integer.toString(duration + 1))
              .add(Integer.toString(count));
        
        return output.toString();
    }
    
    
    
    /**
     * In order to finalize all remaining sessions when the end of input file is reached,
     * the sessions must be sorted by their initial timestamp
     */
    public void reorderSessionsByStartTime()
    {
        TreeSet<ActiveUser> temp = new TreeSet<>();
        while (!active_users.isEmpty())
        {
            String ip = active_users.pollFirst().getIP();
            ActiveUser user = new ActiveUser(ip, user_sessions.get(ip).getStartTime());
            temp.add(user);
        }
        active_users = temp;
    }
    
    
    
    /**
     * Method that converts Calendar instance into String
     * @param cal timestamp
     * @return timestamp as a string
     */
    private static String calendarToString(Calendar cal)
    {
        StringJoiner time = new StringJoiner("");
        time.add(Integer.toString(cal.get(Calendar.YEAR)))
            .add("-")
            .add(String.format("%02d", cal.get(Calendar.MONTH) + 1))
            .add("-")
            .add(String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)))
            .add(" ")
            .add(String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)))
            .add(":")
            .add(String.format("%02d", cal.get(Calendar.MINUTE)))
            .add(":")
            .add(String.format("%02d", cal.get(Calendar.SECOND)));
        
        return time.toString();
    }
    
    
    
    /**
     * Method that calculates time period in seconds between two timestamps
     * @param c1 first timestamp
     * @param c2 second timestamp
     * @return period between timestamps
     */
    private static int getDuration(Calendar c1, Calendar c2)
    { return (int) Math.abs(c1.getTimeInMillis() - c2.getTimeInMillis()) / 1000; }
    
}
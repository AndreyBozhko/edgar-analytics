package edgarAnalytics;

import java.util.*;



/**
 * Class that provides the session statistics for all users
 */
public class ActiveSessions {

    private final int inact_time;
    private Calendar  current;
    
    private final Map<Order, TreeSet<ActiveUser>> active_users;
    private final Map<String, Session>            user_sessions;
    
    public static enum Order {BY_FIRST, BY_LAST};
    
            
    
    /**
     * Constructor that initializes the class 
     * @param time the period of inactivity, after which a session is considered expired
     */
    public ActiveSessions(int time)
    {
        inact_time    = time;
        user_sessions = new HashMap<>();
        active_users  = new HashMap<>();
        
        for (Order o : Order.values())
            active_users.put(o, new TreeSet<>());
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
            // process new webpage request
            user_sessions.get(entry.getIP()).addVisitedWebpage(entry);
        }
        else
        {
            // otherwise start a new session record
            user_sessions.put(entry.getIP(), new Session(entry));
            
            // add user event into the collection that would contain data about user's first event, sorted by date
            // also, some redundant event data may be added
            active_users.get(Order.BY_FIRST).add(new ActiveUser(entry));
        }
        
        // add user event into the collection that would contain data about user's last event, sorted by date
        // also, some redundant event data may be added
        active_users.get(Order.BY_LAST).add(new ActiveUser(entry));
    }
    
    
    
    /**
     * Returns true if not all the sessions are finalized after the input file ends
     * @return {@code true} if there still are sessions to finalize
     */
    public boolean hasSessionsToFinalize()
    {
        if (user_sessions.isEmpty()) return false;
        
        getNextUser(Order.BY_FIRST);
        
        return (!active_users.get(Order.BY_FIRST).isEmpty());
    }
    
    
    
    /**
     * Returns true if there is a session that expired
     * @return {@code true} if there is an expired session
     */
    public boolean hasExpiredSessions()
    {
        if (user_sessions.isEmpty()) return false;
        
        ActiveUser user = getNextUser(Order.BY_LAST);
        
        return (getDuration(current, user.getTime()) > inact_time);
    }
    
    
    
    /**
     * Finds next valid user whose session is to be finalized
     * @param order referes to collection from which user is to be selected
     * @return user
     */
    private ActiveUser getNextUser(Order order)
    {
        ActiveUser user = active_users.get(order).first();
        
        while (!(user_sessions.containsKey(user.getIP())
                && user_sessions.get(user.getIP())
                                .getEntryNumbers()[order.ordinal()]
                                 == user.getNumber()))
        {
            active_users.get(order).pollFirst();
            user = active_users.get(order).first();
        }
        
        return user;
    }
    
    
    
    /**
     * Method that closes the next session from the collection ordered by Order order
     * and produces an output string
     * @param order sorting order of collection, based on which the next session to be closed is chosen
     * @return output string
     */
    public String closeSession(Order order)
    {
        // get session info
        ActiveUser user = active_users.get(order).pollFirst();
        String ip = user.getIP();
        
        Calendar start = user_sessions.get(ip).getStartTime();
        Calendar end   = user_sessions.get(ip).getEndTime();
        int duration   = getDuration(start, end);
        int count      = user_sessions.get(ip).getWebpageCount();
        
        // remove user from active sessions collection
        user_sessions.remove(ip);
        
        // create output string
        StringJoiner output = new StringJoiner(",");
        
        output.add(ip)
              .add(calendarToString(start))
              .add(calendarToString(end))
              .add(Integer.toString(duration + 1))
              .add(Integer.toString(count));
        
        return output.toString();
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
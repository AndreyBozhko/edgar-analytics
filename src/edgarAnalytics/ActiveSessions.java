package edgarAnalytics;

import java.util.*;
import java.text.SimpleDateFormat;



/**
 * Class that provides the session statistics for all users
 */
public class ActiveSessions {

    private final int inact_time;
    private Calendar  current;
    
    private final Map<String, Session> user_sessions;
    private final Map<Order, 
                      TreeMap<Tuple<Calendar, Integer>, String>> active_users;
    
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
            active_users.put(o, new TreeMap<>());
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
        String ip = entry.getIP();
        
        // start a new session record if necessary
        boolean flag = user_sessions.containsKey(ip);
        user_sessions.putIfAbsent(ip, new Session(entry));
        
        Session sess = user_sessions.get(ip);
        // if user is still active
        if (flag)
        {
            // remove user info in order to update it
            active_users.get(Order.BY_LAST)
                        .remove(getUniqueSessionID(sess, Order.BY_LAST));
            
            // process new webpage request
            sess.addVisitedWebpage(entry);
        }
        else
        {
            // add user event into the collection with ordering BY_FIRST
            active_users.get(Order.BY_FIRST)
                        .put(getUniqueSessionID(sess, Order.BY_FIRST), ip);
        }
       
        // add user event into the collection with ordering BY_LAST
        active_users.get(Order.BY_LAST)
                    .put(getUniqueSessionID(sess, Order.BY_LAST), ip);
    }
    
    
    
    /**
     * Returns true if not all the sessions are finalized after the input file ends
     * @return {@code true} if there still are sessions to finalize
     */
    public boolean hasSessions()
    { return (!user_sessions.isEmpty()); }
    
    
    
    /**
     * Returns true if there is a session that expired
     * @return {@code true} if there is an expired session
     */
    public boolean hasExpiredSessions()
    {
        if (user_sessions.isEmpty()) return false;
        
        String ip = active_users.get(Order.BY_LAST).firstEntry().getValue();
        return (getDuration(current, user_sessions.get(ip).getEndTime()) > inact_time);
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
        String ip = active_users.get(order).firstEntry().getValue();

        Session sess   = user_sessions.get(ip);
        
        int count      = sess.getWebpageCount();
        Calendar start = sess.getStartTime();
        Calendar end   = sess.getEndTime();
        int duration   = getDuration(start, end);
        
        
        // remove user from active sessions collection
        for (Order o : Order.values())
            active_users.get(o).remove(getUniqueSessionID(sess, o));
        
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }
    
    
    
    /**
     * Method that calculates time period in seconds between two timestamps
     * @param c1 first timestamp
     * @param c2 second timestamp
     * @return period between timestamps
     */
    private static int getDuration(Calendar c1, Calendar c2)
    { return (int) Math.abs(c1.getTimeInMillis() - c2.getTimeInMillis()) / 1000; }

    
    
    /**
     * Returns unique session id consisting of timestamp of first or last (depending on order)
     * user's activity during the session and unique user's request id 
     * @param sess user's session
     * @param order determines which timestamp to use, first or last
     * @return unique session id
     */
    private static Tuple<Calendar, Integer> getUniqueSessionID(Session sess, Order order)
    {
        switch (order)
        {
        case BY_FIRST:
            return new Tuple<>(sess.getStartTime(), sess.getEntryNumber());
        case BY_LAST:
            return new Tuple<>(sess.getEndTime(),   sess.getEntryNumber());
        default:
            return null;
        }
    }
    
}

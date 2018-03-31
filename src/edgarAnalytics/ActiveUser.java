package edgarAnalytics;

import java.util.*;



/**
 * Class that represents a user that has an active session and allows comparison by timestamp
 */
public class ActiveUser implements Comparable<ActiveUser> {

    private String   ip;
    private Calendar time;
    
    
    
    /**
     * Constructor that creates the class instance from LogEntry
     * @param entry user event data
     */
    public ActiveUser(LogEntry entry)
    {
        ip   = entry.getIP();
        time = entry.getTime();
    }
    
    
    
    /**
     * Constructor that creates the class instance from IP and timestamp
     * @param ip string that represents user's IP
     * @param time timestamp
     */
    public ActiveUser(String ip, Calendar time)
    {
        this.ip   = ip;
        this.time = (Calendar) time.clone();
    }
    
    
    
    /**
     * Returns user's IP
     * @return user's IP
     */
    public String getIP()
    { return ip; }
    
    
    
    /**
     * Returns timestamp of the webpage request
     * @return timestamp
     */
    public Calendar getTime()
    { return (Calendar) time.clone(); }
    
    
    
    /**
     * Implementation of Comparable interface. Two users are identical if they have equal IPs,
     * otherwise the user with the earlier timestamp is "less" than the other user
     */
    public int compareTo(ActiveUser that)
    {
        if (this.ip.equals(that.getIP()))     return  0;
        
        if (this.time.before(that.getTime())) return -1;
        else                                  return +1;
    }
    
}
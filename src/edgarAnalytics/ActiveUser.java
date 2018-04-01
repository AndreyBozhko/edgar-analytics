package edgarAnalytics;

import java.util.*;



/**
 * Class that represents a user that has an active session and allows comparison by timestamp and 
 */
public class ActiveUser implements Comparable<ActiveUser> {

    private String   ip;
    private Calendar time;
    private int      number;
    
    
    
    /**
     * Constructor that creates the class instance from LogEntry
     * @param entry user event data
     */
    public ActiveUser(LogEntry entry)
    {
        this.ip   = entry.getIP();
        this.time = entry.getTime();
        this.number = LogEntry.getEntryNumber();
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
     * Returns the corresponding number of entry from the input file
     * @return entry number
     */
    public int getNumber()
    { return number; }
    
    
    
    /**
     * Implementation of Comparable interface. Two users are identical if they have equal IPs and timestamps,
     * otherwise the user with the earlier timestamp is "less" than the other user.
     * If timestamps are also identical, the appearance order in the input file is used 
     */
    public int compareTo(ActiveUser that)
    {
        int cmp = this.time.compareTo(that.getTime()); 
        if (cmp != 0) return cmp;
        
        return Integer.compare(this.number, that.getNumber());
    }
    
    
    
    /**
     * Implementation of {@code equals} interface. Two users are identical if they have equal IPs
     * and their timestamps represent the same moment of time
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        
        if (!(o instanceof ActiveUser)) return false;
        ActiveUser that = (ActiveUser) o;
        
        return (this.ip.equals(that.getIP())
                && this.time.compareTo(that.getTime()) == 0
                && this.number == that.getNumber());
    }
    
}
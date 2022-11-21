package edgarAnalytics;

import java.util.*;


/**
 * Class that provides the session statistics for all users.
 */
public class ActiveSessions {

    private final int inactTime;
    private Calendar current;

    private final Map<String, Session> userSessions;
    private final Map<Order, TreeMap<Tuple<Calendar, Integer>, String>> activeUsers;

    public enum Order {
        BY_FIRST,
        BY_LAST
    }


    /**
     * Constructor that initializes the class.
     *
     * @param time the period of inactivity, after which a session is considered expired
     */
    public ActiveSessions(int time) {
        inactTime = time;
        userSessions = new HashMap<>();
        activeUsers = new HashMap<>();

        for (Order o : Order.values()) {
            activeUsers.put(o, new TreeMap<>());
        }
    }


    /**
     * Updates current time which is needed to calculate inactivity periods.
     *
     * @param time current time
     */
    public void updateCurrentTime(Calendar time) {
        current = (Calendar) time.clone();
    }


    /**
     * Method that processes new user event data.
     *
     * @param entry user event data
     */
    public void add(LogEntry entry) {
        String ip = entry.getIP();

        // start a new session record if necessary
        boolean flag = userSessions.containsKey(ip);
        userSessions.putIfAbsent(ip, new Session(entry));

        Session sess = userSessions.get(ip);

        // if user is still active
        if (flag) {
            // remove user info in order to update it
            activeUsers.get(Order.BY_LAST)
                    .remove(getUniqueSessionID(sess, Order.BY_LAST));

            // process new webpage request
            sess.addVisitedWebpage(entry);
        } else {
            // add user event into the collection with ordering BY_FIRST
            activeUsers.get(Order.BY_FIRST)
                    .put(getUniqueSessionID(sess, Order.BY_FIRST), ip);
        }

        // add user event into the collection with ordering BY_LAST
        activeUsers.get(Order.BY_LAST)
                .put(getUniqueSessionID(sess, Order.BY_LAST), ip);
    }

    /**
     * Returns true if not all the sessions are finalized after the input file ends.
     *
     * @return {@code true} if there still are sessions to finalize
     */
    public boolean hasSessions() {
        return !userSessions.isEmpty();
    }

    /**
     * Returns true if there is a session that expired.
     *
     * @return {@code true} if there is an expired session
     */
    public boolean hasExpiredSessions() {
        if (userSessions.isEmpty()) {
            return false;
        }

        String ip = activeUsers.get(Order.BY_LAST).firstEntry().getValue();
        return userSessions.get(ip).getDuration(current) > inactTime;
    }

    /**
     * Method that closes the next session from the collection ordered by {@code order}
     * and produces an output string.
     *
     * @param order sorting order of collection, based on which the next session to be closed is chosen
     * @return output string
     */
    public String closeSession(Order order) {
        // get session info
        String ip = activeUsers.get(order).firstEntry().getValue();

        Session sess = userSessions.get(ip);

        // remove user from active sessions collection
        for (Order o : Order.values()) {
            activeUsers.get(o).remove(getUniqueSessionID(sess, o));
        }

        userSessions.remove(ip);

        return ip + "," + sess.toString();
    }

    /**
     * Returns unique session id consisting of timestamp of first or last (depending on order)
     * user's activity during the session and unique user's request id.
     *
     * @param sess  user's session
     * @param order determines which timestamp to use, first or last
     * @return unique session id
     */
    private static Tuple<Calendar, Integer> getUniqueSessionID(Session sess, Order order) {
        switch (order) {
            case BY_FIRST:
                return new Tuple<>(sess.getStartTime(), sess.getEntryNumber());
            case BY_LAST:
                return new Tuple<>(sess.getEndTime(), sess.getEntryNumber());
            default:
                return null;
        }
    }

}

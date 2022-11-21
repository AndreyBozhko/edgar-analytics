package edgarAnalytics;

import java.text.*;
import java.util.*;


/**
 * Class that parses new line from the input file.
 */
public class LogEntry {

    // String[] fieldNames = {"ip", "date", "time", "cik", "accession", "extension"};
    private static Map<String, Integer> positions;
    private static int entryNumber;

    private final String[] fields;
    private final String ip, webpage;

    private final Calendar datetime;


    /**
     * Constructor that parses data from the string passed to it.
     * Checks if ip is not empty.
     *
     * @param line single line from the input file
     * @throws ParseException if something went wrong
     */
    public LogEntry(String line) throws ParseException {
        fields = line.split(",", -1);

        ip = parseIP();
        if (ip.equals("")) {
            throw new IllegalArgumentException();
        }

        datetime = parseDateTime();
        webpage = parseWebpage();

        entryNumber += 1;
    }

    /**
     * Static method that parses the header of the input file, which allows processing of the input file.
     *
     * @param header first line (header) from the input file
     */
    public static void initialize(String header) {
        entryNumber = 0;

        String[] headers = header.split(",", -1);
        positions = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
            positions.put(headers[i], i);
        }
    }

    /**
     * Method that extracts IP from input string.
     *
     * @return IP
     */
    private String parseIP() {
        return fields[positions.get("ip")];
    }

    /**
     * Method that extracts date and time from input string.
     *
     * @return date and time
     * @throws ParseException if date cannot be parsed
     */
    private Calendar parseDateTime() throws ParseException {
        Calendar datetime = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        sdf.setLenient(false);
        datetime.setTime(sdf.parse(fields[positions.get("date")] + " " + fields[positions.get("time")]));

        return (Calendar) datetime.clone();
    }

    /**
     * Method that extracts unique webpage identifier from input string.
     *
     * @return webpage identifier (cik, accession and extention)
     */
    private String parseWebpage() {
        String[] webpageFields = {"cik", "accession", "extention"};

        StringJoiner wp = new StringJoiner(" ");
        for (String field : webpageFields) {
            wp.add(fields[positions.get(field)]);
        }

        return wp.toString();
    }

    /**
     * Returns user IP.
     *
     * @return IP
     */
    public String getIP() {
        return ip;
    }

    /**
     * Returns date and time of user's webpage request.
     *
     * @return date and time
     */
    public Calendar getTime() {
        return (Calendar) datetime.clone();
    }

    /**
     * Returns unique webpage identifier.
     *
     * @return webpage identifier (cik, accession and extention)
     */
    public String getWebpage() {
        return webpage;
    }

    /**
     * Returns the total number of entries read so far.
     *
     * @return number of entries
     */
    public static int getEntryNumber() {
        return entryNumber;
    }

}

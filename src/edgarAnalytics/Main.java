package edgarAnalytics;

import java.io.*;
import java.text.ParseException;


/**
 * Solver class for the EDGAR analytics problem
 */
public class Main {

    private final String inputPath, outputPath;
    private final ActiveSessions userSessions;


    /**
     * Initializes the parameters for the EDGAR analytics solver
     *
     * @param inputPath  path to the input file
     * @param timePath   path to the inactivity time file
     * @param outputPath path to the output file
     * @throws IOException if something went wrong
     */
    public Main(String inputPath, String timePath, String outputPath) throws IOException {
        this.inputPath = inputPath;
        this.outputPath = outputPath;

        userSessions = new ActiveSessions(readInactTime(timePath));
    }

    /**
     * Reads the inactivity time value (assuming 1 <= p <= 86400) from the file.
     *
     * @param path path to the percentile file
     * @return inactivity time value
     * @throws IOException if time cannot be read from the file
     */
    private static int readInactTime(String path) throws IOException {
        String line;
        try (BufferedReader reader = initializeReader(path)) {
            line = reader.readLine();
        }

        int time = Integer.parseInt(line);
        if (time < 1 || time > 86400) {
            throw new IllegalArgumentException();
        }

        return time;
    }

    /**
     * Helper method that initializes the reader in order to read from the file line by line.
     *
     * @param filePath path to the file
     * @return reader
     * @throws FileNotFoundException if file not found
     */
    private static BufferedReader initializeReader(String filePath) throws FileNotFoundException {
        return new BufferedReader(new FileReader(filePath));
    }

    /**
     * Helper method that initializes the writer in order to write into the file.
     *
     * @param filePath path to the file
     * @return writer
     * @throws IOException if file not found
     */
    private static BufferedWriter initializeWriter(String filePath) throws IOException {
        return new BufferedWriter(new FileWriter(filePath));
    }

    /**
     * Method that reads the input file, processes EDGAR entries and outputs user sessions statistics into the output file.
     *
     * @throws IOException if files not found
     * @throws ParseException if any error with parsing
     */
    public void performEdgarAnalysis() throws IOException, ParseException {
        // initialize reader and writer
        try (BufferedReader reader = initializeReader(inputPath);
             BufferedWriter writer = initializeWriter(outputPath)) {

            String line;
            if ((line = reader.readLine()) != null) {
                LogEntry.initialize(line);               // read the header
            }

            while ((line = reader.readLine()) != null) {
                // parse new entry and update current time
                LogEntry entry = new LogEntry(line);
                userSessions.updateCurrentTime(entry.getTime());

                // end all sessions that have expired
                while (userSessions.hasExpiredSessions()) {
                    writer.write(userSessions.closeSession(ActiveSessions.Order.BY_LAST));
                    writer.newLine();
                }

                // add new entry for processing
                userSessions.add(entry);
            }

            // end all remaining sessions
            while (userSessions.hasSessions()) {
                writer.write(userSessions.closeSession(ActiveSessions.Order.BY_FIRST));
                writer.newLine();
            }
        }
    }

    /**
     * {@code main} method that executes the EDGAR analytics code.
     *
     * @param args {@code path1, path2, path3} - paths to the input file, inactivity time file, and output file, respectively
     * @throws Exception if files not found
     */
    public static void main(String[] args) throws Exception {
        Main solver = new Main(args[0], args[1], args[2]);
        solver.performEdgarAnalysis();
    }

}

package edgarAnalytics;

import java.io.*;



/**
 * Solver class for the EDGAR analytics problem
 */
public class Main {
    
    private final String input_path, output_path;
    private final ActiveSessions user_sessions;
    
    
    /**
     * Initializes the parameters for the EDGAR analytics solver 
     * @param input_path path to the input file
     * @param time_path path to the inactivity time file
     * @param output_path path to the output file
     * @throws Exception
     */
    public Main(String input_path, String time_path, String output_path) throws Exception
    {
        this.input_path  = input_path;
        this.output_path = output_path;
        
        user_sessions = new ActiveSessions(readInactTime(time_path));
    }
    
    
    
    /**
     * Reads the inactivity time value (assuming 1 <= p <= 86400) from the file
     * @param path path to the percentile file
     * @return inactivity time value
     * @throws Exception if inactivity time is not Integer in the appropriate range
     */
    private static int readInactTime(String path) throws Exception
    {
        BufferedReader reader = initializeReader(path);
        String line           = reader.readLine();
        reader.close();
        
        int time = Integer.parseInt(line);
        if (time < 1 || time > 86400) throw new Exception();
        
        return time;
    }
    
    
    
    /**
     * Helper method that initializes the reader in order to read from the file line by line
     * @param file_path path to the file
     * @return reader
     * @throws Exception if file not found
     */
    private static BufferedReader initializeReader(String file_path) throws Exception
    {
        File file           = new File(file_path);
        FileInputStream fis = new FileInputStream(file);
        
        return new BufferedReader(new InputStreamReader(fis));
    }
    
    
    
    /**
     * Helper method that initializes the writer in order to write into the file
     * @param file_path path to the file
     * @return writer
     * @throws Exception if file not found
     */
    private static BufferedWriter initializeWriter(String file_path) throws Exception
    {
        return new BufferedWriter(new FileWriter(file_path));
    }
    
    
    
    /**
     * Method that reads the input file, processes EDGAR entries and outputs user sessions statistics into the output file
     * @throws Exception if files not found
     */
    public void performEdgarAnalysis() throws Exception
    {
        // initialize reader and writer
        BufferedReader reader = initializeReader(input_path);
        BufferedWriter writer = initializeWriter(output_path);
        
        String line = null;
        if ((line = reader.readLine()) != null)
            LogEntry.initialize(line);               // read the header
        
        while ((line = reader.readLine()) != null)  // read file line by line
        {
            // parse new entry and update current time
            LogEntry entry = new LogEntry(line);
            user_sessions.updateCurrentTime(entry.getTime());
            
            // end all sessions that have expired
            while (user_sessions.hasExpiredSessions())
            {
                writer.write(user_sessions.closeSession(ActiveSessions.Order.BY_LAST));
                writer.newLine();
            }
            
            // add new entry for processing
            user_sessions.add(entry);
        }
        
        // end all remaining sessions
        while (user_sessions.hasSessions())
        {
            writer.write(user_sessions.closeSession(ActiveSessions.Order.BY_FIRST));
            writer.newLine();
        }
        
        // close input and output files
        reader.close();
        writer.close();
    }
    
    
    
    /**
     * {@code main} method that executes the EDGAR analytics code
     * @param args {@code path1, path2, path3} - paths to the input file, inactivity time file, and output file, respectively
     * @throws Exception if files not found
     */
    public static void main(String[] args) throws Exception
    {
        Main solver = new Main(args[0], args[1], args[2]);
        solver.performEdgarAnalysis();
    }

}

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
     * Helper method that writes the output line into the file
     * @param writer BufferedWriter that writes into file
     * @param output output line
     * @throws Exception if I/O error occurs
     */
    private static void writeToOutput(BufferedWriter writer, String output) throws Exception
    {
        writer.write(output);
        writer.newLine();
    }
    
    
        
    /**
     * Method that reads the input file, processes EDGAR entries and outputs user sessions statistics into the output file
     * @throws Exception if files not found
     */
    /**
     * @throws Exception
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
                writeToOutput(writer, user_sessions.closeSession(ActiveSessions.Order.BY_LAST));
            
            // add new entry for processing
            user_sessions.add(entry);
        }
        
        // end all remaining sessions
        while (user_sessions.hasSessions())
            writeToOutput(writer, user_sessions.closeSession(ActiveSessions.Order.BY_FIRST));
        
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
//        Main solver = new Main(args[0], args[1], args[2]);
//        try
//        {
            Main solver = new Main("tests\\test_3\\input\\log.csv", 
                                   "tests\\test_3\\input\\inactivity_period.txt", 
                                   "tests\\test_3\\output\\sessionization1.txt");

            long startTime = System.nanoTime();
            solver.performEdgarAnalysis();
            long endTime = System.nanoTime();

            double duration = (endTime - startTime) / Math.pow(10.0, 9.0);
            System.out.println(Double.toString(duration) + " seconds");

            System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
//        }
//        catch (Exception e) { System.out.println("EXCEPTION"); }
        
    }

}
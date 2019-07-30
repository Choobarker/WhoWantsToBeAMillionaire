package pdc_assignment;


public class Timer {
    public static long start;
    
    public static void wait(int waitTime)
    {
        long startTime = System.currentTimeMillis();
        long currentTime;
        
        do
        {
            currentTime = System.currentTimeMillis();
        }
        while(currentTime - startTime < waitTime);
    }
    
    public static void start()
    {
        start = System.currentTimeMillis();
    }
    
    public static void stop()
    {
        long stop = System.currentTimeMillis();
        
        int time = (int)(stop - start);
        start = 0;
        
        System.out.println(time);
    }
    
}

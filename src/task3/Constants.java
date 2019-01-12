package task3;

/**
 * @author Pavlo Rozbytskyi
 * @version 1.0.0
 *
 * Here are stored all constant needed in program
 */
public class Constants {
    public final static int S            = 5;      //ranking constant
    public final static float PC_MIN     = 0.5f;
//    public final static float PC_MAX     = 0.54f;
    public final static float PC_MAX     = 0.5f;
    public final static float PC_STEP    = 0.02f;
    public final static float PM_MIN     = 0.03f;
//    public final static float PM_MAX     = 0.008f;
    public final static float PM_MAX     = 0.03f;
//    public final static float PM_MAX     = 0.002f;
    public final static float PM_STEP    = 0.002f;
    public final static int SCALE_FACTOR = 1000000;
    public static final int NUM_OF_ARGS  = 10;
    public static final int GENES_SCALE  = 40;
    public static final int THREADS_NUM  = 1;      //how much threads will be execute calculation (best way is when
                                                   //threads num equals count of physical CPUs)

    public static final String DATE_PATTERN      = "dd.MM.yyyy hh:mm:ss";
    public static final String DATE_FILE_PATTERN = "dd_MM_yyyy_hh_mm_ss";

    public static final boolean GRAPH_SIMULATION = false;
}

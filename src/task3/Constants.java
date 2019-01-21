package task3;

/**
 * @author Pavlo Rozbytskyi
 * @version 1.0.0
 *
 * Here are stored all constant needed in program
 */
public class Constants {
    public final static int S            = 5;      //ranking constant
    public final static float PC_MIN     = 0.2f;
    public final static float PC_MAX     = 0.9f;
    public final static float PC_STEP    = 0.05f;
    public final static float PM_MIN     = 0.0f;
    public final static float PM_MAX     = 0.2f;
    public final static float PM_STEP    = 0.005f;
    public final static int SCALE_FACTOR = 1000000;
    public static final int NUM_OF_ARGS  = 10;
    public static final int GENES_SCALE  = 40;

    public static final boolean GRAPH_SIMULATION = true;
    public static final int THREADS_NUM  = 4;       //how much threads will be execute calculation (best way is when
                                                    //threads num equals count of physical CPUs)

    public static final String DATE_PATTERN      = "dd.MM.yyyy hh:mm:ss";
    public static final String DATE_FILE_PATTERN = "dd_MM_yyyy_hh_mm_ss";

    public static final boolean TSP_RANDOM = false;
    public static final int TSP_SIMULATIONS_COUNT = 100;

    public static final int CITY_COUNT = 200;
    public static final int GRID_SIZE = 100;
    public static final int GENERATIONS_NUM = 2000;
}

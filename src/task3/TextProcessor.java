package task3;

import java.io.*;
import java.util.Arrays;

/**
 * Class handles all file inputs and outputs
 * @author p.rozbytskyi
 * @version 1.0.0
 */
public class TextProcessor {
    private static String TAG = "TextProcessor";

    /**
     * reading from file and creating cities array
     * @param fName file name
     * @return return cities array
     */
    public int [][] readFileTSP(String fName) {
        int [][] tspCities = null;
        boolean citiesCountFound = false;

        File file = new File(fName);
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            String [] arr = null;

            int i = 0;
            while ((line = reader.readLine()) != null){
                int j = 0;
                arr = line.split(" ");
                if(!citiesCountFound){
                    tspCities   = new int [arr.length][arr.length];
                    citiesCountFound = true;
                }
                for(int k = 0; k < arr.length; k++){
                    tspCities[i][j] = Integer.parseInt(arr[k]);
                    j++;
                }
                i++;
            }

            return tspCities;
       } catch (Exception e) {
            Main.printError(e.getMessage(), TAG);
        }
        return null;
    }
}

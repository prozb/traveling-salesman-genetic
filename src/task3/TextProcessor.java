package task3;

import java.io.*;
import java.util.Arrays;

public class TextProcessor {
    private static float pc;
    private static float pm;
    private static float gens;
    private static boolean firstRun;

    public static void main(String[] args) throws IOException {
        firstRun = true;

        BufferedReader reader = new BufferedReader(new FileReader("/Users/p.rozbytskyi/Desktop/GP_Abgabe/schema_2_protect_none.txt"));

        String read = reader.readLine();
        while (read != null){
//            s = changeFirstTwoCols(s);
//            s = s.replace(",", "\t\t");
//            System.out.println(s);

//            sb.append(s);
//            sb.append("\n");
            if(!read.equals("")) {
                findBestPmPc(read);
            }
            read = reader.readLine();
        }

        reader.close();
        System.out.printf("Best pc = %.3f pm = %.3f gens = %.3f", pc, pm, gens);

//        BufferedWriter writer = new BufferedWriter(new FileWriter("plot1.txt"));
//        writer.write(sb.toString());
//        writer.flush();
//        writer.close();
    }

    public static void findBestPmPc(String s){
        String [] sArr = s.replaceAll("\t+", ",").split(",");
        if(firstRun && sArr.length > 0){
            pc = Float.parseFloat(sArr[0]);
            pm = Float.parseFloat(sArr[1]);
            gens = Float.parseFloat(sArr[2]);
            firstRun = false;
        }else if(sArr.length > 0){
            float relGen = Float.parseFloat(sArr[2]);

            if(relGen < gens){
                gens = relGen;
                pc = Float.parseFloat(sArr[0]);
                pm = Float.parseFloat(sArr[1]);
            }
        }
    }
    public static String changeFirstTwoCols(String s){
        String[] sArr = s.split(" ");
        if(!(sArr.length <= 1)) {
            String change = sArr[0];
            sArr[0] = sArr[1];
            sArr[1] = change;

            String res = Arrays.toString(sArr);
            res = res.replace("]", "");
            res = res.replace("[", "");
            res = res.replace(" ", "");
            res = res.trim();

            return res;
        }
        return s;
    }

    public static String processLine(String s){
        s = s.replaceAll("\t+", " ");
//        s = s.replaceAll("\\s+", ",");
//        s = s.replaceAll(",", "\t\t");
        return s;
    }
}

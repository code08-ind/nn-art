package art;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Class:
 * Author: martinvy
 * Date:   28.10.2014
 * Info:
 */
public class ART1 {

    private int n = 7;  // number of input neurons / length of input
    private int m = 1;  // number of output neurons

    // lists of neurons weights
    private List<double[]> b = new ArrayList<double[]>();   // bottom-up
    private List<int[]>    t = new ArrayList<int[]>();      // top-down

    private double vigilance = 0.7;
    private int[][] input = {{1,1,0,0,0,0,1}, {0,0,1,1,1,1,0}, {1,0,1,1,1,1,0}, {0,0,0,1,1,1,0}, {1,1,0,1,1,1,0}};


    public ART1() {

        // init b weights
        double[] wb = new double[n];
        Arrays.fill(wb, 1.0 / (n + 1));
        b.add(wb);

        // init t weights
        int[] wt = new int[n];
        Arrays.fill(wt, 1);
        t.add(wt);

        write();
    }

    public void write() {
        System.out.println("Bottom-up");
        for (double[] i: b) {
            for (double j : i)
                System.out.print(" " + j);
            System.out.println();
        }

        System.out.println("Top-down");
        for (int[] i: t) {
            for (int j : i)
                System.out.print(" " + j);
            System.out.println();
        }
    }
    public String wrin(int[] in) {
        String x = "";
        for (int i = 0; i < n; i++)
            x += in[i];
        return x;
    }

    public void adapt() {

        // loop through input data
        for (int[] in: this.input) {

            System.out.println("**************");

            // create union with output neurons to process
            Set<Integer> A = new HashSet<Integer>();
            for (int i = 0; i < m; i++) A.add(i);   // all neurons are in at the start

            while (!A.isEmpty()) {

                // find neuron with largest y - winner
                int winner = 0;
                double y = 0, ymax = 0;
                for (int j: A) {                // through output neurons in A
                    for (int i = 0; i < n; i++) // through all it's inputs
                        y += b.get(j)[i] * in[i];
                    if (y > ymax) {
                        ymax = y;
                        winner = j;
                    }
                }
                System.out.println("Vitazny neuron: " + winner);

                // compare similarity
                double sum_ti = 0;
                double sum_i  = 0;
                for (int i = 0; i < n; i++) {
                    sum_ti += t.get(winner)[i] * in[i];
                    sum_i += in[i];
                }

                System.out.println("Podobnost: " + sum_ti/sum_i);

                // vigilance test
                if (sum_ti / sum_i >= vigilance) {

                    // vectors are similar enough, update weights
                    for (int i = 0; i < n; i++)
                        b.get(winner)[i] = (t.get(winner)[i] * in[i]) / (0.5 + sum_ti);
                    for (int i = 0; i < n; i++)
                        t.get(winner)[i] = t.get(winner)[i] * in[i];

                    A.clear();  // break loop, get next input

                    System.out.println("Vstupny vektor " + wrin(in) + " priradeny k " + winner);

                } else {
                    // vectors are not similar enough, remove neuron from Set
                    A.remove(winner);

                    System.out.println("Vstupny vektor " + wrin(in) + " nepriradeny k " + winner);

                    // create new neuron
                    if (A.isEmpty()) {
                        m++;

                        // calculate b weights
                        double[] wb = new double[n];
                        for (int i = 0; i < n; i++)
                            wb[i] = in[i] / (0.5 + sum_i);
                        b.add(wb);

                        // set t weights same as input
                        int[] wt = new int[n];
                        System.arraycopy(in, 0, wt, 0, n);
                        t.add(wt);

                        System.out.println("Vstupnemu vektoru " + wrin(in) + " priradena nova trieda " + m);
                    }
                }
            }
            write();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

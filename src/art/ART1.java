package art;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class:
 * Author: martinvy
 * Date:   28.10.2014.
 * Info:
 */
public class ART1 {

    private int n = 4;  // length of input / input layer
    private int m = 1;  // length of output layer

    private double vigilance = 0.6;

    private int[][] input = {{1,0,1,0}, {1,1,1,1}, {0,1,0,0}};


    private double[][] b = new double[m][n]; // from input to output layer
    private int[][]    t = new int[n][m];    // from output to input layer
    private List<int[]> tt = new ArrayList<int[]>();


    public ART1() {
        tt.add(new int[n]);
        System.out.println(tt.get(0));
//        for (int[] t: tt)
//        System.out.println(t.toString()+'a');
    }

    public void adapt() {

        // loop through input data
        for (int[] in: this.input) {

            // create union with output neurons to process
            Set<Integer> A = new HashSet<Integer>();
            for (int i = 0; i < m; i++) A.add(i);   // all neurons are in at the start

            while (!A.isEmpty()) {

                // find neuron with largest y - winner
                int winner = 0;
                double y = 0, ymax = 0;
                for (int j: A) {                // through output neurons in A
                    for (int i = 0; i < n; i++) // through all it's inputs
                        y += b[j][i] * in[i];
                    if (y > ymax) {
                        ymax = y;
                        winner = j;
                    }
                }

                // compare similarity
                int sum_ti = 0, sum_i = 0;
                for (int i = 0; i < n; i++) {
                    sum_ti += t[i][winner] * in[i];
                    sum_i += in[i];
                }

                // vigilance test
                if (sum_ti / sum_i >= vigilance) {

                    // vectors are similar enough, update weights
                    for (int i = 0; i < n; i++)
                        b[winner][i] = (t[i][winner] * in[i]) / (0.5 + sum_ti);
                    for (int i = 0; i < n; i++)
                        t[i][winner] = t[i][winner] * in[i];

                    A.clear();  // break loop, get next input

                } else {
                    // vectors are not similar enough, remove neuron from Set
                    A.remove(winner);

                    // create new neuron
                    if (A.isEmpty()) {
                        m++;

                    }
                }

            }
        }

    }

}

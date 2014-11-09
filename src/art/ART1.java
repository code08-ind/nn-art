package art;

import java.util.*;

/**
 * Class:  ART1 - implementation of Adaptive Resonance Theory 1
 * Author: martinvy
 * Date:   28.10.2014
 * Info:
 */
public class ART1 {

    // lists of neurons weights
    private List<double[]> b;   // bottom-up
    private List<int[]>    t;   // top-down

    private int n;      // number of input neurons / length of input
    private int m;      // number of output neurons

    private double vigilance = 0.8; // limit of similarity


    /**
     * Constructor
     */
    public ART1() {
    }

    /**
     * Initialization of neurons weights
     */
    public void init(int n) {
        System.out.println("Initialization");

        this.n = n; // n input neurons (n == length of input vector)
        this.m = 1; // 1 output neuron in the beginning

        b = new ArrayList<double[]>();  // bottom-up
        t = new ArrayList<int[]>();     // top-down

        // init b weights
        double[] wb = new double[n];
        double num = 1.0 / (n + 1);
        Arrays.fill(wb, num);
        b.add(wb);

        // init t weights
        int[] wt = new int[n];
        Arrays.fill(wt, 1);
        t.add(wt);

        write();
    }

    /**
     * Learn neurons on testing dataset
     */
    public void test_adapt() {

        int[][] input = {{1,1,0,0,0,0,1}, {0,0,1,1,1,1,0}, {1,0,1,1,1,1,0}, {0,0,0,1,1,1,0}, {1,1,0,1,1,1,0}};
        init(input.length);

        // loop through input data
        for (int[] in: input) {
            step(in);
        }
    }

    /**
     * Process one input vector
     * @param in input vector which length is n
     * @return number of neuron to which was input assigned
     */
    public int step(int[] in) {

        System.out.println("\n****** next step ********");
        int assigned_neuron = -1;

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
            double similarity = sum_ti/sum_i;

            System.out.println("Podobnost: " + similarity);

            // vigilance test
            if (similarity >= vigilance) {

                // vectors are similar enough, update weights
                for (int i = 0; i < n; i++)
                    b.get(winner)[i] = (t.get(winner)[i] * in[i]) / (0.5 + sum_ti);
                for (int i = 0; i < n; i++)
                    t.get(winner)[i] = t.get(winner)[i] * in[i];

                A.clear();  // break loop, get next input

                System.out.println("Neuron " + winner + " priradeny k vstupnemu vektoru\n" + wrin(in) + " Sum: " + sum_i);
                assigned_neuron = winner;

            } else {
                // vectors are not similar enough, remove neuron from Set
                A.remove(winner);
                System.out.println("Neuron " + winner + " nie je dostatocne podobny vektoru\n" + wrin(in));

                // create new neuron
                if (A.isEmpty()) {

                    // calculate b weights
                    double[] wb = new double[n];
                    for (int i = 0; i < n; i++)
                        wb[i] = in[i] / (0.5 + sum_i);
                    b.add(wb);

                    // set t weights same as input
                    int[] wt = new int[n];
                    System.arraycopy(in, 0, wt, 0, n);
                    t.add(wt);

                    System.out.println("Neuron " + m + " vytvoreny pre vstupny vektor\n" + wrin(in));
                    assigned_neuron = m;
                    m++;
                }
            }
        }
        write();

//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            br.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return assigned_neuron;
    }

    // printings
    public void write() {
        System.out.println("Bottom-up");
        for (double[] i: b) {
            for (double j : i)
                System.out.print(" " + j);
            System.out.println();
        }

        System.out.println("Top-down");
        for (int[] i: t) {
            int sum = 0;
            for (int j : i) {
                System.out.print(j);
                sum += j;
            }
            System.out.println(" Sum t: " + sum);
        }
    }
    public String wrin(int[] in) {
        String x = "";
        for (int i = 0; i < n; i++)
            x += in[i];
        return x;
    }

    // getters
    public List<double[]> getB() {
        return b;
    }
    public List<int[]> getT() {
        return t;
    }
    public int getN() {
        return n;
    }
    public int getM() {
        return m;
    }
    public double getVigilance() {
        return vigilance;
    }
}

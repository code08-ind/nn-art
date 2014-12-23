package art;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Class:
 * Author: martinvy
 * Date:   29.10.2014
 * Info:
 */
public class Panel extends JPanel implements ActionListener{

    // algorithm
    private ART1 art1;
    private java.util.List<int[]> bn = new ArrayList<int[]>();  // bottom neurons
    private java.util.List<int[]> tn = new ArrayList<int[]>();  // top neurons

    // input
    private int[][] input;
    private int input_index = 0;

    // graphics
    private int n_size = 15;
    private JLabel title = new JLabel("Neurons");
    private JButton step_button = new JButton("step");
    private JButton test_button = new JButton("test");
    private JList<String> list;
    private String[] input_array_string;

    private boolean isInit = false;

    public Panel(ART1 art1) {
        this.art1 = art1;

        add(title);
        test_button.addActionListener(this);
        add(test_button);
        step_button.addActionListener(this);
        add(step_button);
    }

    public void init_test() {

        input = new int[][]{{1,1,0,0,0,0,1}, {0,0,1,1,1,1,0}, {1,0,1,1,1,1,0}, {0,0,0,1,1,1,0}, {1,1,0,1,1,1,0}};
        art1.init(input.length);

        // convert input to strings for printing
        input_array_string = new String[input.length];
        for (int i = 0; i < input.length; i++) {
            input_array_string[i] = "";
            for (int j = 0; j < input[i].length; j++) {
                input_array_string[i] += (char) input[i][j] +1-1;
            }
        }

        // list of input vectors
        list = new JList<String>(input_array_string);
        list.setFixedCellWidth(150);
        add(list);

        drawNeurons();  // init phase
        isInit = true;
        validate();
        paint(this.getGraphics());
    }

    public void drawNeurons() {
        for (int i = 0; i < art1.getN(); i++) {
            bn.add(new int[]{i * (n_size + 20), 300});
        }
        for (int i = 0; i < art1.getM(); i++) {
            tn.add(new int[]{i * n_size + 20, 200});
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (isInit) {
            // draw all b neurons and lines
            for (int[] neuron : bn) {
                g.drawOval(neuron[0], neuron[1], n_size, n_size);

                for (int[] neuron2 : tn) {
                    g.drawOval(neuron2[0], neuron2[1], n_size, n_size);
                    g.drawLine(neuron2[0] + n_size / 2, neuron2[1] + n_size, neuron[0] + n_size / 2, neuron[1]);
                }
            }

            // update class of neurons
            for (int ii = 0; ii < art1.getM(); ii++) {
                String c = "";
                for (int j : art1.getT().get(ii))
                    c += (char) j - 1 + 1;
                g.drawChars(c.toCharArray(), 0, art1.getN(), tn.get(ii)[0], tn.get(ii)[1]);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == step_button) {
            if (input_index < input.length) {
                art1.step(input[input_index++]);
                if (art1.getM() > tn.size()) {
                    tn.add(new int[]{tn.get(tn.size() - 1)[0] + 80, 200});
                    //paintComponent(this.getGraphics());
                    paint(this.getGraphics());
                }
                input_array_string[input_index - 1] += " - " + (tn.size() - 1);
            } else {
                title.setText("Done");
            }
            revalidate();
        }
        else if (e.getSource() == test_button) {
            init_test();
        }
    }
}

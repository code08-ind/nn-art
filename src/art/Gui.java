package art;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.ArrayList;

/**
 * Class:   GUI - graphical user interface for using of ART algorithm
 * Author:  Martin Veselovsky
 * Date:    8.11.2014
 * Info:    Class is processing black and white PNG pictures in RGBA
 *          representation (getting each 4th bit only to check B/W).
 *          Window consists of toolbar, input's picture and information
 *          section and results panel, which contains one JPanel for each
 *          output neuron with names of files which have been associated with
 *          neuron. Use tooltip on name of file to show picture.
 */
public class Gui extends JPanel implements ActionListener, ChangeListener {

    // algorithm
    private ART1 art1;

    // input
    private String path = "";
    private File[] files;
    private int actual_file = 0;
    private BufferedImage img;
    private int[] input_bw = null;

    // graphics
    private JButton open_button = new JButton("open");
    private JButton init_button = new JButton("init");
    private JButton step_button = new JButton("step");
    private JButton run_button  = new JButton("run");
    private JButton save_button = new JButton("save");
    private JLabel  vigilance_lab = new JLabel("");
    private JLabel  path_lab      = new JLabel("");
    private JLabel  status_lab    = new JLabel("");
    private JLabel  input_lab     = new JLabel("");
    private JLabel  input_title   = new JLabel("");
    private JLabel  neurons_title = new JLabel("");
    private JPanel  results_panel = new JPanel();
    private ArrayList<JPanel> neurons_panels = new ArrayList<JPanel>();


    /**
     * Constructor - GUI initialization
     * @param art1 reference to ART1 algorithm
     */
    public Gui(ART1 art1) {
        this.art1 = art1;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.orange);

        // -- toolbar --
        JToolBar jt = new JToolBar();
        jt.setFloatable(false);
        jt.setAlignmentX(Component.LEFT_ALIGNMENT);

            // buttons
            jt.add(open_button);
            open_button.addActionListener(this);
            jt.add(init_button);
            init_button.addActionListener(this);
            jt.add(step_button);
            step_button.addActionListener(this);
            jt.add(run_button);
            run_button.addActionListener(this);
            jt.add(save_button);
            save_button.addActionListener(this);

            // slider - vigilance
            jt.add(Box.createHorizontalGlue());
            JSlider vigilance_slider = new JSlider(JSlider.HORIZONTAL);
            vigilance_slider.addChangeListener(this);
            vigilance_slider.setValue(((int) (art1.getVigilance() * 100)));
            jt.add(vigilance_slider);
            vigilance_lab.setText("Vigilance: " + Double.toString(art1.getVigilance()));
            jt.add(vigilance_lab);

        add(jt);
        // -- end of toolbar --

        // path
        add(path_lab);
        add(Box.createRigidArea(new Dimension(this.getWidth(), 5)));

        // status
        status_lab.setForeground(Color.BLUE);
        add(status_lab);

        // input title label and input label
        add(Box.createRigidArea(new Dimension(this.getWidth(), 10)));
        input_title = new JLabel("Input:");
        input_title.setFont(new Font("Arial", Font.PLAIN, 20));
        add(input_title);
        add(input_lab);

        // neurons title
        add(Box.createRigidArea(new Dimension(this.getWidth(), 20)));
        neurons_title = new JLabel("Neurons:");
        neurons_title.setFont(new Font("Arial", Font.PLAIN, 20));
        add(neurons_title);

        // neurons - results panel with scrollbar
        results_panel.setAlignmentX(LEFT_ALIGNMENT);
        results_panel.setLayout(new GridLayout(0, 2, 5, 5));
        add(results_panel);
        JScrollPane scrollPane = new JScrollPane(results_panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);
        add(scrollPane, BorderLayout.CENTER);

        ToolTipManager.sharedInstance().setInitialDelay(0);
        validate();
    }

    /**
     * Load png image from file and extract black and white
     * information by using each 4th bit from RGBA representation
     */
    public void loadImg() {
        try {
            System.out.println(files[actual_file].getName());

            // load file (png image)
            img = ImageIO.read(files[actual_file].getAbsoluteFile());
            byte[] pixels = ((DataBufferByte) img.getData().getDataBuffer()).getData();
            input_bw = new int[pixels.length / 4];

            // get each fourth pixel from RGBA model to represent BW model
            int k = 0;
            for (int i = 1; i <= pixels.length; i += 4) {
                input_bw[k] = Math.abs((int) pixels[i]) ;
                if (input_bw[k] > 1) {
                    System.out.println("Image " + files[actual_file].getName() + " has invalid format.");
                    status_lab.setText("Image " + files[actual_file].getName() + " has invalid format.");
                }
                k++;
            }

        } catch (IOException e) {
            e.printStackTrace();
            status_lab.setText("IO error occurred while opening " + files[actual_file].getName());
        }
    }

    /**
     * Save new assigned neuron's representation to png file.
     * @param assigned_n number of last assigned neuron
     */
    public void saveImg(int assigned_n) {
        int[] neuron = art1.getT().get(assigned_n);
        byte[] pixels = new byte[input_bw.length * 4];

        int k = -1;
        for (int i = 0; i < pixels.length; i++) {
            if (i % 4 == 0 && k+1 < neuron.length) k++;
            pixels[i] = ((byte) (neuron[k]* (-1)));
        }

        try {
            BufferedImage new_img = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

            System.out.println("---- saving assigned neuron " + assigned_n + " with this t weight ----");
            int[] im = new int[neuron.length];
            for (int i = 0; i < neuron.length; i++) {
                System.out.print(neuron[i]);
                if (neuron[i] == 0)
                    im[i] = Color.BLACK.getRGB();
                else
                    im[i] = Color.WHITE.getRGB();
            }
            System.out.println();

            new_img.setRGB(0, 0, img.getWidth(), img.getHeight(), im, 0, img.getWidth());

            String filepath = path + "/out/" + Integer.toString(assigned_n) + "neuron.png";
            ImageIO.write(new_img, "png", new File(filepath));


            // repaint neuron's picture and append input (actual file name) to it
            if (assigned_n < neurons_panels.size()) {

                // update icon of neuron class
                ((JLabel) neurons_panels.get(assigned_n).getComponent(0)).setIcon(new ImageIcon(new_img));

                // add newly assigned input and set tooltip to its image
                JLabel jl = new JLabel(files[actual_file].getName());
                jl.setToolTipText(
                    "<html><body><img src=\"" + files[actual_file].toURI().toURL() + "\"></body></html>");

                // get
                ((JPanel) neurons_panels.get(assigned_n).getComponent(1)).add(jl);
            }

            // draw new neuron (picture of class represented by neuron)
            else {
                // make new panel
                JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
                jp.setName(Integer.toString(assigned_n) + "-neuron: ");
                jp.setAlignmentX(LEFT_ALIGNMENT);

                // set background color
                if (assigned_n % 2 == 0) {
                    jp.setBackground(new Color(193,248,255));
                } else {
                    jp.setBackground(new Color(150,240,255));
                }

                // draw neuron class
                JLabel n_lab = new JLabel();
                n_lab.setIcon(new ImageIcon(new_img));
                jp.add(n_lab);

                // create another new panel with Grid layout for adding names of assigned inputs
                JPanel assigned_inputs = new JPanel(new GridLayout(0, 2, 10, 10));
                assigned_inputs.setBackground(jp.getBackground());
                jp.add(assigned_inputs);

                // add new neuron title
                JLabel jl = new JLabel(Integer.toString(assigned_n) + "-neuron: ");
                jl.setFont(new Font("Arial Black", Font.BOLD, 18));
                assigned_inputs.add(jl);
                assigned_inputs.add(Box.createRigidArea(new Dimension(0,0)));

                // add input which was assigned and set tooltip to its image
                JLabel in_lab = new JLabel(files[actual_file].getName());
                in_lab.setToolTipText(
                        "<html><body><img src=\"" + files[actual_file].toURI().toURL() + "\"></body></html>");
                assigned_inputs.add(in_lab);

                // add created panel to list of all neurons and to main results panel too
                neurons_panels.add(jp);
                results_panel.add(jp);
            }
            validate();
            repaint();

        } catch (IOException e) {
            status_lab.setText("Error while saving file to disk occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Show actual input (image) and do algorithm step for it.
     * Save and also show assigned class (neuron - neuron's image).
     */
    public void step() {
        input_lab.setText(files[actual_file].getName());
        input_lab.setIcon( new ImageIcon(files[actual_file].getAbsolutePath()) );

        int assigned_n = art1.step(input_bw);
        saveImg(assigned_n);

        actual_file++;
        loadImg();
    }

    /**
     * Application control - processing button actions
     * Buttons: init, step, run, save
     * @param e event
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // select directory
        if (e.getSource() == open_button) {
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            Integer returnVal = chooser.showOpenDialog(this);

            // get path of selected folder
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                path = chooser.getSelectedFile().getAbsolutePath();
                path_lab.setText("Working directory:  " + path);
                status_lab.setText("");

                // create output directory
                File out = new File(path + "/out");
                // make dir - if directory already exists (false returned) -> clean all files in /out
                if (!out.mkdirs()) {
                    for (File file: out.listFiles()) {
                        if(!file.delete()) {
                            status_lab.setText("Error while cleaning output directory /out occurred.");
                        }
                    }
                }

                // open directory to get list of files inside
                files = new File(path).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.lastIndexOf('.') > 0) {
                            int lastIndex = name.lastIndexOf('.');  // get last index for '.' char
                            String str = name.substring(lastIndex); // get extension
                            if(str.equals(".png"))                  // match path name extension
                                return true;
                        }
                        return false;
                    }
                });

                // check valid folder
                if (files.length == 0) {
                    path = "";
                    status_lab.setText("Selected directory does not contain any PNG pictures! Please choose another.");
                }
            }
        }

        // initialization
        else if (e.getSource() == init_button) {
            if (path.isEmpty()) {
                status_lab.setText("Please open directory with input BW pictures first.");
                return;
            }

            actual_file = 0;
            input_bw = null;

            for (JPanel panel: neurons_panels) {
                panel.removeAll();
                results_panel.remove(panel);
            }
            neurons_panels.clear();
            revalidate();
            repaint();

            loadImg();
            art1.init(input_bw.length);

            input_lab.setIcon(null);
            input_lab.setText("");
            status_lab.setText("Initialized with input vector length " + input_bw.length);
            input_title.setText("Input:");
            neurons_title.setText("Neurons:");
        }

        // step of algorithm - process actual input
        else if (e.getSource() == step_button) {
            if (input_bw == null) {
                status_lab.setText("Please do initialization first.");
                return;
            }
            if (actual_file + 1 < files.length) {
                input_title.setText("Input: " + (actual_file + 1));
                step();
                neurons_title.setText("Neurons: " + art1.getM());
            } else {
                status_lab.setText("All input vectors have been processed. ( " + (actual_file + 1) + " )");
                input_title.setText("Input: " + (actual_file + 1));
            }
        }

        // run
        else if (e.getSource() == run_button) {
            if (input_bw == null) {
                status_lab.setText("Please do initialization first.");
                return;
            }
            while (actual_file + 1 < files.length) {
                input_title.setText("Input: " + (actual_file + 1));
                step();
                neurons_title.setText("Neurons: " + art1.getM());

                //TODO nefunguje - asi bude treba spravit thread
                validate();
                revalidate();
                repaint();
            }
            status_lab.setText("All input vectors have been processed. ( " + (actual_file + 1) + " )");
        }

        // save
        else if (e.getSource() == save_button) {
            if (neurons_panels.size() == 0) {
                status_lab.setText("Do initialization and at least one step first.");
                return;
            }
            try {
                PrintWriter res_f = new PrintWriter(path + "/out/results.txt");
                for (JPanel panel: neurons_panels) {
                    for (Component component: panel.getComponents()) {
                        res_f.print(((JLabel) component).getText() + " ");
                    }
                    res_f.println();
                }
                status_lab.setText("Results have been saved to " + path + "/results.txt");
                res_f.close();

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                status_lab.setText("IO error occurred.");
            }
        }
    }

    /**
     * Vigilance parameter slider action
     * @param e event
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            double value = source.getValue() / 100.0;
            art1.setVigilance(value);
            vigilance_lab.setText("Vigilance: " + Double.toString(value));
        }
    }
}

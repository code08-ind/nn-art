package art;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
public class Gui extends JPanel implements ActionListener, ChangeListener, ItemListener {

    // algorithm
    private ART1 art1;

    // input
    private String path = "";
    private File[] files;
    private int actual_file = 0;
    private BufferedImage img;
    private BufferedImage first_img;
    private Boolean valid_file = true;
    private int[] input_bw = null;
    private int processed_files = 0;

    // graphics
    private JButton open_button = new JButton("open");
    private JButton init_button = new JButton("init");
    private JButton step_button = new JButton("step");
    private JButton run_button  = new JButton("run");
    private JButton save_button = new JButton("save");
    private JLabel  vigilance_lab = new JLabel("");
    private JLabel  path_lab      = new JLabel("");
    private JLabel  img_size_lab  = new JLabel("");
    private JLabel  status_lab    = new JLabel("");
    private JLabel  input_lab     = new JLabel("");
    private JLabel  input_title   = new JLabel("");
    private JLabel  finish_lab    = new JLabel("");
    private JLabel  neurons_title = new JLabel("");
    private JPanel  results_panel = new JPanel();
    private ArrayList<JPanel> neurons_panels = new ArrayList<JPanel>();
    private Boolean running = false;

    private JCheckBox logging = new JCheckBox("log");
    private JCheckBox display_images = new JCheckBox("images", true);


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
            open_button.setToolTipText("Open directory for load PNG images.");
            open_button.addActionListener(this);
            jt.add(open_button);

            init_button.setToolTipText("Initialize ART1 algorithm with first image of directory.");
            init_button.addActionListener(this);
            jt.add(init_button);

            step_button.setToolTipText("Classify next image from directory.");
            step_button.addActionListener(this);
            jt.add(step_button);

            run_button.setToolTipText("Classify all images from directory.");
            run_button.addActionListener(this);
            jt.add(run_button);

            save_button.setToolTipText("Save classification information in this window to file.");
            save_button.addActionListener(this);
            jt.add(save_button);

            jt.addSeparator();
            logging.setToolTipText("Print log information to console.");
            logging.addItemListener(this);
            jt.add(logging);
            display_images.setToolTipText("Display images of output neurons.");
            jt.add(display_images);

            // slider - vigilance
            jt.add(Box.createHorizontalGlue());
            JSlider vigilance_slider = new JSlider(JSlider.HORIZONTAL);
            vigilance_slider.addChangeListener(this);
            vigilance_slider.setValue(((int) (art1.getVigilance() * 100)));
            jt.add(vigilance_slider);
            vigilance_lab.setText("Vigilance: " + (String.format("%.02f", art1.getVigilance())));
            vigilance_lab.setToolTipText("Vigilance parameter for similarity comparing.");
            jt.add(vigilance_lab);

        add(jt);
        // -- end of toolbar --

        // first line box
        Box b = Box.createHorizontalBox();
        b.setAlignmentX(LEFT_ALIGNMENT);
        add(b);

            // vertical box on the right in first line
            Box br = Box.createVerticalBox();

            input_title = new JLabel("Input:");
            b.add(input_title);
            b.add(Box.createHorizontalGlue());
            b.add(br);
            b.add(Box.createRigidArea(new Dimension(10, 0)));

            br.add(path_lab);
            br.add(Box.createRigidArea(new Dimension(0, 5)));
            img_size_lab.setForeground(Color.DARK_GRAY);
            br.add(img_size_lab);

        // little space between lines
        add(Box.createRigidArea(new Dimension(0, 10)));

        // second line box
        Box bb = Box.createHorizontalBox();
        bb.setAlignmentX(LEFT_ALIGNMENT);
        add(bb);

            // vertical box on the right on second line
            Box bbr = Box.createVerticalBox();

            input_title.setFont(new Font("Arial", Font.PLAIN, 20));
            bb.add(input_lab);
            bb.add(Box.createHorizontalGlue());
            bb.add(bbr);
            bb.add(Box.createRigidArea(new Dimension(10, 0)));

            status_lab.setForeground(Color.BLUE);
            bbr.add(status_lab);
            bbr.add(Box.createRigidArea(new Dimension(0, 30)));
            finish_lab.setFont(new Font("Arial", Font.BOLD, 16));
            bbr.add(finish_lab);


        // neurons title (on third line)
        add(Box.createRigidArea(new Dimension(this.getWidth(), 20)));
        neurons_title = new JLabel("Output neurons:");
        neurons_title.setFont(new Font("Arial", Font.PLAIN, 20));
        add(neurons_title);

        // neurons - results panel with scrollbar
        results_panel.setAlignmentX(LEFT_ALIGNMENT);
        results_panel.setLayout(new GridLayout(0, 2, 5, 5));
        add(results_panel);
        JScrollPane scrollPane = new JScrollPane(results_panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);
        add(scrollPane);

        ToolTipManager.sharedInstance().setInitialDelay(0);
        validate();
    }

    /**
     * Load png image from file and extract black and white
     * information by using each 4th bit from RGBA representation
     */
    public Boolean loadImg() {
        try {
            log(files[actual_file].getName());

            // load file (png image)
            img = ImageIO.read(files[actual_file].getAbsoluteFile());
            byte[] pixels = ((DataBufferByte) img.getData().getDataBuffer()).getData();
            input_bw = new int[pixels.length / 4];

            // get each fourth pixel from RGBA model to represent BW model
            int k = 0;
            for (int i = 1; i <= pixels.length; i += 4) {
                input_bw[k] = Math.abs((int) pixels[i]) ;
                if (input_bw[k] > 1) {
                    return false;
                }
                k++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            status_lab.setText("IO error occurred while opening " + files[actual_file].getAbsolutePath());
            return false;
        }
        return true;
    }

    /**
     * Save new assigned neuron's representation to png file.
     * @param assigned_n number of last assigned neuron
     */
    public void saveImg(int assigned_n) {

        log("---- saving assigned neuron " + assigned_n + " with this t weight ----");
        int[] neuron = art1.getT().get(assigned_n);

        try {
            // create array of RGB values
            // - we are saving black or white color (according to 0 or 1 in neuron T weights)
            int[] im = new int[neuron.length];
            for (int i = 0; i < neuron.length; i++) {
                if (neuron[i] == 0)
                    im[i] = Color.BLACK.getRGB();
                else
                    im[i] = Color.WHITE.getRGB();
            }

            // create image from RGB array
            BufferedImage new_img = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            new_img.setRGB(0, 0, img.getWidth(), img.getHeight(), im, 0, img.getWidth());

            // save image to file (number)neuron.png in created /out/ in directory of input images
            String filepath = path + "/out/" + Integer.toString(assigned_n) + "neuron.png";
            ImageIO.write(new_img, "png", new File(filepath));


            // repaint neuron's picture and append input (actual file name) to it
            if (assigned_n < neurons_panels.size()) {

                // update icon of neuron class
                if (display_images.isSelected())
                    ((JLabel) neurons_panels.get(assigned_n).getComponent(0)).setIcon(new ImageIcon(new_img));

                // add newly assigned input and set tooltip to its image
                JLabel jl = new JLabel(files[actual_file].getName());
                jl.setToolTipText(
                    "<html><body><img src=\"" + files[actual_file].toURI().toURL() + "\"></body></html>");

                // get neuron panel -> get assigned inputs panel (grid layout)
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
                if (display_images.isSelected())
                    n_lab.setIcon(new ImageIcon(new_img));
                jp.add(n_lab);

                // create another new panel with Grid layout for adding names of assigned inputs
                JPanel assigned_inputs = new JPanel(new GridLayout(0, 2, 10, 10));
                assigned_inputs.setBackground(jp.getBackground());
                jp.add(assigned_inputs);

                // add new neuron title
                JLabel jl = new JLabel(Integer.toString(assigned_n) + "-neuron:");
                jl.setFont(new Font("Arial Black", Font.BOLD, 18));
                assigned_inputs.add(jl);
                assigned_inputs.add(new JLabel());

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
            status_lab.setText("Error occurred while saving neuron file to " + path);
            e.printStackTrace();
        }
    }

    /**
     * Show actual input (image) and do algorithm step for it.
     * Save and also show assigned class (neuron - neuron's image).
     */
    public void step() {

        input_title.setText("Input: " + (actual_file + 1) + " / " + files.length);
        input_lab.setText(files[actual_file].getName());
        input_lab.setIcon( new ImageIcon(files[actual_file].getAbsolutePath()) );

        // check file format is valid
        if (!valid_file) {
            log("Image " + files[actual_file].getName() + " has invalid format.");
            status_lab.setText("Image " + files[actual_file].getName() + " has invalid format.");
        }
        else {
            // if size of current image is same as first -> continue
            if (img.getHeight() == first_img.getHeight() && img.getWidth() == first_img.getWidth()) {

                int assigned_n = art1.step(input_bw);
                saveImg(assigned_n);
                processed_files++;
                status_lab.setText(
                        files[actual_file].getName() +
                        " assigned to " + assigned_n + "-neuron. Similarity: " + (String.format("%.02f", art1.getSimilarity()))
                );

                neurons_title.setText("Output neurons: " + art1.getM());
            } else {
                log("Image " + files[actual_file].getName() + " has invalid size.");
                status_lab.setText("Image " + files[actual_file].getName() + " has invalid size.");
            }
        }

        // load next file if this was not last, validation check on next step
        actual_file++;
        if (actual_file < files.length)
            valid_file = loadImg();
        else {
            input_bw = null;
            log("All valid vectors processed ( " + processed_files + " / " + actual_file + " ).");
            finish_lab.setText("All valid vectors processed ( " + processed_files + " / " + actual_file + " ).");
        }
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
            if (running)
                return;

            try {
                JFileChooser chooser = new JFileChooser(new File("."));
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                Integer returnVal = chooser.showOpenDialog(this);

                // get path of selected folder
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    path = chooser.getSelectedFile().getAbsolutePath();
                    path_lab.setText("Directory:  " + path + " ");
                    status_lab.setText("");
                    input_bw = null;

                    // create output directory
                    File out = new File(path + "/out");
                    // make dir - if directory already exists (false returned) -> clean all files in /out
                    if (!out.mkdirs()) {
                        File[] files = out.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                if (!file.delete()) {
                                    status_lab.setText("Error while cleaning output directory /out occurred.");
                                }
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
                                if (str.equals(".png"))                  // match path name extension
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
            catch (SecurityException ex) {
                status_lab.setText("Security error occurred while opening directory / creating out directory in the path.");
            }
            catch (Exception ex) {
                status_lab.setText("Error occurred while opening directory / creating out directory in the path.");
            }
        }

        // initialization
        else if (e.getSource() == init_button) {
            if (path.isEmpty()) {
                status_lab.setText("Please open directory with input BW pictures first.");
                return;
            }
            if (running)
                return;

            actual_file = 0;
            processed_files = 0;
            input_bw = null;

            // clean panel
            for (JPanel panel: neurons_panels) {
                panel.removeAll();
                results_panel.remove(panel);
            }
            neurons_panels.clear();
            revalidate();
            repaint();

            // load first image and check validity
            valid_file = loadImg();
            if (valid_file) {

                art1.setLogging(logging.isSelected());
                art1.init(input_bw.length);

                first_img = img;    // all next images must have same size
                status_lab.setText("Initialized with input vector length " + input_bw.length);
                finish_lab.setText("");

                img_size_lab.setText("First image: " +
                        Integer.toString(first_img.getWidth()) + " x " + Integer.toString(first_img.getHeight()) +
                        "  ( vector length = " + input_bw.length + " )");
            }
            else {
                status_lab.setText("Initialization failed. Invalid format of " + files[actual_file].getAbsolutePath());
                input_bw = null;
            }

            input_lab.setIcon(null);
            input_lab.setText("");
            input_title.setText("Input: - / " + files.length);
            neurons_title.setText("Output neurons:");
            run_button.setText("run");
            run_button.setToolTipText("Classify all images from directory.");
            running = false;
        }

        // step of algorithm - process actual input
        else if (e.getSource() == step_button) {
            if (running)
                return;

            if (input_bw == null) {
                status_lab.setText("Please do initialization first.");
            }
            else {
                step();
            }
        }

        // run
        else if (e.getSource() == run_button) {
            if (input_bw == null) {
                status_lab.setText("Please do initialization first.");
                return;
            }
            if (!running) {
                running = true;
                run_button.setText("stop");
                run_button.setToolTipText("Stop execution.");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (input_bw != null && running) {
                            step();
                            revalidate();
                            repaint();
                        }
                        if (input_bw == null) {
                            run_button.setText("run");
                            run_button.setToolTipText("Classify all images from directory.");
                            running = false;
                        }
                    }
                }).start();
            }
            else {
                running = false;
                run_button.setText("continue");
                run_button.setToolTipText("Continue in classification of images from directory.");
            }
        }

        // save
        else if (e.getSource() == save_button) {
            if (running)
                return;

            if (neurons_panels.size() == 0) {
                status_lab.setText("Do initialization and at least one step first.");
                return;
            }
            // save currently computed assignments to file
            try {
                PrintWriter res_f = new PrintWriter(path + "/out/results.txt");
                for (JPanel panel: neurons_panels) {
                    for (Component component: ((JPanel) panel.getComponent(1)).getComponents()) {
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
            vigilance_lab.setText("Vigilance: " + (String.format("%.02f", art1.getVigilance())) );
        }
    }

    /**
     * @param s string to log
     */
    public void log(String s) {
        if (logging.isSelected())
            System.out.println(s);
    }

    /**
     * Logging checkbox changed value
     * @param e event
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == logging)
            art1.setLogging(logging.isSelected());
    }
}

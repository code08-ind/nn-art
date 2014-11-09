package art;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class:
 * Author: martinvy
 * Date:   8.11.2014
 * Info:
 */
public class Gui extends JPanel implements ActionListener {

    // algorithm
    private ART1 art1;

    // graphics
    private JButton init_button = new JButton("init");
    private JButton step_button = new JButton("step");
    private JLabel  vigilance_lab = new JLabel("");
    private JLabel  status_lab  = new JLabel("");
    private JLabel  input_lab   = new JLabel("");
    private ArrayList<JLabel> neurons_lab = new ArrayList<JLabel>();

    private String path = "lib/boats_edited";
    private File[] files;
    private int actual_file = 0;
    private BufferedImage img;
    private int[] input_bw = null;

    /**
     * Constructor - GUI initialization
     * @param art1 reference to ART1 algorithm
     */
    public Gui(ART1 art1) {
        this.art1 = art1;
        this.files = new File(path).listFiles(new FilenameFilter() {
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

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // toolbar
        JToolBar jt = new JToolBar();
        jt.setFloatable(false);
        jt.setAlignmentX(Component.LEFT_ALIGNMENT);

        jt.add(init_button);
        init_button.addActionListener(this);

        jt.add(step_button);
        step_button.addActionListener(this);

        jt.add(Box.createHorizontalGlue());
        vigilance_lab.setText("Vigilance: " + Double.toString(art1.getVigilance()));
        jt.add(vigilance_lab);
        add(jt);

        // status
        status_lab.setForeground(Color.BLUE);
        add(status_lab);

        // input
        add(Box.createRigidArea(new Dimension(this.getWidth(), 10)));
        JLabel input_title = new JLabel("Input:");
        input_title.setFont(new Font("Arial", Font.PLAIN, 20));
        add(input_title);
        add(input_lab);

        // neurons
        add(Box.createRigidArea(new Dimension(this.getWidth(), 20)));
        JLabel neurons_title = new JLabel("Neurons:");
        neurons_title.setFont(new Font("Arial", Font.PLAIN, 20));
        add(neurons_title);
    }

    public void loadImg() {
        try {
            System.out.println(files[actual_file].getName());
            img = ImageIO.read(files[actual_file].getAbsoluteFile());
            byte[] pixels = ((DataBufferByte) img.getData().getDataBuffer()).getData();
            input_bw = new int[pixels.length / 4];

            int k = 0;
            for (int i = 1; i <= pixels.length; i += 4) {
                input_bw[k] = Math.abs((int) pixels[i]) ;
                if (input_bw[k] > 1) System.out.println("######## WTF: " + input_bw[k]);
                k++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveImg(int assigned_n) {
        int[] neuron = art1.getT().get(assigned_n);
        byte[] pixels = new byte[input_bw.length * 4];

        int k = -1;
        for (int i = 0; i < pixels.length; i++) {
            if (i % 4 == 0 && k+1 < neuron.length) k++;
            pixels[i] = ((byte) (neuron[k]* (-1)));

//            System.out.print(i + "  " + k + "\n");
        }
//        System.out.println("===");
//
//        for (int i = 0; i < pixels.length; i++)
//            System.out.print(pixels[i]);
//        System.out.println();
//        System.out.println(pixels.length);

        try {
//            DataBuffer buffer = new DataBufferByte(pixels, pixels.length);
//            //3 bytes per pixel: red, green, blue
//            WritableRaster raster = Raster.createInterleavedRaster(buffer, img.getWidth(), img.getHeight(), 3 * img.getWidth(), 3, new int[] {0, 1, 2}, (Point)null);
//            ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
//            BufferedImage new_img = new BufferedImage(cm, raster, true, null);

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
            if (assigned_n < neurons_lab.size()) {
                neurons_lab.get(assigned_n).setIcon(new ImageIcon(new_img));
                neurons_lab.get(assigned_n).setText(
                        neurons_lab.get(assigned_n).getText() + files[actual_file].getName() + ", "
                );
            }
            // draw new neuron (picture of class represented by neuron)
            else {
                JLabel jl = new JLabel(new ImageIcon(new_img));
                jl.setText(Integer.toString(assigned_n) + "-neuron: " + files[actual_file].getName() + ", ");
                neurons_lab.add(jl);
                add(jl);
            }
            validate();
            repaint();

        } catch (IOException e) {
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

    @Override
    public void actionPerformed(ActionEvent e) {

        // step of algorithm - process actual input
        if (e.getSource() == step_button) {
            if (input_bw == null) {
                status_lab.setText("Please do initialization first.");
                return;
            }

            if (actual_file + 1 < files.length) {
                step();
            } else {
                status_lab.setText("All input vectors have been processed. ( " + (actual_file + 1) + " )");
            }
        }

        // initialization
        else if (e.getSource() == init_button) {
            actual_file = 0;
            input_bw = null;
            for (JLabel neuron: neurons_lab) {
                remove(neuron);
            }
            neurons_lab.clear();
            repaint();

            loadImg();
            art1.init(input_bw.length);

            input_lab.setIcon(null);
            input_lab.setText("");
            status_lab.setText("Initialized with input vector length " + input_bw.length);
        }
    }
}

package art;

import javax.swing.*;

/**
 * Class:
 * Author: martinvy
 * Date:   28.10.2014.
 * Info:
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("ART1");

        ART1 art1 = new ART1();
        //art1.test_adapt();

//        boolean init = true;
//
//        File folder = new File("lib/simple");
//        for (File file: folder.listFiles()) {
//            System.out.println(file);
//
//            BufferedImage img = null;
//            try {
//                img = ImageIO.read(new File(folder + "/" + file.getName()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (img != null) {
//                byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
//                int[] input_gray = new int[pixels.length / 3];
//
//                for (byte pixel: pixels)
//                    System.out.print(pixel);
//
//                System.out.println(pixels.length);
//                int k = 0;
//                for (int i = 0; i < pixels.length; i += 3) {
//                    input_gray[k] = Math.abs((int) pixels[i]) ;
//                    if (input_gray[k] > 1)
//                        System.out.println("######## WTF: " + input_gray[k]);
//                    k++;
//                }
//                for (int j = 0; j<k;j++)
//                    System.out.print(input_gray[j]);
//
//                if (init) {
//                    art1.init(k);
//                    init = false;
//                }
//                art1.step(input_gray);
//            }
//        }

        // window
        JFrame window = new JFrame("ART1 algorithm visualisation");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(800, 800);
        window.setVisible(true);

//        Panel jp = new Panel(art1);
        Gui jp = new Gui(art1);

        window.add(jp);
    }
}

package art;

import javax.swing.*;

/**
 * Class:  Main - entry point of application
 * Author: Martin Veselovsky
 * Date:   28.10.2014.
 * Info:   Create main window.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("ART1");

        ART1 art1 = new ART1();
        //art1.test_adapt();

        // window
        JFrame window = new JFrame("Black and white pictures classification by ART neural network");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(800, 700);

        Gui jp = new Gui(art1);

        window.add(jp);
        window.setVisible(true);
    }
}

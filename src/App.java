import javax.swing.SwingUtilities;

import page.home.Register;
import utils.UseGlobal;

public class App {

    private static int screenWidth = 1680;
    private static int screenHeight = 945;

    private static int MIN_SCREEN = 600;

    public static void main(String[] args) throws Exception {
        new UseGlobal(screenWidth, screenHeight, MIN_SCREEN);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Register start = new Register();
                start.setVisible(true);

            }
        });

    }
}

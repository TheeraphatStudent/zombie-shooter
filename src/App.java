import javax.swing.SwingUtilities;

import client.Client;
import client.Server;
import page.home.Register;
import utils.UseGlobal;

public class App {
    // private static int screenWidth = 1920;
    // private static int screenHeight = 1080;
    private static int screenWidth = 1660;
    private static int screenHeight = 933;

    private static int MIN_SCREEN = 600;

    public static void main(String[] args) throws Exception {
        new UseGlobal(screenWidth, screenHeight, MIN_SCREEN);

        Server server = new Server();
        // Client client = new Client(server.getServerIp(), server.getServerPort());

        // StoreCommunication store = new StoreCommunication(server, client);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Register start = new Register(server);
                start.setVisible(true);

            }
        });

    }
}

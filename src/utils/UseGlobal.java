package utils;

public class UseGlobal {
    private static int width;
    private static int height;
    private static int minWidth;
    private static String name;
    private static String ip;

    // Add a flag to prevent reinitialization
    private static boolean isInitialized = false;

    public UseGlobal() {
        if (!isInitialized) {
            initialize();
        }
    }

    public UseGlobal(int width, int height, int min) {
        if (!isInitialized) {
            UseGlobal.width = width;
            UseGlobal.height = height;
            UseGlobal.minWidth = min;
            isInitialized = true;
        }
    }

    private static void initialize() {
        if (!isInitialized) {
            // Set default values
            width = 0;
            height = 0;
            minWidth = 0;
            name = null;
            ip = null;
            isInitialized = true;
        }
    }

    public static void setName(String _name) {
        name = _name;
        System.out.println("Name set to: " + name);
    }

    public static void setWidth(int width) {
        UseGlobal.width = width;
    }

    public static void setHeight(int height) {
        UseGlobal.height = height;
    }

    public static void setMinWidth(int minWidth) {
        UseGlobal.minWidth = minWidth;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static int getMinWidth() {
        return minWidth;
    }

    public static String getName() {
        return name;
    }

    public static void printState() {
        System.out.println("Current state:");
        System.out.println("Name: " + name);
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("MinWidth: " + minWidth);
        System.out.println("IP: " + ip);
    }
}

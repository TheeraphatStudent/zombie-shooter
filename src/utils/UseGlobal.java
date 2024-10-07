package utils;

public class UseGlobal {
    public static int width;
    public static int height;
    public static int minWidth;
    public static String name;
    public static String ip;

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

    public UseGlobal(int width, int height, int min, String name, String ip) {
        initialize();

        UseGlobal.width = width;
        UseGlobal.height = height;
        UseGlobal.minWidth = min;
        UseGlobal.name = name;
        UseGlobal.ip = ip;
    }

    private static void initialize() {
        if (!isInitialized) {
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

    public static void setIp(String _ip) {
        ip = _ip;
        System.out.println("IP set to: " + name);
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

    public static String getIp() {
        return ip;
    }

    public static void printState() {
        System.out.println("Current state:");
        System.out.println("Name: " + name);
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        System.out.println("MinWidth: " + minWidth);
        System.out.println("IP: " + ip);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

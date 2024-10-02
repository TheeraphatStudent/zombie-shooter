package utils;

public class UseGlobal {
    private static int width;
    private static int height;
    private static int minWidth;

    public UseGlobal(int width, int height, int min) {
        UseGlobal.width = width;
        UseGlobal.height = height;
        UseGlobal.minWidth = min;
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
}
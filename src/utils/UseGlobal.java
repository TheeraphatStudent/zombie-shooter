package utils;

public class UseGlobal {
    public static int width;
    public static int height;
    public static int minWidth;

    public UseGlobal(int width, int height, int min) {
        UseGlobal.width = width;
        UseGlobal.height = height;
        UseGlobal.minWidth = min;
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
        return UseGlobal.width;
    }

    public static int getHeight() {
        return UseGlobal.height;
    }

    public static int getMinWidth() {
        return UseGlobal.minWidth;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

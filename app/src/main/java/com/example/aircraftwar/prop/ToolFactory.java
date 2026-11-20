package com.example.aircraftwar.prop;

public class ToolFactory {

    public static Tool createTool(int x, int y) {
        return createTool(x, y, 5);
    }

    public static Tool createTool(int x, int y, int count) {
        int type = (int)(Math.random() * count);

        switch (type) {
            case 0:
                return new HpTool(x, y, 0, 5, 50);
            case 1:
                return new FireTool(x, y, 0, 5, 10);
            case 2:
                return new StrongFireTool(x, y, 0, 5, 10);
            case 3:
                return new BoomTool(x, y, 0, 5, 10);
            case 4:
                return new FrozeTool(x, y, 0, 5, 10);
            default:
                return null;
        }
    }
}

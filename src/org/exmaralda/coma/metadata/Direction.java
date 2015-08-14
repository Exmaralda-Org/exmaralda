package org.exmaralda.coma.metadata;

public class Direction {
    
    int direction = BIDIRECTIONAL;
    
    public static final int LINKSTO = 1;
    public static final int LINKSFROM = 2;
    public static final int BIDIRECTIONAL = 0;

    public Direction() {}
    
    public Direction(int d) {
	direction = d;
    }
    
    public void setDirection(int d) {
	direction = d;
    }
    
    public int getDirection() {
	return direction;
    }
}

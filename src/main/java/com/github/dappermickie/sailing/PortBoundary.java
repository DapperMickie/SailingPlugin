package com.github.dappermickie.sailing;

import net.runelite.api.coords.WorldPoint;
import lombok.Getter;

@Getter
public class PortBoundary {
    private final WorldPoint topRight;
    private final WorldPoint bottomLeft;

    public PortBoundary(WorldPoint topRight, WorldPoint bottomLeft) {
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
    }

    public boolean contains(WorldPoint point) {
        return point.getX() >= bottomLeft.getX() && 
               point.getX() <= topRight.getX() && 
               point.getY() >= bottomLeft.getY() && 
               point.getY() <= topRight.getY();
    }
} 
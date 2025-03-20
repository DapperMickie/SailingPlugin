package com.github.dappermickie.sailing;

import net.runelite.api.Client;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import lombok.Getter;

@Getter
public class ShipBoundary {
    private final WorldPoint topRight;
    private final WorldPoint bottomLeft;

    public ShipBoundary(WorldPoint topRight, WorldPoint bottomLeft) {
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
    }

    public boolean containsShip(Client client) {
        // Get the center tile of the currently displayed tiles
        Tile[][] tiles = client.getTopLevelWorldView().getScene().getTiles()[client.getTopLevelWorldView().getPlane()];
        if (tiles == null || tiles.length == 0 || tiles[0].length == 0) {
            return false;
        }

        // Calculate center tile coordinates
        int centerX = tiles.length / 2;
        int centerY = tiles[0].length / 2;
        
        // Get the center tile
        Tile centerTile = tiles[centerX][centerY];
        if (centerTile == null) {
            return false;
        }

        // Check if the center tile is within the boundary
        WorldPoint centerPoint = centerTile.getWorldLocation();
        return contains(centerPoint);
    }

    private boolean contains(WorldPoint point) {
        return point.getX() >= bottomLeft.getX() && 
               point.getX() <= topRight.getX() && 
               point.getY() >= bottomLeft.getY() && 
               point.getY() <= topRight.getY();
    }
} 
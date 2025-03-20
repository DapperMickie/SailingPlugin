package com.github.dappermickie.sailing;

import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

public enum Port {
    PORT_SARIM("Port Sarim", 
        new PortBoundary(new WorldPoint(3050, 3218, 0), new WorldPoint(3022, 3192, 0)),
        new ShipBoundary(new WorldPoint(3061, 3208, 0), new WorldPoint(3045, 3183, 0))),
    PANDEMONIUM("The Pandemonium", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        new ShipBoundary(new WorldPoint(3084, 2998, 0), new WorldPoint(3068, 2974, 0))),
    CATHERBY("Catherby", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null),
    DOGNOSE_ISLAND("Dognose Island", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null),
    THE_END("The End", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null),
    MUSA_POINT("Musa Point", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null),
    LAST_LIGHT("Last Light", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null),
    CHARRED_ISLAND("Charred Island", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null),
    ENTRANA("Entrana", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null),
    WITCHAVEN("Witchaven", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null),
    BRIMHAVEN("Brimhaven", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null),
    ARDOUGNE("Ardougne", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null),
    PORT_KHAZARD("Port Khazard", 
        new PortBoundary(new WorldPoint(3069, 3004, 0), new WorldPoint(3020, 2965, 0)),
        null);

    private final String displayName;
    private final PortBoundary boundary;
    private final ShipBoundary shipBoundary;

    Port(String displayName, PortBoundary boundary, ShipBoundary shipBoundary) {
        this.displayName = displayName;
        this.boundary = boundary;
        this.shipBoundary = shipBoundary;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PortBoundary getBoundary() {
        return boundary;
    }

    public ShipBoundary getShipBoundary() {
        return shipBoundary;
    }

    public static Port fromDisplayName(String displayName) {
        for (Port port : values()) {
            if (port.displayName.equals(displayName)) {
                return port;
            }
        }
        return null;
    }

    public boolean contains(WorldPoint point) {
        return boundary.contains(point);
    }

    public boolean containsShip(Client client) {
        return shipBoundary != null && shipBoundary.containsShip(client);
    }

    public static Port getCurrentPort(Client client) {
        // First check player location
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        for (Port port : values()) {
            if (port.contains(playerLocation)) {
                return port;
            }
        }

        // Then check ship location
        for (Port port : values()) {
            if (port.containsShip(client)) {
                return port;
            }
        }

        return null;
    }
} 
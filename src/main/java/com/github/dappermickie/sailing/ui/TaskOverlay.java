package com.github.dappermickie.sailing.ui;

import com.github.dappermickie.sailing.Port;
import com.github.dappermickie.sailing.tasks.PortTask;
import com.github.dappermickie.sailing.tasks.PortTaskHelper;
import com.github.dappermickie.sailing.tasks.TaskState;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class TaskOverlay extends OverlayPanel {
    private final PortTaskHelper taskHelper;
    private final Client client;

    @Inject
    public TaskOverlay(PortTaskHelper taskHelper, Client client) {
        super();
        this.taskHelper = taskHelper;
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Add current location
        String locationText = getCurrentLocationText();
        panelComponent.getChildren().add(TitleComponent.builder()
            .text("Current Location")
            .color(Color.YELLOW)
            .build());
        panelComponent.getChildren().add(LineComponent.builder()
            .left(locationText)
            .leftColor(Color.WHITE)
            .build());
        
        // Add spacer
        panelComponent.getChildren().add(LineComponent.builder()
            .left("")
            .build());

        // Only show tasks section if there are active tasks
        if (taskHelper.getActiveTaskCount() > 0) {
            panelComponent.getChildren().add(TitleComponent.builder()
                .text("Active Port Tasks")
                .color(Color.YELLOW)
                .build());

            for (PortTask task : taskHelper.getActiveTasks()) {
                // Add task description
                panelComponent.getChildren().add(LineComponent.builder()
                    .left(task.getDescription())
                    .build());

                // Add progress information
                String progressText = getProgressText(task);
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("Progress:")
                    .right(progressText)
                    .rightColor(getProgressColor(task))
                    .build());

                // Add a spacer between tasks
                panelComponent.getChildren().add(LineComponent.builder()
                    .left("")
                    .build());
            }
        }

        return super.render(graphics);
    }

    private String getCurrentLocationText() {
        // First check player location
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        Port playerPort = getCurrentPort(playerLocation);
        if (playerPort != null) {
            return playerPort.getDisplayName();
        }

        // Then check ship location
        for (Port port : Port.values()) {
            if (port.containsShip(client)) {
                return "Ship at " + port.getDisplayName();
            }
        }

        // Not at any port
        return "Not at a port";
    }

    private Port getCurrentPort(WorldPoint location) {
        for (Port port : Port.values()) {
            if (port.contains(location)) {
                return port;
            }
        }
        return null;
    }

    private String getProgressText(PortTask task) {
        switch (task.getState()) {
            case NOT_STARTED:
                return "Not Started";
            case ACCEPTED:
                return "Go to " + task.getSourcePort().getDisplayName();
            case LOADING_CARGO:
                return "Load cargo into ship";
            case SAILING:
                return "Sail to " + task.getDestinationPort().getDisplayName();
            case DELIVERING:
                return "Deliver to ledger";
            case COMPLETED:
                return "Completed!";
            default:
                return "Unknown";
        }
    }

    private Color getProgressColor(PortTask task) {
        switch (task.getState()) {
            case COMPLETED:
                return Color.GREEN;
            case NOT_STARTED:
                return Color.RED;
            default:
                return Color.ORANGE;
        }
    }
} 
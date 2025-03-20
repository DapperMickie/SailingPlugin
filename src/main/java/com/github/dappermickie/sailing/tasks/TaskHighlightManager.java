package com.github.dappermickie.sailing.tasks;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import java.awt.Color;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TaskHighlightManager {
    private static final Color TASK_BOARD_COLOR = new Color(255, 255, 0, 100);
    private static final Color SHIP_COLOR = new Color(0, 255, 0, 100);
    private static final Color LEDGER_COLOR = new Color(255, 165, 0, 100);
    
    private final Client client;
    private final ModelOutlineRenderer modelOutlineRenderer;
    private final PortTaskHelper taskHelper;

    @Inject
    public TaskHighlightManager(Client client, ModelOutlineRenderer modelOutlineRenderer, PortTaskHelper taskHelper) {
        this.client = client;
        this.modelOutlineRenderer = modelOutlineRenderer;
        this.taskHelper = taskHelper;
    }

    public void highlightObjects() {
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();

        for (int z = 0; z < tiles.length; z++) {
            for (int x = 0; x < tiles[z].length; x++) {
                for (int y = 0; y < tiles[z][x].length; y++) {
                    Tile tile = tiles[z][x][y];
                    if (tile == null) continue;

                    for (GameObject object : tile.getGameObjects()) {
                        if (object == null) continue;

                        ObjectComposition composition = client.getObjectDefinition(object.getId());
                        if (composition == null) continue;

                        String name = composition.getName().toLowerCase();
                        if (name == null) continue;

                        // Highlight task board if we can take tasks
                        if (name.contains("task board")) {
                            modelOutlineRenderer.drawOutline(object, 2, TASK_BOARD_COLOR, 100);
                        }
                        // Highlight ship if we have tasks requiring cargo loading/unloading
                        else if (name.contains("ship") && hasTasksRequiringShip()) {
                            modelOutlineRenderer.drawOutline(object, 2, SHIP_COLOR, 100);
                        }
                        // Highlight ledger if we have deliverable tasks
                        else if (name.contains("ledger") && hasDeliverableTasks()) {
                            modelOutlineRenderer.drawOutline(object, 2, LEDGER_COLOR, 100);
                        }
                    }
                }
            }
        }
    }

    private boolean hasTasksRequiringShip() {
        return taskHelper.getActiveTasks().stream()
            .anyMatch(task -> task.getState() == TaskState.LOADING_CARGO || 
                             task.getState() == TaskState.SAILING);
    }

    private boolean hasDeliverableTasks() {
        return taskHelper.getActiveTasks().stream()
            .anyMatch(task -> task.getState() == TaskState.DELIVERING);
    }
} 
package com.github.dappermickie.sailing.tasks;

import com.github.dappermickie.sailing.Port;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Singleton
public class TaskManager {
    private static final int NOTICE_BOARD_ID = 56858;
    private static final String TASK_DETAILS_PREFIX = "Task Details:";

    private final Client client;
    private final PortTaskHelper taskHelper;
    private final ConfigManager configManager;
    private final ClientThread clientThread;

    @Inject
    public TaskManager(Client client, PortTaskHelper taskHelper, ConfigManager configManager, 
                      ClientThread clientThread) {
        this.client = client;
        this.taskHelper = taskHelper;
        this.configManager = configManager;
        this.clientThread = clientThread;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        String message = event.getMessage();
        if (!message.startsWith(TASK_DETAILS_PREFIX)) {
            return;
        }

        // Extract task description from message
        String taskDescription = message.substring(TASK_DETAILS_PREFIX.length()).trim();
        
        // Get current port based on player location
        Port currentPort = Port.getCurrentPort(client);
        if (currentPort == null) {
            return;
        }

        // Create and add the task
        createAndAddTask(currentPort, taskDescription);
    }

    private void createAndAddTask(Port sourcePort, String description) {
        // Parse task details from description
        Port destination = getRandomDestination(sourcePort);
        boolean hasCombat = description.toLowerCase().contains("combat") || 
                           description.toLowerCase().contains("fight") ||
                           description.toLowerCase().contains("battle");
        String cargoType = parseCargoType(description);
        int cargoAmount = parseCargoAmount(description);

        CourierTask task = new CourierTask(UUID.randomUUID().toString(), 1, sourcePort, destination, 
                                         hasCombat, description, cargoType, cargoAmount, client);
        taskHelper.addTask(task);
    }

    private String parseCargoType(String description) {
        // TODO: Implement proper cargo type parsing
        return "General goods";
    }

    private int parseCargoAmount(String description) {
        // TODO: Implement proper cargo amount parsing
        return 5;
    }

    private Port getRandomDestination(Port currentPort) {
        // For now, just return The Pandemonium if we're at Port Sarim, or vice versa
        return currentPort == Port.PORT_SARIM ? Port.PANDEMONIUM : Port.PORT_SARIM;
    }

    private GameObject findGameObject(int id) {
        Tile[][] tiles = client.getScene().getTiles()[client.getPlane()];
        for (Tile[] tileRow : tiles) {
            for (Tile tile : tileRow) {
                if (tile != null) {
                    for (GameObject gameObject : tile.getGameObjects()) {
                        if (gameObject != null && gameObject.getId() == id) {
                            return gameObject;
                        }
                    }
                }
            }
        }
        return null;
    }
} 
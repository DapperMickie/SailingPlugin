package com.github.dappermickie.sailing.commands;

import com.github.dappermickie.sailing.Port;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.plugins.Plugin;

import javax.inject.Inject;

public class PortLocationCommand {
    private final Client client;
    private final ChatCommandManager chatCommandManager;
    private final ClientThread clientThread;

    @Inject
    public PortLocationCommand(Client client, ChatCommandManager chatCommandManager, ClientThread clientThread) {
        this.client = client;
        this.chatCommandManager = chatCommandManager;
        this.clientThread = clientThread;
    }

    public void registerCommands(Plugin plugin) {
        chatCommandManager.registerCommandAsync("playerportlocation", (message, args) -> {
            WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
            Port currentPort = getPortAtLocation(playerLocation);
            
            String response = currentPort != null 
                ? "You are currently at: " + currentPort.getDisplayName()
                : "You are not currently at any port.";
                
            clientThread.invokeLater(() -> {
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", response, null);
            });
        });

        chatCommandManager.registerCommandAsync("shipportlocation", (message, args) -> {
            Port currentPort = getPortAtShipLocation();
            
            String response = currentPort != null 
                ? "Your ship is currently at: " + currentPort.getDisplayName()
                : "Your ship is not currently at any port.";
                
            clientThread.invokeLater(() -> {
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", response, null);
            });
        });
    }

    private Port getPortAtLocation(WorldPoint location) {
        for (Port port : Port.values()) {
            if (port.contains(location)) {
                return port;
            }
        }
        return null;
    }

    private Port getPortAtShipLocation() {
        for (Port port : Port.values()) {
            if (port.containsShip(client)) {
                return port;
            }
        }
        return null;
    }
} 
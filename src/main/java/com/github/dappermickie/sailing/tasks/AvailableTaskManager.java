package com.github.dappermickie.sailing.tasks;

import com.github.dappermickie.sailing.Port;
import com.github.dappermickie.sailing.tasks.PortTaskHelper;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class AvailableTaskManager {
    private static final int NOTICE_BOARD_ID = 56858;
    private static final int TASKS_BOARD_WIDGET = 60489731;
    private static final int TASK_DETAILS_WIDGET = 60358662;
    private static final int TASK_ACCEPT_WIDGET = 60358664;
    private static final String TASK_ACCEPT_PREFIX = "You have accepted a port task:";
    private static final int MAX_TASKS_PER_PORT = 8;
    private static final String SELECT_TASK_OPTION = "Select ";
    private static final Pattern TASK_DETAILS_PATTERN = Pattern.compile(
        "Level: (\\d+)<br>" +
        "Cargo Location: ([^<]+)<br>" +
        "Destination: ([^<]+)<br>" +
        "Combat: ([^<]+)<br>" +
        "Cargo: ([^<]+)<br>" +
        "Amount of cargo: (\\d+)<br><br>" +
        "([\\s\\S]+)"
    );

    private final Map<Port, Set<PortTask>> availableTasksPerPort = new HashMap<>();
    private final PortTaskHelper portTaskHelper;
    private final Client client;
    private final ClientThread clientThread;

    @Inject
    public AvailableTaskManager(PortTaskHelper portTaskHelper, Client client, ClientThread clientThread) {
        this.portTaskHelper = portTaskHelper;
        this.client = client;
        this.clientThread = clientThread;
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == TASKS_BOARD_WIDGET >> 16) {
            clientThread.invokeLater(() -> {
                Widget tasksWidget = client.getWidget(TASKS_BOARD_WIDGET);
                if (tasksWidget == null) return;

                // Clear existing tasks for this port
                Port currentPort = Port.getCurrentPort(client);
                if (currentPort == null) return;
                clearAvailableTasks(currentPort);

                // Process each task widget (every 4th child)
                Widget[] children = tasksWidget.getChildren();
                if (children == null) return;

                // Limit to max 8 tasks
                int taskCount = 0;
                for (int i = 0; i < children.length && taskCount < MAX_TASKS_PER_PORT; i += 4) {
                    Widget taskWidget = children[i];
                    if (taskWidget != null && taskWidget.getType() == 6) {
                        taskCount++;
                    }
                }
            });
        } else if (event.getGroupId() == TASK_DETAILS_WIDGET >> 16) {
            clientThread.invokeLater(() -> {
                handleTaskDetailsWidget();
            });
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (SELECT_TASK_OPTION.equals(event.getMenuOption())) {
            // When a task is selected, wait for the details widget to load and then process it
            clientThread.invokeLater(() -> {
                Widget detailsWidget = client.getWidget(TASK_DETAILS_WIDGET);
                if (detailsWidget != null) {
                    //handleTaskDetailsWidget();
                }
            });
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        String message = event.getMessage();
        if (message.startsWith(TASK_ACCEPT_PREFIX)) {
            handleTaskAccepted(message);
        }
    }

    private void handleTaskAccepted(String message) {
        Port currentPort = Port.getCurrentPort(client);
        if (currentPort == null) {
            return;
        }

        String taskDescription = Text.removeTags(message.substring(TASK_ACCEPT_PREFIX.length()).trim());
        Set<PortTask> availableTasks = availableTasksPerPort.get(currentPort);
        if (availableTasks == null) {
            return;
        }

        Optional<PortTask> acceptedTask = availableTasks.stream()
            .findFirst();

        acceptedTask.ifPresent(task -> {
            availableTasks.remove(task);
            task.setState(TaskState.ACCEPTED);
            portTaskHelper.addTask(task);
        });
    }

    public void handleTaskDetailsWidget() {
        Widget detailsWidget = client.getWidget(TASK_DETAILS_WIDGET);
        if (detailsWidget == null) return;
        Port currentPort = Port.getCurrentPort(client);
        if (currentPort == null) {
            return;
        }
        Set<PortTask> availableTasks = availableTasksPerPort.get(currentPort);
        if (availableTasks != null) {
            availableTasks.clear();
        }
        String details = detailsWidget.getText();
        Matcher matcher = TASK_DETAILS_PATTERN.matcher(details);
        if (!matcher.find()) return;

        int level = Integer.parseInt(matcher.group(1));
        String cargoLocation = matcher.group(2);
        String destination = matcher.group(3);
        String combat = matcher.group(4);
        String cargoType = matcher.group(5);
        int cargoAmount = Integer.parseInt(matcher.group(6));
        String description = matcher.group(7).trim();

        Port sourcePort = Port.fromDisplayName(cargoLocation);
        Port destPort = Port.fromDisplayName(destination);
        boolean hasCombat = !"None".equals(combat);

        if (sourcePort != null && destPort != null) {
            Set<PortTask> portTasks = availableTasksPerPort.computeIfAbsent(sourcePort, k -> new HashSet<>());
            if (portTasks.size() < MAX_TASKS_PER_PORT) {
                CourierTask task = new CourierTask(
                    UUID.randomUUID().toString(),
                    level,
                    sourcePort,
                    destPort,
                    hasCombat,
                    description,
                    cargoType,
                    cargoAmount,
                    client
                );
                portTasks.add(task);
            }
        }
    }

    public Set<PortTask> getAvailableTasks(Port port) {
        return availableTasksPerPort.getOrDefault(port, Collections.emptySet());
    }

    public void clearAvailableTasks(Port port) {
        availableTasksPerPort.remove(port);
    }
}
package com.github.dappermickie.sailing.tasks;

import com.github.dappermickie.sailing.Port;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TaskInteractionManager {
    private static final String TASK_BOARD_OPTION = "Inspect";
    private static final String LOAD_CARGO_OPTION = "Load-cargo";
    private static final String DELIVER_CARGO_OPTION = "Deliver-cargo";
    
    private final Client client;
    private final PortTaskHelper taskHelper;
    private final AvailableTaskManager availableTaskManager;

    @Inject
    public TaskInteractionManager(Client client, PortTaskHelper taskHelper, AvailableTaskManager availableTaskManager) {
        this.client = client;
        this.taskHelper = taskHelper;
        this.availableTaskManager = availableTaskManager;
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        String option = event.getMenuOption();
        String target = event.getMenuTarget();

        if (option.equals(TASK_BOARD_OPTION)) {
            handleTaskBoardClick();
        } else if (option.equals(LOAD_CARGO_OPTION)) {
            handleLoadCargoClick();
        } else if (option.equals(DELIVER_CARGO_OPTION)) {
            handleDeliverCargoClick();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getMessage().contains("You have loaded the cargo")) {
            handleCargoLoaded();
        } else if (event.getMessage().contains("You have delivered the cargo")) {
            handleCargoDelivered();
        } else if (event.getMessage().contains("You have arrived at")) {
            handlePortArrival();
        }
    }

    private void handleTaskBoardClick() {
        // Task board interactions are handled by AvailableTaskManager
    }

    private void handleLoadCargoClick() {
        Port currentPort = Port.getCurrentPort(client);
        if (currentPort == null) return;

        taskHelper.getActiveTasks().stream()
            .filter(task -> task.getState() == TaskState.ACCEPTED && 
                           task.getSourcePort() == currentPort)
            .forEach(task -> task.setState(TaskState.LOADING_CARGO));
    }

    private void handleDeliverCargoClick() {
        Port currentPort = Port.getCurrentPort(client);
        if (currentPort == null) return;

        taskHelper.getActiveTasks().stream()
            .filter(task -> task.getState() == TaskState.SAILING && 
                           task.getDestinationPort() == currentPort)
            .forEach(task -> task.setState(TaskState.DELIVERING));
    }

    private void handleCargoLoaded() {
        taskHelper.getActiveTasks().stream()
            .filter(task -> task.getState() == TaskState.LOADING_CARGO)
            .forEach(task -> task.setState(TaskState.SAILING));
    }

    private void handleCargoDelivered() {
        taskHelper.getActiveTasks().stream()
            .filter(task -> task.getState() == TaskState.DELIVERING)
            .forEach(task -> {
                task.incrementProgress();
                if (task.getState() == TaskState.COMPLETED) {
                    taskHelper.removeTask(task.getId().toString());
                }
            });
    }

    private void handlePortArrival() {
        Port currentPort = Port.getCurrentPort(client);
        if (currentPort == null) return;

        taskHelper.getActiveTasks().stream()
            .filter(task -> task.getState() == TaskState.SAILING && 
                           task.getDestinationPort() == currentPort)
            .forEach(task -> task.setState(TaskState.DELIVERING));
    }
} 
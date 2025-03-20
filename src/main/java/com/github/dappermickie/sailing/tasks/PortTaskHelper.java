package com.github.dappermickie.sailing.tasks;

import com.github.dappermickie.sailing.Port;
import com.github.dappermickie.sailing.config.PortTaskConfig;
import com.google.inject.Singleton;

import lombok.Getter;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class PortTaskHelper {
    @Getter
    private final List<PortTask> activeTasks = new ArrayList<>();
    private final ConfigManager configManager;

    @Inject
    public PortTaskHelper(ConfigManager configManager) {
        this.configManager = configManager;
        loadTasks();
    }

    private void loadTasks() {
        String tasksJson = configManager.getConfiguration(PortTaskConfig.GROUP, "activeTasks");
        activeTasks.addAll(PortTaskConfig.TaskStorage.loadTasks(tasksJson));
    }

    private void saveTasks() {
        String tasksJson = PortTaskConfig.TaskStorage.saveTasks(activeTasks);
        configManager.setConfiguration(PortTaskConfig.GROUP, "activeTasks", tasksJson);
    }

    /**
     * Get all available tasks that start from a specific port
     * @param currentPort The port to get tasks from
     * @return List of tasks that start at the given port
     */
    public List<PortTask> getTasksFromPort(Port currentPort) {
        return activeTasks.stream()
                .filter(task -> task.getSourcePort() == currentPort)
                .collect(Collectors.toList());
    }

    /**
     * Get all tasks that can be completed from the current port
     * @param currentPort The port to check for completable tasks
     * @return List of tasks that can be completed at the given port
     */
    public List<PortTask> getCompletableTasksAtPort(Port currentPort) {
        return activeTasks.stream()
                .filter(task -> task.getDestinationPort() == currentPort)
                .collect(Collectors.toList());
    }

    /**
     * Add a new task to the active tasks list
     * @param task The task to add
     */
    public void addTask(PortTask task) {
        activeTasks.add(task);
        saveTasks();
    }

    /**
     * Remove a completed task
     * @param taskId The ID of the task to remove
     */
    public void removeTask(String taskId) {
        activeTasks.removeIf(task -> task.getId().toString().equals(taskId));
        saveTasks();
    }

    /**
     * Get the number of active tasks
     * @return Number of active tasks
     */
    public int getActiveTaskCount() {
        return activeTasks.size();
    }

    /**
     * Check if there are any tasks that can be started from the current port
     * @param currentPort The port to check
     * @return true if there are available tasks at the port
     */
    public boolean hasAvailableTasksAtPort(Port currentPort) {
        return !getTasksFromPort(currentPort).isEmpty();
    }

    /**
     * Check if there are any tasks that can be completed at the current port
     * @param currentPort The port to check
     * @return true if there are completable tasks at the port
     */
    public boolean hasCompletableTasksAtPort(Port currentPort) {
        return !getCompletableTasksAtPort(currentPort).isEmpty();
    }
} 
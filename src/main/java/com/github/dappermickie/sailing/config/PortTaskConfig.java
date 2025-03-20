package com.github.dappermickie.sailing.config;

import com.github.dappermickie.sailing.Port;
import com.github.dappermickie.sailing.tasks.CourierTask;
import com.github.dappermickie.sailing.tasks.PortTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@ConfigGroup("porttasks")
public interface PortTaskConfig extends Config {
    String GROUP = "porttasks";

    @ConfigSection(
        name = "Active Tasks",
        description = "Currently active port tasks",
        position = 0
    )
    String activeTasksSection = "activeTasks";

    @ConfigItem(
        keyName = "activeTasks",
        name = "Active Tasks",
        description = "List of currently active port tasks",
        section = activeTasksSection,
        hidden = true
    )
    default String getActiveTasks() {
        return "[]";
    }

    void setActiveTasks(String tasks);

    class TaskStorage {
        private static final Gson gson = new Gson();
        private static final Type taskListType = new TypeToken<ArrayList<CourierTask>>(){}.getType();

        public static List<PortTask> loadTasks(String json) {
            if (json == null || json.isEmpty() || json.equals("[]")) {
                return new ArrayList<>();
            }
            return gson.fromJson(json, taskListType);
        }

        public static String saveTasks(List<PortTask> tasks) {
            return gson.toJson(tasks);
        }
    }
} 
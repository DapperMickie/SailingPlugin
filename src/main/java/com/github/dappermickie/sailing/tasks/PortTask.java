package com.github.dappermickie.sailing.tasks;

import com.github.dappermickie.sailing.Port;
import java.util.UUID;

public class PortTask {
    private final UUID id;
    private final String description;
    private TaskState state;
    private Port sourcePort;
    private Port destinationPort;
    private int cargoDelivered;
    private int cargoAmount;
    private String cargoType;
    private boolean hasCombat;

    public PortTask(UUID id, String description) {
        this.id = id;
        this.description = description;
        this.state = TaskState.NOT_STARTED;
        this.cargoDelivered = 0;
        this.cargoAmount = 1;
        this.cargoType = "Unknown";
        this.hasCombat = false;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public Port getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(Port sourcePort) {
        this.sourcePort = sourcePort;
    }

    public Port getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(Port destinationPort) {
        this.destinationPort = destinationPort;
    }

    public int getCargoDelivered() {
        return cargoDelivered;
    }

    public void setCargoDelivered(int cargoDelivered) {
        this.cargoDelivered = cargoDelivered;
        if (cargoDelivered >= cargoAmount) {
            state = TaskState.COMPLETED;
        }
    }

    public int getCargoAmount() {
        return cargoAmount;
    }

    public void setCargoAmount(int cargoAmount) {
        this.cargoAmount = cargoAmount;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public boolean hasCombat() {
        return hasCombat;
    }

    public void setHasCombat(boolean hasCombat) {
        this.hasCombat = hasCombat;
    }

    public void incrementProgress() {
        cargoDelivered++;
        if (cargoDelivered >= cargoAmount) {
            state = TaskState.COMPLETED;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortTask task = (PortTask) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
} 
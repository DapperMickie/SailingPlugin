package com.github.dappermickie.sailing.tasks;

import com.github.dappermickie.sailing.Port;
import lombok.Getter;
import net.runelite.api.Client;
import java.util.UUID;

@Getter
public class CourierTask extends PortTask {
    public CourierTask(String taskId, int level, Port cargoLocation, Port destination,
                      boolean hasCombat, String description, String cargoType, int cargoAmount,
                      Client client) {
        super(UUID.fromString(taskId), description);
        setSourcePort(cargoLocation);
        setDestinationPort(destination);
        setHasCombat(hasCombat);
        setCargoType(cargoType);
        setCargoAmount(cargoAmount);
    }
} 
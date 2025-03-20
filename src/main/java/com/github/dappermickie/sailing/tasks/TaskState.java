package com.github.dappermickie.sailing.tasks;

public enum TaskState {
    NOT_STARTED,           // Task is available but not accepted
    ACCEPTED,             // Task is accepted but cargo not picked up
    LOADING_CARGO,        // Cargo is being loaded onto the ship
    SAILING,              // Ship is sailing
    DELIVERING,           // Cargo is being delivered to the ledger
    COMPLETED             // All cargo for this task has been delivered
} 
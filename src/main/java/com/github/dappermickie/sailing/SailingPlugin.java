package com.github.dappermickie.sailing;

import com.github.dappermickie.sailing.commands.PortLocationCommand;
import com.github.dappermickie.sailing.tasks.TaskHighlightManager;
import com.github.dappermickie.sailing.tasks.TaskInteractionManager;
import com.github.dappermickie.sailing.tasks.PortTaskHelper;
import com.github.dappermickie.sailing.tasks.AvailableTaskManager;
import com.github.dappermickie.sailing.ui.TaskOverlay;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Sailing",
	description = "Helps with sailing tasks and navigation",
	tags = {"sailing", "transport", "minigame"}
)
public class SailingPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SailingConfig config;

	@Inject
	private PortLocationCommand portLocationCommand;

	@Inject
	private TaskOverlay taskOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private TaskHighlightManager taskHighlightManager;

	@Inject
	private TaskInteractionManager taskInteractionManager;

	@Inject
	private PortTaskHelper portTaskHelper;

	@Inject
	private AvailableTaskManager availableTaskManager;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Sailing plugin started!");
		portLocationCommand.registerCommands(this);
		overlayManager.add(taskOverlay);
		eventBus.register(taskInteractionManager);
		eventBus.register(availableTaskManager);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Sailing plugin stopped!");
		overlayManager.remove(taskOverlay);
		eventBus.unregister(taskInteractionManager);
		eventBus.unregister(availableTaskManager);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Sailing says " + config.greeting(), null);
		}
	}

	@Provides
	SailingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SailingConfig.class);
	}
}

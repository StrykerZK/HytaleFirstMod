package dev.hytalemodding.commands;


import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class TestCommand extends AbstractPlayerCommand {

    public TestCommand() {
        super("test", "Super test command!");
    }

    private final RequiredArg<String> messageArg = this.withRequiredArg("argument_name", "Argument Description", ArgTypes.STRING);

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType()); // also a component
        UUIDComponent component = store.getComponent(ref, UUIDComponent.getComponentType());
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        player.sendMessage(Message.raw("Player#getUuid() : " + player.getUuid()));
        player.sendMessage(Message.raw("UUIDComponent : " + component.getUuid()));
        player.sendMessage(Message.raw("equal : " + player.getUuid().equals(component.getUuid())));
        player.sendMessage(Message.raw("Transform : " + transform.getPosition()));

        // Get the text the player typed
        String playerMessage = messageArg.get(commandContext);
        player.sendMessage(Message.raw("Message Argument: " + playerMessage));
    }

}

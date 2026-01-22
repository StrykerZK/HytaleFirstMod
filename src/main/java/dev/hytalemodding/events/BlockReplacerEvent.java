package dev.hytalemodding.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.MouseButtonState;
import com.hypixel.hytale.protocol.MouseButtonType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseButtonEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.hytalemodding.ExamplePlugin;

public class BlockReplacerEvent {

    public static void onPlayerClick(PlayerMouseButtonEvent event) {
        // DEBUG: Step 0 - Did the event fire at all?
        ExamplePlugin.LOGGER.atInfo().log("DEBUG: Event fired! Button: " + event.getMouseButton().mouseButtonType + ", State: " + event.getMouseButton().state);

        // 1. Input Check
        if (event.getMouseButton().mouseButtonType != MouseButtonType.Left
                || event.getMouseButton().state != MouseButtonState.Pressed) {
            // Commenting this out to reduce spam, but un-comment if you suspect button mapping issues
            // ExamplePlugin.LOGGER.atInfo().log("DEBUG: Wrong button or state.");
            return;
        }

        // 2. Target Check
        Vector3i targetPos = event.getTargetBlock();
        if (targetPos == null) {
            ExamplePlugin.LOGGER.atInfo().log("DEBUG: targetPos is null (Clicking air?)");
            return;
        }
        ExamplePlugin.LOGGER.atInfo().log("DEBUG: Target passed: " + targetPos);

        // 3. Sneak Check
        Ref<EntityStore> entityRef = event.getPlayerRefComponent().getReference();
        if (entityRef == null) {
            ExamplePlugin.LOGGER.atInfo().log("DEBUG: EntityRef is null");
            return;
        }

        Store<EntityStore> store = entityRef.getStore();
        MovementStatesComponent moveState = store.getComponent(entityRef, MovementStatesComponent.getComponentType());

        if (moveState == null) {
            ExamplePlugin.LOGGER.atInfo().log("DEBUG: MoveState component is null on this entity.");
            return;
        }

        // Print the actual value so we know if 'crouching' is the right field
        boolean isCrouching = moveState.getMovementStates().crouching;
        ExamplePlugin.LOGGER.atInfo().log("DEBUG: Crouching state: " + isCrouching);

        if (!isCrouching) {
            return; // Stops here if not sneaking
        }

        // 4. Item Check
        Item item = event.getItemInHand();
        if (item == null) {
            ExamplePlugin.LOGGER.atInfo().log("DEBUG: Item is null (Empty hand?)");
            return;
        }

        // Log what is actually in the hand
        ExamplePlugin.LOGGER.atInfo().log("DEBUG: Item in hand ID: " + item.getId());

        if (!item.hasBlockType()) {
            ExamplePlugin.LOGGER.atInfo().log("DEBUG: Item does not have a block type.");
            return;
        }

        String blockKey = item.getBlockId();
        if (blockKey == null || blockKey.equals("Empty")) {
            ExamplePlugin.LOGGER.atInfo().log("DEBUG: Block key is null or Empty.");
            return;
        }

        // 5. Lookup & Swap
        BlockType newBlock = BlockType.fromString(blockKey);
        if (newBlock == null) {
            ExamplePlugin.LOGGER.atWarning().log("DEBUG: Could not find block definition for key: " + blockKey);
            return;
        }

        ExamplePlugin.LOGGER.atInfo().log("DEBUG: Replacer Active! Swapping...");

        event.setCancelled(true);

        World world = store.getExternalData().getWorld();
        if (world != null) {
            // Note: Don't use 'assert' in production code; use a real check
            world.setBlock(targetPos.getX(), targetPos.getY(), targetPos.getZ(), newBlock.getId());
            ExamplePlugin.LOGGER.atInfo().log("DEBUG: World setBlock called.");
        } else {
            ExamplePlugin.LOGGER.atWarning().log("DEBUG: World object was null!");
        }
    }
}
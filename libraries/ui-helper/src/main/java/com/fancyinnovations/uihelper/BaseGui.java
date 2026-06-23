package com.fancyinnovations.uihelper;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import org.joml.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Base class for creating GUI pages with simplified API.
 *
 * Example usage:
 * public class MyGui extends BaseGui&gt;MyGuiData> {
 *
 *     public MyGui(PlayerRef playerRef) {
 *         super(playerRef, MyGuiData.CODEC);
 *     }
 *
 *     {@literal @}Override
 *     protected void buildUI(UIBuilder ui, GuiContext ctx) {
 *         ui.page("Pages/MyPlugin/MyPage.ui")
 *           .text("#Title", "My Page")
 *           .onClick("#SaveButton", "Save")
 *           .list("#Items", getItems(), "Pages/MyPlugin/ItemEntry.ui", (item, row) -> {
 *               row.text("#Name", item.getName())
 *                  .onClick("#Edit", "Edit:" + item.getId());
 *           });
 *     }
 *
 *     {@literal @}Override
 *     protected void onAction(UIAction action, MyGuiData data, GuiContext ctx) {
 *         if (action.is("Save")) {
 *             // handle save
 *         } else if (action.is("Edit")) {
 *             String id = action.arg();
 *             // handle edit
 *         }
 *     }
 * }
 *
 * @param <T> The GUI data type
 */
public abstract class BaseGui<T extends BaseGuiData> extends InteractiveCustomUIPage<T> {

    public BaseGui(@Nonnull PlayerRef playerRef, @Nonnull BuilderCodec<T> codec) {
        super(playerRef, CustomPageLifetime.CanDismiss, codec);
    }

    public BaseGui(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime, @Nonnull BuilderCodec<T> codec) {
        super(playerRef, lifetime, codec);
    }

    @Override
    public final void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        UIBuilder ui = new UIBuilder(uiCommandBuilder, uiEventBuilder);
        GuiContext ctx = new GuiContext(ref, store, this);
        buildUI(ui, ctx);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull T data) {
        super.handleDataEvent(ref, store, data);

        GuiContext ctx = new GuiContext(ref, store, this);

        // Handle button/action
        if (data.hasButton()) {
            UIAction action = data.action();
            onAction(action, data, ctx);
        } else {
            // No button = input event (ValueChanged from text fields, checkboxes, sliders, etc.)
            // The data object has custom fields populated via the codec
            onInput(data.input != null ? data.input : "", data, ctx);
        }
    }

    /**
     * Builds the UI using the fluent UIBuilder.
     * Override this to define your UI.
     */
    protected abstract void buildUI(@Nonnull UIBuilder ui, @Nonnull GuiContext ctx);

    /**
     * Handles button actions.
     * Override this to handle button clicks.
     */
    protected void onAction(@Nonnull UIAction action, @Nonnull T data, @Nonnull GuiContext ctx) {
        // Override in subclass
    }

    /**
     * Handles input field changes.
     * Override this to handle text input.
     */
    protected void onInput(@Nonnull String value, @Nonnull T data, @Nonnull GuiContext ctx) {
        // Override in subclass
    }

    /**
     * Refreshes the entire UI by rebuilding it.
     */
    protected void refresh(@Nonnull GuiContext ctx) {
        UIBuilder ui = new UIBuilder();
        buildUI(ui, ctx);
        this.sendUpdate(ui.getCommands(), ui.getEvents(), false);
    }

    /**
     * Sends a partial UI update.
     */
    protected void sendUpdate(@Nonnull UIBuilder ui) {
        this.sendUpdate(ui.getCommands(), ui.getEvents(), false);
    }

    /**
     * Context object providing access to player, store, and utilities.
     */
    public static class GuiContext {
        private final Ref<EntityStore> ref;
        private final Store<EntityStore> store;
        private final BaseGui<?> gui;

        GuiContext(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull BaseGui<?> gui) {
            this.ref = ref;
            this.store = store;
            this.gui = gui;
        }

        /**
         * Gets the entity reference.
         */
        @Nonnull
        public Ref<EntityStore> ref() {
            return ref;
        }

        /**
         * Gets the entity store.
         */
        @Nonnull
        public Store<EntityStore> store() {
            return store;
        }

        /**
         * Gets the player reference.
         */
        @Nonnull
        public PlayerRef playerRef() {
            return store.getComponent(ref, PlayerRef.getComponentType());
        }

        /**
         * Gets the player component.
         */
        @Nonnull
        public Player player() {
            return store.getComponent(ref, Player.getComponentType());
        }

        /**
         * Gets the player's current position.
         */
        @Nullable
        public Vector3d position() {
            TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
            return transform != null ? transform.getPosition() : null;
        }

        /**
         * Gets the player's current world.
         */
        @Nullable
        public World world() {
            return player().getWorld();
        }

        /**
         * Sends a message to the player.
         */
        public void message(@Nonnull String text) {
            playerRef().sendMessage(Message.raw(text));
        }

        /**
         * Sends a formatted message to the player.
         */
        public void message(@Nonnull Message message) {
            playerRef().sendMessage(message);
        }

        /**
         * Opens another GUI page.
         */
        public <D extends BaseGuiData> void open(@Nonnull BaseGui<D> newPage) {
            player().getPageManager().openCustomPage(ref, store, newPage);
        }

        /**
         * Opens a custom UI page (for non-BaseGui pages).
         */
        public void openPage(@Nonnull InteractiveCustomUIPage<?> page) {
            player().getPageManager().openCustomPage(ref, store, page);
        }

        /**
         * Teleports the player to a position in a world.
         */
        public void teleport(@Nonnull String worldName, @Nonnull Vector3d position) {
            World world = Universe.get().getWorld(worldName);
            if (world != null) {
                store.addComponent(ref, Teleport.getComponentType(), new Teleport(world, position, new Vector3f()));
            }
        }

        /**
         * Teleports the player to a position in the current world.
         */
        public void teleport(@Nonnull Vector3d position) {
            World world = world();
            if (world != null) {
                store.addComponent(ref, Teleport.getComponentType(), new Teleport(world, position, new Vector3f()));
            }
        }

        /**
         * Closes the current GUI page by setting the page to None.
         */
        public void close() {
            player().getPageManager().setPage(ref, store, Page.None);
        }
    }
}

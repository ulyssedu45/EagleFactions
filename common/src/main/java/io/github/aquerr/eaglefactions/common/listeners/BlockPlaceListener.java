package io.github.aquerr.eaglefactions.common.listeners;

import io.github.aquerr.eaglefactions.api.EagleFactions;
import io.github.aquerr.eaglefactions.api.config.ProtectionConfig;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Piston;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.FallingBlock;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.EventContext;

public class BlockPlaceListener extends AbstractListener
{
    private final ProtectionConfig protectionConfig;

    public BlockPlaceListener(EagleFactions plugin)
    {
        super(plugin);
        this.protectionConfig = plugin.getConfiguration().getProtectionConfig();
    }

    @Listener(order = Order.EARLY)
    public void onBlockPlace(ChangeBlockEvent.Place event)
    {
        final Object source = event.getSource();
//        final EventContext eventContext = event.getContext();
        if(source instanceof Piston)
            return;

        User user = null;
        if(event.getCause().containsType(Player.class))
        {
            user = event.getCause().first(Player.class).get();
        }
        else if(event.getCause().containsType(User.class))
        {
            user = event.getCause().first(User.class).get();
        }

        if(user instanceof Player)
        {
            final Player player = (Player) user;

            //Requested for sand/tnt cannons by Turner
            if(source instanceof FallingBlock && this.protectionConfig.shouldAllowExplosionsByOtherPlayersInClaims())
                return;

            for (Transaction<BlockSnapshot> transaction : event.getTransactions())
            {
                if(!super.getPlugin().getProtectionManager().canPlace(transaction.getFinal().getLocation().get(), player, true))
                    event.setCancelled(true);
            }
        }
        else if(user == null)
        {
//            final boolean pistonExtend = event.getContext().containsKey(EventContextKeys.PISTON_EXTEND);
//            final boolean pistonRetract = event.getContext().containsKey(EventContextKeys.PISTON_RETRACT);
//
//            final List<BlockSnapshot> sourceBlockSnapshots = event.getTransactions().stream().map(Transaction::getOriginal).collect(Collectors.toList());
//            if(pistonExtend || pistonRetract)
//            {
//                final BlockSnapshot blockSnapshot = sourceBlockSnapshots.get(sourceBlockSnapshots.size() - 1);
//                final Direction direction = blockSnapshot.get(Keys.DIRECTION).get();
//                final Location<World> directionLocation = location.getBlockRelative(direction);
//                sourceLocations.add(directionLocation);
//
//                if (user == null)
//                {
//                    user = event.getContext().get(EventContextKeys.OWNER).orElse(null);
//                }
//            }

            for (Transaction<BlockSnapshot> transaction : event.getTransactions())
            {
                //Block fire from thunder
                if(transaction.getFinal().getState().getType() == BlockTypes.FIRE && super.getPlugin().getFactionLogic().getFactionByChunk(transaction.getFinal().getWorldUniqueId(), transaction.getFinal().getLocation().get().getChunkPosition()).isPresent())
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}

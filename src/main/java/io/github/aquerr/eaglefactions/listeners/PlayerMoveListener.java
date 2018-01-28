package io.github.aquerr.eaglefactions.listeners;

import com.flowpowered.math.vector.Vector3i;
import io.github.aquerr.eaglefactions.EagleFactions;
import io.github.aquerr.eaglefactions.PluginInfo;
import io.github.aquerr.eaglefactions.logic.FactionLogic;
import io.github.aquerr.eaglefactions.logic.MainLogic;
import io.github.aquerr.eaglefactions.services.PlayerService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerMoveListener
{
    @Listener
    public void onPlayerMove(MoveEntityEvent event, @Root Player player)
    {

        //Check if player changed chunk.
        World world = player.getWorld();
        Location lastLocation = new Location(world, PlayerService.getPlayerChunkPosition(player.getUniqueId()));
        Location newLocation = player.getLocation();

        if(!lastLocation.getChunkPosition().toString().equals(player.getLocation().getChunkPosition().toString()))
        {
            //World world = player.getWorld();
            Vector3i oldChunk = lastLocation.getChunkPosition();
            Vector3i newChunk = newLocation.getChunkPosition();

            //Inform a player about entering faction's land.
            if(!FactionLogic.getFactionNameByChunk(world.getUniqueId(), oldChunk).equals(FactionLogic.getFactionNameByChunk(world.getUniqueId(), newChunk)))
            {
                if(!FactionLogic.getFactionNameByChunk(world.getUniqueId(), newChunk).equals("SafeZone") && !FactionLogic.getFactionNameByChunk(world.getUniqueId(),newChunk).equals("WarZone") && !FactionLogic.getFactionNameByChunk(world.getUniqueId(), newChunk).equals(""))
                {
                    if(!EagleFactions.AdminList.contains(player.getUniqueId()))
                    {
                        if(!FactionLogic.hasOnlinePlayers(FactionLogic.getFactionNameByChunk(world.getUniqueId(), newChunk)) && MainLogic.getBlockEnteringFactions())
                        {
                            //Teleport player back if all entering faction's players are offline.
                            player.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "You can't enter this faction! None of this faction's players are online!"));
                            player.setLocation(new Location<World>(world, lastLocation.getBlockPosition()));
                            return;
                        }
                    }
                }

                else if(FactionLogic.getFactionNameByChunk(world.getUniqueId(), oldChunk).equals("WarZone") && FactionLogic.getFactionNameByChunk(world.getUniqueId(), newChunk).equals("SafeZone"))
                {
                    if(!EagleFactions.AdminList.contains(player.getUniqueId()) && MainLogic.shouldBlockSafeZoneFromWarZone())
                    {
                        //Block player before going to SafeZone from WarZone
                        player.setLocation(new Location<World>(world, lastLocation.getBlockPosition()));
                        player.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "You can't enter SafeZone when you are in WarZone"));
                        return;
                    }
                }

                String factionName = FactionLogic.getFactionNameByChunk(world.getUniqueId(), newChunk);
                if(factionName == "") factionName = "Wilderness";

                //TODO: Show respective colors for enemy faction, alliance & neutral.
                Text information = Text.builder()
                        .append(Text.of("You have entered faction ", TextColors.GOLD, factionName))
                        .build();

                player.sendMessage(ChatTypes.ACTION_BAR, information);
            }
            //Check if player has tuned on AutoClaim
            if(EagleFactions.AutoClaimList.contains(player.getUniqueId()))
            {
                Sponge.getCommandManager().process(player, "f claim");
            }

            //Check if player has turned on AutoMap
            if(EagleFactions.AutoMapList.contains(player.getUniqueId()))
            {
                Sponge.getCommandManager().process(player, "f map");
            }

        }

        //TODO:Bad approach... change it so that the player's position does not set every time player moves. This will increase performance.
        //Set new player chunk location.
        PlayerService.setPlayerChunkPosition(player.getUniqueId(), player.getLocation().getBlockPosition());
        return;
    }
}

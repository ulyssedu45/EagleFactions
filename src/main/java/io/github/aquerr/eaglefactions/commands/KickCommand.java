package io.github.aquerr.eaglefactions.commands;

import io.github.aquerr.eaglefactions.EagleFactions;
import io.github.aquerr.eaglefactions.PluginInfo;
import io.github.aquerr.eaglefactions.entities.Faction;
import io.github.aquerr.eaglefactions.entities.IFactionPlayer;
import io.github.aquerr.eaglefactions.logic.PluginMessages;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class KickCommand extends AbstractCommand implements CommandExecutor
{
    public KickCommand(EagleFactions plugin)
    {
        super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException
    {
        Optional<IFactionPlayer> optionalSelectedPlayer = context.<IFactionPlayer>getOne(Text.of("player"));

        if (optionalSelectedPlayer.isPresent())
        {
            if(source instanceof Player)
            {
                Player player = (Player)source;
                Optional<Faction> optionalPlayerFaction = getPlugin().getFactionLogic().getFactionByPlayerUUID(player.getUniqueId());

                if(optionalPlayerFaction.isPresent())
                {
                    Faction playerFaction = optionalPlayerFaction.get();
                    if(playerFaction.getLeader().equals(player.getUniqueId()) || playerFaction.getOfficers().contains(player.getUniqueId()))
                    {
                        IFactionPlayer selectedPlayer = optionalSelectedPlayer.get();
                        Optional<Faction> optionalSelectedPlayerFaction = getPlugin().getFactionLogic().getFactionByPlayerUUID(selectedPlayer.getUniqueId());

                        if(optionalSelectedPlayerFaction.isPresent() && optionalSelectedPlayerFaction.get().getName().equals(playerFaction.getName()))
                        {
                            if(playerFaction.getLeader().equals(selectedPlayer.getUniqueId()) || (playerFaction.getOfficers().contains(player.getUniqueId()) && playerFaction.getOfficers().contains(selectedPlayer.getUniqueId())))
                            {
                                source.sendMessage(Text.of(PluginInfo.ERROR_PREFIX, TextColors.RED, PluginMessages.YOU_CANT_KICK_THIS_PLAYER));
                            }
                            else
                            {
                                getPlugin().getFactionLogic().kickPlayer(selectedPlayer.getUniqueId(), playerFaction.getName());

                                //TODO: Add listener that will inform players in a faction that someone has left their faction.

                                source.sendMessage(Text.of(PluginInfo.PLUGIN_PREFIX, TextColors.GREEN, PluginMessages.YOU_KICKED + " ", TextColors.GOLD, selectedPlayer.getName(), TextColors.GREEN, " " + PluginMessages.FROM_THE_FACTION));

                                if(getPlugin().getPlayerManager().isPlayerOnline(selectedPlayer.getUniqueId()))
                                {
                                    getPlugin().getPlayerManager().getPlayer(selectedPlayer.getUniqueId()).get().sendMessage(Text.of(PluginInfo.PLUGIN_PREFIX, PluginMessages.YOU_WERE_KICKED_FROM_THE_FACTION));
                                }

                                EagleFactions.AutoClaimList.remove(selectedPlayer.getUniqueId());

                                return CommandResult.success();
                            }
                        }
                        else
                        {
                            source.sendMessage(Text.of(PluginInfo.ERROR_PREFIX, TextColors.RED, PluginMessages.THIS_PLAYER_IS_NOT_IN_YOUR_FACTION));
                        }
                    }
                    else
                    {
                        source.sendMessage(Text.of(PluginInfo.ERROR_PREFIX, TextColors.RED, PluginMessages.YOU_MUST_BE_THE_FACTIONS_LEADER_OR_OFFICER_TO_DO_THIS));
                    }
                }
                else
                {
                    source.sendMessage(Text.of(PluginInfo.ERROR_PREFIX, TextColors.RED, PluginMessages.YOU_MUST_BE_IN_FACTION_IN_ORDER_TO_USE_THIS_COMMAND));
                }
            }
            else
            {
                source.sendMessage (Text.of (PluginInfo.ERROR_PREFIX, TextColors.RED, PluginMessages.ONLY_IN_GAME_PLAYERS_CAN_USE_THIS_COMMAND));
            }
        }
        else
        {
            source.sendMessage(Text.of(PluginInfo.ERROR_PREFIX, TextColors.RED, PluginMessages.WRONG_COMMAND_ARGUMENTS));
            source.sendMessage(Text.of(TextColors.RED, PluginMessages.USAGE + " /f kick <player>"));
        }

        return CommandResult.success();
    }
}

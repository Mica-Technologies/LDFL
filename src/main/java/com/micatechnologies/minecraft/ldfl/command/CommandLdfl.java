package com.micatechnologies.minecraft.ldfl.command;

import com.micatechnologies.minecraft.ldfl.hero.HeroCapability;
import com.micatechnologies.minecraft.ldfl.hero.HeroData;
import com.micatechnologies.minecraft.ldfl.hero.core.HeroState;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

/**
 * {@code /ldfl} operator command, for testing heroes and for admins.
 *
 * <ul>
 *   <li>{@code /ldfl ult [player]}: fill the ultimate meter of the player's active hero.</li>
 *   <li>{@code /ldfl reset [player]}: clear the active hero's cooldowns.</li>
 * </ul>
 */
public class CommandLdfl extends CommandBase {

    private static final String USAGE = "/ldfl <ult|reset> [player]";

    @Override
    public String getName() {
        return "ldfl";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return USAGE;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
        throws CommandException {
        if (args.length < 1 || args.length > 2) {
            throw new WrongUsageException(USAGE);
        }
        EntityPlayerMP player = args.length == 2
            ? getPlayer(server, sender, args[1]) : getCommandSenderAsPlayer(sender);
        HeroData data = HeroCapability.get(player);
        HeroState state = data == null ? null : data.state();
        if (state == null || state.hero() == null) {
            throw new CommandException(player.getName() + " is not a hero (hold a signature item).");
        }
        switch (args[0]) {
            case "ult":
                state.addUltPoints(state.hero().ultCost());
                break;
            case "reset":
                state.applySync(new int[state.cooldownSnapshot().length], state.ultCharge());
                break;
            default:
                throw new WrongUsageException(USAGE);
        }
        data.dirty = true;
        sender.sendMessage(new TextComponentString(
            "[LDFL] " + args[0] + " applied to " + player.getName() + "."));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
                                          String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "ult", "reset");
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}

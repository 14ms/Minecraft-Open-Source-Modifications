/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package us.myles.ViaVersion.commands;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.command.ViaCommandSender;
import us.myles.ViaVersion.api.command.ViaSubCommand;
import us.myles.ViaVersion.api.command.ViaVersionCommand;
import us.myles.ViaVersion.commands.defaultsubs.AutoTeamSubCmd;
import us.myles.ViaVersion.commands.defaultsubs.DebugSubCmd;
import us.myles.ViaVersion.commands.defaultsubs.DisplayLeaksSubCmd;
import us.myles.ViaVersion.commands.defaultsubs.DontBugMeSubCmd;
import us.myles.ViaVersion.commands.defaultsubs.DumpSubCmd;
import us.myles.ViaVersion.commands.defaultsubs.HelpSubCmd;
import us.myles.ViaVersion.commands.defaultsubs.ListSubCmd;
import us.myles.ViaVersion.commands.defaultsubs.PPSSubCmd;
import us.myles.ViaVersion.commands.defaultsubs.ReloadSubCmd;
import us.myles.viaversion.libs.bungeecordchat.api.ChatColor;

public abstract class ViaCommandHandler
implements ViaVersionCommand {
    private final Map<String, ViaSubCommand> commandMap = new HashMap<String, ViaSubCommand>();

    public ViaCommandHandler() {
        try {
            this.registerDefaults();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void registerSubCommand(ViaSubCommand command) throws Exception {
        Preconditions.checkArgument((boolean)command.name().matches("^[a-z0-9_-]{3,15}$"), (Object)(command.name() + " is not a valid sub-command name."));
        if (this.hasSubCommand(command.name())) {
            throw new Exception("ViaSubCommand " + command.name() + " does already exists!");
        }
        this.commandMap.put(command.name().toLowerCase(Locale.ROOT), command);
    }

    @Override
    public boolean hasSubCommand(String name) {
        return this.commandMap.containsKey(name.toLowerCase(Locale.ROOT));
    }

    @Override
    public ViaSubCommand getSubCommand(String name) {
        return this.commandMap.get(name.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean onCommand(ViaCommandSender sender, String[] args) {
        if (args.length == 0) {
            this.showHelp(sender);
            return false;
        }
        if (!this.hasSubCommand(args[0])) {
            sender.sendMessage(ViaCommandHandler.color("&cThis command does not exist."));
            this.showHelp(sender);
            return false;
        }
        ViaSubCommand handler = this.getSubCommand(args[0]);
        if (!this.hasPermission(sender, handler.permission())) {
            sender.sendMessage(ViaCommandHandler.color("&cYou are not allowed to use this command!"));
            return false;
        }
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        boolean result = handler.execute(sender, subArgs);
        if (!result) {
            sender.sendMessage("Usage: /viaversion " + handler.usage());
        }
        return result;
    }

    @Override
    public List<String> onTabComplete(ViaCommandSender sender, String[] args) {
        Set<ViaSubCommand> allowed = this.calculateAllowedCommands(sender);
        ArrayList<String> output = new ArrayList<String>();
        if (args.length == 1) {
            if (!args[0].isEmpty()) {
                for (ViaSubCommand sub : allowed) {
                    if (!sub.name().toLowerCase().startsWith(args[0].toLowerCase(Locale.ROOT))) continue;
                    output.add(sub.name());
                }
            } else {
                for (ViaSubCommand sub : allowed) {
                    output.add(sub.name());
                }
            }
        } else if (args.length >= 2 && this.getSubCommand(args[0]) != null) {
            ViaSubCommand sub = this.getSubCommand(args[0]);
            if (!allowed.contains(sub)) {
                return output;
            }
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            List<String> tab = sub.onTabComplete(sender, subArgs);
            Collections.sort(tab);
            return tab;
        }
        return output;
    }

    public void showHelp(ViaCommandSender sender) {
        Set<ViaSubCommand> allowed = this.calculateAllowedCommands(sender);
        if (allowed.isEmpty()) {
            sender.sendMessage(ViaCommandHandler.color("&cYou are not allowed to use these commands!"));
            return;
        }
        sender.sendMessage(ViaCommandHandler.color("&aViaVersion &c" + Via.getPlatform().getPluginVersion()));
        sender.sendMessage(ViaCommandHandler.color("&6Commands:"));
        for (ViaSubCommand cmd : allowed) {
            sender.sendMessage(ViaCommandHandler.color(String.format("&2/viaversion %s &7- &6%s", cmd.usage(), cmd.description())));
        }
        allowed.clear();
    }

    private Set<ViaSubCommand> calculateAllowedCommands(ViaCommandSender sender) {
        HashSet<ViaSubCommand> cmds = new HashSet<ViaSubCommand>();
        for (ViaSubCommand sub : this.commandMap.values()) {
            if (!this.hasPermission(sender, sub.permission())) continue;
            cmds.add(sub);
        }
        return cmds;
    }

    private boolean hasPermission(ViaCommandSender sender, String permission) {
        return permission == null || sender.hasPermission(permission);
    }

    private void registerDefaults() throws Exception {
        this.registerSubCommand(new ListSubCmd());
        this.registerSubCommand(new PPSSubCmd());
        this.registerSubCommand(new DebugSubCmd());
        this.registerSubCommand(new DumpSubCmd());
        this.registerSubCommand(new DisplayLeaksSubCmd());
        this.registerSubCommand(new DontBugMeSubCmd());
        this.registerSubCommand(new AutoTeamSubCmd());
        this.registerSubCommand(new HelpSubCmd());
        this.registerSubCommand(new ReloadSubCmd());
    }

    public static String color(String string) {
        try {
            string = ChatColor.translateAlternateColorCodes('&', string);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return string;
    }

    public static void sendMessage(ViaCommandSender sender, String message, Object ... args) {
        sender.sendMessage(ViaCommandHandler.color(args == null ? message : String.format(message, args)));
    }
}


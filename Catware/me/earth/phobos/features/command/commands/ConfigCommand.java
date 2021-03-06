package me.earth.phobos.features.command.commands;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;

public class ConfigCommand extends Command {
  public ConfigCommand() {
    super("config", new String[] { "<save/load>" });
  }
  
  public void execute(String[] commands) {
    if (commands.length == 1) {
      sendMessage("You`ll find the config files in your gameProfile directory under catware/config");
      return;
    } 
    if (commands.length == 2)
      if ("list".equals(commands[0])) {
        String configs = "Configs: ";
        File file = new File("catware/");
        List<File> directories = (List<File>)Arrays.<File>stream(file.listFiles()).filter(File::isDirectory).filter(f -> !f.getName().equals("util")).collect(Collectors.toList());
        StringBuilder builder = new StringBuilder(configs);
        for (File file1 : directories)
          builder.append(file1.getName() + ", "); 
        configs = builder.toString();
        sendMessage("§a" + configs);
      } else {
        sendMessage("§cNot a valid command... Possible usage: <list>");
      }  
    if (commands.length >= 3) {
      switch (commands[0]) {
        case "save":
          Phobos.configManager.saveConfig(commands[1]);
          sendMessage("§aConfig has been saved.");
          return;
        case "load":
          Phobos.moduleManager.onUnload();
          Phobos.configManager.loadConfig(commands[1]);
          Phobos.moduleManager.onLoad();
          sendMessage("§aConfig has been loaded.");
          return;
      } 
      sendMessage("§cNot a valid command... Possible usage: <save/load>");
    } 
  }
}

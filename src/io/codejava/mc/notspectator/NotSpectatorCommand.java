package io.codejava.mc.notspectator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NotSpectatorCommand implements CommandExecutor, TabCompleter {

    private final NotSpectator plugin;

    public NotSpectatorCommand(NotSpectator plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("notspectator.admin")) {
            sender.sendMessage(Component.text("이 명령어를 실행할 권한이 없습니다.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "whitelist":
                handleWhitelistCommand(sender, args);
                break;
            case "gamemodemsg":
                handleGamemodeMsgCommand(sender, args);
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void handleWhitelistCommand(CommandSender sender, String[] args) {
        // /notspectator whitelist ...
        if (args.length == 1) {
            sendHelp(sender);
            return;
        }

        String action = args[1].toLowerCase();
        switch (action) {
            case "set":
                // /notspectator whitelist set player1 player2 ...
                if (args.length < 3) {
                    sender.sendMessage(Component.text("Usage: /notspectator whitelist set <player1> [player2] ...", NamedTextColor.RED));
                    return;
                }
                Set<String> newWhitelist = Arrays.stream(args, 2, args.length)
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());
                plugin.setWhitelist(newWhitelist);
                sender.sendMessage(Component.text("화이트리스트를 플레이어 " + newWhitelist.size() + "명으로 설정했습니다.", NamedTextColor.GREEN));
                break;

            case "check":
                // /notspectator whitelist check
                Set<String> whitelist = plugin.getWhitelist();
                if (whitelist.isEmpty()) {
                    sender.sendMessage(Component.text("화이트리스트가 비었습니다.", NamedTextColor.YELLOW));
                } else {
                    sender.sendMessage(Component.text("화이트리스트에 등록된 플레이어: " + String.join(", ", whitelist), NamedTextColor.AQUA));
                }
                break;

            case "remove":
                // /notspectator whitelist remove player1 player2 ...
                if (args.length < 3) {
                    sender.sendMessage(Component.text("Usage: /notspectator whitelist remove <player1> [player2] ...", NamedTextColor.RED));
                    return;
                }
                for (int i = 2; i < args.length; i++) {
                    plugin.removeFromWhitelist(args[i]);
                }
                sender.sendMessage(Component.text("한 명 이상의 플레이어를 화이트리스트에서 제거했습니다.", NamedTextColor.GREEN));
                plugin.saveWhitelist(); // Save all changes at once
                break;

            default:
                // This handles /notspectator whitelist <playerid>
                // It's the "add" command by default
                String playerName = args[1];
                plugin.addToWhitelist(playerName);
                sender.sendMessage(Component.text("플레이어 " + playerName + "을(를) 화이트리스트에 추가했습니다.", NamedTextColor.GREEN));
                break;
        }
    }

    private void handleGamemodeMsgCommand(CommandSender sender, String[] args) {
        // /notspectator gamemodemsg <true|false>
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /notspectator gamemodemsg <true|false>", NamedTextColor.RED));
            return;
        }

        boolean show;
        if (args[1].equalsIgnoreCase("true")) {
            show = true;
        } else if (args[1].equalsIgnoreCase("false")) {
            show = false;
        } else {
            sender.sendMessage(Component.text("Usage: /notspectator gamemodemsg <true|false>", NamedTextColor.RED));
            return;
        }

        // Your request was: if "false", hide messages.
        // The GameRule is 'sendCommandFeedback'.
        // So 'false' from you means 'false' for the gamerule.
        // And 'true' from you means 'true' for the gamerule.
        plugin.setShowGamemodeMessages(show);
        plugin.saveWhitelist(); // Save the setting

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, show);
        }

        if (show) {
            sender.sendMessage(Component.text("게임모드 변경 메시지가 출력됩니다.", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("게임모드 변경 메시지가 숨겨집니다.", NamedTextColor.GREEN));
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("[NotSpectator] v1.0-java", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/notspectator whitelist <player>", NamedTextColor.AQUA)
                .append(Component.text(" - <player>을(를) 화이트리스트에 추가합니다. (1명 단위)", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/notspectator whitelist set <players...>", NamedTextColor.AQUA)
                .append(Component.text(" - 기존 화이트리스트를 삭제하고 1명 이상의 플레이어(들)로 설정합니다.", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/notspectator whitelist remove <players...>", NamedTextColor.AQUA)
                .append(Component.text(" - 1명 이상의 플레이어를 화이트리스트에서 제거합니다.", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/notspectator whitelist check", NamedTextColor.AQUA)
                .append(Component.text(" - 현재 화이트리스트에 있는 플레이어를 확인합니다.", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/notspectator gamemodemsg <true|false>", NamedTextColor.AQUA)
                .append(Component.text(" - 게임모드 변경 메시지를 출력 또는 숨깁니다.", NamedTextColor.GRAY)));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("notspectator.admin")) {
            return List.of();
        }

        if (args.length == 1) {
            return List.of("whitelist", "gamemodemsg");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("whitelist")) {
            return List.of("set", "remove", "check"); // "add" is default, so we suggest others
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("gamemodemsg")) {
            return List.of("true", "false");
        }

        return List.of(); // No suggestions
    }
}
package re.imc.nps;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class IMCNpsFabric implements ModInitializer {

	public static Path path;
	@Override
	public void onInitialize() {
		path = FabricLoader.getInstance().getGameDir().resolve("mods").resolve("imcnps");;
		path.toFile().mkdirs();
		String token = System.getProperty("nps.accesstoken", null);
		ClientMain.setOutHandler((s) -> {});
		ClientMain.setLogHandler(IMCNpsFabric::sendPlayer);

		ClientMain.setStartHandler((process) -> {});
		if (token == null) {
			Path file = path.resolve("token.txt");
			if (file.toFile().exists()) {
				ClientMain.start(path);
			}
		} else {
			ClientMain.start(path);
		}



		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				dispatcher.register(literal("nps")
						.requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2))
						.then(argument("arg", MessageArgumentType.message())
						.executes((IMCNpsFabric::onCommand)))));

		Executors.newSingleThreadScheduledExecutor()
				.scheduleAtFixedRate(new Runnable() {
					boolean sent = false;
					@Override
					public void run() {
						if (MinecraftClient.getInstance().player != null) {
							if (!sent) {
								sendTips();
								sent = true;
							}
						} else {
							sent = false;
						}
					}
				}, 2, 5, TimeUnit.SECONDS);
	}

	public static void sendPlayer(String info) {
		if (MinecraftClient.getInstance().player != null) {
			MinecraftClient.getInstance().player.sendMessage(Text.of(info.replaceAll("\r\n","\n")), false);
		}
	}

	public static int onCommand(CommandContext<ServerCommandSource> context) {
		if (ClientMain.getConfig() != null) {
			sendPlayer("§cIMCNps 已被启用!");
		    return 0;
		}
		String[] cut = context.getInput().split("\\s+");
		if (cut.length < 2) {
			sendPlayer("§c未发现Token! 请使用/nps <token> 设置Token");
			sendPlayer("§c如果你没有Token, 请进入联机大厅申请");
		}
		String line = cut[1];
		Path file = path.resolve("token.txt");
		if (!file.toFile().exists()) {
			try {
				InputStream in = ClientMain.class.getClassLoader().getResourceAsStream("token.txt");
				Files.copy(in, file);
			} catch (IOException e) {
				e.printStackTrace();
				return 0;
			}
		}

		try {
			if (line != null) {
				Files.write(file, line.getBytes(StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ClientMain.start(path);
		sendTips();
        return 0;
    }

	public static void sendTips() {
		if (ClientMain.getConfig() == null) {
			sendPlayer("§c未发现Token! 请使用/nps <token> 设置Token");
			sendPlayer("");
			sendPlayer("§c如果你没有Token, 请进入联机大厅申请:");
			sendPlayer("§e  正版验证 → dg.cymolink.com");
			sendPlayer("§2  离线玩家 → off.dg.cymolink.com");
		} else {
			sendPlayer("§aIMCNps 已启用, 别忘了下载并调§eLanServerProperties §aMod的设置");
			sendPlayer("§a使用 §e离线模式 + UUID修复");
			sendPlayer("");
			sendPlayer("§b=======================");
			sendPlayer("§b房间号: " + ClientMain.getConfig().getRoomId());
			sendPlayer("§b可输入/jr " + ClientMain.getConfig().getRoomId() + " 进入");
			sendPlayer("§b=======================");
		}
		sendPlayer("§7路径: " + path);

	}

}
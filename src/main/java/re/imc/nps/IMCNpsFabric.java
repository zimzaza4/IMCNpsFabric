package re.imc.nps;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import re.imc.nps.i18n.LocaleMessage;
import re.imc.nps.roomlist.RoomList;
import re.imc.nps.utils.UUIDUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class IMCNpsFabric implements ModInitializer {

	public static Path path;

	public static Boolean ONLINE_MODE;
	@Override
	public void onInitialize() {

		path = FabricLoader.getInstance().getGameDir().resolve("mods").resolve("imcnps");;
		path.toFile().mkdirs();

		String token = System.getProperty("nps.accesstoken", null);
		ClientMain.setup(path, Info.Platform.FABRIC);
		ClientMain.setOutHandler((s) -> {});
		ClientMain.setLogHandler(IMCNpsFabric::sendPlayer);

		ClientMain.setStartHandler((process) -> {});
		if (token == null) {
			Path file = path.resolve("token.txt");
			if (file.toFile().exists()) {
				// ClientMain.start(path);
			}
		} else {
			// ClientMain.start(path);
		}



		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				dispatcher.register(literal("nps")
						.requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2))
						.then(argument("arg", MessageArgumentType.message())
						.executes((IMCNpsFabric::onStartCommand)))));


		ServerLifecycleEvents.SERVER_STARTING
						.register(server -> {
							IntegratedServer integratedServererver = MinecraftClient.getInstance().getServer();
							if (integratedServererver.isRemote()) {
								integratedServererver.setOnlineMode(false);
							}
							server.setOnlineMode(false);
						});

		RoomList.startServerGetTask();
		Executors.newSingleThreadScheduledExecutor()
				.scheduleAtFixedRate(new Runnable() {
					boolean sent = false;
					@Override
					public void run() {

						IntegratedServer server = MinecraftClient.getInstance().getServer();

						if (server != null && server.isRemote() && ClientMain.DATA_PATH == null && MinecraftClient.getInstance().isIntegratedServerRunning()) {
							ClientMain.start(path, Info.Platform.FABRIC, server.getServerPort());
							// sendPlayer("P:" + server.getServerPort());
						}
						if (!MinecraftClient.getInstance().isIntegratedServerRunning()) {
							if (ClientMain.getProcess() != null) {
								if (!ClientMain.getProcess().isStop()) {
									ClientMain.getProcess().stop();
								} else if (server != null && ClientMain.DATA_PATH != null) {
									ClientMain.start(path, Info.Platform.FABRIC, server.getServerPort());
								}
							}
						}

						if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().isIntegratedServerRunning() && server.isRemote()) {
							if (!sent) {
								sendTips();
								sent = true;
							}

							if (server.isOnlineMode()) {
								server.setOnlineMode(false);
							}

						} else {
							sent = false;
						}

					}
				}, 2, 3, TimeUnit.SECONDS);
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("imcnps", "token"), (client, handler, buf, responseSender) -> {
			String tokenReceived = buf.getCharSequence(0, buf.capacity(), StandardCharsets.UTF_8).toString();
			if (tokenReceived.length() > 500) {
				return;
			}
			if (setToken(tokenReceived)) {
				sendPlayer(LocaleMessage.message("fabric_get_token"));
			}
		});

	}

	public static void sendPlayer(String info) {
		if (MinecraftClient.getInstance().player != null) {
			MinecraftClient.getInstance().player.sendMessage(Text.of(info.replaceAll("\r\n","\n")), false);
		}
	}

	public static int onStartCommand(CommandContext<ServerCommandSource> context) {
		if (ClientMain.getConfig() != null) {
			sendPlayer(LocaleMessage.message("fabric_already_start"));
		}
		String[] cut = context.getInput().split("\\s+");
		if (cut.length < 2) {
			sendPlayer("/nps <token>");
		}
		String line = cut[1];
		setToken(line);

		IntegratedServer server = MinecraftClient.getInstance().getServer();

		int port = server != null ? server.getServerPort() : 25565;

		if (ClientMain.getConfig() == null) {
			ClientMain.start(path, Info.Platform.FABRIC, port);
			sendTips();
		}
        return 0;
    }

	public static boolean setToken(String token) {
		Path file = path.resolve("token.txt");
		if (!file.toFile().exists()) {
			try {
				InputStream in = ClientMain.class.getClassLoader().getResourceAsStream("token.txt");
				Files.copy(in, file);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		try {
			List<String> lines = Files.readAllLines(file);
			if (lines.isEmpty()) {
				return true;
			}
			String oldToken = lines.get(0);
			if (oldToken.equals(token)) {
				return false;
			}
		} catch (IOException e) {

        }

        try {
			if (token != null) {
				Files.write(file, token.getBytes(StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void sendTips() {
		if (ClientMain.getConfig() == null) {
			/*
			sendPlayer("§c未发现Token! 请使用/nps <token> 设置Token");
			sendPlayer("");
			sendPlayer("§c如果你没有Token, 请进入联机大厅申请:");
			sendPlayer("§e  正版验证 → dg.cymolink.com");
			sendPlayer("§2  离线玩家 → off.dg.cymolink.com");

			 */

			sendPlayer(LocaleMessage.message("fabric_no_token_tip"));
		} else {
			/*
			sendPlayer("§aIMCNps 已启用, 别忘了下载并调§eLanServerProperties §aMod的设置");
			sendPlayer("§a使用 §e离线模式 + UUID修复");
			sendPlayer("");
			sendPlayer("§b=======================");
			sendPlayer("§b房间号: " + ClientMain.getConfig().getRoomId());
			sendPlayer("§b可输入/jr " + ClientMain.getConfig().getRoomId() + " 进入");
			sendPlayer("§b=======================");

			 */
			sendPlayer(LocaleMessage.message("fabric_room_id_tip").replace("%room_id%", String.valueOf(ClientMain.getConfig().getRoomId())));

		}
		// sendPlayer(LocaleMessage.message("fabric_path").replace("%path%", String.valueOf(path)));

	}


	public static boolean isOnlineMode() {
		if (ONLINE_MODE == null) {
			Session session = MinecraftClient.getInstance().getSession();
			@Nullable UUID uuid = UUIDUtils.getOfficialUUID(session.getUsername());
			if (session.getUuidOrNull() == null) {
				ONLINE_MODE = false;
			}
			if (uuid == null) {
				ONLINE_MODE = false;
			} else if (!uuid.equals(session.getUuidOrNull())) {
				ONLINE_MODE = false;
			}
		}
		if (ONLINE_MODE == null) {
			ONLINE_MODE = true;
		}
		return ONLINE_MODE;
    }


}
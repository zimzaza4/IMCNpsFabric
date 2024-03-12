package re.imc.nps.roomlist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import re.imc.nps.Info;
import re.imc.nps.dto.IMCNpsSimpleServerDTO;
import re.imc.nps.dto.ResultDTO;
import re.imc.nps.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RoomList {
    public static List<IMCNpsSimpleServerDTO> CACHED_SERVER = new ArrayList<>();

    public static final Gson GSON = new Gson();
    public static void startServerGetTask() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    try {

                        if (MinecraftClient.getInstance().player != null) {
                            return;
                        }

                        String result = HttpUtils.sendGet(Info.API_URL + "/imc-nps/list-servers", "");
                        ResultDTO<List<IMCNpsSimpleServerDTO>> resultDTO = GSON.fromJson(result, new TypeToken<ResultDTO<List<IMCNpsSimpleServerDTO>>>() {
                        }.getType());

                        if (resultDTO.isSuccess()) {
                            CACHED_SERVER = resultDTO.getData();
                        }
                    } catch (Throwable ignored) {}
                }, 1, 6, TimeUnit.SECONDS);
    }

}

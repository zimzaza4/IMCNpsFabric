package re.imc.nps.room;

import lombok.Getter;
import re.imc.nps.AtlasAPI;
import re.imc.nps.ClientMain;
import re.imc.nps.IMCNpsFabric;
import re.imc.nps.dto.IMCNpsNodeDTO;
import re.imc.nps.dto.IMCNpsServerDTO;
import re.imc.nps.dto.IMCNpsSimpleServerDTO;
import re.imc.nps.dto.ResultDTO;
import re.imc.nps.req.IMCNpsUserModifyServerReq;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PlayerRoomInfo {

    private static IMCNpsServerDTO roomInfo;

    private static List<IMCNpsNodeDTO> nodes;

    public static IMCNpsServerDTO getRoomInfo() {
        return roomInfo;
    }

    public static void setRoomInfo(IMCNpsServerDTO roomInfo) {
        PlayerRoomInfo.roomInfo = roomInfo;
    }

    public static List<IMCNpsNodeDTO> getNodes() {
        return nodes;
    }

    public static void startGetRoomInfoTask() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    try {
                        if (IMCNpsFabric.TOKEN == null) {
                            return;
                        }
                        nodes = AtlasAPI.getNodes();
                        roomInfo = AtlasAPI.getServerInfo();
                    } catch (Throwable t) {}
                }, 0, 5, TimeUnit.SECONDS);
    }

    public static ResultDTO setRoomInfo(IMCNpsUserModifyServerReq roomInfo) {
        return AtlasAPI.modifyServer(roomInfo);
    }

}

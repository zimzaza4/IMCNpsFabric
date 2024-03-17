package re.imc.nps;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import re.imc.nps.dto.IMCNpsNodeDTO;
import re.imc.nps.dto.IMCNpsServerDTO;
import re.imc.nps.dto.ResultDTO;
import re.imc.nps.req.IMCNpsUserModifyServerReq;
import re.imc.nps.utils.HttpUtils;

import java.util.List;

public class AtlasAPI {


    public static final Gson GSON = new Gson();
    public static List<IMCNpsNodeDTO> getNodes() {
        String url = "/imc-nps/list-nodes";
        String s1 = HttpUtils.sendGet(Info.API_URL + url, "");
        ResultDTO resultDTO = GSON.fromJson(s1, ResultDTO.class);

        Object data = resultDTO.getData();

        List<IMCNpsNodeDTO> servers = GSON.fromJson(GSON.toJson(data), new TypeToken<List<IMCNpsNodeDTO>>() {}.getType());
        return servers;

    }

    public static IMCNpsServerDTO getServerInfo() {
        String resultStr = HttpUtils.sendGet(Info.API_URL + "/imc-nps/user-check-info", "token=" + IMCNpsFabric.TOKEN);
        ResultDTO result = GSON.fromJson(resultStr, ResultDTO.class);
        return GSON.fromJson(GSON.toJson(result.getData()), IMCNpsServerDTO.class);
    }

    public static ResultDTO modifyServer(IMCNpsUserModifyServerReq req) {
        req.setToken(IMCNpsFabric.TOKEN);
        return GSON.fromJson(HttpUtils.sendPostJson(Info.API_URL + "/imc-nps/user-modify-nps", GSON.toJson(req), false), ResultDTO.class);
    }


}

package re.imc.nps.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import re.imc.nps.ClientMain;
import re.imc.nps.IMCNpsFabric;
import re.imc.nps.dto.IMCNpsServerDTO;

import java.io.Serializable;

public class IMCNpsUserModifyServerReq implements Serializable {

    private String name;
    private String description;
    private String node;
    private String infoForwarding;
    private String token;

    public IMCNpsUserModifyServerReq(String name, String description, String node, String infoForwarding, String token) {
        this.name = name;
        this.description = description;
        this.node = node;
        this.infoForwarding = infoForwarding;
        this.token = token;
    }

    public IMCNpsUserModifyServerReq() {
    }

    public static IMCNpsUserModifyServerReq fromDTO(IMCNpsServerDTO dto) {
        return new IMCNpsUserModifyServerReq(dto.getName(), dto.getDescription(), dto.getNode(), dto.getInfoForwarding(), IMCNpsFabric.TOKEN);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getNode() {
        return node;
    }

    public String getInfoForwarding() {
        return infoForwarding;
    }

    public String getToken() {
        return token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public void setInfoForwarding(String infoForwarding) {
        this.infoForwarding = infoForwarding;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

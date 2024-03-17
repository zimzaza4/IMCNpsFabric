package re.imc.nps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class IMCNpsNodeDTO {
    private String id;
    private String nodeName;

    public IMCNpsNodeDTO(String id, String nodeName) {
        this.id = id;
        this.nodeName = nodeName;
    }

    public IMCNpsNodeDTO() {
    }

    public String getId() {
        return id;
    }

    public String getNodeName() {
        return nodeName;
    }
}

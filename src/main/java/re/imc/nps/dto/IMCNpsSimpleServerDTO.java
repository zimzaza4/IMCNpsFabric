package re.imc.nps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class IMCNpsSimpleServerDTO {
    private String name;
    private Integer roomId;

    public Integer getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }
}

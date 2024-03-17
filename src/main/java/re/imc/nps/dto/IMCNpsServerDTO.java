package re.imc.nps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class IMCNpsServerDTO implements Cloneable {
    private String uuid;
    private String name;
    private String description;
    private Integer maxPlayers;
    private String node;
    private String infoForwarding;
    private Integer playerVipLevel;

    public IMCNpsServerDTO(String uuid, String name, String description, Integer maxPlayers, String node, String infoForwarding, Integer playerVipLevel) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.maxPlayers = maxPlayers;
        this.node = node;
        this.infoForwarding = infoForwarding;
        this.playerVipLevel = playerVipLevel;
    }

    public IMCNpsServerDTO() {
    }

    @Override
    public IMCNpsServerDTO clone() {
        try {
            return (IMCNpsServerDTO) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public String getNode() {
        return node;
    }

    public String getInfoForwarding() {
        return infoForwarding;
    }

    public Integer getPlayerVipLevel() {
        return playerVipLevel;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public void setInfoForwarding(String infoForwarding) {
        this.infoForwarding = infoForwarding;
    }

    public void setPlayerVipLevel(Integer playerVipLevel) {
        this.playerVipLevel = playerVipLevel;
    }
}

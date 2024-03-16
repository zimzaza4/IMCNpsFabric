package re.imc.nps.mixin;

import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.LanServerQueryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import re.imc.nps.IMCNpsFabric;
import re.imc.nps.dto.IMCNpsSimpleServerDTO;
import re.imc.nps.roomlist.RoomList;

import java.util.List;

@Mixin(LanServerQueryManager.LanServerEntryList.class)
public abstract class MixinLanServerList {

    @Shadow @Final private List<LanServerInfo> serverEntries;

    @Shadow private boolean dirty;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void init(CallbackInfo ci) {
        boolean online = IMCNpsFabric.isOnlineMode();

        for (IMCNpsSimpleServerDTO imcNpsSimpleServerDTO : RoomList.CACHED_SERVER) {
            serverEntries.add(new LanServerInfo(imcNpsSimpleServerDTO.getName(),
                    imcNpsSimpleServerDTO.getRoomId()
                            + ".nps." + (online ? "cymolink.com" : "off.cymolink.com")));

        }
        dirty = true;
    }


}

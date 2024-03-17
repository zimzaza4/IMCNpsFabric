package re.imc.nps.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import re.imc.nps.dto.IMCNpsSimpleServerDTO;
import re.imc.nps.room.RoomList;
import re.imc.nps.utils.TextUtils;

@Mixin(MultiplayerServerListWidget.LanServerEntry.class)
public abstract class MixinLanServerEntry {

    @Shadow @Final protected MinecraftClient client;

    @Shadow @Final private static Text TITLE_TEXT;

    @Shadow @Final protected LanServerInfo server;

    @Shadow @Final private MultiplayerScreen screen;

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (server.getAddressPort().contains(".nps.")) {

            String desc = "";
            for (IMCNpsSimpleServerDTO imcNpsSimpleServerDTO : RoomList.CACHED_SERVER) {
                if (server.getAddressPort().contains(imcNpsSimpleServerDTO.getRoomId().toString())) {
                    desc = imcNpsSimpleServerDTO.getDescription();
                    break;
                }
            }
            if (hovered) {
                screen.setMultiplayerScreenTooltip(TextUtils.autoFeedLine(desc));
            }
            // context.drawText(client.textRenderer, TITLE_TEXT, x + 32 + 3, y + 1, 16777215, false);
            context.drawText(this.client.textRenderer, server.getMotd(), x + 32 + 3, y + 1, 16777215, false);
            context.drawText(this.client.textRenderer, this.server.getAddressPort(), x + 32 + 3, y + 12 + 11, 3158064, false);

            ci.cancel();
        }

    }
}

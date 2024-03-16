package re.imc.nps.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen {
    @Shadow private ServerInfo selectedEntry;

    @Shadow
    protected abstract void connect(ServerInfo info);

    @Inject(method = "directConnect", at = @At("HEAD"), cancellable = true)
    public void directConnectRoomAndServer(boolean confirmedAction, CallbackInfo ci) {
        if (confirmedAction) {
            if (this.selectedEntry.address.contains(".nps.")) {
                this.connect(this.selectedEntry);
                ci.cancel();
            }
        }
    }
}

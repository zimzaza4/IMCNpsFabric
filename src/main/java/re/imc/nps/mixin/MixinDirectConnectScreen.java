package re.imc.nps.mixin;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import re.imc.nps.IMCNpsFabric;

@Mixin(DirectConnectScreen.class)
public class MixinDirectConnectScreen {

    @Shadow private TextFieldWidget addressField;

    @Shadow @Final private ServerInfo serverEntry;

    @Shadow @Final private BooleanConsumer callback;

    @Inject(method = "saveAndClose", at = @At(value = "HEAD"), cancellable = true)
    public void close(CallbackInfo ci) {
        try {
            int id = Integer.parseInt(this.addressField.getText());
            this.serverEntry.address = id + ".nps." + (IMCNpsFabric.isOnlineMode() ? "cymolink.com" : "off.cymolink.com");
            this.callback.accept(true);
            ci.cancel();
        } catch (Exception ignored) {

        }
    }
}

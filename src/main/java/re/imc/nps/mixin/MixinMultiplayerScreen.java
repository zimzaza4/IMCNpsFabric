package re.imc.nps.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import re.imc.nps.screen.RoomEditScreen;
import re.imc.nps.utils.TextUtils;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {
    @Shadow private ServerInfo selectedEntry;

    private ButtonWidget buttonMyRoom;

    protected MixinMultiplayerScreen(Text title) {
        super(title);
    }

    @Shadow
    protected abstract void connect(ServerInfo info);

    @Inject(method = "init", at = @At(value = "RETURN"))
    public void createButton(CallbackInfo ci) {
        buttonMyRoom = ButtonWidget.builder(TextUtils.locale("screen_title"), button -> {
            this.client.setScreen(new RoomEditScreen(this));
        }).size(98, 18).position(width/2 + 113, 10).build();
        this.addDrawableChild(buttonMyRoom);
    }

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

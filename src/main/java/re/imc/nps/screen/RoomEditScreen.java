package re.imc.nps.screen;

import com.mojang.logging.LogUtils;
import net.minecraft.MinecraftVersion;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import re.imc.nps.AtlasAPI;
import re.imc.nps.IMCNpsFabric;
import re.imc.nps.dto.IMCNpsNodeDTO;
import re.imc.nps.dto.IMCNpsServerDTO;
import re.imc.nps.dto.ResultDTO;
import re.imc.nps.req.IMCNpsUserModifyServerReq;
import re.imc.nps.room.PlayerRoomInfo;
import re.imc.nps.utils.TextUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class RoomEditScreen extends Screen {

    private TextFieldWidget nameField;
    private EditBoxWidget descriptionField;
    private ButtonWidget confirmButton;
    private ButtonWidget cancelButton;

    private IMCNpsServerDTO serverInfo;
    private CyclingButtonWidget<IMCNpsNodeDTO> nodeButton;
    private Screen parent;
    public RoomEditScreen(Screen parent) {
        super(TextUtils.locale("screen_title"));
        if (IMCNpsFabric.isTokenExist()) {
            this.serverInfo = PlayerRoomInfo.getRoomInfo().clone();
        }
        this.parent = parent;
    }

    @Override
    protected void init() {
        cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, (button) -> {
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height / 4 + 120 + (IMCNpsFabric.isTokenExist() ? 60 : 12), 200, 20).build());

        if (IMCNpsFabric.isTokenExist()) {

            this.nameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 56, 200, 20, Text.of("abc"));
            this.nameField.setMaxLength(15);
            this.nameField.setText(serverInfo.getName());
            this.nameField.setChangedListener((text) -> {
                serverInfo.setName(text);
            });

            String current = serverInfo.getDescription();
            this.descriptionField = new EditBoxWidget(this.textRenderer, this.width / 2 - 100, 106, 200, 100, Text.of("abc"), Text.of(""));
            this.descriptionField.setMaxLength(100);
            this.descriptionField.setText(current);
            this.descriptionField.setChangeListener(text -> {
                serverInfo.setDescription(text);
            });

            List<IMCNpsNodeDTO> nodes = PlayerRoomInfo.getNodes();

            if (!nodes.isEmpty()) {
                IMCNpsNodeDTO currentNode = nodes.get(0);
                for (IMCNpsNodeDTO node : nodes) {
                    if (node.getId().equals(serverInfo.getNode())) {
                        currentNode = node;
                    }
                }

                this.nodeButton = new CyclingButtonWidget.Builder<IMCNpsNodeDTO>(value -> Text.of(value.getNodeName())).values(nodes).initially(currentNode).build(this.width / 2 - 100, this.height / 4 + 120, 200, 20, TextUtils.locale("screen_room_node"), (button, value) -> {
                    serverInfo.setNode(value.getId());
                });
            }
            this.addDrawableChild(nameField);
            this.addDrawableChild(descriptionField);
            if (nodeButton != null) {
                this.addDrawableChild(nodeButton);
            }
            confirmButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
                ResultDTO result = AtlasAPI.modifyServer(IMCNpsUserModifyServerReq.fromDTO(serverInfo));
                if (result.isSuccess()) {
                    PlayerRoomInfo.setRoomInfo(serverInfo);
                    client.setScreen(parent);
                }
            }).dimensions(this.width / 2 - 100, this.height / 4 + 120 + 28, 200, 20).build());

        } else {
            ButtonWidget button = ButtonWidget.builder(TextUtils.locale("screen_join_lobby"), b -> {
                IMCNpsFabric.startMultiplayer(this.client, (IMCNpsFabric.isOnlineMode() ? "" : "off.") + "cymolink.com");
            }).dimensions(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20).build();
            this.addDrawableChild(button);
        }
    }


    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String name = null;
        String desc = null;
        if (nameField != null) {
            name = nameField.getText();
            desc = descriptionField.getText();
        }

        this.init(client, width, height);
        nameField.setText(name);
        if (nameField != null) {
            descriptionField.setText(desc);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Method method;
        try {
            if (SharedConstants.getGameVersion().getProtocolVersion() <= 3465) {
                method = this.getClass().getSuperclass().getDeclaredMethod("method_25420", DrawContext.class);
                method.invoke(this, context);
            }
        } catch (Throwable e) {

        }
        super.render(context, mouseX, mouseY, delta);

        if (IMCNpsFabric.isTokenExist()) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, 16777215);

            context.drawTextWithShadow(this.textRenderer, TextUtils.locale("screen_room_name"), this.width / 2 - 100 + 1, 45, 10526880);
            context.drawTextWithShadow(this.textRenderer, TextUtils.locale("screen_room_description"), this.width / 2 - 100 + 1, 94, 10526880);
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
            context.drawTextWithShadow(this.textRenderer, TextUtils.locale("screen_no_token"), this.width / 2 - 100 + 1, 100, Formatting.YELLOW.getColorValue());

        }


    }
}

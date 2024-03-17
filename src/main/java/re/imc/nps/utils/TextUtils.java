package re.imc.nps.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.text.Text;
import re.imc.nps.i18n.LocaleMessage;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static Text locale(String key) {
        return toText(LocaleMessage.message(key));
    }
    public static Text toText(Component component) {
        return Text.Serialization.fromJson(GsonComponentSerializer.gson().serialize(component).replace("\\r", ""));
    }
    public static List<Text> autoFeedLine(String lines) {
        List<Text> result = new ArrayList<>();
        for (String line : lines.split("\n")) {
            if (line.length() > 20) {
                int index = 0;
                while (index < line.length()) {
                    int endIndex = Math.min(index + 20, line.length());
                    result.add(Text.of("§8► §f" + line.substring(index, endIndex)));
                    index = endIndex;
                }
            } else {
                result.add(Text.of("§8► §f" + line));
            }
        }
        return result;
    }

}

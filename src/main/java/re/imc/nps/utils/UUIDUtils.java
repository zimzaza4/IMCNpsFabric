package re.imc.nps.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

public class UUIDUtils {
    @Nullable
    public static UUID getOfficialUUID(String playerName) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
        try {
            String UUIDJson = IOUtils.toString(new URL(url), Charset.defaultCharset());
            if (!UUIDJson.isEmpty()) {
                JsonObject root = JsonParser.parseString(UUIDJson).getAsJsonObject();
                String playerName2 = root.getAsJsonPrimitive("name").getAsString();
                String uuidString = root.getAsJsonPrimitive("id").getAsString();
                // com.mojang.util.UUIDTypeAdapter.fromString(String)
                long uuidMSB = Long.parseLong(uuidString.substring(0, 8), 16);
                uuidMSB <<= 32;
                uuidMSB |= Long.parseLong(uuidString.substring(8, 16), 16);
                long uuidLSB = Long.parseLong(uuidString.substring(16, 24), 16);
                uuidLSB <<= 32;
                uuidLSB |= Long.parseLong(uuidString.substring(24, 32), 16);
                UUID uuid = new UUID(uuidMSB, uuidLSB);

                if (playerName2.equalsIgnoreCase(playerName))
                    return uuid;
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }
}

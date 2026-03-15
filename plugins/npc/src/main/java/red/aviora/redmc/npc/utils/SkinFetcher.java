package red.aviora.redmc.npc.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import red.aviora.redmc.api.utils.ApiUtils;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.BiConsumer;

public class SkinFetcher {

	public record SkinResult(String texture, String signature) {}

	public static void fetchAsync(String username, BiConsumer<SkinResult, String> callback) {
		Thread.ofVirtual().start(() -> {
			try {
				String uuid = fetchUuid(username);
				if (uuid == null) {
					callback.accept(null, "Player not found: " + username);
					return;
				}

				SkinResult skin = fetchSkin(uuid);
				if (skin == null) {
					callback.accept(null, "No skin data for: " + username);
					return;
				}

				callback.accept(skin, null);
			} catch (Exception e) {
				ApiUtils.log("SkinFetcher error for " + username + ": " + e.getMessage());
				callback.accept(null, e.getMessage());
			}
		});
	}

	private static String fetchUuid(String username) throws Exception {
		URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			return null;
		}

		try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
			JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
			String raw = obj.get("id").getAsString();
			return raw.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
		}
	}

	private static SkinResult fetchSkin(String uuid) throws Exception {
		URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			return null;
		}

		try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
			JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
			JsonArray properties = obj.getAsJsonArray("properties");

			for (var element : properties) {
				JsonObject prop = element.getAsJsonObject();
				if ("textures".equals(prop.get("name").getAsString())) {
					String value = prop.get("value").getAsString();
					String signature = prop.has("signature") ? prop.get("signature").getAsString() : null;
					return new SkinResult(value, signature);
				}
			}
		}

		return null;
	}
}

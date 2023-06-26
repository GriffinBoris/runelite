package net.runelite.client.plugins.randall.models;

import com.google.gson.JsonObject;
import net.runelite.api.Client;

public class CameraModel implements DataModel {

    private final Client client;

    public CameraModel(Client client) {
        this.client = client;
    }

    @Override
    public JsonObject toJson() {
        JsonObject data = new JsonObject();
        data.addProperty("x", client.getCameraX());
        data.addProperty("y", client.getCameraX());
        data.addProperty("z", client.getCameraX());
        data.addProperty("pitch", client.getCameraPitch());
        data.addProperty("yaw", client.getCameraYaw());
        return data;
    }
}

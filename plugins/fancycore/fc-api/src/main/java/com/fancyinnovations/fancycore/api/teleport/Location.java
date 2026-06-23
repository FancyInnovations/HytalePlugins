package com.fancyinnovations.fancycore.api.teleport;

import com.google.gson.annotations.SerializedName;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Rotation3fc;
import org.joml.Vector3d;

public record Location(
        @SerializedName("world_name") String worldName,
        double x,
        double y,
        double z,
        float yaw,
        float pitch
) {

    public Vector3d positionVec() {
        return new Vector3d(x, y, z);
    }

    public Rotation3fc rotationVec() {
        return new Rotation3f(pitch, yaw, 0f);
    }
}

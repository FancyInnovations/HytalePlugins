package com.fancyinnovations.fancycore.api.teleport;

import com.google.gson.annotations.SerializedName;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;

public record SpawnLocation(
        @SerializedName("world_name") String worldName,
        double x,
        double y,
        double z,
        float yaw,
        float pitch
) {

    public Transform toTransform() {
        return new Transform(
                new Vector3d(x, y, z),
                new Vector3f(yaw, pitch)
        );
    }
}

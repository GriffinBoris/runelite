package net.runelite.client.plugins.alfred.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum TransportTypes {
    DOOR("Door", "door"),
    STAIR("Stair", "stair");

    public final String name;
    public final String value;

    @Override
    public String toString() {
        return name;
    }
}

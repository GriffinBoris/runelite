package net.runelite.client.plugins.alfred.api.rs.account;

import net.runelite.client.config.ConfigProfile;
import net.runelite.client.plugins.alfred.Alfred;

import java.util.List;

public class RSAccountHelperAdapter {

    public static List<ConfigProfile> getProfiles() {
        return Alfred.Companion.getProfileManager().lock().getProfiles();
    }

    public static Boolean getProfileIsActive(ConfigProfile profile) {
        return profile.isActive();
    }

    public static String getProfileName(ConfigProfile profile) {
        return profile.getName();
    }

    public static String getProfilePassword(ConfigProfile profile) {
        return profile.getPassword();
    }
}

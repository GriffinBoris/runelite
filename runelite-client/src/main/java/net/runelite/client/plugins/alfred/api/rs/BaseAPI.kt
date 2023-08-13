package net.runelite.client.plugins.alfred.api.rs

import net.runelite.client.plugins.alfred.api.rs.account.RSAccountHelper
import net.runelite.client.plugins.alfred.api.rs.bank.RSBankHelper
import net.runelite.client.plugins.alfred.api.rs.camera.RSCameraHelper
import net.runelite.client.plugins.alfred.api.rs.combat.RSCombatHelper
import net.runelite.client.plugins.alfred.api.rs.equipment.RSEquipmentHelper
import net.runelite.client.plugins.alfred.api.rs.inventory.RSInventoryHelper
import net.runelite.client.plugins.alfred.api.rs.item.RSGroundItemHelper
import net.runelite.client.plugins.alfred.api.rs.menu.RSMenuHelper
import net.runelite.client.plugins.alfred.api.rs.minimap.RSMiniMapHelper
import net.runelite.client.plugins.alfred.api.rs.npc.RSNpcHelper
import net.runelite.client.plugins.alfred.api.rs.objects.RSObjectHelper
import net.runelite.client.plugins.alfred.api.rs.player.RSPlayerHelper
import net.runelite.client.plugins.alfred.api.rs.screen.RSScreenHelper
import net.runelite.client.plugins.alfred.api.rs.tab.RSTabHelper
import net.runelite.client.plugins.alfred.api.rs.walk.RSWalkHelper
import net.runelite.client.plugins.alfred.api.rs.widget.RSWidgetHelper
import net.runelite.client.plugins.alfred.api.rs.world.RSWorldHelper

class BaseAPI {
    private val rsBankHelper: RSBankHelper = RSBankHelper()
    private val rsPlayerHelper: RSPlayerHelper = RSPlayerHelper()
    private val rsObjectHelper: RSObjectHelper = RSObjectHelper()
    private val rsTabHelper: RSTabHelper = RSTabHelper()
    private val rsWorldHelper: RSWorldHelper = RSWorldHelper()
    private val rsInventoryHelper: RSInventoryHelper = RSInventoryHelper()
    private val rsMenuHelper: RSMenuHelper = RSMenuHelper()
    private val rsWidgetHelper: RSWidgetHelper = RSWidgetHelper()
    private val rsCameraHelper: RSCameraHelper = RSCameraHelper()
    private val rsNpcHelper: RSNpcHelper = RSNpcHelper()
    private val rsScreenHelper: RSScreenHelper = RSScreenHelper()
    private val rsMiniMapHelper: RSMiniMapHelper = RSMiniMapHelper()
    private val rsCombatHelper: RSCombatHelper = RSCombatHelper()
    private val rsGroundItemHelper: RSGroundItemHelper = RSGroundItemHelper()
    private val rsWalkHelper: RSWalkHelper = RSWalkHelper()
    private val rsAccountHelper: RSAccountHelper = RSAccountHelper()
    private val rsEquipmentHelper: RSEquipmentHelper = RSEquipmentHelper()

    fun banks(): RSBankHelper {
        return rsBankHelper
    }

    fun players(): RSPlayerHelper {
        return rsPlayerHelper
    }

    fun objects(): RSObjectHelper {
        return rsObjectHelper
    }

    fun tabs(): RSTabHelper {
        return rsTabHelper
    }

    fun world(): RSWorldHelper {
        return rsWorldHelper
    }

    fun inventory(): RSInventoryHelper {
        return rsInventoryHelper
    }

    fun menu(): RSMenuHelper {
        return rsMenuHelper
    }

    fun widgets(): RSWidgetHelper {
        return rsWidgetHelper
    }

    fun camera(): RSCameraHelper {
        return rsCameraHelper
    }

    fun npcs(): RSNpcHelper {
        return rsNpcHelper
    }

    fun screen(): RSScreenHelper {
        return rsScreenHelper
    }

    fun miniMap(): RSMiniMapHelper {
        return rsMiniMapHelper
    }

    fun combat(): RSCombatHelper {
        return rsCombatHelper
    }

    fun items(): RSGroundItemHelper {
        return rsGroundItemHelper
    }

    fun walk(): RSWalkHelper {
        return rsWalkHelper
    }

    fun account(): RSAccountHelper {
        return rsAccountHelper
    }

    fun equipment(): RSEquipmentHelper {
        return rsEquipmentHelper
    }
}

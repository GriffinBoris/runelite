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
import net.runelite.client.plugins.alfred.api.rs.quest.RSQuestHelper
import net.runelite.client.plugins.alfred.api.rs.screen.RSScreenHelper
import net.runelite.client.plugins.alfred.api.rs.tab.RSTabHelper
import net.runelite.client.plugins.alfred.api.rs.walk.RSWalkHelper
import net.runelite.client.plugins.alfred.api.rs.widget.RSWidgetHelper
import net.runelite.client.plugins.alfred.api.rs.world.RSWorldHelper

class BaseAPI {
    val banks: RSBankHelper = RSBankHelper()
    val players: RSPlayerHelper = RSPlayerHelper()
    val objects: RSObjectHelper = RSObjectHelper()
    val tabs: RSTabHelper = RSTabHelper()
    val world: RSWorldHelper = RSWorldHelper()
    val inventory: RSInventoryHelper = RSInventoryHelper()
    val menu: RSMenuHelper = RSMenuHelper()
    val widgets: RSWidgetHelper = RSWidgetHelper()
    val camera: RSCameraHelper = RSCameraHelper()
    val npcs: RSNpcHelper = RSNpcHelper()
    val screen: RSScreenHelper = RSScreenHelper()
    val miniMap: RSMiniMapHelper = RSMiniMapHelper()
    val combat: RSCombatHelper = RSCombatHelper()
    val items: RSGroundItemHelper = RSGroundItemHelper()
    val walk: RSWalkHelper = RSWalkHelper()
    val account: RSAccountHelper = RSAccountHelper()
    val equipment: RSEquipmentHelper = RSEquipmentHelper()
    val quest: RSQuestHelper = RSQuestHelper()
}

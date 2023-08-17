package net.runelite.client.plugins.alfred.scripts.gerber.tasks

import net.runelite.api.ItemID
import net.runelite.api.Skill
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.api.rs.inventory.RSInventoryItem
import net.runelite.client.plugins.alfred.enums.WorldDestinations
import net.runelite.client.plugins.alfred.scripts.gerber.GerberConfig
import net.runelite.client.plugins.alfred.scripts.gerber.GerberThread

class Combat(private val config: GerberConfig) : BaseTask() {
    companion object {
        private val LUMBRIDGE_CHICKENS_WORLD_AREA = WorldArea(3225, 3287, 12, 15, 0)
        private val LUMBRIDGE_COWS_WORLD_AREA = WorldArea(3255, 3258, 9, 37, 0)
    }

    private enum class ScriptState {
        SETUP,
        WAITING,
        FIGHTING,
        LOOTING
    }

    private var scriptState: ScriptState

    init {
        Alfred.setStatus("Training Combat")
        scriptState = ScriptState.SETUP
    }


    override fun getBankLocation(): WorldPoint {
        return WorldDestinations.LUMBRIDGE_BANK.worldPoint
    }

    override fun getRequiredItems(): List<Pair<Int, Int>> {
        val player = Alfred.api.players().localPlayer
        val requiredItems: MutableList<Pair<Int, Int>> = mutableListOf()

        val attackLevel = player.getSkillLevel(Skill.ATTACK)
        if (attackLevel >= 1) {
            requiredItems.add(Pair(ItemID.BRONZE_2H_SWORD, 1))
            requiredItems.add(Pair(ItemID.BRONZE_DAGGER, 1))
            requiredItems.add(Pair(ItemID.BRONZE_SWORD, 1))
            requiredItems.add(Pair(ItemID.BRONZE_LONGSWORD, 1))
            requiredItems.add(Pair(ItemID.BRONZE_SCIMITAR, 1))

            requiredItems.add(Pair(ItemID.IRON_2H_SWORD, 1))
            requiredItems.add(Pair(ItemID.IRON_DAGGER, 1))
            requiredItems.add(Pair(ItemID.IRON_SWORD, 1))
            requiredItems.add(Pair(ItemID.IRON_LONGSWORD, 1))
            requiredItems.add(Pair(ItemID.IRON_SCIMITAR, 1))
        }
        if (attackLevel >= 5) {
            requiredItems.add(Pair(ItemID.STEEL_2H_SWORD, 1))
            requiredItems.add(Pair(ItemID.STEEL_DAGGER, 1))
            requiredItems.add(Pair(ItemID.STEEL_SWORD, 1))
            requiredItems.add(Pair(ItemID.STEEL_LONGSWORD, 1))
            requiredItems.add(Pair(ItemID.STEEL_SCIMITAR, 1))
        }
        if (attackLevel >= 10) {
            requiredItems.add(Pair(ItemID.BLACK_2H_SWORD, 1))
            requiredItems.add(Pair(ItemID.BLACK_DAGGER, 1))
            requiredItems.add(Pair(ItemID.BLACK_SWORD, 1))
            requiredItems.add(Pair(ItemID.BLACK_LONGSWORD, 1))
            requiredItems.add(Pair(ItemID.BLACK_SCIMITAR, 1))
        }
        if (attackLevel >= 20) {
            requiredItems.add(Pair(ItemID.MITHRIL_2H_SWORD, 1))
            requiredItems.add(Pair(ItemID.MITHRIL_DAGGER, 1))
            requiredItems.add(Pair(ItemID.MITHRIL_SWORD, 1))
            requiredItems.add(Pair(ItemID.MITHRIL_LONGSWORD, 1))
            requiredItems.add(Pair(ItemID.MITHRIL_SCIMITAR, 1))
        }
        if (attackLevel >= 30) {
            requiredItems.add(Pair(ItemID.ADAMANT_2H_SWORD, 1))
            requiredItems.add(Pair(ItemID.ADAMANT_DAGGER, 1))
            requiredItems.add(Pair(ItemID.ADAMANT_SWORD, 1))
            requiredItems.add(Pair(ItemID.ADAMANT_LONGSWORD, 1))
            requiredItems.add(Pair(ItemID.ADAMANT_SCIMITAR, 1))
        }
        if (attackLevel >= 40) {
            requiredItems.add(Pair(ItemID.RUNE_2H_SWORD, 1))
            requiredItems.add(Pair(ItemID.RUNE_DAGGER, 1))
            requiredItems.add(Pair(ItemID.RUNE_SWORD, 1))
            requiredItems.add(Pair(ItemID.RUNE_LONGSWORD, 1))
            requiredItems.add(Pair(ItemID.RUNE_SCIMITAR, 1))
        }

//        val strengthLevel = player.getSkillLevel(Skill.STRENGTH)
//        if (strengthLevel >= 1) {
//            requiredItems.add(Pair(ItemID.BRONZE_PICKAXE, 1))
//            requiredItems.add(Pair(ItemID.IRON_PICKAXE, 1))
//        }
//        if (strengthLevel >= 5) {
//            requiredItems.add(Pair(ItemID.STEEL_PICKAXE, 1))
//        }
//        if (strengthLevel >= 10) {
//            requiredItems.add(Pair(ItemID.BLACK_PICKAXE, 1))
//        }
//        if (strengthLevel >= 20) {
//            requiredItems.add(Pair(ItemID.MITHRIL_PICKAXE, 1))
//        }
//        if (strengthLevel >= 30) {
//            requiredItems.add(Pair(ItemID.ADAMANT_PICKAXE, 1))
//        }
//        if (strengthLevel >= 40) {
//            requiredItems.add(Pair(ItemID.RUNE_PICKAXE, 1))
//        }
//
//        val defenceLevel = player.getSkillLevel(Skill.DEFENCE)
//        if (defenceLevel >= 1) {
//            requiredItems.add(Pair(ItemID.BRONZE_PICKAXE, 1))
//            requiredItems.add(Pair(ItemID.IRON_PICKAXE, 1))
//        }
//        if (defenceLevel >= 5) {
//            requiredItems.add(Pair(ItemID.STEEL_PICKAXE, 1))
//        }
//        if (defenceLevel >= 10) {
//            requiredItems.add(Pair(ItemID.BLACK_PICKAXE, 1))
//        }
//        if (defenceLevel >= 20) {
//            requiredItems.add(Pair(ItemID.MITHRIL_PICKAXE, 1))
//        }
//        if (defenceLevel >= 30) {
//            requiredItems.add(Pair(ItemID.ADAMANT_PICKAXE, 1))
//        }
//        if (defenceLevel >= 40) {
//            requiredItems.add(Pair(ItemID.RUNE_PICKAXE, 1))
//        }
        return requiredItems
    }

    override fun shouldTrain(): Boolean {
        val player = Alfred.api.players().localPlayer
        val attackLevel = player.getSkillLevel(Skill.ATTACK)
        val strengthLevel = player.getSkillLevel(Skill.STRENGTH)
        val defenceLevel = player.getSkillLevel(Skill.DEFENCE)

        if (attackLevel < config.attackLevel()) {
            return true
        } else if (strengthLevel < config.strengthLevel()) {
            return true
        } else if (defenceLevel < config.defenseLevel()) {
            return true
        }
        return false
    }

    override fun process(): Boolean {
        val minimumSkillRequirement = minimumSkillRequirement

        if (minimumSkillRequirement < 10) {
            Alfred.setTaskSubStatus("Fighting Chickens")
            GerberThread.countLabel = "Chickens Killed"
            fightNPC(LUMBRIDGE_CHICKENS_WORLD_AREA, WorldDestinations.LUMBRIDGE_CHICKENS.worldPoint, "chicken", listOf(ItemID.FEATHER, ItemID.BONES))

        } else if (minimumSkillRequirement < 20) {
            Alfred.setTaskSubStatus("Fighting Cows")
            GerberThread.countLabel = "Cows Killed"
            fightNPC(LUMBRIDGE_COWS_WORLD_AREA, WorldDestinations.LUMBRIDGE_COWS.worldPoint, "cow", listOf(ItemID.COWHIDE, ItemID.BONES))

        } else {
            return false
        }

        return true
    }

    private val minimumSkillRequirement: Int
        get() {
            val player = Alfred.api.players().localPlayer
            return listOf(player.getSkillLevel(Skill.ATTACK), player.getSkillLevel(Skill.STRENGTH), player.getSkillLevel(Skill.DEFENCE)).min()
        }

    private fun fightNPC(worldArea: WorldArea, worldPoint: WorldPoint, npcName: String, itemLootNames: List<Int>) {
        val player = Alfred.api.players().localPlayer

        if (player.isMoving || player.isInteracting) {
            return
        }

        when (scriptState) {
            ScriptState.SETUP -> {
                if (!isWieldingRequiredWeapon) {
                    val weapon = getRequiredWeaponFromInventory
                    if (weapon != null) {
                        val inventoryCount = Alfred.api.inventory().count()
                        weapon.leftClick()
                        Alfred.sleepUntil({ Alfred.api.inventory().count() == inventoryCount - 1 }, 100, 3000)
                    }
                } else {
                    scriptState = ScriptState.WAITING
                }
            }

            ScriptState.WAITING -> {
                if (!worldArea.contains(player.worldLocation)) {
                    Alfred.api.walk().walkTo(worldPoint)
                }
                scriptState = ScriptState.FIGHTING
            }

            ScriptState.FIGHTING -> {
                setCombatStyle()
                if (Alfred.tasks.npc.findAndAttack(npcName)) {
                    GerberThread.count++
                    scriptState = ScriptState.LOOTING
                }
            }

            ScriptState.LOOTING -> {
                if (config.collectItems()) {
                    if (Alfred.api.inventory().isFull) {
                        buryBones()
                        Alfred.sleep(200)
                    }

                    if (Alfred.api.inventory().isFull) {
                        Alfred.api.walk().walkTo(getBankLocation())
                        Alfred.tasks.banking.depositInventory()
                        Alfred.sleep(200)
                    }

                    Alfred.tasks.item.findAndLootItems(itemLootNames, 2)
                }
                scriptState = ScriptState.WAITING
            }
        }

    }

    private fun setCombatStyle() {
        val player = Alfred.api.players().localPlayer

        val attackLevel = player.getSkillLevel(Skill.ATTACK)
        val strengthLevel = player.getSkillLevel(Skill.STRENGTH)
        val defenceLevel = player.getSkillLevel(Skill.DEFENCE)

        if (attackLevel < config.attackLevel()) {
            if (!Alfred.api.combat().isPunchSelected) {
                Alfred.api.combat().clickPunch()
            }
        } else if (strengthLevel < config.strengthLevel()) {
            if (!Alfred.api.combat().isKickSelected) {
                Alfred.api.combat().clickKick()
            }
        } else if (defenceLevel < config.defenseLevel()) {
            if (!Alfred.api.combat().isBlockSelected) {
                Alfred.api.combat().clickBlock()
            }
        }
    }

    private fun buryBones() {
        val player = Alfred.api.players().localPlayer
        if (!config.buryBones() || player.getSkillLevel(Skill.PRAYER) >= config.prayerLevel()) {
            return
        }
        for (item in Alfred.api.inventory().getItems("bones")) {
            item.leftClick()
            Alfred.sleep(1000)
            Alfred.sleepUntil({ !player.isMoving && !player.isInteracting && player.isIdle }, 200, 1000 * 30)
        }
    }

    private val isWieldingRequiredWeapon: Boolean
        get() {
            Alfred.setStatus("Checking for weapon")
            if (!Alfred.api.equipment().isWeaponEquipped) {
                return false
            }
            return getRequiredItems().map { thing: Pair<Int, Int> -> thing.first }.contains(Alfred.api.equipment().weaponId)
        }
    private val getRequiredWeaponFromInventory: RSInventoryItem?
        get() {
            val inventoryItems = Alfred.api.inventory().items
            getRequiredItems()
                .map { thing: Pair<Int, Int> -> thing.first }
                .forEach { requiredItemId: Int ->
                    inventoryItems.forEach { rsInventoryItem: RSInventoryItem ->
                        if (rsInventoryItem.id == requiredItemId) {
                            return rsInventoryItem
                        }
                    }
                }
            return null
        }
}

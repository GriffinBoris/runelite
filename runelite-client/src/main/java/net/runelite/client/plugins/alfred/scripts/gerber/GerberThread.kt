package net.runelite.client.plugins.alfred.scripts.gerber

import net.runelite.api.GameState
import net.runelite.api.Skill
import net.runelite.client.plugins.alfred.Alfred
import net.runelite.client.plugins.alfred.scripts.gerber.tasks.*
import net.runelite.client.plugins.alfred.util.PlayTimer

class GerberThread(private var config: GerberConfig) : Thread() {

    companion object {
        val overallTimer = PlayTimer()
        val taskTimer = PlayTimer()
        var countLabel = ""
        var count = 0
    }

    override fun run() {
        login()
        Alfred.api.camera.setPitch(1.0f)
        Alfred.api.camera.setYaw(315)

        var trainableSkills = setupTrainableSkills()

        overallTimer.setRandomTimeout(config.minTotalTime(), config.maxTotalTime())
        overallTimer.start()

        while (!overallTimer.isTimerComplete) {

            if (trainableSkills.isEmpty()) {
                trainableSkills = setupTrainableSkills()
                if (trainableSkills.isEmpty()) {
                    break
                }
            }

            val skillToTrain = trainableSkills.removeAt(0)

            taskTimer.reset()
            taskTimer.setRandomTimeout(config.minTaskTime(), config.maxTaskTime())
            taskTimer.start()

            when (skillToTrain) {
                Combat::class.toString() -> {
                    if (config.trainCombat()) {
                        Combat(config).run()
                    }
                }

                Mining::class.toString() -> {
                    if (config.trainMining()) {
                        Mining(config).run()
                    }
                }

                Woodcutting::class.toString() -> {
                    if (config.trainWoodcutting()) {
                        Woodcutting(config).run()
                    }
                }

                Fishing::class.toString() -> {
                    if (config.trainFishing()) {
                        Fishing(config).run()
                    }
                }

                Agility::class.toString() -> {
                    if (config.trainAgility()) {
                        Agility(config).run()
                    }
                }
            }

            Alfred.api.camera.setPitch(1.0f)
            Alfred.api.camera.setYaw(315)
        }

        logout()
    }

    private fun setupTrainableSkills(): MutableList<String> {
        val trainableSkills: MutableSet<String> = mutableSetOf()
        val player = Alfred.api.players.localPlayer

        if (player.getSkillLevel(Skill.ATTACK) < config.attackLevel()) {
            trainableSkills.add(Combat::class.toString())
        }

        if (player.getSkillLevel(Skill.STRENGTH) < config.strengthLevel()) {
            trainableSkills.add(Combat::class.toString())
        }

        if (player.getSkillLevel(Skill.DEFENCE) < config.defenseLevel()) {
            trainableSkills.add(Combat::class.toString())
        }

        if (player.getSkillLevel(Skill.MINING) < config.miningLevel()) {
            trainableSkills.add(Mining::class.toString())
        }

        if (player.getSkillLevel(Skill.WOODCUTTING) < config.woodcuttingLevel()) {
            trainableSkills.add(Woodcutting::class.toString())
        }

        if (player.getSkillLevel(Skill.FISHING) < config.fishingLevel()) {
            trainableSkills.add(Fishing::class.toString())
        }

        if (player.getSkillLevel(Skill.AGILITY) < config.agilityLevel()) {
            trainableSkills.add(Agility::class.toString())
        }

        val skillsList = trainableSkills.toMutableList()
        skillsList.shuffle()
        return skillsList
    }

    private fun login() {
        if (Alfred.client.getGameState() != GameState.LOGGED_IN) {
            Alfred.api.account.login(config.worldNumber())
            Alfred.sleep(2000)
        }
    }

    private fun logout() {
        if (Alfred.client.getGameState() == GameState.LOGGED_IN) {
            Alfred.api.account.logout()
        }
    }
}

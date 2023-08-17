package net.runelite.client.plugins.alfred.api.rs.camera

import net.runelite.api.coords.WorldPoint
import net.runelite.client.plugins.alfred.Alfred
import java.awt.event.KeyEvent
import kotlin.math.abs
import kotlin.math.atan2

class RSCameraHelper {
    private val dumbCameraAngle: Int
        get() {
            val cameraAngle = abs(Alfred.getClient().getCameraYaw() / 45.51 * 8).toInt()
            return (360 + cameraAngle) % 360
        }

    private fun getAngleBetweenPoints(point1: WorldPoint, point2: WorldPoint): Int {
        return Math.toDegrees(atan2((point1.y - point2.y).toDouble(), (point1.x - point2.x).toDouble())).toInt()
    }

    private fun getAngleDistance(angle1: Int, angle2: Int): Int {
        val distance = angle1 - angle2
        return (90 - distance) % 360
    }

    private fun isCameraAngleInRange(worldPoint: WorldPoint, range: Int): Boolean {
        val cameraAngle = dumbCameraAngle
        val angleToLocation = getAngleBetweenPoints(Alfred.getClient().getLocalPlayer().getWorldLocation(), worldPoint)
        val distance = getAngleDistance(cameraAngle, angleToLocation)
        return abs(distance.toDouble()) <= range
    }

    private fun internalLookAt(worldPoint: WorldPoint) {
        val cameraAngle = dumbCameraAngle
        val angleToLocation = getAngleBetweenPoints(Alfred.getClient().getLocalPlayer().getWorldLocation(), worldPoint)
        val distance = getAngleDistance(cameraAngle, angleToLocation)
        val turnLeft = distance in 1..179

        if (turnLeft) {
            Alfred.getKeyboard().holdArrowKey(KeyEvent.VK_RIGHT)
            Alfred.sleepUntilExecution({ Alfred.getKeyboard().releaseArrowKey(KeyEvent.VK_RIGHT) }, { isCameraAngleInRange(worldPoint, 15) }, 10, 1000 * 30)
        } else {
            Alfred.getKeyboard().holdArrowKey(KeyEvent.VK_LEFT)
            Alfred.sleepUntilExecution({ Alfred.getKeyboard().releaseArrowKey(KeyEvent.VK_LEFT) }, { isCameraAngleInRange(worldPoint, 15) }, 10, 1000 * 30)
        }
    }

    fun setYaw(angle: Int) {
        val cameraAngle = dumbCameraAngle
        val distance = getAngleDistance(cameraAngle, angle)
        val turnLeft = distance in 1..179

        if (turnLeft) {
            Alfred.getKeyboard().holdArrowKey(KeyEvent.VK_RIGHT)
            Alfred.sleepUntilExecution({ Alfred.getKeyboard().releaseArrowKey(KeyEvent.VK_RIGHT) }, { getAngleDistance(dumbCameraAngle, angle) <= 5 }, 10, 1000 * 30)
        } else {
            Alfred.getKeyboard().holdArrowKey(KeyEvent.VK_LEFT)
            Alfred.sleepUntilExecution({ Alfred.getKeyboard().releaseArrowKey(KeyEvent.VK_LEFT) }, { getAngleDistance(dumbCameraAngle, angle) <= 5 }, 10, 1000 * 30)
        }
    }

    fun lookAt(worldPoint: WorldPoint) {
        internalLookAt(worldPoint)
    }

    private fun cameraPitchPercentage(): Float {
        val minPitch = 128
        val maxPitch = 383
        val currentPitch = Alfred.getClient().cameraPitch

        val adjustedPitch = currentPitch - minPitch
        val adjustedMaxPitch = maxPitch - minPitch

        return adjustedPitch.toFloat() / adjustedMaxPitch.toFloat()
    }

    fun setPitch(percentage: Float) {
        val currentPercentage = cameraPitchPercentage()

        if (currentPercentage < percentage) {
            Alfred.getKeyboard().holdArrowKey(KeyEvent.VK_UP)
            Alfred.sleepUntilExecution({ Alfred.getKeyboard().releaseArrowKey(KeyEvent.VK_UP) }, { cameraPitchPercentage() >= percentage }, 10, 1000 * 30)
        } else {
            Alfred.getKeyboard().holdArrowKey(KeyEvent.VK_DOWN)
            Alfred.sleepUntilExecution({ Alfred.getKeyboard().releaseArrowKey(KeyEvent.VK_DOWN) }, { cameraPitchPercentage() <= percentage }, 10, 1000 * 30)
        }
    }
}
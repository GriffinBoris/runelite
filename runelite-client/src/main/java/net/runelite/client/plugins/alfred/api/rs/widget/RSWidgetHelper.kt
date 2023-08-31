package net.runelite.client.plugins.alfred.api.rs.widget

import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.alfred.Alfred

class RSWidgetHelper {
    private fun internalGetWidget(widgetId: Int): Widget? {
        return Alfred.clientThread.invokeOnClientThread { Alfred.client.getWidget(widgetId) }
    }

    fun getWidget(widgetInfo: WidgetInfo): Widget? {
        return internalGetWidget(widgetInfo.id)
    }

    fun getWidget(widgetId: Int): Widget? {
        return internalGetWidget(widgetId)
    }

    private fun internalGetChildWidget(widgetId: Int, childId: Int): Widget? {
        return Alfred.clientThread.invokeOnClientThread {
            val parentWidget = internalGetWidget(widgetId) ?: return@invokeOnClientThread null
            parentWidget.getChild(childId)
        }
    }

    fun getChildWidget(widgetInfo: WidgetInfo, childId: Int): Widget? {
        return internalGetChildWidget(widgetInfo.id, childId)
    }

    fun getChildWidget(widgetId: Int, childId: Int): Widget? {
        return internalGetChildWidget(widgetId, childId)
    }

    private fun internalClickWidget(widget: Widget?, rightClick: Boolean): Boolean {
        widget ?: return false
        widget.bounds ?: return false
        if (widget.isHidden || widget.isSelfHidden) {
            return false
        }

        if (rightClick) {
            Alfred.mouse.rightClick(widget.bounds)
        } else {
            Alfred.mouse.leftClick(widget.bounds)
        }
        return true
    }

    fun leftClickWidget(widget: Widget): Boolean {
        return internalClickWidget(widget, false)
    }

    fun leftClickWidget(widgetInfo: WidgetInfo): Boolean {
        return internalClickWidget(getWidget(widgetInfo), false)
    }

    fun leftClickWidget(widgetId: Int): Boolean {
        return internalClickWidget(getWidget(widgetId), false)
    }

    fun rightClickWidget(widget: Widget): Boolean {
        return internalClickWidget(widget, true)
    }

    fun rightClickWidget(widgetInfo: WidgetInfo): Boolean {
        return internalClickWidget(getWidget(widgetInfo), true)
    }

    fun rightClickWidget(widgetId: Int): Boolean {
        return internalClickWidget(getWidget(widgetId), true)
    }

    private fun internalGetAllWidgets(widget: Widget): List<Widget> {
        return Alfred.clientThread.invokeOnClientThread {
            val foundWidgets: MutableList<Widget> = mutableListOf()

            widget.children?.forEach { childWidget ->
                foundWidgets.add(childWidget)
                foundWidgets.addAll(internalGetAllWidgets(childWidget))
            }

            widget.nestedChildren.forEach { childWidget ->
                foundWidgets.add(childWidget)
                foundWidgets.addAll(internalGetAllWidgets(childWidget))
            }

            widget.staticChildren.forEach { childWidget ->
                foundWidgets.add(childWidget)
                foundWidgets.addAll(internalGetAllWidgets(childWidget))
            }

            widget.dynamicChildren.forEach { childWidget ->
                foundWidgets.add(childWidget)
                foundWidgets.addAll(internalGetAllWidgets(childWidget))
            }

            return@invokeOnClientThread foundWidgets
        }
    }

    val allWidgets: List<Widget>
        get() = Alfred.clientThread.invokeOnClientThread<List<Widget>> {
            val foundWidgets: MutableList<Widget> = mutableListOf()

            Alfred.client.widgetRoots.forEach { rootWidget ->
                foundWidgets.add(rootWidget)
                foundWidgets.addAll(internalGetAllWidgets(rootWidget))
            }

            return@invokeOnClientThread foundWidgets
        }
}

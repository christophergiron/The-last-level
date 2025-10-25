package godot
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.CanvasLayer
import godot.api.Node
import godot.core.Callable
import godot.core.Callable.Companion.invoke
import godot.core.StringName
import godot.core.VariantArray
import godot.core.VariantArray.Companion.invoke

@RegisterClass
class Retry_menu : CanvasLayer() {

	private val MAIN_MENU_FILE = "res://Objects/UI_Screens/Title.tscn"
    private var swoosh: AudioStreamPlayer? = null

	@RegisterFunction
	override fun _ready() {
		getTree()?.paused = true
	}

    private fun delayedSceneChange(scenePath: String) {
        swoosh?.play()

        val tree = getTree() ?: return
        val timer = tree.createTimer(0.4)

        val args = VariantArray<Any?>()
        args.append(scenePath)

        timer?.connect(
            StringName("timeout"),
            Callable(this, StringName("_on_delay_finished")).bindv(args)
        )
    }

    @RegisterFunction
    fun _on_delay_finished(scenePath: String) {
        getTree()?.paused = false
        getTree()?.changeSceneToFile(scenePath)
    }

	@RegisterFunction
	fun _on_restart_button_down() {
		getTree()?.paused = false
		getTree()?.callDeferred("reload_current_scene")
	}

	@RegisterFunction
	fun _on_title_button_down(){
        val root = getTree()?.root
        val music = root?.getNodeOrNull("MusicManager") as? Node
        val player = music?.getNodeOrNull("AudioStreamPlayer") as? AudioStreamPlayer
        player?.play()

        delayedSceneChange(MAIN_MENU_FILE)
	}
}

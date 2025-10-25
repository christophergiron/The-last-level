package godot.UI.Menus
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.CanvasLayer
import godot.core.Callable
import godot.core.Callable.Companion.invoke
import godot.core.StringName
import godot.core.VariantArray
import godot.core.VariantArray.Companion.invoke

@RegisterClass
class Leaderboards : CanvasLayer() {

	private val MAIN_MENU_FILE = "res://Objects/UI_Screens/Title.tscn"
	private val OFFLINE_FILE = "res://Objects/UI_Screens/Leaderboard_Screens/Offline.tscn"
	private val ONLINE_FILE = "res://Objects/UI_Screens/Leaderboard_Screens/Online.tscn"
	private var swoosh: AudioStreamPlayer? = null

	@RegisterFunction
	override fun _ready() {
		swoosh = getNodeOrNull("swoosh") as? AudioStreamPlayer
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
		getTree()?.changeSceneToFile(scenePath)
	}

	@RegisterFunction
	fun _on_local_button_down(){
		delayedSceneChange(OFFLINE_FILE)
	}

	@RegisterFunction
	fun _on_online_button_down(){
		delayedSceneChange(ONLINE_FILE)
	}

	@RegisterFunction
	fun _on_back_button_down(){
		delayedSceneChange(MAIN_MENU_FILE)
	}
}

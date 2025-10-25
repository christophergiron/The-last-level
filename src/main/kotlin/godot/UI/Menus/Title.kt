package godot.UI.Menus

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.CanvasLayer
import godot.api.Node
import godot.core.Callable
import godot.core.StringName
import godot.core.VariantArray
import godot.global.GD

@RegisterClass
class Title : CanvasLayer() {

	private val GAME_FILE = "res://Objects/main_scene.tscn"
	private val LEAD_FILE = "res://Objects/UI_Screens/Leaderboards_screen.tscn"
	private val CREATE_USER_FILE = "res://Objects/UI_Screens/NicknameCreator.tscn"
	private var swoosh: AudioStreamPlayer? = null
	private var deacuerdo: AudioStreamPlayer? = null

	@RegisterFunction
	override fun _ready() {
		swoosh = getNodeOrNull("swoosh") as? AudioStreamPlayer
		deacuerdo = getNodeOrNull("deacuerdo") as? AudioStreamPlayer
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
	fun _on_start_button_down() {
		val root = getTree()?.root
		val music = root?.getNodeOrNull("MusicManager") as? Node
		val player = music?.getNodeOrNull("AudioStreamPlayer") as? AudioStreamPlayer
		player?.stop()

		delayedSceneChange(GAME_FILE)
	}

	@RegisterFunction
	fun _on_leaderbords_button_down() {
		delayedSceneChange(LEAD_FILE)
	}

	@RegisterFunction
	fun _on_story_button_down() {
		GD.print("Detecta")
		swoosh?.play()
	}

	@RegisterFunction
	fun _on_change_user_button_down() {
		delayedSceneChange(CREATE_USER_FILE)
	}

	@RegisterFunction
	fun _on_paypal_button_down() {
		GD.print("Detecta")
		deacuerdo?.play()
	}
}

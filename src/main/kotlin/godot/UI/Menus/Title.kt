package godot.UI.Menus

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.CanvasLayer
import godot.global.GD

@RegisterClass
class Title : CanvasLayer() {

	private val GAME_FILE = "res://Objects/main_scene.tscn"
	private val LEAD_FILE = "res://Objects/UI_Screens/Leaderboards_screen.tscn"
	private val CREATE_USER_FILE = "res://Objects/UI_Screens/NicknameCreator.tscn"
	private var swoosh: AudioStreamPlayer? = null

	@RegisterFunction
	override fun _ready() {
		swoosh = getNodeOrNull("swoosh") as? AudioStreamPlayer
	}

	@RegisterFunction
	fun _on_start_button_down() {
		swoosh?.play()
		getTree()?.callDeferred("change_scene_to_file", GAME_FILE)
	}

	@RegisterFunction
	fun _on_leaderbords_button_down() {
		swoosh?.play()
		getTree()?.callDeferred("change_scene_to_file", LEAD_FILE)
	}

	@RegisterFunction
	fun _on_story_button_down() {
		swoosh?.play()
		GD.print("Detecta")
	}

	@RegisterFunction
	fun _on_change_user_button_down() {
		swoosh?.play()
		getTree()?.callDeferred("change_scene_to_file", CREATE_USER_FILE)
	}

	@RegisterFunction
	fun _on_paypal_button_down() {
		swoosh?.play()
		GD.print("Detecta")

	}
}

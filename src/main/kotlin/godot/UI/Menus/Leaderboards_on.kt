package godot.UI.Menus
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.CanvasLayer

@RegisterClass
class Leaderboards_on : CanvasLayer() {

	private val LEAD_MENU_FILE = "res://Objects/UI_Screens/Leaderboards_screen.tscn"
	private var swoosh: AudioStreamPlayer? = null

	@RegisterFunction
	override fun _ready() {
		swoosh = getNodeOrNull("swoosh") as? AudioStreamPlayer
	}

	@RegisterFunction
	fun _on_atras_button_down() {
		swoosh?.play()
		getTree()?.changeSceneToFile(LEAD_MENU_FILE)
	}
}

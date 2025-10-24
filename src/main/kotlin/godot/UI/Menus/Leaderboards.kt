package godot.UI.Menus
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.CanvasLayer

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

	@RegisterFunction
	fun _on_local_button_down(){
        swoosh?.play()
		getTree()?.changeSceneToFile(OFFLINE_FILE)
	}

	@RegisterFunction
	fun _on_online_button_down(){
        swoosh?.play()
		getTree()?.changeSceneToFile(ONLINE_FILE)

	}

	@RegisterFunction
	fun _on_back_button_down(){
        swoosh?.play()
		getTree()?.changeSceneToFile(MAIN_MENU_FILE)
	}
}

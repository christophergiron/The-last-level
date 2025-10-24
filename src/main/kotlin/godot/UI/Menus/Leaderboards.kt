package godot.UI.Menus
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.CanvasLayer

@RegisterClass
class Leaderboards : CanvasLayer() {

	private val MAIN_MENU_FILE = "res://Objects/UI_Screens/Title.tscn"
	private val OFFLINE_FILE = "res://Objects/UI_Screens/Leaderboard_Screens/Offline.tscn"
	private val ONLINE_FILE = "res://Objects/UI_Screens/Leaderboard_Screens/Online.tscn"

	@RegisterFunction
	override fun _ready() {
	}

	@RegisterFunction
	fun _on_local_button_down(){
		getTree()?.changeSceneToFile(OFFLINE_FILE)
	}

	@RegisterFunction
	fun _on_online_button_down(){
		getTree()?.changeSceneToFile(ONLINE_FILE)
	}

	@RegisterFunction
	fun _on_back_button_down(){
		getTree()?.changeSceneToFile(MAIN_MENU_FILE)
	}
}

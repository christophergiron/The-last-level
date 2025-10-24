package godot
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.CanvasLayer

@RegisterClass
class Retry_menu : CanvasLayer() {

	private val MAIN_MENU_FILE = "res://Objects/UI_Screens/Title.tscn"

	@RegisterFunction
	override fun _ready() {
		getTree()?.paused = true
	}

	@RegisterFunction
	fun _on_restart_button_down() {
		getTree()?.paused = false
		getTree()?.callDeferred("reload_current_scene")
	}

	@RegisterFunction
	fun _on_title_button_down(){
		getTree()?.paused = false
		getTree()?.changeSceneToFile(MAIN_MENU_FILE)
	}
}

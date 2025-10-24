package godot.UI.Menus
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.CanvasLayer
import godot.global.GD

@RegisterClass
class NickCreator : CanvasLayer() {

	private val MAIN_MENU_FILE = "res://Objects/UI_Screens/Title.tscn"

	@RegisterFunction
	override fun _ready() {
	}

	@RegisterFunction
	fun _on_ok_button_down(){
		GD.print("Ok")
	}
	
	@RegisterFunction
	fun _on_cancel_button_down(){
		getTree()?.changeSceneToFile(MAIN_MENU_FILE)
	}
}

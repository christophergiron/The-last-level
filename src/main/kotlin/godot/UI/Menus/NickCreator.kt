package godot.UI.Menus
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.CanvasLayer
import godot.global.GD

@RegisterClass
class NickCreator : CanvasLayer() {

	private val MAIN_MENU_FILE = "res://Objects/UI_Screens/Title.tscn"
    private var swoosh: AudioStreamPlayer? = null

	@RegisterFunction
	override fun _ready() {
        swoosh = getNodeOrNull("swoosh") as? AudioStreamPlayer
	}

	@RegisterFunction
	fun _on_ok_button_down(){
        swoosh?.play()
		GD.print("Ok")
	}
	
	@RegisterFunction
	fun _on_cancel_button_down(){
        swoosh?.play()
		getTree()?.changeSceneToFile(MAIN_MENU_FILE)
	}
}

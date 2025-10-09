package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Node2D
import godot.global.GD

@RegisterClass
class Main : Node2D() {

	@RegisterFunction
	override fun _ready() {
		GD.print("Main cargado correctamente")
	}
}

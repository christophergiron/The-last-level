package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Node2D
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class PlumberMan : Node2D() {

	private var direction = 1
	private val speed = 275.0
	private var minY = 100.0
	private var maxY = 575.0

	@RegisterFunction
	override fun _ready() {
		GD.print("PlumberMan cargado correctamente")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		moverVertical(delta)
	}

	private fun moverVertical(delta: Double) {

		position = Vector2(position.x, position.y + direction * speed * delta)

		if (position.y < minY) direction = 1
		if (position.y > maxY) direction = -1
	}
}

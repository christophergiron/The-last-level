package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Node2D
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class PlumberMan : Node2D() {

	private var direction = 1       // 1 = sube, -1 = baja
	private val speed = 100.0       // velocidad de movimiento vertical
	private var minY = 100.0        // límite inferior
	private var maxY = 700.0        // límite superior

	@RegisterFunction
	override fun _ready() {
		GD.print("PlumberMan cargado correctamente")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		moverVertical(delta)
	}

	private fun moverVertical(delta: Double) {
		// Mueve el personaje en Y
		position = Vector2(position.x, position.y + direction * speed * delta)

		// Si llega a los límites, cambia de dirección
		if (position.y < minY) direction = 1
		if (position.y > maxY) direction = -1
	}
}

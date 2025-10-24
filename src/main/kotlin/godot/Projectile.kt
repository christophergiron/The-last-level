package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Area2D
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class Projectile : Area2D() {

	var velocidad = 400.0 // velocidad de movimiento

	@RegisterFunction
	override fun _process(delta: Double) {
		// Mover el proyectil hacia la izquierda
		position += Vector2(-velocidad * delta, 0.0)

		// Eliminar si sale de la pantalla
		if (position.x < -100.0) {
			GD.print("Proyectil eliminado fuera de pantalla")
			queueFree()
		}
	}
}


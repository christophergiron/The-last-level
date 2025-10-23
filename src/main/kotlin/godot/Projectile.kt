package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Area2D
import godot.core.Vector2

@RegisterClass
class Projectile : Area2D() {

	var velocidad = 300.0

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		position += Vector2(velocidad * delta, 0.0)
	}
}

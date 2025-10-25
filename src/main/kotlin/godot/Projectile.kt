package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AnimationPlayer
import godot.api.Area2D
import godot.api.Node
import godot.api.Node2D
import godot.core.StringName
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class Projectile : Area2D() {

	private var speed = 600.0
	private var direction = Vector2(-1.0, 0.0)
	private var animationLlave: AnimationPlayer? = null

	@RegisterFunction
	override fun _ready() {
		animationLlave= getNodeOrNull("AnimationLlave") as? AnimationPlayer
		animationLlave?.play("Llave")
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		position += direction * speed * delta
	}

	@RegisterFunction
	fun configurar(vel: Double, dir: Vector2) {
		speed = vel
		direction = dir.normalized()
	}

	@RegisterFunction
	fun _on_area_2d_body_entered(body: Node) {
		if (body.isInGroup("Player")) {
			GD.print("Cucca fue alcanzada — GAME OVER")
			val cucca = body as? Node
			val dieMethod = StringName("die")
			if (cucca != null && cucca.hasMethod(dieMethod)) {
				cucca.call(dieMethod)
				GD.print("Método 'die()' ejecutado correctamente.")
			}
		}
	}
}

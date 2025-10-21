package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Input
import godot.api.Node
import godot.api.RigidBody2D
import godot.core.MouseButton
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class Cucca : RigidBody2D() {

	private val jumpPower = 450.0
	private val maxFallSpeed = 600.0

	@RegisterFunction
	override fun _ready() {
		GD.print("Cucca cargada exitosamente")
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		handleInput()
		if (linearVelocity.y > maxFallSpeed) {
			linearVelocity = Vector2(linearVelocity.x, maxFallSpeed)
		}
	}

	private fun handleInput() {
		if (Input.isActionJustPressed("ui_accept") || Input.isMouseButtonPressed(MouseButton.LEFT)) {
			linearVelocity = Vector2(linearVelocity.x, 0.0)
			applyCentralImpulse(Vector2(0.0, -jumpPower))
		}
	}
}

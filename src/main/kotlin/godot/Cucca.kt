package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Input
import godot.api.Node2D
import godot.core.Color
import godot.core.MouseButton
import godot.core.Rect2
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class Cucca : Node2D() {

	private var velocityY = 0.0
	private val gravity = 1000.0
	private val jumpForce = -350.0
	private val width = 50.0
	private val height = 50.0

	@RegisterFunction
	override fun _ready() {
		position = Vector2(200.0, 400.0)
		GD.print("Cucca cargada correctamente")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		applyGravity(delta)
		handleInput()
		queueRedraw() // Redibuja cada frame
	}

	private fun applyGravity(delta: Double) {
		velocityY += gravity * delta
		position = Vector2(position.x, position.y + velocityY * delta)

		// Evita que caiga más allá del suelo
		if (position.y > 600.0) {
			position = Vector2(position.x, 600.0)
			velocityY = 0.0
		}
	}

	private fun handleInput() {
		// Salto con teclado o pantalla táctil
		if (Input.isActionJustPressed("ui_accept") || Input.isMouseButtonPressed(MouseButton.LEFT)) {
			velocityY = jumpForce
		}
	}

	@RegisterFunction
	override fun _draw() {
		drawRect(
			Rect2(Vector2(-width / 2, -height / 2), Vector2(width, height)),
			Color(1.0, 0.8, 0.2)
		)
	}
}

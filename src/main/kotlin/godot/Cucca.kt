package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Input
import godot.api.Node
import godot.api.PackedScene
import godot.api.ResourceLoader.load
import godot.api.RigidBody2D
import godot.core.StringName
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class Cucca : RigidBody2D() {

	private val RETRY_MENU = "res://Objects/Hud/RestartMenu.tscn"
	private val jumpPower = 450.0
	private val maxFallSpeed = 600.0
	private var isDead = false

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
		if (Input.isActionJustPressed("Jump")) {
			linearVelocity = Vector2(linearVelocity.x, 0.0)
			applyCentralImpulse(Vector2(0.0, -jumpPower))
		}
	}

	@RegisterFunction
	fun _on_floordetector_body_entered(body: Node) {
		GD.print(" Señal recibida de: ${body.name}")
		// Si tocamos el suelo, guardamos y subimos puntaje
		if (body.name == StringName("Piso")) {
			val timerNode = getTree()?.currentScene?.findChild("Game Controller", true, false)
			timerNode?.call("save_current_time")
		}

		if (body.isInGroup("death_zone")) {
			GD.print(" Cucca cayó en zona de muerte")
			die()
		}
	}

	@RegisterFunction
	fun die() {
		val retry_instance = load("res://Objects/Hud/RestartMenu.tscn") as PackedScene
		val retryInstance = retry_instance.instantiate()

		GD.print("Guardando progreso antes de reiniciar...")

		val timerNode = getTree()?.currentScene?.findChild("Game Controller", true, false)
		timerNode?.call("save_current_time")

		getTree()?.paused = true

		val root = getTree()?.currentScene
		root?.addChild(retryInstance)

		GD.print("Game Over mostrado en pantalla.")
	}
}

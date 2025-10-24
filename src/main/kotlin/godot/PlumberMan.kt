package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.ColorRect
import godot.api.Node2D
import godot.api.Timer
import godot.core.Callable
import godot.core.StringName
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class PlumberMan : Node2D() {

	// Movimiento vertical
	private var direction = 1
	private val speed = 275.0
	private var minY = 100.0
	private var maxY = 575.0

	// Contenedor de proyectiles (hijo del plomero)
	private var projectileContainer: Node2D? = null

	@RegisterFunction
	override fun _ready() {
		GD.print("PlumberMan cargado correctamente")

		// Buscar contenedor y timer
		projectileContainer = getNode("ProjectileContainer") as? Node2D

		val shootTimer = getNode("ShootTimer") as? Timer
		shootTimer?.connect(
			StringName("timeout"),
			Callable(this, StringName("_on_shoot_timer_timeout"))
		)

		projectileContainer?.setPosition(Vector2(0.0, 0.0))
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		moverVertical(delta)
		moverProyectiles(delta)
	}

	private fun moverVertical(delta: Double) {
		val p = getPosition()
		setPosition(Vector2(p.x, p.y + direction * speed * delta))
		val y = getPosition().y
		if (y < minY) direction = 1
		if (y > maxY) direction = -1
	}

	// Disparo activado por el Timer
	@RegisterFunction
	fun _on_shoot_timer_timeout() {
		crearProyectil()
	}

	// Crea un proyectil tipo ColorRect (rectángulo rojo)
	private fun crearProyectil() {
		val proyectil = ColorRect()
		proyectil.setColor(godot.core.Color(1.0, 0.2, 0.2, 1.0)) // rojo visible
		proyectil.setSize(Vector2(30.0, 15.0))

		// Sale desde el frente izquierdo del plomero
		proyectil.setPosition(Vector2(-50.0, 0.0))

		projectileContainer?.addChild(proyectil)
		GD.print("Proyectil creado en Y: ${getPosition().y}")
	}

	// Mueve proyectiles hacia la izquierda y los elimina al salir
	private fun moverProyectiles(delta: Double) {
		val container = projectileContainer ?: return
		container.getChildren().forEach { node ->
			if (node is ColorRect) {
				val pos = node.getPosition()
				node.setPosition(Vector2(pos.x - 400.0 * delta, pos.y))
				if (pos.x < -200.0) { // eliminar al salir de pantalla
					node.queueFree()
				}
			}
		}
	}
}

package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Node2D
import godot.api.PackedScene
import godot.api.ResourceLoader
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class PlumberMan : Node2D() {

	private var direction = 1
	private val speed = 275.0
	private var minY = 100.0
	private var maxY = 575.0

	// Control del lanzamiento
	private var keyScene: PackedScene? = null
	private var throwCount = 0
	private var cycle = 1

	@RegisterFunction
	override fun _ready() {
		keyScene = ResourceLoader.load("res://Objects/Plumber/projectile.tscn") as PackedScene
		GD.print("PlumberMan listo para lanzar llaves")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		moverVertical(delta)
	}

	private fun moverVertical(delta: Double) {
		position.y += direction * speed * delta
		if (position.y < minY) direction = 1
		if (position.y > maxY) direction = -1
	}

	@RegisterFunction
	fun lanzarLlave() {
		keyScene?.let { scene ->
			val llave = scene.instantiate() as Projectile
			getParent()?.addChild(llave)

			llave.position = Vector2(position.x + 50, position.y)

			val velocidad = when (throwCount % 3) {
				0 -> 150.0  // Lenta
				1 -> 300.0  // Normal
				else -> 500.0  // Rápida
			}

			val escala = when (throwCount % 3) {
				0 -> Vector2(1.4, 1.4) // Grande
				1 -> Vector2(1.0, 1.0) // Mediana
				else -> Vector2(0.7, 0.7) // Pequeña
			}

			llave.scale = escala
			llave.velocidad = velocidad

			throwCount++
			if (throwCount % 9 == 0) {
				cycle++
				GD.print("Ciclo reiniciado #$cycle")
			}
		}
	}
}

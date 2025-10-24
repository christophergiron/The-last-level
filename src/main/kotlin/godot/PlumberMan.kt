package godot

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.*
import godot.core.*
import godot.global.GD

@RegisterClass
class PlumberMan : Node2D() {

	private var direction = 1
	private val speed = 275.0
	private var minY = 100.0
	private var maxY = 575.0
	private var projectileContainer: Node2D? = null
	private var throwCount = 0

	@RegisterFunction
	override fun _ready() {
		GD.print("PlumberMan cargado correctamente")

		projectileContainer = getNode("ProjectileContainer") as? Node2D

		val shootTimer = getNode("ShootTimer") as? Timer
		shootTimer?.connect(
			StringName("timeout"),
			Callable(this, StringName("_on_shoot_timer_timeout"))
		)
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

	@RegisterFunction
	fun _on_shoot_timer_timeout() {
		for (i in 0 until 2) {
			getTree()?.createTimer(0.15 * i)?.timeout?.connect {
				crearProyectil()
			}
		}
	}

	private fun crearProyectil() {
		val proyectil = ColorRect()
		val fase = throwCount % 3
		val (velocidad, tamano) = when (fase) {
			0 -> Pair(200.0, Vector2(40.0, 20.0))
			1 -> Pair(400.0, Vector2(30.0, 15.0))
			else -> Pair(650.0, Vector2(20.0, 10.0))
		}

		proyectil.setColor(Color(1.0, 0.2, 0.2, 1.0))
		proyectil.setSize(tamano)
		proyectil.setPosition(Vector2(-50.0, 0.0))

		val angleDeg = GD.randi() % 120 - 60
		val angleRad = Math.toRadians(angleDeg.toDouble())
		val dir = Vector2(-Math.cos(angleRad), Math.sin(angleRad)).normalized()

		proyectil.setMeta("velocidad", velocidad)
		proyectil.setMeta("direccion", dir)

		val hitbox = Area2D()
		val shape = CollisionShape2D()
		val rectShape = RectangleShape2D()
		rectShape.setSize(tamano)
		shape.shape = rectShape
		shape.setPosition(Vector2(tamano.x / 2, tamano.y / 2))
		hitbox.addChild(shape)

		hitbox.connect(
			StringName("body_entered"),
			Callable(this, StringName("_on_projectile_hit"))
		)

		proyectil.addChild(hitbox)
		projectileContainer?.addChild(proyectil)
		throwCount++

		GD.print("Proyectil #$throwCount lanzado → Velocidad: $velocidad, Dirección: $angleDeg°")
	}

	private fun moverProyectiles(delta: Double) {
		val container = projectileContainer ?: return
		container.getChildren().forEach { node ->
			if (node is ColorRect) {
				val pos = node.getPosition()
				val velocidad = node.getMeta("velocidad") as? Double ?: 400.0
				val direccion = node.getMeta("direccion") as? Vector2 ?: Vector2(-1.0, 0.0)
				node.setPosition(pos + direccion * velocidad * delta)
				if (pos.x < -600.0 || pos.y < -300.0 || pos.y > 1200.0) node.queueFree()
			}
		}
	}

	@RegisterFunction
	fun _on_projectile_hit(body: Node) {
		if (body.isInGroup("player")) {
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

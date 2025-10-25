package godot.UI.Menus

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.CanvasLayer
import godot.api.*
import godot.core.Dictionary
import godot.core.VariantArray
import godot.global.GD

@RegisterClass
class Leaderboards_on : CanvasLayer() {

	private val LEAD_MENU_FILE = "res://Objects/UI_Screens/Leaderboards_screen.tscn"
	private lateinit var Principal: Label
	private var swoosh: AudioStreamPlayer? = null
	private var firebaseManager: Node? = null

	@RegisterFunction
	override fun _ready() {
		swoosh = getNodeOrNull("swoosh") as? AudioStreamPlayer
		Principal = getNode("Principal") as Label

		// ✅ buscar el nodo FirebaseManager
		firebaseManager = getTree()?.root?.findChild("FirebaseManager", true, false)

		if (firebaseManager == null) {
			GD.printErr("❌ No se encontró el nodo FirebaseManager en la escena.")
			Principal.text = "Error: Firebase no encontrado."
			return
		}

		// ✅ Pedir los datos a Firebase
		GD.print("📡 Solicitando datos a Firebase...")
		firebaseManager?.call("get_best_times")
	}

	// 🔹 Se llama cuando FirebaseManager termina de cargar los datos
	@RegisterFunction
	fun _on_leaderboard_loaded(data: VariantArray<Dictionary<Any?, Any?>>) {
		try {
			if (data.isEmpty()) {
				Principal.text = "No hay tiempos registrados aún."
				return
			}

			data class Record(val username: String, val time: Double, val timestamp: Double)
			val records = data.mapNotNull {
				val username = it["username"]?.toString() ?: "???"
				val time = it["time"]?.toString()?.toDoubleOrNull() ?: 0.0
				val timestamp = it["timestamp"]?.toString()?.toDoubleOrNull() ?: 0.0
				Record(username, time, timestamp)
			}

			val sorted = records.sortedWith(
				compareByDescending<Record> { it.time }
					.thenByDescending { it.timestamp }
			)

			val sb = StringBuilder()
			for (i in 0 until minOf(5, sorted.size)) {
				val record = sorted[i]
				val formatted = fixTime(record.time)
				sb.append("${i + 1}. ${record.username} — $formatted\n")
			}

			Principal.text = sb.toString().trimEnd()

		} catch (e: Exception) {
			GD.printErr("Error procesando datos de Firebase: ${e.message}")
			Principal.text = "Error al cargar datos."
		}
	}

	@RegisterFunction
	fun fixTime(timeValue: Double): String {
		val totalSeconds = timeValue.toInt()
		val minutes = totalSeconds / 60
		val seconds = totalSeconds % 60
		return "%02d:%02d".format(minutes, seconds)
	}

	@RegisterFunction
	fun _on_atras_button_down() {
		swoosh?.play()
		getTree()?.changeSceneToFile(LEAD_MENU_FILE)
	}
}


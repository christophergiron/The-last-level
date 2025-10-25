package godot.UI.Menus
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.CanvasLayer
import godot.api.FileAccess
import godot.api.JSON
import godot.core.Callable
import godot.core.Callable.Companion.invoke
import godot.core.StringName
import godot.core.VariantArray
import godot.core.VariantArray.Companion.invoke
import godot.global.GD

@RegisterClass
class Leaderboards_off : CanvasLayer() {

	private val LEAD_MENU_FILE = "res://Objects/UI_Screens/Leaderboards_screen.tscn"
	private val BEST_TIMES_PATH = "user://best_times.json"
	private lateinit var Principal: godot.api.Label
	//Sonidos
	private var swoosh: AudioStreamPlayer? = null

	@RegisterFunction
	override fun _ready() {
		swoosh = getNodeOrNull("swoosh") as? AudioStreamPlayer
		Principal = getNode("Principal") as godot.api.Label

		val text = drawLeaderboard()
		Principal.text = text
	}

    private fun delayedSceneChange(scenePath: String) {
        swoosh?.play()

        val tree = getTree() ?: return
        val timer = tree.createTimer(0.4)

        val args = VariantArray<Any?>()
        args.append(scenePath)

        timer?.connect(
            StringName("timeout"),
            Callable(this, StringName("_on_delay_finished")).bindv(args)
        )
    }

    @RegisterFunction
    fun _on_delay_finished(scenePath: String) {
        getTree()?.changeSceneToFile(scenePath)
    }

	@RegisterFunction
	fun drawLeaderboard(): String {
		try {
			if (!FileAccess.fileExists(BEST_TIMES_PATH)) {
				return "No hay tiempos registrados aún."
			}

			val file = FileAccess.open(BEST_TIMES_PATH, FileAccess.ModeFlags.READ)
			val jsonText = file?.getAsText() ?: ""
			file?.close()

			val parsed = JSON.parseString(jsonText)
			if (parsed !is godot.core.VariantArray<*>) return "Archivo JSON inválido."

			data class Record(val username: String, val time: Double, val timestamp: Double)

			val records = parsed.mapNotNull {
				val dict = it as? godot.core.Dictionary<*, *> ?: return@mapNotNull null
				val username = dict["username"]?.toString() ?: "???"
				val time = dict["time"]?.toString()?.toDoubleOrNull() ?: 0.0
				val timestamp = dict["timestamp"]?.toString()?.toDoubleOrNull() ?: 0.0
				Record(username, time, timestamp)
			}

			if (records.isEmpty()) return "No hay tiempos registrados aún."

			val sorted = records.sortedWith(
				compareByDescending<Record> { it.time }
					.thenByDescending { it.timestamp }
			)

			val sb = StringBuilder()
			for (i in 0 until minOf(5, sorted.size)) {
				val (username, time) = sorted[i]
				val formatted = fixTime(time)
				sb.append("${i + 1}. $username — $formatted\n")
			}

			return sb.toString().trimEnd()

		} catch (e: Exception) {
			GD.printErr("Error al leer leaderboard: ${e.message}")
			return "Error al cargar datos."
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
        delayedSceneChange(LEAD_MENU_FILE)
	}
}

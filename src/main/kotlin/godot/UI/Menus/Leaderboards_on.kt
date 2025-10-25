package godot.UI.Menus
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.CanvasLayer
import godot.api.Label
import godot.api.Node
import godot.core.Callable
import godot.core.Dictionary
import godot.core.StringName
import godot.core.VariantArray

@RegisterClass
class Leaderboards_on : CanvasLayer() {

    private val LEAD_MENU_FILE = "res://Objects/UI_Screens/Leaderboards_screen.tscn"
    private var swoosh: AudioStreamPlayer? = null
    private lateinit var principal: Label

    @RegisterFunction
    override fun _ready() {
        swoosh = getNodeOrNull("swoosh") as? AudioStreamPlayer
        principal = getNode("Principal") as Label
        principal.text = "Cargando leaderboard..."

        val firebaseMgr = getNodeOrNull("/root/FirebaseManager") as? Node
        if (firebaseMgr != null) {
            // Conectar señal GDScript -> Kotlin
            firebaseMgr.connect(StringName("best_times_ready"), Callable(this, StringName("_on_best_times_ready")))
            // Solicitar datos
            firebaseMgr.call("request_best_times")
        } else {
            principal.text = "No se encontró FirebaseManager"
        }
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
    fun _on_atras_button_down() {
        delayedSceneChange(LEAD_MENU_FILE)
    }

    @RegisterFunction
    fun _on_best_times_ready(data: VariantArray<Any?>) {
        if (data.isEmpty()) {
            principal.text = "No hay datos online disponibles."
            return
        }

        val sb = StringBuilder()
        val limit = minOf(5, data.size)
        for (i in 0 until limit) {
            val rec = data[i] as? Dictionary<Any?, Any?> ?: continue
            val username = (rec["username"] ?: "???").toString()
            val time = (rec["time"] as? Number)?.toDouble() ?: 0.0
            sb.append("${i + 1}. ${username} — ${formatTime(time)}\n")
        }
        principal.text = sb.toString().trimEnd()
    }

    @RegisterFunction
    fun formatTime(timeValue: Double): String {
        val totalSeconds = timeValue.toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

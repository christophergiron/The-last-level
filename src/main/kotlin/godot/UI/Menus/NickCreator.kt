package godot.UI.Menus
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.AudioStreamPlayer
import godot.api.Button
import godot.api.CanvasLayer
import godot.api.FileAccess
import godot.api.JSON
import godot.api.TextEdit
import godot.global.GD
import java.util.regex.Pattern

@RegisterClass
class NickCreator : CanvasLayer() {
	private val MAIN_MENU_FILE = "res://Objects/UI_Screens/Title.tscn"
	private val SETTINGS_PATH = "user://settings.json"
	//late int para evitar cpmprovaciones para null
	private lateinit var textEdit: TextEdit
	private lateinit var okButton: Button
	private lateinit var cancelButton: Button
	private lateinit var errorLabel: godot.api.Label
	private lateinit var errorTimer: godot.api.Timer
	private lateinit var mainMenuTimer: godot.api.Timer
	//Sonidos
	private var swoosh: AudioStreamPlayer? = null


	@RegisterFunction
	override fun _ready() {
		swoosh = getNodeOrNull("swoosh") as? AudioStreamPlayer
		textEdit = getNode("TextEdit") as TextEdit
		okButton = getNode("Ok") as Button
		cancelButton = getNode("Cancel") as Button
		errorLabel = getNode("TempLabel") as godot.api.Label
		errorTimer = getNode("TempLabel/TempTimer") as godot.api.Timer
		mainMenuTimer = getNode("Ok/GoToMainTimer") as godot.api.Timer

		val username = loadUsername()
		if (username.isNotEmpty()) {
			textEdit.text = username
		} else {
			textEdit.placeholderText = "usuario"
		}
	}

	@RegisterFunction
	fun _on_ok_button_down(){
		swoosh?.play()
		val name = textEdit.text.trim()

		if (!isValidUsername(name)) {
			showMessage("Usename inválido. Usa solo letras, números o '_' y 8 caracteres como maximo!!.")
			return
		}

		saveUsername(name)
		showMessage("Username guardado!!")

		mainMenuTimer.waitTime = 1.0
		mainMenuTimer.start()
	}
	
	@RegisterFunction
	fun _on_cancel_button_down(){
		swoosh?.play()
		getTree()?.changeSceneToFile(MAIN_MENU_FILE)
	}

	@RegisterFunction
	fun isValidUsername(name: String): Boolean {
		if (name.isEmpty() || name.length > 8) return false
		val pattern = Pattern.compile("^[A-Za-z0-9_]+$")
		return pattern.matcher(name).matches()
	}

	@RegisterFunction
	fun escapeJson(str: String): String {
		return str.replace("\\", "\\\\").replace("\"", "\\\"")
	}

	@RegisterFunction
	fun saveUsername(name: String) {
		try {
			val jsonText = "{\"username\":\"${escapeJson(name)}\"}"
			val file = FileAccess.open(SETTINGS_PATH, FileAccess.ModeFlags.WRITE)
			file?.storeString(jsonText)
			file?.close()
			GD.print("Nombre guardado con exito")
		} catch (e: Exception) {
			GD.printErr("Error al guardar Username: ${e.message}")
		}
	}

	@RegisterFunction
	fun loadUsername(): String {
		return try {
			if (!FileAccess.fileExists(SETTINGS_PATH)) return ""
			val file = FileAccess.open(SETTINGS_PATH, FileAccess.ModeFlags.READ)
			val jsonText = file?.getAsText() ?: ""
			file?.close()

			val regex = Regex("\"username\"\\s*:\\s*\"([^\"]*)\"")
			val match = regex.find(jsonText)
			if (match != null && match.groupValues.size > 1) {
				match.groupValues[1]
			} else {
				""
			}
		} catch (e: Exception) {
			GD.printErr("Error al leer el username: ${e.message}")
			""
		}
	}

	@RegisterFunction
	fun showMessage(text: String, duration: Double = 2.0) {
		errorLabel.text = text
		errorLabel.visible = true
		errorTimer.waitTime = duration
		errorTimer.start()
	}

	@RegisterFunction
	fun _on_temp_timer_timeout(){
		errorLabel.visible = false
	}

	@RegisterFunction
	fun _on_go_to_main_timer_timeout() {
		getTree()?.changeSceneToFile(MAIN_MENU_FILE)
	}
}

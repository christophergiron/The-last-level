extends Node

signal timeS_update(seconds_added)
signal timeM_update(minutes_added)

@export var player_name := "Player1"
@export var time_minutes = 0
@export var time_seconds = 0

const SAVE_PATH := "user://best_times.json"
const MAX_SCORES := 5

# Referencia al FirebaseManager
var firebase_manager

func _on_timer_timeout() -> void:
	if time_seconds >= 0:
		time_seconds = time_seconds + 1
	
	if time_seconds == 60:
		time_minutes = time_minutes + 1
		time_seconds = 0
	
	timeS_update.emit(time_seconds)
	timeM_update.emit(time_minutes)
	
func save_current_time() -> void:
	var total_seconds = time_minutes * 60 + time_seconds
	var best_times = load_best_times()
	
	var new_time_data = {
		"username": player_name,
		"time": total_seconds,
		"timestamp": Time.get_unix_time_from_system()
	}
	
	best_times.append(new_time_data)
	
	# Ordenar por tiempo (de menor a menor tiempo es mejor)
	best_times.sort_custom(func(a, b): return a["time"] < b["time"])
	
	# Mantener solo los mejores tiempos
	if best_times.size() > MAX_SCORES:
		best_times = best_times.slice(0, MAX_SCORES)
	
	# Guardar localmente
	var file = FileAccess.open(SAVE_PATH, FileAccess.WRITE)
	file.store_string(JSON.stringify(best_times, "\t"))
	file.close()
	
	# Subir a Firestore si está disponible
	if FirebaseManager.instance and FirebaseManager.instance.has_method("upload_best_times"):
		FirebaseManager.instance.upload_best_times()
	else:
		printerr("FirebaseManager no está disponible")

func load_best_times() -> Array:
	if FileAccess.file_exists(SAVE_PATH):
		var file = FileAccess.open(SAVE_PATH, FileAccess.READ)
		var content = file.get_as_text()
		file.close()
		var result = JSON.parse_string(content)
		if typeof(result) == TYPE_ARRAY:
			return result
	return []
	
#formato bonito <3

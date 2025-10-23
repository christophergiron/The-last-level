extends Node

signal timeS_update(seconds_added)
signal timeM_update(minutes_added)

@export var time_minutes = 0
@export var time_seconds = 0

func _on_timer_timeout() -> void:
	if time_seconds >= 0:
		time_seconds = time_seconds + 1
	
	if time_seconds == 60:
		time_minutes = time_minutes + 1
		time_seconds = 0
	
	timeS_update.emit(time_seconds)
	timeM_update.emit(time_minutes)

func save_time_json() -> void:
	var save_data = {
		"minutes": time_minutes,
		"seconds": time_seconds
	}
	var file = FileAccess.open("user://time_data.json", FileAccess.WRITE)
	var json_string = JSON.stringify(save_data, "\t")
	file.store_string(json_string)
	file.close()
	print("Tiempo guardado en JSON:", json_string)

func load_time_json() -> void:
	if FileAccess.file_exists("user://time_data.json"):
		var file = FileAccess.open("user://time_data.json", FileAccess.READ)
		var content = file.get_as_text()
		file.close()
		var result = JSON.parse_string(content)
		if typeof(result) == TYPE_DICTIONARY:
			time_minutes = result.get("minutes", 0)
			time_seconds = result.get("seconds", 0)
			print("Tiempo cargado desde JSON:", result)
		else:
			push_error("Error al parsear el JSON del tiempo guardado.")

#formato bonito <3

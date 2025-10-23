extends Node

signal timeS_update(seconds_added)
signal timeM_update(minutes_added)

@export var player_name := "Player1"
@export var time_minutes = 0
@export var time_seconds = 0

const SAVE_PATH := "user://best_times.json"
const MAX_SCORES := 5

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
	
	best_times.append({
		"username": player_name,
		"time": total_seconds
	})
	
	best_times.sort_custom(func(a, b): return a["time"] > b["time"])
	
	if best_times.size() > MAX_SCORES:
		best_times = best_times.slice(0, MAX_SCORES)
	
	var file = FileAccess.open(SAVE_PATH, FileAccess.WRITE)
	file.store_string(JSON.stringify(best_times, "\t"))
	file.close()

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

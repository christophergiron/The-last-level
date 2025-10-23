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
	
#formato bonito <3

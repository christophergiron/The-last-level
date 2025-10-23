extends Node

func _on_game_controller_time_s_update(seconds_added: Variant) -> void:
	if seconds_added < 10:
		$BoxContainer/Segundos.text = str("0", seconds_added)
	else:
		$BoxContainer/Segundos.text = str(seconds_added)

func _on_game_controller_time_m_update(minutes_added: Variant) -> void:
	$BoxContainer/Minutos.text = str(minutes_added)

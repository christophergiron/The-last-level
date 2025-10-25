extends Node

# Hace que esta clase esté disponible globalmente
static var instance

# Referencias a Firebase
var firebase
var firestore
var auth
var firebase_initialized = false
var auth_initialized = false
var auth_in_progress = false

# Señal para notificar cuando se obtienen los mejores tiempos
signal best_times_ready(data)

func _init():
	instance = self

func _ready():
	# Evitar múltiples instancias activas de este manager
	if instance != self:
		return
	# Inicializar Firebase buscando el autoload en /root
	await get_tree().process_frame  # da tiempo a que los autoloads se monten
	firebase = get_node_or_null("/root/Firebase")
	if firebase == null:
		printerr("Error: No se encontró el autoload '/root/Firebase'")
		print("Verifica que:")
		print("   1. En 'project.godot' exista [autoload] Firebase=\"*res://addons/godot-firebase/firebase/firebase.tscn\"")
		print("   2. El plugin esté habilitado en Configuración del Proyecto > Plugins")
		print("   3. Existe 'res://addons/godot-firebase/.env' con sección [firebase/environment_variables]")
		return
	
	print("Inicializando Firebase (autoload encontrado)...")
	
	# Aplicar configuración en tiempo de ejecución si el .env no se cargó
	apply_runtime_firebase_config()
	
	# Inicializar Auth
	auth = firebase.get_node_or_null("Auth")
	if auth:
		print("Modulo Auth encontrado")
		if not auth.login_succeeded.is_connected(_on_auth_success):
			auth.login_succeeded.connect(_on_auth_success)
		if not auth.login_failed.is_connected(_on_auth_failed):
			auth.login_failed.connect(_on_auth_failed)
		if not auth_initialized and not auth_in_progress:
			await login_anonymous()
	else:
		printerr("No se pudo obtener el nodo hijo 'Auth' del autoload Firebase")
	
	# Inicializar Firestore
	firestore = firebase.get_node_or_null("Firestore")
	if firestore:
		firebase_initialized = true
		print("Modulo Firestore encontrado")
		# Conectar manejo de errores de Firestore para ver detalles
		if not firestore.error.is_connected(_on_firestore_error):
			firestore.error.connect(_on_firestore_error)
		if firebase.has_method("get_plugin_version"):
			print("   - Versión del plugin: ", firebase.get_plugin_version())
	else:
		printerr("No se pudo obtener el nodo hijo 'Firestore' del autoload Firebase")

# Función para autenticación anónima
func login_anonymous():
	if auth_initialized:
		return true
	if auth_in_progress:
		while auth_in_progress:
			await get_tree().process_frame
		return auth_initialized
	auth_in_progress = true
	print("Iniciando autenticación anónima...")
	auth.login_anonymous()
	var result = await auth.auth_request
	auth_in_progress = false
	if result[0] == 1:
		var auth_result = result[1]
		print("Autenticación anónima exitosa")
		print("   - User ID: ", auth_result.localid)
		auth_initialized = true
		return true
	else:
		var error_code = result[0]
		var error_message = result[1]
		printerr("Error en autenticación anónima (Código %s): %s" % [str(error_code), str(error_message)])
		return false

# Señal que se dispara cuando la autenticación es exitosa
func _on_auth_success(auth_result):
	print("Usuario autenticado: ", auth_result.localid)
	auth_initialized = true

# Señal que se dispara cuando falla la autenticación
func _on_auth_failed(code, message):
	printerr("Error de autenticación (Código %s): %s" % [str(code), str(message)])
	auth_initialized = false

# Función para subir los mejores tiempos a Firestore
func upload_best_times():
	# Verificar autenticación primero
	if not auth_initialized:
		print("Autenticación no inicializada, intentando autenticar...")
		var auth_result = await login_anonymous()
		if not auth_result:
			printerr("No se pudo autenticar con Firebase")
			return
	
	if firestore == null:
		printerr("Firestore no está inicializado")
		return
	
	# Ruta al archivo local de mejores tiempos
	var file_path = "user://best_times.json"
	if not FileAccess.file_exists(file_path):
		printerr("No se encontró el archivo de mejores tiempos")
		return
	
	var file = FileAccess.open(file_path, FileAccess.READ)
	if not file:
		printerr("No se pudo abrir el archivo de mejores tiempos")
		return
	
	# Leer y parsear el JSON
	var json = JSON.new()
	var error = json.parse(file.get_as_text())
	file.close()
	
	if error != OK:
		printerr("Error al parsear el JSON: ", json.get_error_message())
		return
	
	var best_times = json.get_data()
	
	if not best_times is Array:
		printerr("El formato del archivo de tiempos no es válido")
		return
	
	if best_times.size() == 0:
		print("No hay datos para subir a Firestore")
		return
	
	print("Subiendo ", best_times.size(), " registros a Firestore...")
	
	# Subir cada tiempo a Firestore
	var success_count = 0
	for time_data in best_times:
		# Asegurarse de que los datos tengan el formato correcto
		if not ("username" in time_data and "time" in time_data):
			printerr("Datos incompletos en registro: ", time_data)
			continue
			
		# Añadir marca de tiempo si no existe
		if not "timestamp" in time_data:
			time_data["timestamp"] = Time.get_unix_time_from_system()

		# Generar un document_id único (username-time-timestamp)
		var uname = str(time_data.get("username", "player"))
		var tval = str(time_data.get("time", 0))
		var ts = str(time_data.get("timestamp", Time.get_unix_time_from_system()))
		var document_id = "%s-%s-%s" % [uname, tval, ts]
		document_id = document_id.replace(" ", "_")
		
		# Intentar subir a Firestore
		print("   Subiendo registro: ", document_id)
		var collection_ref = firestore.collection("best_times")
		var result = await collection_ref.set_doc(document_id, time_data)
		if result == null:
			printerr("   Error al subir documento ", document_id)
		else:
			print("   Documento subido: ", document_id)
			success_count += 1
	
	# Mostrar resumen
	print("\nResumen de la subida:")
	print("   - Total de registros: ", best_times.size())
	print("   - Subidos con éxito:  ", success_count)
	print("   - Errores:           ", best_times.size() - success_count)
	
	if success_count > 0:
		print("Datos subidos correctamente a Firestore")
	else:
		printerr("No se pudo subir ningún registro a Firestore")

# Función para obtener los mejores tiempos de Firestore
func get_best_times() -> Array:
	if firestore == null:
		printerr("Firestore no está inicializado")
		return []
	
	print("Obteniendo los mejores tiempos de Firestore...")
	
	# Construir consulta usando FirestoreQuery (API del plugin)
	var q := FirestoreQuery.new()
	q.from("best_times", false)
	q.order_by("time", FirestoreQuery.DIRECTION.DESCENDING)
	q.limit(5)

	var result = await firestore.query(q) # devuelve Array[FirestoreDocument]

	# Procesar los documentos a Dictionary normal
	var best_times: Array = []
	if typeof(result) == TYPE_ARRAY:
		for doc in result:
			if doc and doc.has_method("get_unsafe_document"):
				best_times.append(doc.get_unsafe_document())

	# Normalizar y ordenar por tiempo ascendente en cliente por seguridad
	var normalized: Array = []
	for rec in best_times:
		if typeof(rec) == TYPE_DICTIONARY:
			var t = rec.get("time", null)
			var tnum := -1.0
			if typeof(t) in [TYPE_FLOAT, TYPE_INT]:
				tnum = float(t)
			elif typeof(t) == TYPE_STRING:
				var parsed = str(t).to_float()
				if parsed > 0 or t == "0" or t == "0.0":
					tnum = parsed
			if tnum >= 0.0:
				normalized.append({
					"username": str(rec.get("username", "???")),
					"time": tnum,
					"timestamp": rec.get("timestamp", 0)
				})

	normalized.sort_custom(func(a, b):
		return a["time"] > b["time"]
	)

	# Tomar los 5 mejores
	var top5: Array = []
	for i in range(min(5, normalized.size())):
		top5.append(normalized[i])

	print("Se obtuvieron ", top5.size(), " registros de Firestore")
	return top5

# Solicita los mejores tiempos y emite una señal cuando estén listos
func request_best_times() -> void:
	var data = await get_best_times()
	emit_signal("best_times_ready", data)

# Maneja errores emitidos por Firebase.Firestore
func _on_firestore_error(err):
	var code = ""
	var status = ""
	var message = ""
	if typeof(err) == TYPE_DICTIONARY:
		var inner = err.get("error", err)
		code = str(inner.get("code", ""))
		status = str(inner.get("status", ""))
		message = str(inner.get("message", ""))
		printerr("Firestore error [%s %s]: %s" % [code, status, message])
	else:
				printerr("Firestore error: %s" % [str(err)])

# Aplica configuración de Firebase en tiempo de ejecución si el .env no fue cargado
func apply_runtime_firebase_config() -> void:
	if firebase == null:
		return
	# El autoload define un diccionario _config. Si apiKey está vacío, seteamos todo.
	var cfg: Dictionary = firebase._config
	if typeof(cfg) != TYPE_DICTIONARY:
		return
	var api_key := str(cfg.get("apiKey", ""))
	if api_key != "":
		return
	# Valores provistos por el usuario
	var runtime_config := {
		"apiKey": "AIzaSyAp96RKKO-UYvpgYSngFOfN0xYfClohGvg",
		"authDomain": "thelastlevel-c8125.firebaseapp.com",
		"databaseURL": "https://thelastlevel-c8125-default-rtdb.firebaseio.com",
		"projectId": "thelastlevel-c8125",
		"storageBucket": "thelastlevel-c8125.appspot.com",
		"messagingSenderId": "307166269379",
		"appId": "1:307166269379:web:8490412e45239739baf776",
		"measurementId": "G-SX1NDBKCDP",
		"clientId": cfg.get("clientId", ""),
		"clientSecret": cfg.get("clientSecret", ""),
		"domainUriPrefix": cfg.get("domainUriPrefix", ""),
		"functionsGeoZone": cfg.get("functionsGeoZone", ""),
		"cacheLocation": cfg.get("cacheLocation", ""),
		"emulators": cfg.get("emulators", {"ports": {
			"authentication": "",
			"firestore": "",
			"realtimeDatabase": "",
			"functions": "",
			"storage": "",
			"dynamicLinks": ""
		}}),
		"workarounds": cfg.get("workarounds", {"database_connection_closed_issue": false}),
		"auth_providers": cfg.get("auth_providers", {
			"facebook_id": "",
			"facebook_secret": "",
			"github_id": "",
			"github_secret": "",
			"twitter_id": "",
			"twitter_secret": ""
		})
	}
	firebase._config = runtime_config
	# Reconfigurar módulos con la nueva config
	if firebase.has_method("_setup_modules"):
		firebase._setup_modules()

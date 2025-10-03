plugins {
    id("com.utopia-rise.godot-kotlin-jvm") version "0.13.1-4.4.1"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

godot {
    // ---------Setup-----------------

    // the script registration which you'll attach to nodes are generated into this directory
    registrationFileBaseDir.set(projectDir.resolve("gdj"))

	// Create .gdj files from all JVM scripts
	isRegistrationFileGenerationEnabled.set(true)

    // defines whether the script registration files should be generated hierarchically according to the classes package path or flattened into `registrationFileBaseDir`
    //isRegistrationFileHierarchyEnabled.set(true)

    // defines whether your scripts should be registered with their fqName or their simple name (can help with resolving script name conflicts)
    //isFqNameRegistrationEnabled.set(false)

    // ---------Android----------------

    val sdkRoot = System.getenv("ANDROID_SDK_ROOT")
        ?: throw GradleException("ANDROID_SDK_ROOT must be set to your Android SDK path")

    val buildToolsDir = File(sdkRoot, "build-tools").listFiles()
        ?.maxByOrNull { it.name }  // elige la versión más reciente instalada
        ?: throw GradleException("No build-tools found in $sdkRoot/build-tools")

    val platformsDir = File(sdkRoot, "platforms").listFiles()
        ?.filter { it.name.startsWith("android-") }
        ?.maxByOrNull { it.name.removePrefix("android-").toIntOrNull() ?: 0 }
        ?: throw GradleException("No Android platforms found in $sdkRoot/platforms")

    godot {
        registrationFileBaseDir.set(projectDir.resolve("gdj"))
        isRegistrationFileGenerationEnabled.set(true)

        isAndroidExportEnabled.set(true)
        d8ToolPath.set(File(buildToolsDir, "d8"))
        androidCompileSdkDir.set(platformsDir)
    }
	// --------Library authors------------

	// library setup. See: https://godot-kotl.in/en/stable/develop-libraries/
    //classPrefix.set("MyCustomClassPrefix")
    //projectName.set("LibraryProjectName")
    //projectName.set("LibraryProjectName")
}

description = "AI Assitant for ZAP"

zapAddOn {
    addOnName.set("AIAssitant")
    zapVersion.set("2.12.0")

    manifest {
        author.set("Infosec Team")
    }
}

dependencies {
    implementation("com.github.rjeschke:txtmark:0.13")
}

crowdin {
    configuration {
        val resourcesPath = "org/zaproxy/addon/${zapAddOn.addOnId.get()}/resources/"
        tokens.put("%messagesPath%", resourcesPath)
        tokens.put("%helpPath%", resourcesPath)
    }
}

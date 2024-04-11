plugins {
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "22"
    modules = arrayOf("javafx.swing", "javafx.web").toMutableList()
}

description = "CIPHER extension."

zapAddOn {
    addOnName.set("CIPHER")

    manifest {
        author.set("ISLAB AI")
    }
}

crowdin {
    configuration {
        val resourcesPath = "org/zaproxy/addon/${zapAddOn.addOnId.get()}/resources/"
        tokens.put("%messagesPath%", resourcesPath)
        tokens.put("%helpPath%", resourcesPath)
    }
}

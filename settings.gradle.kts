dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    versionCatalogs {
        create("androidx") {
            from(files("gradle/androidx.versions.toml"))
        }
        create("deps") {
            from(files("gradle/dependencies.versions.toml"))
        }
        create("compose") {
            from(files("gradle/compose.versions.toml"))
        }
        create("app") {
            from(files("gradle/app.versions.toml"))
        }
    }
}

// --------------
// App
// --------------

include(":ComposeDialogs:Core")
project(":ComposeDialogs:Core").projectDir = file("library/core")
include(":ComposeDialogs:Modules:Info")
project(":ComposeDialogs:Modules:Info").projectDir = file("library/modules/info")
include(":ComposeDialogs:Modules:Input")
project(":ComposeDialogs:Modules:Input").projectDir = file("library/modules/input")
include(":ComposeDialogs:Modules:Number")
project(":ComposeDialogs:Modules:Number").projectDir = file("library/modules/number")
include(":ComposeDialogs:Modules:Color")
project(":ComposeDialogs:Modules:Color").projectDir = file("library/modules/color")
include(":ComposeDialogs:Modules:Date")
project(":ComposeDialogs:Modules:Date").projectDir = file("library/modules/date")
include(":ComposeDialogs:Modules:Time")
project(":ComposeDialogs:Modules:Time").projectDir = file("library/modules/time")
include(":ComposeDialogs:Modules:List")
project(":ComposeDialogs:Modules:List").projectDir = file("library/modules/list")
include(":ComposeDialogs:Modules:Progress")
project(":ComposeDialogs:Modules:Progress").projectDir = file("library/modules/progress")
include(":ComposeDialogs:Modules:Billing")
project(":ComposeDialogs:Modules:Billing").projectDir = file("library/modules/billing")

include(":demo")
project(":demo").projectDir = file("demo")

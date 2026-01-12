pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://repo1.maven.org/maven2/org/jmrtd/jmrtd/")
        maven(url = "https://mvnrepository.com/artifact/net.sf.scuba/scuba-sc-android")
        maven("https://mvnrepository.com/artifact/net.sf.scuba/scuba-sc-j2se")
    }
}

rootProject.name = "eMRTDApplication"
include(":app")

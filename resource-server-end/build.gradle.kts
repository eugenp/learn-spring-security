tasks.register("build") {
    gradle.includedBuilds.forEach { build ->
        dependsOn(build.task(":build"))
    }
}

tasks.register("clean") {
    gradle.includedBuilds.forEach { build ->
        dependsOn(build.task(":clean"))
    }
}
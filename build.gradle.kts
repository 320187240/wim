plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.15.0"
}

group = "cn.gbox"
version = "1.4.3"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    maven { url = uri("https://maven.aliyun.com/repository/google/") }
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("ch.qos.logback:logback-core:1.2.6")

    // 可选：使用 JSR-250 注解
    implementation("javax.annotation:jsr250-api:1.0")

    // 可选：使用 JetBrains 注解
    implementation("org.jetbrains:annotations:24.0.1")
}

intellij {
    version.set("2023.2.4")
    type.set("IU")
    pluginName.set("wim")
    downloadSources.set(true)
    plugins.set(
        listOf(
            "com.intellij.java" // 声明是一个插件
        )
    )
}

tasks {
    runIde {
        systemProperty("idea.is.internal", true)
        autoReloadPlugins.set(true)
        jvmArgs("-XX:+AllowEnhancedClassRedefinition")
    }

    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("")
    }
}
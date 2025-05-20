// 应用所需插件
plugins {
    id("java") // Java插件，用于支持Java项目构建
    id("org.jetbrains.intellij") version "1.15.0" // IntelliJ IDEA插件开发所需
}

// 项目信息配置
group = "cn.gbox" // 项目组ID
version = "1.4.3" // 项目版本号

// Maven仓库配置
repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/google/")
    }
    mavenLocal()
}

// Java版本配置，使用JDK 17
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // 设置Java语言版本为17
    }
}

// 项目依赖配置
dependencies {
    implementation("cn.hutool:hutool-all:5.8.26") // Hutool工具包
    implementation("com.belerweb:pinyin4j:2.5.1") // 拼音工具包
    implementation("com.github.jsqlparser:jsqlparser:4.7") // SQL解析工具
    implementation("mysql:mysql-connector-java:8.0.22") // MySQL数据库连接器
    implementation("org.jasypt:jasypt:1.9.3") // 加密工具库
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.16.1") // XML数据格式处理
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    implementation("org.slf4j:slf4j-api:1.7.32") // SLF4J API
    implementation("ch.qos.logback:logback-classic:1.2.6")  // Logback 实现
    implementation("ch.qos.logback:logback-core:1.2.6")  // Logback 核心
}

// Gradle IntelliJ插件配置
intellij {
    version.set("2023.2.4") // 指定目标IDE版本
    type.set("IU") // IC 代表idea 社区版，IU 代表idea 旗舰版
    pluginName.set("wim") // 插件名称

    downloadSources.set(true) // 是否下载IDE源码

    plugins.set(
        listOf(
            "com.intellij.java", // Java支持
            "com.intellij.database", // 数据库工具支持
            "com.jetbrains.sh", // Shell脚本支持
            "org.jetbrains.kotlin", // Kotlin支持
            "JavaScript" // JavaScript支持
        )
    )
}

// Gradle任务配置
tasks {
    // 配置runIde任务，用于运行插件
    runIde {
        systemProperty("idea.is.internal", true) // 开启IDE内部模式
        autoReloadPlugins.set(true) // 开启插件自动重载

        // 启用hotswap热替换功能（需要JBR 17+或JBR 11 + DCEVM）
        jvmArgs(listOf("-XX:+AllowEnhancedClassRedefinition"))
    }

    // 设置JVM的兼容性版本为Java 17
    withType<JavaCompile> {
        sourceCompatibility = "17" // 源代码兼容版本
        targetCompatibility = "17" // 目标代码兼容版本
        options.encoding = "UTF-8" // 编码格式为UTF-8
    }

    // 自动修改插件的XML配置文件
    patchPluginXml {
//        pluginDescription.set(projectDir.resolve("DESCRIPTION.md").readText()) // 插件描述读取自DESCRIPTION.md
//        changeNotes.set(projectDir.resolve("CHANGENOTES.md").readText()) // 更新日志读取自CHANGENOTES.md

        // 设置支持的IDE版本范围
        sinceBuild.set("232") // 最低支持版本 2022.3
        untilBuild.set("") // 不限制最高支持版本
    }

}


tasks.withType<JavaCompile> {
    sourceCompatibility = "17" // 编译时使用 Java 17
    targetCompatibility = "17" // 目标版本设置为 Java 17
}

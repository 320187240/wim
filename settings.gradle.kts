// 插件管理配置
pluginManagement {
    repositories {
        mavenCentral() // 使用中央Maven仓库，下载插件相关依赖
        gradlePluginPortal() // 使用Gradle官方插件门户，提供各种Gradle插件支持
    }
}

// 设置根项目名称
rootProject.name = "wim" // 定义当前项目的名称，用于在构建时标识项目

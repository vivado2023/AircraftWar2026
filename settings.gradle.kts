pluginManagement {
    repositories {
        // 阿里云镜像优先（国内网络更稳定），官方仓库兜底
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
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
plugins {
    // 注：模板自带的 foojay-resolver-convention 仅用于自动下载 JDK 工具链，
    // 本机已安装 JDK 17，移除以减少一次远程依赖解析（网络不稳定时更可靠）
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        google()
        mavenCentral()
    }
}

rootProject.name = "AircraftWar2026"
include(":app")

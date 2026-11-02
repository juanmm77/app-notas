plugins {
    id("com.android.application") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21" apply false
    id("com.google.devtools.ksp") version "2.2.21-2.0.4" apply false
}
tasks.wrapper {
    gradleVersion = "8.13"
    distributionSha256Sum = "20f1b1176237254a6fc204d8434196fa11a4cfb387567519c61556e8710aed78"
}

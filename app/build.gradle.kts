plugins {
    id("urlshortener.spring-app-conventions")
    kotlin("plugin.spring")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":delivery"))
    implementation(project(":repositories"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.webjars:bootstrap:${Version.BOOTSTRAP}")
    implementation("org.webjars:jquery:${Version.JQUERY}")

    implementation("com.maxmind.geoip2:geoip2:4.2.0")
    implementation("com.maxmind.db:maxmind-db:2.16.0")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    runtimeOnly("org.hsqldb:hsqldb")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${Version.MOCKITO}")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.apache.httpcomponents.client5:httpclient5")
}

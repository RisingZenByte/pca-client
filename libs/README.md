# Compile-only dependencies

Place MaLiLib and Tweakeroo JARs here for **local** builds (not committed to git).

Current target Minecraft version is defined in the root `gradle.properties` (`minecraft_version`).

Example for 26.2:

- `malilib-fabric-26.2-0.29.2.jar`
- `tweakeroo-fabric-26.2-0.29.2.jar`

Download from [Modrinth MaLiLib](https://modrinth.com/mod/malilib) and [Modrinth Tweakeroo](https://modrinth.com/mod/tweakeroo), matching your MC version.

GitHub Actions downloads the correct versions automatically during CI and release builds.

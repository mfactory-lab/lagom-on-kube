# Lagom Cube client side

1. lagomServiceLocator dependency

   a. using lagomServiceLocator locally

       git clone https://github.com/lagom/lagom.git

       cd lagom

       git reset --hard bd3c862

       sbt

       project devmode-scaladsl

       publishLocal

   b. auto downloading lagomServiceLocator dependency:

   if you don't want to use lagomServiceLocator locally, comment [line 13][build.sbt-13]:
   ```scala
   ...
   // "com.lightbend.lagom" %% "lagom-service-locator" % "1.3.4"
   ...
   ```
   and un-comment [line 16][build.sbt-16]:
   ```scala
   ...
   .dependsOn(lagomServiceLocator)
   ...
   ```
   in [build.sbt][build.sbt].

1. publish lagomKubeClientSide locally

    ```shell
    cd lagomKubeClientSide
    sbt publishLocal
    ```

[build.sbt]: build.sbt
[build.sbt-13]: build.sbt#L13
[build.sbt-16]: build.sbt#L16
# Building a Lagom Kube Service Registry and packing it into a Docker container

1. Paste your **Docker ID** into the [build.sbt][kube-service-registry-sbt-19] file:
    ```scala
    ...
    dockerRepository := Some("docker.io/YOUR_DOCKER_REPOSITORY")
    ...
    ```
    If you don't already have a Docker ID, create a **Docker account** at the [Docker Cloud][docker-cloud].

1. Set up the **lagomServiceLocator** dependency

    - To use lagomServiceLocator *locally*:

        ```shell
        git clone -b '1.3.4' --single-branch https://github.com/lagom/lagom.git

        cd lagom

        sbt

        project service-locator

        publishLocal

        ```

    - To download the lagomServiceLocator dependency *automatically*:

        If you don't want to use lagomServiceLocator locally, comment-out [line 13][kube-service-registry-sbt-13]
        ```scala
        ...
        // "com.lightbend.lagom" %% "lagom-service-locator" % "1.3.3"
        ...
        ```
        and uncomment [line 22][kube-service-registry-sbt-22]
        ```scala
        ...
        .dependsOn(lagomServiceLocator)
        ...
        ```
        in the [build.sbt][kube-service-registry-sbt] file.

1. Download, install and run the [Docker toolbox][docker-toolbox-site]

1. Run **Lagom Kube Service Registry** on a local machine in development mode

    - Download and install [DBeaver][dbeaver], a free universal SQL client

    - Install [Apache Cassandra][apache-cassandra] inside of a docker container:

        `docker run -d -p 7000-7001:7000-7001 -p 7199:7199 -p 9042:9042 -p 9160:9160 cassandra:3`

    - If Cassandra does not run locally, update the *cassandra connection ip* variable in [build.sbt][kube-service-registry-sbt-2931]:
        ```scala
        ...
        System.setProperty("CASSANDRA_CONNECTION_IP", "127.0.0.1")
        ...
        ```

    - Run the **Service Registry** in development mode:

        ```shell
        cd lagomKubeServiceRegister
        sbt
        setEnvTask
        run
        ```

1. Build the **Docker container**

    - update docker repository settings in [build.sbt][kube-service-registry-sbt]

        ```scala
            ...
                dockerRepository := Some("docker.io/YOUR_DOCKER_REPOSITORY"),
            ...
        ```

    - In the *lagomKubeServiceRegister* folder, create and publish a **Docker image**:
        ```shell
        cd lagomKubeServiceRegister
        sbt docker:publish
        ```

    - To make sure that the **Docker image** has been created:
        ```shell
        docker images
        ```

1. Push the image to the **Docker hub** as described in the [Docker documentation][docker-cloud-push-images]

[kube-service-registry-sbt]: build.sbt
[kube-service-registry-sbt-19]: build.sbt#L19
[kube-service-registry-sbt-13]: build.sbt#L13
[kube-service-registry-sbt-22]: build.sbt#L22
[kube-service-registry-sbt-29]: build.sbt#L29
[docker-cloud]: https://cloud.docker.com
[apache-cassandra]: http://cassandra.apache.org/
[apache-cassandra-installation]: http://cassandra.apache.org/doc/latest/getting_started/installing.html
[application.conf]: src/main/resources/application.conf#L2
[docker-toolbox-site]: https://www.docker.com/products/docker-toolbox
[docker-cloud-push-images]: https://docs.docker.com/docker-cloud/builds/push-images/
[dbeaver]: http://dbeaver.jkiss.org/download/enterprise/

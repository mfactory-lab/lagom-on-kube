# Building and pushing Lagom Kube services to Docker

1. Publish the **lagomKubeClientSide** library on the local machine

    Before building the Lagom services, we need to publish [lagomKubeClientSide][lagomKubeClientSide] locally, as described [here][lagomKubeClientSide-doc].

1. Configure the [Wolfram Alpha][wolfram-alpha] **AppID** for your build

    If you don't have a **Wolfram Alpha AppID** yet:
    - Create an account on the [Wolfram Alpha Developer Portal][wolfram-alpha]
    - Create a new Wolfram Alpha **application**

    Enter your **AppID** into [build.sbt][bot-build.sbt-114]:
    ```scala
    ...
    System.setProperty("WOLFRAM_APPID", {{ WOLFRAM_APPID }})
    ...
    ```

1. Configure the **Telegram bot token** for your build

    If you don't have a Telegram bot token yet, create a **Telegram bot** as described in the [Introduction for Telegram developers][introduction-telegram-developers].

    Enter your bot's **token** into [build.sbt][bot-build.sbt-115]:
    ```scala
    ...
    System.setProperty("TELEGRAM_BOT_TOKEN", {{ TELEGRAM_BOT_TOKEN }})
    ...
    ```

1. Run **Lagom services** on a local machine in development mode

    Take the following steps:
    - Run the **Lagom Kube Service Registry** (see [Lagom Kube Service Registry][lagom-kube-registry] how-to)
    - Go to the *TelegramWolframBot* folder
    - Launch the Lagom services:
        ```shell
        sbt
        setEnvTask
        runAll
        ```
    - Test the bot in **Telegram**: ```/test``` and ```/ask {{ question }}```

1. Insert your **Docker ID** into *build.sbt*, lines [35][bot-build.sbt-35], [62][bot-build.sbt-62], [89][bot-build.sbt-89]
    ```scala
    ...
    dockerRepository := Some("docker.io/YOUR_DOCKER_REPOSITORY")
    ...
    ```
    If you don't already have a **Docker ID**, create a **Docker account** at the [Docker Cloud][docker-cloud]

1. Run the **Docker Machine**
    - Run the Docker Machine:
    ```shell
    docker-machine start
    ```

1. Build the **Docker container**

    - Create a **Docker image**:
    ```shell
    sbt docker:publishLocal
    ```

    To make sure that the Docker image has been created:
     ```shell
    docker images
    ```

1. Push the image to the **Docker hub** as described in the [Docker documentation][docker-cloud-push-images]

[lagom-kube-registry]: ../lagomKubeServiceRegister/README.md
[lagomKubeClientSide]: ../lagomKubeClientSide
[lagomKubeClientSide-doc]: ../lagomKubeClientSide/README.md
[wolfram-alpha]: https://developer.wolframalpha.com/portal
[introduction-telegram-developers]: https://core.telegram.org/bots#6-botfather
[bot-build.sbt]: build.sbt
[bot-build.sbt-114]: build.sbt#L114
[bot-build.sbt-115]: build.sbt#L115
[bot-build.sbt-35]: build.sbt#L35
[bot-build.sbt-62]: build.sbt#L62
[bot-build.sbt-89]: build.sbt#L89
[docker-cloud]: https://cloud.docker.com
[docker-cloud-push-images]: https://docs.docker.com/docker-cloud/builds/push-images/

# Manual installation of kops

<!--- Does it need?
    for [config based][kops-config] or [ansible][ansible] bootstraps read the prementioned docs
-->

This how-to is based on [github.com/kubernetes/kops/blob/master/docs/aws.md][git-kops-guide].

1. You need to follow several **[preparation steps][aws-config]** to make sure the prerequisites for the installation are in place:

    - You have an [AWS][aws] account
    - A DNS route is configured, e.g. `http://lagom-cluster.some-domain.test`
    - An **S3 bucket** is created, e.g. `s3://lagom-cluster.config.12345`
    - The [CLI Tools][cli-tools] are installed

1. Export the **S3 bucket**

    ```shell
    export KOPS_STATE_STORE=s3://lagom-cluster.config.12345
    ```

1. Create an **Amazon cluster configuration** with *one master node* on a **t2.micro** instance and *two minion nodes* on **t2.medium** instances in the **eu-central-1a** zone

    ```shell
    kops create cluster --name=lagom-cluster.some-domain.test  --zones=eu-central-1a --node-count=1 --node-size=t2.medium --master-     size=t2.micro  --dns-zone=lagom-cluster.some-domain.test
    ```

    All of the nodes must have **20 GB** of **GP2** storage assigned.

    This can be adjusted in the **spec** section of the [config file][kops-config-file]:
    
    `kops edit ig --name=lagom-cluster.some-domain.test nodes`
    or
    `kops edit ig --name=lagom-cluster.some-domain.test master-eu-central-1a`.

    ```yaml
    spec:
    ...
    rootVolumeSize: 100
    rootVolumeType: gp2
    ...
    ```

1. Bootstrap the **AWS cluster**

    ```shell
    kops update cluster lagom-cluster.some-domain.test --yes
    ```

1. After the cluster is ready, it can be verified with `kops validate cluster`; all checks should be passed successfully

## Now that your cluster is ready, you can proceed to configure [Kubernetes][kubernetes]

[git-kops-guide]: https://github.com/kubernetes/kops/blob/master/docs/aws.md
[aws-config]: aws.md
[aws]: https://console.aws.amazon.com
[cli-tools]: cli-tools.md
[kubernetes]: kubernetes.md
[kops-config]: kops-config.md
[ansible]: ansible.md
[kops-config-file]: ../kube/kops/config.yaml

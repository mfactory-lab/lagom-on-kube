# ansible configuration [variables][vars]

## cluster name

*cluster_name*: `lagom-cluster.some-domain.test`

## state store

*state_store*: `s3://lagom-cluster.config.12345`

## ssh key

*ssh_public_key*: `~/.ssh/id_rsa.pub`

## aws region

*aws_region*: `eu-central-1`

## aws avaibility zone

*aws_zone*: `eu-central-1a`

## node parameters

*master_size*: `t2.small`

*master_count*: `1`

*master_rootVolumeSize*: `100` # in Gigabytes

*node_size*: `t2.medium`

*node_count*: `1`

*node_rootVolumeSize*: `100`

## [newrelic][newrelic] token

*new_relic_token*: `1234567`

## docker container for lagom kube service register

*lagom_sr_image*: `docker.io/alexray/lagomkubeserviceregister:0.0.14`

## wolfram application id

*wolfram_app_id*: `AAA-BBB`

## telegram bot token id

*telegram_bot_token*: `1111:AAABBB`

[newrelic]: https://newrelic.com/
[vars]: ./../kube/ansible/group_vars/all/vars.yaml
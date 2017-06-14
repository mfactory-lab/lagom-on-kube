package me.alexray.lagom.kube.discovery.storage

/**
  * Created by Alexander Ray on 12.05.17.
  **/


trait KubeSerializable extends Serializable

trait Command extends KubeSerializable
trait Event extends KubeSerializable
trait State extends KubeSerializable

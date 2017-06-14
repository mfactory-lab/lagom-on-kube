/**
  * Created by Alexander Ray on 09.05.17.
  **/

package me.alexray.lagom.kube.api

case class ServiceDescriptor(name: String, version: String) {
  def versionedPath(path: String) = s"/api/$name/$version/$path"
}
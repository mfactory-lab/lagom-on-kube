package me.alexray.lagom.kube.discovery.storage

import akka.util.Timeout
import com.lightbend.lagom.discovery.UnmanagedServices
import com.lightbend.lagom.internal.javadsl.registry.ServiceRegistryService
import org.joda.time.DateTime

case class ServiceName(name: String) extends KubeSerializable

case class ServiceInstanceName(name: String) extends KubeSerializable
case class ServiceDescription(serviceName: ServiceName,
                              instanceName: ServiceInstanceName,
                              srs: ServiceRegistryService) extends KubeSerializable
{
  def toRegisterStateEntry: (ServiceName, ServiceInstances) = serviceName -> ServiceInstances(srs, instanceName)
}

object ServiceDescription {
  val default = "default"

  def toNameInstanceTuple(name: String): (ServiceName, ServiceInstanceName) = {
    val s = name.split("-")
    (ServiceName(s(0)), ServiceInstanceName(if (s.length > 1) s(1) else default))
  }

  def apply(name: String, service: ServiceRegistryService): ServiceDescription = {
    val (sn, si) = toNameInstanceTuple(name)
    ServiceDescription(sn, si, service)
  }
}

case class PropertyName(name: String)
object PropertyName {
  def heartBeatProperty = PropertyName("heartBeatProperty")
}

sealed trait PropertyValue extends KubeSerializable

case class DateTimeProperty(date: DateTime) extends PropertyValue
object DateTimeProperty {
  def apply(): DateTimeProperty = DateTimeProperty(new DateTime())
}

case class ServiceInstance(name: ServiceInstanceName,
                           props: Map[PropertyName, PropertyValue]) extends KubeSerializable
{
  def refreshHeartBeat(): ServiceInstance = copy(props = props + ServiceInstance.heartBeat)
}
object ServiceInstance {
  def heartBeat: (PropertyName, DateTimeProperty) = PropertyName.heartBeatProperty -> DateTimeProperty()
  def apply(name: ServiceInstanceName): ServiceInstance = ServiceInstance(name, Map(heartBeat))
}

case class ServiceInstances(srs: ServiceRegistryService, instances: Map[ServiceInstanceName, ServiceInstance]) {
  def get(name: ServiceInstanceName): Option[ServiceInstance] = instances.get(name)
  def update(instance: ServiceInstance): ServiceInstances = copy(instances = instances + (instance.name -> instance))
  def addNewOrUpdateHeartBeat(name: ServiceInstanceName): ServiceInstances =
    update(instances.get(name).fold(ServiceInstance(name))(si => si.refreshHeartBeat()))
  def remove(instanceName: ServiceInstanceName): ServiceInstances = copy(instances = instances - instanceName)
}

object ServiceInstances {
  def apply(srs: ServiceRegistryService): ServiceInstances =
    ServiceInstances(srs, Map.empty[ServiceInstanceName, ServiceInstance])
  def apply(srs: ServiceRegistryService, instanceName: ServiceInstanceName): ServiceInstances =
    ServiceInstances(srs, Map(instanceName -> ServiceInstance(instanceName)))
}

case class RegisterState(registry: Map[ServiceName, ServiceInstances]) extends State {
  def addNewOrUpdateHeartBeat(d: ServiceDescription): RegisterState = {
    val instances = registry.getOrElse(d.serviceName, ServiceInstances(d.srs))
    update(d.serviceName, instances.addNewOrUpdateHeartBeat(d.instanceName))
  }

  def update(serviceName: ServiceName, instances: ServiceInstances): RegisterState =
    RegisterState(registry + (serviceName -> instances))

  def get(name: ServiceName): Option[ServiceInstances] = registry.get(name)
  def get(name: String): Option[ServiceInstances] = get(ServiceName(name))

  def remove(serviceName: ServiceName, instanceName: ServiceInstanceName): RegisterState = {
    get(serviceName).fold(RegisterState.empty) { (si: ServiceInstances) =>
      val nsi = si.remove(instanceName)

      if (nsi.instances.isEmpty) {
        RegisterState(registry - serviceName)
      } else {
        update(serviceName, nsi)
      }

    }
  }

  def remove(t: (ServiceName, ServiceInstanceName)): RegisterState = remove(t._1, t._2)

  def remove(l: List[(ServiceName, ServiceInstanceName)]): RegisterState =
    if (l.isEmpty) this else remove(l.head).remove(l.tail)

  def getDeadServices(now: DateTime, timeout: Timeout): List[(ServiceName, ServiceInstanceName)] = {

    val dt = new DateTime()
    val year2000 = dt.withYear(2000)
    val twoHoursLater = dt.plusHours(2)

    val timeoutDT = now.minusMillis(timeout.duration.toMillis.toInt)

    registry.mapValues { ins =>
        ins.instances.filter {
          case (in: ServiceInstanceName, instance: ServiceInstance) =>
            instance.props.getOrElse(PropertyName.heartBeatProperty, DateTimeProperty()) match {
              case DateTimeProperty(dt) => dt.isBefore(timeoutDT)
              case _ => true
            }
        }
    }.filter(_._2.nonEmpty).flatMap(x => x._2.keys.map(x._1 -> _)).toList

  }
}

object RegisterState {
  def empty: RegisterState = RegisterState(Map.empty[ServiceName, ServiceInstances])

  def apply(unmanagedServices: UnmanagedServices): RegisterState = {
    RegisterState(
      unmanagedServices.services.map { s =>
        ServiceDescription(s._1, s._2).toRegisterStateEntry
     }
    )
  }

}
import javax.inject.Inject

import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}
import services.{WaydataService, WaydataUserService, WaydataWayService}

class Module @Inject() (environment: Environment,
                        configuration: Configuration)
  extends AbstractModule {

  override def configure() = {

    bind(classOf[WaydataService]).asEagerSingleton()
    bind(classOf[WaydataUserService]).asEagerSingleton()
    bind(classOf[WaydataWayService]).asEagerSingleton()
  }
}

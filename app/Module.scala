import javax.inject.Inject

import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}
import services.{WaydataPointService, WaydataUserService, WaydataWayService}

class Module @Inject() (environment: Environment,
                        configuration: Configuration)
  extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[WaydataPointService]).asEagerSingleton()
    bind(classOf[WaydataUserService]).asEagerSingleton()
    bind(classOf[WaydataWayService]).asEagerSingleton()
  }
}

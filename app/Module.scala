import javax.inject.Inject

import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}
import services.WaydataService

class Module @Inject() (environment: Environment,
                        configuration: Configuration)
  extends AbstractModule {

  override def configure() = {

    bind(classOf[WaydataService]).asEagerSingleton()
  }
}

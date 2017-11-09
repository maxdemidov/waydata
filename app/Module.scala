import com.google.inject.AbstractModule

import services.WaydataService

class Module extends AbstractModule {

  override def configure() = {

    bind(classOf[WaydataService]).asEagerSingleton()
  }
}

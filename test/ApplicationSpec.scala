import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "Routes" should {

    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }
  }

  // TODO - write a tests for waydata

}

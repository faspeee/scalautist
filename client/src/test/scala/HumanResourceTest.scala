
import java.sql.Date

import akka.Done
import akka.actor.Terminated
import caseclass.CaseClassDB.{Contratto, Disponibilita, Login, Persona, StoricoContratto, Terminale, Turno, Zona}
import caseclass.CaseClassHttpMessage.Assumi
import model.utils.ModelUtils._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AsyncFlatSpec
import model.entity.HumanResourceModel
import utils.ClientAkkaHttp

import scala.concurrent.Future

class HumanResourceTest extends AsyncFlatSpec with BeforeAndAfterEach with ClientAkkaHttp {
  var terminale:HumanResourceModel=_
  protected var insertPersona: Assumi = _
  override def beforeEach(): Unit = {
    terminale = HumanResourceModel()
    val daAssumere:Persona = Persona("JuanitoS","PerezS","569918598",Some(""),3,isNew = true,"")
    val contratto:StoricoContratto = StoricoContratto(new Date(System.currentTimeMillis()),None,None,1,Some(1),Some(2))
    val disp:Disponibilita = Disponibilita("Lunes","Sabato")
    insertPersona = Assumi(daAssumere,contratto,Some(disp))
  }

  behavior of "contract"
  it should "return login with credential of a person" in {
    val futureRecruit:Future[Option[Login]]=terminale.recruit(insertPersona)
    futureRecruit map { recruit => assert(recruit.isDefined)}
  }
  it should "return ok when delete person" in {
    val futureDelete:Future[Unit]=terminale.fires(8)
    futureDelete map { recruit => assert(recruit==())}
  }
  it should "return list of terminal lenght 2" in {
    val futureTerminale:Future[Option[List[Terminale]]]=terminale.getTerminalByZone(1)
    futureTerminale map { terminale => assert(terminale.head.length==2)}
  }
  it should "return defined option with zona" in {
    val futureZona:Future[Option[Zona]]=terminale.getZone(1)
    futureZona map { terminale => assert(terminale.isDefined)}
  }
  it should "return type contract with length 8" in {
    val futureContract:Future[Option[List[Contratto]]]=terminale.getAllContract
    futureContract map { contract => assert(contract.head.length==8)}
  }
  it should "return all shift with length 6" in {
    val futureshift:Future[Option[List[Turno]]]=terminale.getAllShift
    futureshift map { shift => assert(shift.head.length==6)}
  }
  it should "shutdown System" in {
    val futureTerminated:Future[Terminated]=terminale.shutdownActorSystem()
    futureTerminated map { terminated => assert(true)}
  }
}

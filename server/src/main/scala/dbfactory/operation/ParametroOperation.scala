package dbfactory.operation

import caseclass.CaseClassDB.{GiornoInSettimana, Parametro, ZonaTerminale}
import caseclass.CaseClassHttpMessage.InfoAlgorithm
import dbfactory.implicitOperation.ImplicitInstanceTableDB.{InstanceGiornoInSettimana, InstanceRegola, InstanceZonaTerminal}
import dbfactory.implicitOperation.OperationCrud
import dbfactory.util.Helper._
import messagecodes.StatusCodes
import slick.jdbc.SQLServerProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** @author Fabian Aspee Encina
 *  Trait which allows to perform operations on the parametro table.
 */
trait ParametroOperation extends OperationCrud[Parametro]{

  /**
   * Method which allow save info of a parametrization, this info contains name parameters, saturday rule and
   * info for normal week that can be day in week and modification for teoric request, and ruler for this operation
   * that can be a integer or percent or media o moda
   * @param infoAlgorithm case class that represent info that user want save in database
   * @return Future of Option of Int that can be :
   *         None if delete all have a problem, this is a extreme case
   *          [[messagecodes.StatusCodes.ERROR_CODE1]] if exist error while insert Parametro
   *          [[messagecodes.StatusCodes.ERROR_CODE2]] if exist error while insert GiornoInSettimana
   *          [[messagecodes.StatusCodes.ERROR_CODE3]] if ZonaTerminal is empty or name parameter is empty
   *          [[messagecodes.StatusCodes.ERROR_CODE4]] if Regola not exist
   *          [[messagecodes.StatusCodes.ERROR_CODE5]] if GiornoInSettimana contains quantity less that zero
   *          [[messagecodes.StatusCodes.SUCCES_CODE]] if not exist error in operation
   */
  def saveInfoAlgorithm(infoAlgorithm: InfoAlgorithm):Future[Option[Int]]

  /**
   * Method that allow return info for specific parametrization this contains name parameter, saturday rule and all
   * info for normal week
   * @param idParameter represent a parametrization in Parametro Table
   * @return Future of Option of InfoAlgorithm
   */
  def getParameter(idParameter:Int):Future[Option[InfoAlgorithm]]
}
object ParametroOperation extends ParametroOperation {

  override def saveInfoAlgorithm(infoAlgorithm: InfoAlgorithm): Future[Option[Int]] = infoAlgorithm match {
    case value if value.zonaTerminale.isEmpty || value.parametro.nome.isEmpty =>
      Future.successful(Some(StatusCodes.ERROR_CODE3))
    case value if value.giornoInSettimana.forall(giorno=>giorno.exists(quant=>quant.quantita<=0))=>
      Future.successful(Some(StatusCodes.ERROR_CODE5))
    case _ =>for{
      idParametri <- insert(infoAlgorithm.parametro)
      result <- insertGiornoInSettimanaAndZonaTerminal(idParametri,infoAlgorithm)
    }yield result
  }

  private def insertGiornoInSettimanaAndZonaTerminal(idParametri:Option[Int],infoAlgorithm: InfoAlgorithm):Future[Option[Int]]={
    (idParametri,infoAlgorithm.giornoInSettimana) match {
      case (Some(id),Some(giornoInSettimana)) => verifyRegolaAndInsert(giornoInSettimana).flatMap{
        case Some(StatusCodes.SUCCES_CODE) => GiornoInSettimanaOperation.insertAll(giornoInSettimana.map(value=>value.copy(parametriId = Some(id)))).flatMap {
          case Some(_) => insertZonaTerminal(infoAlgorithm.zonaTerminale,idParametri)
          case None =>deleteParametri(idParametri)
        }
        case Some(StatusCodes.ERROR_CODE4) =>deleteParametri(idParametri).collect(_=>Some(StatusCodes.ERROR_CODE4))
      }
      case (Some(_),None)=>insertZonaTerminal(infoAlgorithm.zonaTerminale,idParametri)
      case (None,_) =>Future.successful(Some(StatusCodes.ERROR_CODE1))
    }
  }
  private def verifyRegolaAndInsert(giornoInSettimana: List[GiornoInSettimana])={
    InstanceRegola.operation().selectFilter(_.id.inSet(giornoInSettimana.map(_.regolaId))).collect {
      case Some(value) if value.length==giornoInSettimana.map(_.regolaId).length=> Some(StatusCodes.SUCCES_CODE)
      case Some(_) => Some(StatusCodes.ERROR_CODE4)
      case None => Some(StatusCodes.ERROR_CODE4)
    }
  }
  private def insertZonaTerminal(zonaTerminale:List[ZonaTerminale],idParametro:Option[Int]): Future[Option[Int]]  ={
    ZonaTerminaleOperation.insertAll(zonaTerminale.map(_.copy(parametriId=idParametro))).flatMap {
      case Some(_) =>Future.successful(Some(StatusCodes.SUCCES_CODE))
      case None =>deleteParametri(idParametro)
    }
  }
  private def deleteParametri(idParametri:Option[Int]): Future[Option[Int]] ={
    idParametri.map(result=>delete(result)).convert().collect {
      case Some(_) => Some(StatusCodes.ERROR_CODE2)
      case None =>None
    }
  }

  override def getParameter(idParameter: Int): Future[Option[InfoAlgorithm]] =
    for{
      parameter<-select(idParameter)
      zonaTerminal<-InstanceZonaTerminal.operation()
        .selectFilter(_.parametriId===idParameter)
      giornoInSettimana<-InstanceGiornoInSettimana.operation()
        .selectFilter(_.parametriId===idParameter)
    }yield (parameter,zonaTerminal) match {
      case (Some(value),Some(zonaTerminal)) => Some(InfoAlgorithm(value,zonaTerminal, giornoInSettimana))
      case _ => None
    }
}
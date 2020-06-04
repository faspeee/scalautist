package view.fxview.mainview

import java.net.URL
import java.util.ResourceBundle

<<<<<<< HEAD
import caseclass.CaseClassDB
import caseclass.CaseClassDB.{Assenza, Contratto, Persona, Terminale, Turno, Zona}
=======
import caseclass.CaseClassDB._
>>>>>>> develop
import caseclass.CaseClassHttpMessage.Assumi
import controller.HumanResourceController
import javafx.stage.Stage
<<<<<<< HEAD
import view.{BaseView, DialogView}
import view.fxview.{AbstractFXDialogView, FXHelperFactory}
import view.fxview.component.HumanResources.subcomponent.IllBoxParent
import view.fxview.component.HumanResources.{HRHome, HRViewParent}
=======
import view.BaseView
import view.fxview.AbstractFXDialogView
import view.fxview.component.HumanResources.HRHome
import view.fxview.component.HumanResources.subcomponent.EmployeeView
import view.fxview.component.HumanResources.subcomponent.parent.HRHomeParent
>>>>>>> develop

/**
 * @author Francesco Cassano
 *
 * A view to manage human resource Views functionalities.
 * It extends [[view.BaseView]]
 *
 */
trait HumanResourceView extends DialogView {

  /**
   * Show child's recruit view
   *
   */
  def drawRecruit(zones: List[Zona], contracts: List[Contratto], shifts: List[Turno]): Unit

  /**
   * Show terminals into child's recruit view
   *
   */
  def drawTerminal(terminals: List[Terminale]): Unit

  /**
   * Show the view that requested the list of employees
   *
   */
  def drawEmployeeView(employeesList: List[Persona], viewToDraw: String): Unit
}

/**
 * @author Francesco Cassano
 *
 * Companion object of [[view.fxview.mainview.HumanResourceView]]
 *
 */
object HumanResourceView {

  def apply(stage: Stage): HumanResourceView = new HumanResourceHomeFX(stage)

  /**
   * HumanResourceView FX implementation
   *
   * @param stage
   *              Stage that load view
   */
  private class HumanResourceHomeFX(stage: Stage) extends AbstractFXDialogView(stage)
    with HumanResourceView with HRHomeParent {

    private var myController: HumanResourceController = _
    private var hrHome: HRHome = _

    /**
     * Closes the view.
     */
    override def close(): Unit = stage.close()

    override def initialize(location: URL, resources: ResourceBundle): Unit = {
      super.initialize(location, resources)
      myController = HumanResourceController()
      myController.setView(this)
      hrHome = HRHome()
      hrHome.setParent(this)
      pane.getChildren.add(hrHome.pane)
      FXHelperFactory.modalWithMessage(myStage,"Strunz").show()
    }

    ///////////////////////////////////////////////////////////////// Da VIEW A CONTROLLER impl HRViewParent

    override def recruitClicked(persona: Assumi): Unit =
      myController.recruit(persona)

    override def fireClicked(employees: Seq[Int]): Unit = println("ciao")
      //myController.fires()

    override def loadRecruitTerminals(zona: Zona): Unit =
      myController.getTerminals(zona)

    override def drawRecruitPanel: Unit =
      myController.getRecruitData

<<<<<<< HEAD
    override def drawRecruit(zones: List[Zona], contracts: List[Contratto], shifts: List[Turno]): Unit = {
=======
    override def drawEmployeePanel(viewToDraw: String): Unit =
      myController.getAllPersona(viewToDraw)

    ///////////////////////////////////////////////////////////////// Da CONTROLLER A VIEW impl HumanResourceView

    override def drawRecruit(zones: List[Zona], contracts: List[Contratto], shifts: List[Turno]): Unit =
>>>>>>> develop
      hrHome.drawRecruit(zones, contracts, shifts)
    }

    override def drawTerminal(terminals: List[Terminale]): Unit =
      hrHome.drawRecruitTerminals(terminals)

<<<<<<< HEAD
    override def saveAbsence(absence: Assenza): Unit = myController.saveAbsence(absence)
=======
    override def drawEmployeeView(employeesList: List[Persona], viewToDraw: String): Unit = viewToDraw match {
      case EmployeeView.fire => hrHome.drawFire(employeesList)
    }

>>>>>>> develop
  }
}
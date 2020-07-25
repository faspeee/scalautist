package view.fxview

import java.net.URL
import java.util.ResourceBundle

import javafx.application.Platform
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control.{Button, Label}
import javafx.scene.image.Image
import javafx.scene.layout.{BorderPane, StackPane}
import javafx.stage.{Modality, Stage}
import model.entity.HumanResourceModel
import view.{BaseView, DialogView, GoBackView}
import view.fxview.loader.FXLoader

import scala.util.Success
import view.fxview.util.ResourceBundleUtil._
/**
 * @author Giovanni Mormone.
 *
 * Template class of type [[view.BaseView]] with basic funtionality to show
 * and hide a view loaded from fxml file.
 * @param myStage
 *                The [[javafx.stage.Stage]] where the view is Shown.
 *
 */
abstract class AbstractFXDialogView(val myStage:Stage) extends Initializable with DialogView{
  /**
   * The base pane of the fxView where the components are added.
   */
  @FXML
  protected var pane: StackPane = _
  protected var generalResources: ResourceBundle = _
  private val image = new Image(getClass.getResource("../../../images/program_icon.png").toString)
  myStage.getIcons.add(image)
  /**
   * Stage of this view.
   */
  FXLoader.loadScene(myStage,this,"Base")

  override def initialize(location: URL, resources: ResourceBundle): Unit ={
    myStage.setTitle(resources.getResource("nome"))
    generalResources = resources
  }

  override def show(): Unit =
    myStage.show()

  override def hide(): Unit =
    myStage.hide()

  override def showMessage(message: String): Unit =
    FXHelperFactory.modalWithMessage(myStage,message).show()

  override def showMessageFromKey(message: String): Unit =
    Platform.runLater(() =>showMessage(generalResources.getResource(message)))

  override def alertMessage(message: String): Boolean =
    FXHelperFactory.modalAlert(myStage,message)
}

/**
 * @author Giovanni Mormone.
 *
 * Template class of type [[view.GoBackView]] with basic funtionality to show
 * and hide a view loaded from fxml file and to go back to a previous scene, if present.
 * @param myStage
 *              The [[javafx.stage.Stage]] where the view is Shown.
 * @param oldScene
 *                 The Scene to show if go back is called.
 *
 */
abstract class AbstractFXViewWithBack(override val myStage:Stage, oldScene: Option[Scene]) extends AbstractFXDialogView(myStage) with GoBackView{
  override def back(): Unit =
    myStage.setScene(oldScene.getOrElse(myStage.getScene))
}

/**
 * @author Fabian Aspee, Giovanni Mormone.
 *
 * Template class of type [[view.DialogView]] with basic funtionality to show
 * and hide a view loaded from fxml file.
 * @param parentStage
 *                The [[javafx.stage.Stage]] where the view is Shown.
 *
 */
abstract class AbstractFXModalView(val parentStage:Stage) extends Initializable with DialogView{
  /**
   * The base pane of the fxView where the components are added.
   */
  @FXML
  protected var pane: StackPane = _
  protected var generalResources: ResourceBundle = _
  protected val myStage = new Stage()
  private val PREDEF_WIDTH_SIZE: Double = 350
  private val PREDEF_HEIGHT_SIZE: Double = 300
  /**
   * Stage of this view.
   */
  FXLoader.loadScene(myStage,this,"Base")

  override def initialize(location: URL, resources: ResourceBundle): Unit ={
    myStage.setTitle(resources.getResource("nome"))
    myStage.setResizable(false)
    myStage.setMinWidth(PREDEF_WIDTH_SIZE)
    myStage.setMinHeight(PREDEF_HEIGHT_SIZE)
    generalResources = resources
  }

  override def show(): Unit = {
    myStage.initModality(Modality.APPLICATION_MODAL)
    myStage initOwner parentStage
    myStage.showAndWait()
  }
  override def hide(): Unit =
    myStage.hide()

  override def showMessage(message: String): Unit =
    FXHelperFactory.modalWithMessage(myStage,message).show()

  override def showMessageFromKey(message: String): Unit = {
      showMessage(generalResources.getResource(message))
  }
  override def alertMessage(message: String): Boolean =
    FXHelperFactory.modalAlert(myStage,message)
}
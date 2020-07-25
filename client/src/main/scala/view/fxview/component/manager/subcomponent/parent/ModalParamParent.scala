package view.fxview.component.manager.subcomponent.parent

import caseclass.CaseClassHttpMessage.InfoAlgorithm
import view.fxview.component.manager.subcomponent.ParamsModal.DataForParamasModel
import view.fxview.component.modal.ModalParent

/**
 *
 */
trait ModalParamParent extends ModalParent {

  /**
   *
   * @param idp
   */
  def getInfoToShow(idp: Int, data: DataForParamasModel): Unit

  /**
   *
   * @param param
   */
  def loadParam(param: InfoAlgorithm): Unit

}
